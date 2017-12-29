//    CogLogo : an implementation of the Cogniton architecture
//    Copyright (C) 2017  SURO François (suro@lirmm.fr)
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <https://www.gnu.org/licenses/>.

package src.commands;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.nlogo.agent.Turtle;
import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.Reporter;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;

import src.CognitiveScheme;
import src.CognitiveScheme.Cogniton;
import src.CognitonExtension;
import src.editor.AgentWatcher;

public class ChooseNextPlan implements Reporter{

	public Syntax getSyntax(){
		return SyntaxJ.reporterSyntax(Syntax.StringType());
	}
	
	@Override
	public Object report(Argument[] args, Context context) throws ExtensionException {
		if(!CognitonExtension.isLoaded)
			CognitonExtension.loadCognitiveSchemes(context.workspace().getModelPath());

		Double groupId;
		Turtle t = (Turtle) context.getAgent();
		String breedName = t.getBreed().printName();
		CognitiveScheme myCogScheme = CognitonExtension.cognitiveSchemes.get(t.getVariable(CognitonExtension.getBreedVariableOffset(breedName)-1));		
		if(myCogScheme != null)
		{
			ArrayList<Double> values = new ArrayList<Double>();
			for(int i = 0 ; i < myCogScheme.cognitonNames.size(); i++)
			{
				if(t.getVariable(CognitonExtension.getBreedVariableOffset(breedName)+i).getClass() == String.class)
					values.add(null);
				else
					values.add((Double) t.getVariable(CognitonExtension.getBreedVariableOffset(breedName)+i));	
			}
			HashMap<String, Double> plans = new HashMap<>();
			for(int i = 0 ; i < myCogScheme.cognitonNames.size(); i++)
			{
				if(values.get(i)!=null)
					for(String p : myCogScheme.cognitons.get(i).dependencyLinks)
						plans.put(p, 0.0);					
			}
			for(int i = 0; i < myCogScheme.groupNames.size(); i++)
			{
				groupId = (Double) t.getVariable(myCogScheme.getCulturonOffset(breedName)+(i*3));
				if(groupId != -1 && myCogScheme.amIInGroup(groupId, t))
				{
					for(Cogniton c : myCogScheme.getCulturonsForGroupRole(myCogScheme.groupNames.get(i), (String) t.getVariable(myCogScheme.getCulturonOffset(breedName)+(i*3)+1)))
						for(String p : c.dependencyLinks)
							plans.put(p, 0.0);	
				}
			}
			for(int i = 0 ; i < myCogScheme.cognitonNames.size(); i++)
			{
				for(String p : myCogScheme.cognitons.get(i).influencelinks.keySet())
				{
					if(plans.get(p)!=null && values.get(i)!=null)
						plans.put(p, plans.get(p)+(myCogScheme.cognitons.get(i).influencelinks.get(p)*values.get(i)));											
				}
			}
			
			Double participationValue;
			
			for(int i = 0; i < myCogScheme.groupNames.size(); i++)
			{
				groupId = (Double) t.getVariable(myCogScheme.getCulturonOffset(breedName)+(i*3));
				if(groupId != -1 && myCogScheme.amIInGroup(groupId, t))
				{
					participationValue = (Double) t.getVariable(myCogScheme.getCulturonOffset(breedName)+(i*3)+2);
					for(Cogniton c : myCogScheme.getCulturonsForGroupRole(myCogScheme.groupNames.get(i), (String) t.getVariable(myCogScheme.getCulturonOffset(breedName)+(i*3)+1)))
						for(String p : c.influencelinks.keySet())
							if(plans.get(p)!=null)
								plans.put(p, plans.get(p)+(c.influencelinks.get(p)*participationValue*myCogScheme.getCulturonValueForGroupInstance(groupId,c.Name)));	
				}
			}
			String plan = "die";
			if(myCogScheme.myDecisionMaker.equals(CognitiveScheme.DecisionMakers[0]))
				plan =  MaxWeightDecisionMaker(plans);
			else if(myCogScheme.myDecisionMaker.equals(CognitiveScheme.DecisionMakers[1]))
				plan =  WeightedStochasticDecisionMaker(plans);
			else if(myCogScheme.myDecisionMaker.equals(CognitiveScheme.DecisionMakers[2]))
				plan =  BiasedWeightedStochasticDecisionMaker(plans,myCogScheme.DecisionMakerBias);
			else
				CognitonExtension.myConsole.println("WARNING : the cognitive scheme < " + myCogScheme.Name + " > did not have a valid decision maker and the agent was terminated");	
			if(AgentWatcher.clearDispPlan)
				t.label("");
			else if(AgentWatcher.dispPlan)
				t.label(plan);
			return plan;
		}
		CognitonExtension.myConsole.println("WARNING : the agent < "+ ((Turtle) context.getAgent())._id +" > did not have a cognitive scheme and was terminated");
		return "die";
	}

	private String MaxWeightDecisionMaker(HashMap<String, Double> plans) {
		double bestWeight = 0.0;
		String bestPlan = "";
		Set<String> keys = plans.keySet();
		for(String s : keys)
		{
			if(bestPlan.equals("") || plans.get(s) > bestWeight)
			{
				bestPlan = s;
				bestWeight = plans.get(s);
			}
		}		
		return bestPlan;
	}
	
	private String WeightedStochasticDecisionMaker(HashMap<String, Double> plans) {
		double sum = 0;
		double temp = 0;
		double gen = Math.random();
		double worstWeight=0;
		double offset=0;
		boolean init = true;
		
		if(plans.keySet().size()>1)
		{
			for(String s : plans.keySet())
			{
				if(!init)
				{
					if(plans.get(s) < worstWeight)
						worstWeight = plans.get(s);									
				}
				else
				{
					init = false;
					worstWeight = plans.get(s);				
				}
			}
			
			if(worstWeight < 1.0)
				offset = 1.0 - worstWeight;
			for(String s : plans.keySet())
				sum += plans.get(s) + offset;	
			
			for(String s : plans.keySet())
			{
				temp += (plans.get(s) + offset) / sum;

				if(gen <= temp)
					return s;				
			}
			
		}else if (plans.keySet().size()>0)
			return plans.keySet().iterator().next();
		CognitonExtension.myConsole.println("WARNING : the stochastic decision maker was not able to determine a plan to use and the agent was terminated");	
		return "die";
	}
	
	private String BiasedWeightedStochasticDecisionMaker(HashMap<String, Double> plans, double decisionMakerBias) {
		double sum = 0;
		double temp = 0;
		double gen = Math.pow(Math.random(),decisionMakerBias);
		double offset = 0.0;
		boolean init = true;
		boolean sorted = false;
		ArrayList<String> planNames = new ArrayList<>();
		ArrayList<Double> planValues = new ArrayList<>();
		
		if(plans.keySet().size()>1)
		{
			for(String s : plans.keySet())
			{
				if(!init)
				{
					sorted = false;
					for(int i = 0 ; i < planValues.size(); i++)
					{
						if(plans.get(s)>planValues.get(i))
						{
							planValues.add(i, plans.get(s));
							planNames.add(i, s);
							sorted = true;
							break;
						}
					}
					if(!sorted)
					{
						planValues.add(plans.get(s));
						planNames.add(s);
					}						
				}
				else
				{
					init = false;
					planNames.add(s);
					planValues.add(plans.get(s));
				}
			}
			if(planValues.get(planValues.size()-1) < 1.0)
				offset = 1.0 - planValues.get(planValues.size()-1);
			for(double d : planValues)
				sum += d + offset;	
			for(int i = 0 ; i < planNames.size() ; i++)
			{
				temp += (planValues.get(i) + offset) / sum;
				if(gen <= temp)
					return planNames.get(i);				
			}
			
		}else if (plans.keySet().size()>0)
			return plans.keySet().iterator().next();
		CognitonExtension.myConsole.println("WARNING : the stochastic decision maker was not able to determine a plan to use and the agent was terminated");	
		return "die";
	}
}
	

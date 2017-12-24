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

import org.nlogo.agent.Turtle;
import org.nlogo.api.Argument;
import org.nlogo.api.Command;
import org.nlogo.api.Context;
import org.nlogo.api.ExtensionException;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;

import src.CognitiveScheme;
import src.CognitonExtension;

public class InitCognitons implements Command{

	public Syntax getSyntax(){
		return SyntaxJ.commandSyntax();
	}

	@Override
	public void perform(Argument[] args, Context context) throws ExtensionException {
		//CognitonExtension.loadCognitiveSchemes(context.workspace().getModelPath());
		CognitonExtension.updateContext(context);
		Turtle t = (Turtle) context.getAgent();
		String breedName = t.getBreed().printName();
		CognitiveScheme myCogScheme = selectCognitiveScheme(breedName);		

		
		/*
		 * 
		 * setting cognitons and culturons as follows :
		 * 
		 * after the last agent variable, add the name of the cognitive scheme
		 * then add cognitons in the order of the arraylist.
		 * if the cogniton is active add a Double(0.0). this is the weight of the cogniton.
		 * if the cogniton is not active add a String("NULL")
		 * 
		 * after the last cogniton add groups. groups are added in the order of the arraylist (groupnames). each group is a sequence of 3 variables
		 * 0 : the id of the instance of the group the agent is in (positive int), -1 if he is not in a group of this type.  
		 * 1 : the role the agent has in the group , String.
		 * 2 : the participation factor the agent has in the group , Double
		 * 
		 */
		
		if(myCogScheme != null)
		{
			int VarCount = t.variables().length;
			CognitonExtension.setBreedVariableOffset(VarCount + 1, breedName);
			Object newVar[] = new Object[1+t.variables().length+myCogScheme.cognitons.size()+(myCogScheme.groupNames.size()*3)];
			for(int i = 0 ; i < t.variables().length; i++)
				newVar[i] = t.variables()[i];
			newVar[t.variables().length] = myCogScheme.Name;
			for(int i = 0 ; i < myCogScheme.cognitons.size(); i++)
			{
				if(myCogScheme.cognitons.get(i).starting)
					newVar[CognitonExtension.getBreedVariableOffset(breedName)+i] = new Double(myCogScheme.cognitons.get(i).startingValue);
				else
					newVar[CognitonExtension.getBreedVariableOffset(breedName)+i] = "NULL";
			}
			for(int i = 0 ; i < myCogScheme.groupNames.size(); i++)
			{
				newVar[myCogScheme.getCulturonOffset(breedName)+(i*3)] = new Double(-1);
				newVar[myCogScheme.getCulturonOffset(breedName)+(i*3)+1] = "__NULL__";
				newVar[myCogScheme.getCulturonOffset(breedName)+(i*3)+2] = new Double(0);
			}						
			t.setVariables(newVar);	
		}
	}

	private CognitiveScheme selectCognitiveScheme(String brName) {
		for(CognitiveScheme cs : CognitonExtension.cognitiveSchemes.values())
		{
			for(String n : cs.linkedBreeds)
				if (n.equals(brName.toLowerCase()))
					return cs;
		}
		return CognitonExtension.cognitiveSchemes.get(brName.toLowerCase());
	}
}

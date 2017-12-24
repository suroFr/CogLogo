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
import org.nlogo.api.AgentException;
import org.nlogo.api.Argument;
import org.nlogo.api.Command;
import org.nlogo.api.Context;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.LogoException;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;

import src.CognitiveScheme;
import src.CognitonExtension;

public class FeedBackFromPlan implements Command{

	
	/// name of the plan / value to add
	public Syntax getSyntax(){
		return SyntaxJ.commandSyntax(new int[] {Syntax.StringType(),Syntax.NumberType()});
	}

	@Override
	public void perform(Argument[] args, Context context) throws ExtensionException {
		Turtle t = (Turtle) context.getAgent();
		String breedName = t.getBreed().printName();
		CognitiveScheme myCogScheme = CognitonExtension.cognitiveSchemes.get(t.getVariable(CognitonExtension.getBreedVariableOffset(breedName)-1));		
		if(myCogScheme != null)
		{
			double addValue = args[1].getDoubleValue();
			try {
				for(int i = 0 ; i < myCogScheme.cognitons.size(); i++)
				{
					if(t.getVariable(CognitonExtension.getBreedVariableOffset(breedName)+i).getClass() != String.class)
					{
						double oldVar = (double) t.getVariable(CognitonExtension.getBreedVariableOffset(breedName)+i);
						t.setVariable(CognitonExtension.getBreedVariableOffset(breedName)+i, oldVar 
								+ (addValue * myCogScheme.cognitons.get(i).getFeedBackCoefficientForPlan(args[0].getString())));	
					}
				}
				for(int i = 0; i < myCogScheme.groupNames.size(); i++)
					myCogScheme.feedBackToGroupInstance((Double) t.getVariable(myCogScheme.getCulturonOffset(breedName)+(i*3)), args[0].getString(),addValue);
			} catch (LogoException | AgentException e) {
				e.printStackTrace();
			}	
		}		
	}
}

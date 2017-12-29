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
import src.myutils.GroupInstanceScrubber;

public class AddToParticipation implements Command{

	
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
			GroupInstanceScrubber.checkAndUpdateAgentGroupVariables(((double)t.getVariable(myCogScheme.getGroupVariableOffset(breedName,args[0].getString()))),
					t, myCogScheme, breedName,args[0].getString());;
			if(myCogScheme.checkGroupTypeExists(args[0].getString()) &&((double)t.getVariable(myCogScheme.getGroupVariableOffset(breedName,args[0].getString()))) != -1)
			{
				double oldVar = ((double)t.getVariable(myCogScheme.getGroupVariableOffset(breedName,args[0].getString())+2));
				try {
					t.setVariable(myCogScheme.getGroupVariableOffset(breedName,args[0].getString())+2, oldVar + args[1].getDoubleValue());
				} catch (LogoException | AgentException e) {
					e.printStackTrace();
				}
			}
		}		
	}
}

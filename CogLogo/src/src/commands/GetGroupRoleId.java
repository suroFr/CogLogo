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
import org.nlogo.api.Context;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.Reporter;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;

import src.CognitiveScheme;
import src.CognitonExtension;
import src.myutils.GroupInstanceScrubber;

public class GetGroupRoleId implements Reporter{

	public Syntax getSyntax(){
		return SyntaxJ.reporterSyntax(new int[] {Syntax.StringType(),Syntax.StringType()},Syntax.NumberType());
	}
	
	@Override
	public Object report(Argument[] args, Context context) throws ExtensionException {
		Turtle t = (Turtle) context.getAgent();
		String breedName = t.getBreed().printName();
		CognitiveScheme myCogScheme = CognitonExtension.cognitiveSchemes.get(t.getVariable(CognitonExtension.getBreedVariableOffset(breedName)-1));		
		if(myCogScheme != null)
		{
			if(myCogScheme.checkGroupRoleTypeExists(args[0].getString(), args[1].getString())
					&& ((String)t.getVariable(myCogScheme.getGroupVariableOffset(breedName,args[0].getString())+1)).equals(args[1].getString()))
				return GroupInstanceScrubber.checkAndUpdateAgentGroupVariables(((double)t.getVariable(myCogScheme.getGroupVariableOffset(breedName,args[0].getString()))),
						t, myCogScheme, breedName,args[0].getString());;
		}
		return -1.0;
	}
}
	

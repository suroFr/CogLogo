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
import org.nlogo.api.LogoException;
import org.nlogo.api.Reporter;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;

import src.CognitiveScheme;
import src.CognitonExtension;

public class GetCogniton implements Reporter{

	public Syntax getSyntax(){
		return SyntaxJ.reporterSyntax(new int[] {Syntax.StringType()},Syntax.NumberType());
	}

	public Object report(Argument[] args, Context context) throws ExtensionException {
		Turtle t = (Turtle) context.getAgent();
		String breedName = t.getBreed().printName();
		CognitiveScheme myCogScheme = CognitonExtension.cognitiveSchemes.get(t.getVariable(CognitonExtension.getBreedVariableOffset(breedName)-1));		
		if(myCogScheme != null)
		{
			try {
				if(myCogScheme.cognitonNames.indexOf(args[0].getString()) != -1){
					if(t.getVariable(CognitonExtension.getBreedVariableOffset(breedName)+myCogScheme.cognitonNames.indexOf(args[0].getString())).getClass() != String.class)
						return t.getVariable(CognitonExtension.getBreedVariableOffset(breedName)+myCogScheme.cognitonNames.indexOf(args[0].getString()));
				}else
				{
					CognitonExtension.myConsole.println("WARNING : the agent < "+ ((Turtle) context.getAgent())._id +" > with cognitive scheme < "+myCogScheme.Name+" > does not have cogniton < "+args[0].getString()+" > , nothing was done");
				}
			} catch (LogoException e) {
				e.printStackTrace();
			}	
		}
		return 0.0;		
	}
}

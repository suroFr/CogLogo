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

import org.nlogo.api.Argument;
import org.nlogo.api.Command;
import org.nlogo.api.Context;
import org.nlogo.api.ExtensionException;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;

import src.CognitonExtension;

public class CognitonEditor implements Command{

	public Syntax getSyntax(){
		return SyntaxJ.commandSyntax();
	}	
	
	@Override
	public void perform(Argument[] args, Context context) throws ExtensionException {
		CognitonExtension.updateContext(context);
		//CognitonExtension.loadCognitiveSchemes(context.workspace().getModelPath());
		CognitonExtension.frame.setVisible(!CognitonExtension.frame.isVisible());
	}
}

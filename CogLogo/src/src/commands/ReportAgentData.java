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

import org.nlogo.agent.Turtle;
import org.nlogo.api.Argument;
import org.nlogo.api.Command;
import org.nlogo.api.Context;
import org.nlogo.api.ExtensionException;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;

import src.CognitiveScheme;
import src.CognitonExtension;
import src.editor.AgentWatcher;
import src.myutils.GroupInstanceScrubber;

public class ReportAgentData implements Command{
	
	public Syntax getSyntax(){
		return SyntaxJ.commandSyntax();
	}

	@Override
	public void perform(Argument[] args, Context context) throws ExtensionException {
		if(!CognitonExtension.isLoaded)
			CognitonExtension.loadCognitiveSchemes(context.workspace().getModelPath());
		Turtle t = (Turtle) context.getAgent();
		String breedName = t.getBreed().printName();
		CognitiveScheme myCogScheme = CognitonExtension.cognitiveSchemes.get(t.getVariable(CognitonExtension.getBreedVariableOffset(breedName)-1));		
		if(myCogScheme != null)
		{
			ArrayList<Double> values = new ArrayList<Double>();
			ArrayList<Double> culturonsGRPID = new ArrayList<Double>();
			ArrayList<String> culturonsROLE = new ArrayList<String>();
			ArrayList<Double> culturonsVALUE = new ArrayList<Double>();
			for(int i = 0 ; i < myCogScheme.cognitonNames.size(); i++)
			{
				if(t.getVariable(CognitonExtension.getBreedVariableOffset(breedName)+i).getClass() == String.class)
					values.add(null);
				else
					values.add((Double) t.getVariable(CognitonExtension.getBreedVariableOffset(breedName)+i));
			}
			for(int i = 0; i < myCogScheme.groupNames.size(); i++)
			{
				culturonsGRPID.add(GroupInstanceScrubber.checkAndUpdateAgentGroupVariables(((double)t.getVariable(myCogScheme.getCulturonOffset(breedName)+(i*3))),
						t, myCogScheme, breedName,i));
				culturonsROLE.add((String) t.getVariable(myCogScheme.getCulturonOffset(breedName)+(i*3)+1));
				culturonsVALUE.add((Double) t.getVariable(myCogScheme.getCulturonOffset(breedName)+(i*3)+2));	
			}
			AgentWatcher.reportAgentValues(t._id, breedName.toLowerCase(),myCogScheme.Name , values, culturonsGRPID,culturonsROLE,culturonsVALUE, context.getAgent());
		}
	}
}

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

package src.editor;

import src.CognitiveScheme;
import src.CognitiveScheme.Cogniton;

public class CulturonEditAction extends CognitonEditActions{

	
	
	public CulturonEditAction(CognitiveScheme cs, GCogniton gc, int i, CognitiveStructurePanel pa) {
		super(cs, gc, i, pa);
		editTarget = "Culturon";
	}	
	
	protected void renameOp(Cogniton cog, String name)
	{
		cs.renameCulturon(((CulturonStructureEditor)panelAdress).currentGroup.getText(),((CulturonStructureEditor)panelAdress).currentRole.getText(),cog,name);
	}
	
	protected void removeAllInfluenceOp(String name)
	{
		cs.removeAllCulturonInfluenceLink(((CulturonStructureEditor)panelAdress).currentGroup.getText(),((CulturonStructureEditor)panelAdress).currentRole.getText(),name);
	}
	
	protected void removeAllDependencyOp(String name)
	{
		cs.removeAllCulturonDependencyLink(((CulturonStructureEditor)panelAdress).currentGroup.getText(),((CulturonStructureEditor)panelAdress).currentRole.getText(),name);
	}
	
	protected void addDependencyOp(String name,String plan)
	{
		cs.addCulturonDependencyLink(((CulturonStructureEditor)panelAdress).currentGroup.getText(),((CulturonStructureEditor)panelAdress).currentRole.getText(),name,plan);
	}
	
	protected void addInfluenceOp(String name,Double val,String plan)
	{
		cs.addCulturonInfluenceLink(((CulturonStructureEditor)panelAdress).currentGroup.getText(),((CulturonStructureEditor)panelAdress).currentRole.getText(),name, val, plan);
	}
	
	protected void addReinforcementOp(String name,Double val,String plan)
	{
		cs.addCulturonReinforcementLink(((CulturonStructureEditor)panelAdress).currentGroup.getText(),((CulturonStructureEditor)panelAdress).currentRole.getText(),name, val, plan);
	}
	
	protected void removeAllReinforcementOp(String name) {
		cs.removeAllCulturonReinforcementLink(((CulturonStructureEditor)panelAdress).currentGroup.getText(),((CulturonStructureEditor)panelAdress).currentRole.getText(),name);
	}
}

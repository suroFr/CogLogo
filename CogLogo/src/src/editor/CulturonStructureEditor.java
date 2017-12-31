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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Box;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import src.CognitiveScheme;
import src.myutils.MyButton;

@SuppressWarnings("serial")
public class CulturonStructureEditor extends CognitiveStructurePanel {

	JMenuItem GRP_ADD = new JMenuItem("Add group");
	JMenuItem ROL_ADD = new JMenuItem("Add role");
	JMenuItem GRP_EDT = new JMenuItem("Edit group");
	JMenuItem ROL_EDT = new JMenuItem("Edit role");
	JMenuItem GRP_REM = new JMenuItem("Remove current group");
	JMenuItem ROL_REM = new JMenuItem("Remove current role");
	
	JMenu groupMenu = new JMenu("Groups :");
	JMenu roleMenu = new JMenu("Roles :");
	
	JLabel currentGroup = new JLabel("");
	JLabel currentRole = new JLabel("");
	
	
	public CulturonStructureEditor(MainPanel mainPanel) {
		super(mainPanel);
		MyButton addCulturon = new MyButton("Add culturon");
		addCulturon.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				displayAddCulturonPanel();
			}
		});	
		
		toolBar.addSeparator();
        toolBar.add(currenCog);
		toolBar.addSeparator();
		JMenuBar bar1 = new JMenuBar();
		toolBar.add(bar1);
		groupMenu.setBorder(MyButton.b);
		bar1.add(groupMenu);
		toolBar.add(currentGroup);
		toolBar.addSeparator();
		JMenuBar bar2 = new JMenuBar();
		toolBar.add(bar2);
		roleMenu.setBorder(MyButton.b);
		bar2.add(roleMenu);
		toolBar.add(currentRole);
		toolBar.addSeparator();
        toolBar.add(addCulturon);  
        toolBar.addSeparator();
        toolBar.add(Box.createHorizontalGlue());

        groupMenu.add(GRP_ADD);
        groupMenu.add(GRP_EDT);
        groupMenu.add(GRP_REM);
        groupMenu.addSeparator();

        roleMenu.add(ROL_ADD);
        roleMenu.add(ROL_EDT);
        roleMenu.add(ROL_REM);
        roleMenu.addSeparator();

        MCultListener listener = new MCultListener(this);
        GRP_ADD.addActionListener(listener);
        GRP_EDT.addActionListener(listener);
        GRP_REM.addActionListener(listener);
        ROL_ADD.addActionListener(listener);
        ROL_EDT.addActionListener(listener);
        ROL_REM.addActionListener(listener);
	}

	public void setCurrentCognitiveScheme(CognitiveScheme cs)
	{
		if(cs != null)
		{
			this.setVisible(true);
			currentCognitiveScheme = cs;
			currenCog.setText(cs.Name);
			loadGroupMenuItems();
			displayGroupRole();			
		}
		else
			this.setVisible(false);
	}
	
	public void displayGroupRole() {
		clearDisplayAndData();
		initializeDrawing();
		this.repaint();
		this.revalidate();
	}
	
	public void updateDisplayedElements()
	{
		this.removeAll();
		displayGStructure();
		this.repaint();
		this.revalidate();
	}
	
	protected void initializeDrawing() {	
		int space = 0;
		planCount = 0;
		if(currentCognitiveScheme.getCulturonsForGroupRole(currentGroup.getText(), currentRole.getText())!=null)
		{
			for (CognitiveScheme.Cogniton c: currentCognitiveScheme.getCulturonsForGroupRole(currentGroup.getText(), currentRole.getText())){		
				if(c.posX == null)
				{
					MakeGCognitonAndLinkedElements(c,80,40+espacement*space);
					space+=2;					
				}
				else
					MakeGCognitonAndLinkedElements(c,c.posX,c.posY);
			}			
		}
		displayGStructure();
	}
	
	public void addCulturonToScheme(String text, double d) {
		currentCognitiveScheme.addCulturon(currentGroup.getText(),currentRole.getText(),text,d);
		displayGroupRole();
	}
	
	public GPlan MakeGPlan(String p , double posX , double posY){
		GPlan curr = super.MakeGPlan(p, posX, posY);
		curr.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
			    if(SwingUtilities.isRightMouseButton(e) || (SwingUtilities.isLeftMouseButton(e)&&e.isControlDown())){
					displayPlanPopup(e, (GPlan)e.getSource());
			    }
				if(SwingUtilities.isLeftMouseButton(e)){
					switchSelectedElement( (GPlan)e.getSource());
				}
			}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
		});
		return curr;			
	}
		
	public GCogniton makeGCogniton(CognitiveScheme.Cogniton c , double posX , double posY)
	{
		GCogniton cur = super.makeGCogniton(c, posX, posY);
		cur.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
			    if(SwingUtilities.isRightMouseButton(e) || (SwingUtilities.isLeftMouseButton(e)&&e.isControlDown())){ // rip stev joobs , you will be remembered (unfortunately)
					displayCulturonPopup(e, (GCogniton)e.getSource());
			    }
				if(SwingUtilities.isLeftMouseButton(e)){	
					switchSelectedElement( (GCogniton)e.getSource());
				}
			}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
		});
		return cur;
	}
	
	public void displayCulturonPopup(MouseEvent e , GCogniton c){
		final GCogniton refCogniton =c;

		popupGCognitons = new JPopupMenu(("Culturon"));
		JMenuItem editCogniton = new JMenuItem(("Edit Culturon"));
		editCogniton.addActionListener(new CulturonEditAction(this.currentCognitiveScheme,c,0,this));
		popupGCognitons.add(editCogniton);
		JMenuItem editInfluences = new JMenuItem("Edit influence links");
		editInfluences.addActionListener(new CulturonEditAction(this.currentCognitiveScheme,c,1,this));
		popupGCognitons.add(editInfluences);
		JMenuItem editConditions = new JMenuItem("Edit conditional links");
		editConditions.addActionListener(new CulturonEditAction(this.currentCognitiveScheme,c,2,this));
		popupGCognitons.add(editConditions);
		JMenuItem editReinforcement = new JMenuItem("Edit reinforcement links");
		editReinforcement.addActionListener(new CulturonEditAction(this.currentCognitiveScheme,c,3,this));
		popupGCognitons.add(editReinforcement);
		
		JMenuItem supprimerCogniton = new JMenuItem("Delete");
		supprimerCogniton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentCognitiveScheme.removeCulturon(currentGroup.getText(), currentRole.getText(), refCogniton.cogniton.Name);
				((CulturonStructureEditor) panelAdress).displayGroupRole();
			}
		});
		popupGCognitons.add(supprimerCogniton);
		popupGCognitons.show(this, (int)c.getX() + e.getX(), (int)c.getY() + e.getY());
	}
	
	protected void displayPlanPopup(MouseEvent e, GPlan p) {
		popupGPlans = new JPopupMenu(("Plan"));
		JMenuItem editPlan = new JMenuItem(("Edit Plan"));
		editPlan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JLabel lab = new JLabel("Plan name :");
			    JTextField field = new JTextField(p.plan);
				JLabel labcol = new JLabel("Custom color :");
				JColorChooser chooser;
				if(currentCognitiveScheme.planColors.get(p.plan)!=null)
					chooser = new JColorChooser(currentCognitiveScheme.planColors.get(p.plan));
				else
					chooser = new JColorChooser();	
				Object[] array = {lab,field,labcol,chooser};
				int sel = JOptionPane.showConfirmDialog(panelAdress, array, "Edit plan",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE,null);
				if (sel == JOptionPane.OK_OPTION && !field.getText().equals("")) 
			    {
					if(currentCognitiveScheme.planColors.get(p.plan)!=null)
						currentCognitiveScheme.planColors.remove(p.plan);
					if(currentCognitiveScheme.getCulturonsForGroupRole(currentGroup.getText(), currentRole.getText())!=null)
					{
						for (CognitiveScheme.Cogniton c: currentCognitiveScheme.getCulturonsForGroupRole(currentGroup.getText(), currentRole.getText())){		
							if(c.dependencyLinks.contains(p.plan))
							{
								c.dependencyLinks.remove(p.plan);
								c.dependencyLinks.add(field.getText());
							}
							if(c.influencelinks.get(p.plan)!=null)
							{
								double val = c.influencelinks.get(p.plan);
								c.influencelinks.remove(p.plan);
								c.influencelinks.put(field.getText(),val);
							}
						}			
					}
					if(chooser.getColor()!=null)
						currentCognitiveScheme.planColors.put(p.plan,chooser.getColor());
					displayGroupRole();
			    }
			}
		});
		popupGPlans.add(editPlan);
		

		JMenuItem deletePlan = new JMenuItem("Delete");
		deletePlan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(currentCognitiveScheme.getCulturonsForGroupRole(currentGroup.getText(), currentRole.getText())!=null)
				{
					for (CognitiveScheme.Cogniton c: currentCognitiveScheme.getCulturonsForGroupRole(currentGroup.getText(), currentRole.getText())){		
						if(c.dependencyLinks.contains(p.plan))
						c.dependencyLinks.remove(p.plan);
					
					if(c.influencelinks.get(p.plan)!=null)
						c.influencelinks.remove(p.plan);}
				}
				displayGroupRole();
			}
		});
		popupGPlans.add(deletePlan);
		popupGPlans.show(this, (int)p.getX() + e.getX(), (int)p.getY() + e.getY());
	}

	public void displayGeneralPopup(MouseEvent e){
		JPopupMenu popupGeneral = new JPopupMenu(("Menu"));

		JMenuItem addCogniton = new JMenuItem("Add culturon");
		addCogniton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				displayAddCulturonPanel();
			}
		});
		popupGeneral.add(addCogniton);
		
		JRadioButtonMenuItem customColor = new JRadioButtonMenuItem("Toggle Type colors");
		customColor.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				displayCustomColor = !displayCustomColor;
				panelAdress.repaint();
			}
		});
		popupGeneral.add(customColor);

		JRadioButtonMenuItem influenceLink = new JRadioButtonMenuItem("Toggle Influence Link display");
		influenceLink.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				displayInfluenceLink = !displayInfluenceLink;
				updateDisplayedElements();
			}
		});
		popupGeneral.add(influenceLink);
		
		JRadioButtonMenuItem conditionalLink = new JRadioButtonMenuItem("Toggle Conditional Link display");
		conditionalLink.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				displayConditionalLink = !displayConditionalLink;
				updateDisplayedElements();
			}
		});
		popupGeneral.add(conditionalLink);
		
		JRadioButtonMenuItem reinforcementLink = new JRadioButtonMenuItem("Toggle Reinforcement Link display");
		reinforcementLink.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				displayReinforcementLink = !displayReinforcementLink;
				updateDisplayedElements();
			}
		});
		popupGeneral.add(reinforcementLink);
		
		JRadioButtonMenuItem savePos = new JRadioButtonMenuItem("Save elements position");
		savePos.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				for(GCogniton gc : gCognitons)
				{
					gc.cogniton.posX = gc.X;
					gc.cogniton.posY = gc.Y;
				}
				for(String gp : gPlan.keySet())
				{
					currentCognitiveScheme.planSavedPosition.put(gp, new Point(gPlan.get(gp).X,gPlan.get(gp).Y));
				}
				updateDisplayedElements();
			}
		});
		popupGeneral.add(savePos);
		
		JRadioButtonMenuItem reinitPos = new JRadioButtonMenuItem("Set Elements to default position");
		reinitPos.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				for(GCogniton gc : gCognitons)
				{
					gc.cogniton.posX = null;
					gc.cogniton.posY = null;
				}
				currentCognitiveScheme.planSavedPosition.clear();
				displayGroupRole();
			}
		});
		popupGeneral.add(reinitPos);
		
		popupGeneral.show(this, (int)e.getX(), (int) e.getY());
	}
	
	public void newGroupMenuItem(String groupName) {
		JRadioButtonMenuItem n = new JRadioButtonMenuItem(groupName);
		n.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switchGroup(groupName);
			}
		});
		groupMenu.add(n);
		switchGroup(n.getText());
	}
	
	public void newRoleMenuItem(String roleName) {
		JRadioButtonMenuItem n = new JRadioButtonMenuItem(roleName);
		n.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switchRole(roleName);
			}
		});
		roleMenu.add(n);
		switchRole(n.getText());
	}
	
	public void switchGroup(String groupName)
	{
		if(groupName == null)
		{
			currentGroup.setText("");
			loadRoleMenuItems(null);		
		}
		else
		{
			currentGroup.setText(groupName);
			selectMenuItem(groupMenu, groupName);
			loadRoleMenuItems(groupName);			
		}
	}

	public void switchRole(String roleName)
	{
		if(roleName == null)
		{
			currentRole.setText("");
		}
		else
		{
			currentRole.setText(roleName);
			selectMenuItem(roleMenu, roleName);			
		}
		displayGroupRole();
	}
	
	private void loadGroupMenuItems() {
		groupMenu.removeAll();
		groupMenu.add(GRP_ADD);
		groupMenu.add(GRP_EDT);
		groupMenu.add(GRP_REM);
		groupMenu.addSeparator();
		JRadioButtonMenuItem n = null;
		for(String r : currentCognitiveScheme.groupNames)
		{
			n = new JRadioButtonMenuItem(r);
			n.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					switchGroup(r);
				}
			});
			groupMenu.add(n);
		}
		if(n != null)
			switchGroup(n.getText());
		else
			switchGroup(null);
	}
	
	private void loadRoleMenuItems(String groupName) {
		roleMenu.removeAll();
		roleMenu.add(ROL_ADD);
		roleMenu.add(ROL_EDT);
        roleMenu.add(ROL_REM);
        roleMenu.addSeparator();
		JRadioButtonMenuItem n = null;
		if(groupName != null)
		{
			for(String r : currentCognitiveScheme.getRolesFromGroup(groupName))
			{
				n = new JRadioButtonMenuItem(r);
				n.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						switchRole(r);
					}
				});
				roleMenu.add(n);
			}			
		}
		if(n != null)
			switchRole(n.getText());
		else
			switchRole(null);
	}


	private void selectMenuItem(JMenu Menu, String Name) {
		for(int i = 0 ; i < Menu.getItemCount(); i++)
		{
			if(Menu.getItem(i) != null)
			{
				if (Menu.getItem(i).getText().toLowerCase().equals(Name.toLowerCase()))
					Menu.getItem(i).setSelected(true);
				else
					Menu.getItem(i).setSelected(false);				
			}
		}
	}

	public void displayAddCulturonPanel()
	{
		JLabel lab = new JLabel("Culturon name :");
	    JTextField field = new JTextField();
	    field.requestFocus();
	    JLabel labVal = new JLabel("default value :");
	    SpinnerNumberModel model  = new SpinnerNumberModel(0.0,null,null,0.1);
	    JSpinner jspin  = new JSpinner(model);
		jspin.setMinimumSize(new Dimension(40, 40));
		jspin.setEditor(new JSpinner.NumberEditor(jspin, "0.00"));	
		Object[] array = {lab,field,labVal,jspin};
		int sel = JOptionPane.showConfirmDialog(panelAdress, array, "Create new culturon",JOptionPane.OK_CANCEL_OPTION);
		if (sel == JOptionPane.OK_OPTION && !field.getText().equals("")) 
	    {
			addCulturonToScheme(field.getText(),(double)jspin.getValue()); 		
	    }
	}
		
	private class MCultListener implements ActionListener {

		public MCultListener(CulturonStructureEditor inwin) {}

		@Override
		public void actionPerformed(ActionEvent e) {
			JMenuItem source = (JMenuItem) e.getSource();

			if (source.equals(ROL_ADD)) {
				JLabel lab = new JLabel("Role name :");
			    JTextField field = new JTextField();
				Object[] array = {lab,field};
				int sel = JOptionPane.showConfirmDialog(panelAdress, array, "Create new role",JOptionPane.OK_CANCEL_OPTION);
				if (sel == JOptionPane.OK_OPTION && !field.getText().equals("")) 
				{
					if(currentCognitiveScheme.addRole(currentGroup.getText(), field.getText()))
						newRoleMenuItem(field.getText());
				}
			}else if (source.equals(ROL_EDT)) {
				JLabel lab = new JLabel("Role name :");
			    JTextField field = new JTextField(currentRole.getText());
				Object[] array = {lab,field};
				int sel = JOptionPane.showConfirmDialog(panelAdress, array, "Edit role",JOptionPane.OK_CANCEL_OPTION);
				if (sel == JOptionPane.OK_OPTION && !field.getText().equals("")) 
				{
					if(currentCognitiveScheme.renameRole(currentGroup.getText(),currentRole.getText(),field.getText()))
						renameCurrentRoleMenuItem(field.getText());
				}
			}else if (source.equals(ROL_REM)) {
				JLabel lab = new JLabel("Are you sure you want to delete the current role and all its culturons ? (can't be undone!)");
				Object[] array = {lab};
				int sel = JOptionPane.showConfirmDialog(panelAdress, array, "Delete role",JOptionPane.OK_CANCEL_OPTION);
				if (sel == JOptionPane.OK_OPTION) 
			    {
					currentCognitiveScheme.removeRole(currentGroup.getText(), currentRole.getText());
					int i = 0;
					while(i < roleMenu.getItemCount())
					{
						if(roleMenu.getItem(i) != null)
						{
							if(roleMenu.getItem(i).getText().equals(currentRole.getText()))
								roleMenu.remove(i);
						}
						i++;
					}
					if(currentCognitiveScheme.getRolesFromGroup(currentGroup.getText())!=null
							&& currentCognitiveScheme.getRolesFromGroup(currentGroup.getText()).size() > 0)
						switchRole(currentCognitiveScheme.getRolesFromGroup(currentGroup.getText()).get(0));
					else
						switchRole(null);						
			    }
			}else if (source.equals(GRP_ADD)) {
				JLabel lab = new JLabel("Group name :");
			    JTextField field = new JTextField();
				Object[] array = {lab,field};
				int sel = JOptionPane.showConfirmDialog(panelAdress, array, "Create new group",JOptionPane.OK_CANCEL_OPTION);
				if (sel == JOptionPane.OK_OPTION && !field.getText().equals("")) 
				{
					if(currentCognitiveScheme.addGroup(field.getText()))
						newGroupMenuItem(field.getText());					
				}
			}else if (source.equals(GRP_EDT)) {
				JLabel lab = new JLabel("Group name :");
			    JTextField field = new JTextField(currentGroup.getText());
				Object[] array = {lab,field};
				int sel = JOptionPane.showConfirmDialog(panelAdress, array, "Edit group",JOptionPane.OK_CANCEL_OPTION);
				if (sel == JOptionPane.OK_OPTION && !field.getText().equals("")) 
				{
					if(currentCognitiveScheme.renameGroup(currentGroup.getText(),field.getText()))
						renameCurrentGroupMenuItem(field.getText());
				}
			}else if (source.equals(GRP_REM)) {
				JLabel lab = new JLabel("Are you sure you want to delete the current group and all its roles and culturons ? (can't be undone!)");
				Object[] array = {lab};
				int sel = JOptionPane.showConfirmDialog(panelAdress, array, "Delete group",JOptionPane.OK_CANCEL_OPTION);
				if (sel == JOptionPane.OK_OPTION) 
			    {
					currentCognitiveScheme.removeGroup(currentGroup.getText());
					int i = 0;
					while(i < groupMenu.getItemCount())
					{
						if(groupMenu.getItem(i) != null)
						{
							if(groupMenu.getItem(i).getText().equals(currentGroup.getText()))
								groupMenu.remove(i);
						}
						i++;
					}
					if(currentCognitiveScheme.groupNames.size()>0)
						switchGroup(currentCognitiveScheme.groupNames.get(0));
					else
						switchGroup(null);						
			    }
			}
		}

		private void renameCurrentRoleMenuItem(String text) {
			JRadioButtonMenuItem n = new JRadioButtonMenuItem(text);
			n.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					switchRole(text);
				}
			});
			int rem = -1;
			/// skip menu
			for(int i = 4 ; i < roleMenu.getMenuComponentCount(); i++)
			{
				if(roleMenu.getItem(i).getText().equals(currentRole.getText()))
					rem = i;
			}
			roleMenu.remove(rem);
			roleMenu.add(n);
			switchRole(n.getText());
		
		}

		private void renameCurrentGroupMenuItem(String text) {
			JRadioButtonMenuItem n = new JRadioButtonMenuItem(text);
			n.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					switchGroup(text);
				}
			});
			int rem = -1;
			/// skip menu
			for(int i = 4 ; i < groupMenu.getMenuComponentCount(); i++)
			{
				if(groupMenu.getItem(i).getText().equals(currentGroup.getText()))
					rem = i;
			}
			groupMenu.remove(rem);
			groupMenu.add(n);
			switchGroup(n.getText());
		}
	} 
}

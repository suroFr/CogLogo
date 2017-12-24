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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import src.CognitiveScheme;
import src.CognitiveScheme.Cogniton;
import src.myutils.MyButton;

@SuppressWarnings("serial")
public class CognitiveStructureEditor extends CognitiveStructurePanel {

	public CognitiveScheme currentCognitiveScheme = null;
	
	public CognitiveStructureEditor(MainPanel mainPanel) {
		super(mainPanel);
		MyButton addCogniton = new MyButton("Add cogniton");
		addCogniton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				displayAddCognitonPanel();
			}
		});	
		toolBar.addSeparator();
        toolBar.add(currenCog);
		toolBar.addSeparator();
        toolBar.add(addCogniton); 
        toolBar.addSeparator();
        toolBar.add(Box.createHorizontalGlue());
	}

	public void setCurrentCognitiveScheme(CognitiveScheme cs)
	{
		if(cs != null)
		{
			this.setVisible(true);
			currentCognitiveScheme = cs;
			currenCog.setText(cs.Name);
			displayCognitiveScheme();			
		}
		else
			this.setVisible(false);
	}
	
	public void displayCognitiveScheme() {
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
		for (CognitiveScheme.Cogniton c: currentCognitiveScheme.cognitons){		
			MakeGCognitonAndLinkedElements(c,80,40+espacement*space);
				space++;
		}
		displayGStructure();
	}
	
	public void addCognitonToScheme(String text, boolean b, double val) {
		currentCognitiveScheme.addCogniton(text,b,val);
		displayCognitiveScheme();
	}
	
	public GPlan MakeGPlan(String p , double posX , double posY){
		GPlan curr = super.MakeGPlan(p, posX, posY);
		curr.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
			    if(SwingUtilities.isRightMouseButton(e) || (SwingUtilities.isLeftMouseButton(e)&&e.isControlDown())){ //cancer didn't cure that
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
			    if(SwingUtilities.isRightMouseButton(e) || (SwingUtilities.isLeftMouseButton(e)&&e.isControlDown())){ //cancer didn't cure that
					displayCognitonPopup(e, (GCogniton)e.getSource());
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
	
	public void displayAddCognitonPanel()
	{
		JLabel lab = new JLabel("Cogniton name :");
	    JTextField field = new JTextField();
	    JCheckBox start = new JCheckBox("is added at birth ?");
	    JLabel labVal = new JLabel("default value :");
	    SpinnerNumberModel model  = new SpinnerNumberModel(0.0,null,null,0.1);
	    JSpinner jspin  = new JSpinner(model);
		jspin.setMinimumSize(new Dimension(40, 40));
		jspin.setEditor(new JSpinner.NumberEditor(jspin, "0.00"));	
	    field.requestFocus();
		Object[] array = {lab,field,start,labVal,jspin};
		int sel = JOptionPane.showConfirmDialog(panelAdress, array, "Create new cogniton",JOptionPane.OK_CANCEL_OPTION);
		if (sel == JOptionPane.OK_OPTION && !field.getText().equals("")) 
	    {
			addCognitonToScheme(field.getText(), start.isSelected(),(double)jspin.getValue());
	    }
	}
	
	public void displayGeneralPopup(MouseEvent e){
		JPopupMenu popupGeneral = new JPopupMenu(("Menu"));
		JRadioButtonMenuItem affichageCustom = new JRadioButtonMenuItem("Toggle Type colors");
		affichageCustom.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				for( GCogniton gc : gCognitons)
					gc.switchDisplayColor();
				panelAdress.repaint();
			}
		});
		popupGeneral.add(affichageCustom);
		JMenuItem addCogniton = new JMenuItem("Add cogniton");
		addCogniton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				displayAddCognitonPanel();
			}
		});
		popupGeneral.add(addCogniton);
		popupGeneral.show(this, (int)e.getX(), (int) e.getY());
	}

	public void displayCognitonPopup(MouseEvent e , GCogniton c){
		final GCogniton refCogniton =c;

		popupGCognitons = new JPopupMenu(("Cogniton"));
		JMenuItem editCogniton = new JMenuItem(("Edit Cogniton"));
		editCogniton.addActionListener(new CognitonEditActions(this.currentCognitiveScheme,c,0,this));
		popupGCognitons.add(editCogniton);
		JMenuItem editInfluences = new JMenuItem("Edit influence links");
		editInfluences.addActionListener(new CognitonEditActions(this.currentCognitiveScheme,c,1,this));
		popupGCognitons.add(editInfluences);
		JMenuItem editConditions = new JMenuItem("Edit conditional links");
		editConditions.addActionListener(new CognitonEditActions(this.currentCognitiveScheme,c,2,this));
		popupGCognitons.add(editConditions);
		JMenuItem editReinforcement = new JMenuItem("Edit reinforcement links");
		editReinforcement.addActionListener(new CognitonEditActions(this.currentCognitiveScheme,c,3,this));
		popupGCognitons.add(editReinforcement);

		JMenuItem deleteCogniton = new JMenuItem("Delete");
		deleteCogniton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentCognitiveScheme.removeCogniton(refCogniton.cogniton);
				((CognitiveStructureEditor) panelAdress).displayCognitiveScheme();
			}
		});
		popupGCognitons.add(deleteCogniton);
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
					for(Cogniton c : currentCognitiveScheme.cognitons)
					{
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
					if(chooser.getColor()!=null)
						currentCognitiveScheme.planColors.put(p.plan,chooser.getColor());
					((CognitiveStructureEditor) panelAdress).displayCognitiveScheme();
			    }
			}
		});
		popupGPlans.add(editPlan);
		

		JMenuItem deletePlan = new JMenuItem("Delete");
		deletePlan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for(Cogniton c : currentCognitiveScheme.cognitons)
				{
					if(c.dependencyLinks.contains(p.plan))
						c.dependencyLinks.remove(p.plan);
					
					if(c.influencelinks.get(p.plan)!=null)
						c.influencelinks.remove(p.plan);
				}
				((CognitiveStructureEditor) panelAdress).displayCognitiveScheme();
			}
		});
		popupGPlans.add(deletePlan);
		popupGPlans.show(this, (int)p.getX() + e.getX(), (int)p.getY() + e.getY());
	}
}

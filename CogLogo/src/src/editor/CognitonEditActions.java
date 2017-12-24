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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import src.CognitiveScheme;
import src.CognitiveScheme.Cogniton;


	public class CognitonEditActions implements ActionListener{
		CognitiveScheme cs;
		GCogniton gc;
		int index;
		protected CognitiveStructurePanel panelAdress;
		public String editTarget = "Cogniton";
		
		public CognitonEditActions(CognitiveScheme cs,GCogniton gc, int i, CognitiveStructurePanel pa)
		{
			this.gc = gc;
			this.cs = cs;
			index = i;
			panelAdress = pa;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if (index == 0){//edit cogniton
				JLabel lab = new JLabel(editTarget+" name :");
			    JTextField field = new JTextField(gc.cogniton.Name);
			    JCheckBox start = new JCheckBox("is added at birth ?");
			    start.setSelected(gc.cogniton.starting);
				JLabel labVal = new JLabel("default value :");
			    SpinnerNumberModel model  = new SpinnerNumberModel(gc.cogniton.startingValue,null,null,0.1);
			    JSpinner jspin  = new JSpinner(model);
				jspin.setMinimumSize(new Dimension(40, 40));
				jspin.setEditor(new JSpinner.NumberEditor(jspin, "0.00"));			    
				JLabel labcol = new JLabel("Custom color :");
				JColorChooser chooser = new JColorChooser(gc.cogniton.getDisplayColor());
				Object[] array = {lab,field,start,labVal,jspin, labcol, chooser};
				if(!editTarget.equals("Cogniton"))
					start.setVisible(false);
				int sel = JOptionPane.showConfirmDialog(panelAdress, array, "Edit "+editTarget.toLowerCase(),JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE,null);
				if (sel == JOptionPane.OK_OPTION && !field.getText().equals("")) 
			    {
					renameOp(gc.cogniton, field.getText());
					gc.cogniton.setDisplayColor(chooser.getColor());
					gc.cogniton.starting = start.isSelected();
					if(gc.cogniton.starting || !editTarget.equals("Cogniton"))
						gc.cogniton.startingValue = (double)jspin.getValue();
					else
						gc.cogniton.startingValue = 0.0;
					panelAdress.repaint();
					panelAdress.revalidate();
			    }
			}
			else if (index == 1){//edit influence
				JLabel lab = new JLabel("Edit influence links :");
				InfluenceLinksPanel infPan = new InfluenceLinksPanel(gc.cogniton.influencelinks);
				JScrollPane scrollpane = new JScrollPane(infPan);
				scrollpane.setPreferredSize(new Dimension(300,300));
				Object[] array = {lab, scrollpane};
				int sel = JOptionPane.showConfirmDialog(panelAdress, array, "Edit influence links",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE,null);
				if (sel == JOptionPane.OK_OPTION) 
			    {
					removeAllInfluenceOp(gc.cogniton.Name);
					for(InfluenceLinksPanel.InfluenceBox b : infPan.fields)
					{
						
						if(!b.field.getText().equals("") && Double.parseDouble(b.jspin.getValue().toString()) != 0.0)
						{
							addInfluenceOp(gc.cogniton.Name, Double.parseDouble(b.jspin.getValue().toString()), b.field.getText());
							panelAdress.makeGLinkInfluence(gc,b.field.getText(),Double.parseDouble(b.jspin.getValue().toString()));
						}
					}
					panelAdress.updateDisplayedElements();
			    }
			}
			else if (index == 2){//edit conditional
				JLabel lab = new JLabel("Edit conditional links :");
				DependencyLinksPanel depPan = new DependencyLinksPanel(gc.cogniton.dependencyLinks);
				JScrollPane scrollpane = new JScrollPane(depPan);
				scrollpane.setPreferredSize(new Dimension(300,300));
				Object[] array = {lab, scrollpane};
				int sel = JOptionPane.showConfirmDialog(panelAdress, array, "Edit conditional links",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE,null);
				if (sel == JOptionPane.OK_OPTION) 
			    {
					removeAllDependencyOp(gc.cogniton.Name);
					for(DependencyLinksPanel.DependencyBox b : depPan.fields)
					{
						if(!b.field.getText().equals(""))
						{
							addDependencyOp(gc.cogniton.Name,b.field.getText());
							panelAdress.makeGLinkConditional(gc,b.field.getText());
						}
					}
					panelAdress.updateDisplayedElements();
			    }
			}
			else if (index == 3){//edit reinforcement links
				JLabel lab = new JLabel("Edit reinforcement links :");
				InfluenceLinksPanel infPan = new InfluenceLinksPanel(gc.cogniton.feedBackLinks);
				JScrollPane scrollpane = new JScrollPane(infPan);
				scrollpane.setPreferredSize(new Dimension(300,300));
				Object[] array = {lab, scrollpane};
				int sel = JOptionPane.showConfirmDialog(panelAdress, array, "Edit reinforcement links",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE,null);
				if (sel == JOptionPane.OK_OPTION) 
			    {
					removeAllReinforcementOp(gc.cogniton.Name);
					for(InfluenceLinksPanel.InfluenceBox b : infPan.fields)
					{
						
						if(!b.field.getText().equals("") && Double.parseDouble(b.jspin.getValue().toString()) != 0.0)
						{
							addReinforcementOp(gc.cogniton.Name, Double.parseDouble(b.jspin.getValue().toString()), b.field.getText());
							panelAdress.makeGLinkReinforcement(gc,b.field.getText(),Double.parseDouble(b.jspin.getValue().toString()));
						}
					}
					panelAdress.updateDisplayedElements();
			    }
			}
		}
		
		protected void addReinforcementOp(String name,Double val,String plan){
			cs.addReinforcementLink(name, val, plan);
		}

		protected void removeAllReinforcementOp(String name) {
			cs.removeAllReinforcementLink(name);
			int i = 0;
			while(i < panelAdress.gLiensReinf.size())
			{
				if(panelAdress.gLiensReinf.get(i).cogniton.cogniton.Name.equals(name))
					panelAdress.gLiensReinf.remove(i);
				else
					i++;				
			}					
		}

		protected void renameOp(Cogniton cog, String name)
		{
			cs.renameCogniton(cog,name);
		}
		
		protected void removeAllInfluenceOp(String name)
		{
			cs.removeAllInfluenceLink(name);
			int i = 0;
			while(i < panelAdress.gLiens.size())
			{
				if(panelAdress.gLiens.get(i).cogniton.cogniton.Name.equals(name))
					panelAdress.gLiens.remove(i);
				else
					i++;				
			}					
		}
		
		protected void removeAllDependencyOp(String name)
		{
			cs.removeAllDependencyLink(name);
			int i = 0;
			while(i < panelAdress.gLiensConditionnels.size())
			{
				if(panelAdress.gLiensConditionnels.get(i).cogniton.cogniton.Name.equals(name))
					panelAdress.gLiensConditionnels.remove(i);
				else
					i++;				
			}					
		}
		
		protected void addDependencyOp(String name,String plan)
		{
			cs.addDependencyLink(name,plan);
		}
		
		protected void addInfluenceOp(String name,Double val,String plan)
		{
			cs.addInfluenceLink(name, val, plan);
		}
		
		@SuppressWarnings("serial")
		class InfluenceLinksPanel extends JPanel
		{
			class InfluenceBox extends Box
			{
				SpinnerNumberModel model;
				JSpinner jspin;
				JTextField field;
				public InfluenceBox(int axis) {
					super(axis);
				}
				public InfluenceBox()
				{
					super(BoxLayout.LINE_AXIS);
				}
				public InfluenceBox(String s, Double d) {
					super(BoxLayout.LINE_AXIS);
					field = new JTextField(s);
					field.setMinimumSize(new Dimension(20, 40));
					model  = new SpinnerNumberModel(d,null,null,0.1);
					jspin  = new JSpinner(model);
					jspin.setMinimumSize(new Dimension(40, 40));
					jspin.setEditor(new JSpinner.NumberEditor(jspin, "0.00000"));
					this.add(field);
					this.add(jspin);
				}
			}
			JPanel inner = new JPanel();
			ArrayList<InfluenceBox> fields = new ArrayList<>();
			
			public InfluenceLinksPanel(HashMap<String, Double> in) {
				super();
				this.setLayout(new BorderLayout());
				inner.setLayout(new BoxLayout(inner, BoxLayout.PAGE_AXIS));
				for(String s : in.keySet())
				{
					InfluenceBox b = new InfluenceBox(s,in.get(s));
					fields.add(b);
					inner.add(b);
				}
				this.add(inner,BorderLayout.CENTER);
				JButton add = new JButton("add");
				add.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						InfluenceBox b = new InfluenceBox("",0.0);
						fields.add(b);
						inner.add(b);
						inner.repaint();
						inner.revalidate();
					}
				});
				this.add(add,BorderLayout.SOUTH);	
				inner.repaint();
				inner.revalidate();
			}
		}
		
		@SuppressWarnings("serial")
		class DependencyLinksPanel extends JPanel
		{
			class DependencyBox extends Box
			{
				JTextField field;
				public DependencyBox(int axis) {
					super(axis);
				}
				public DependencyBox()
				{
					super(BoxLayout.LINE_AXIS);
				}
				public DependencyBox(String s) {
					super(BoxLayout.LINE_AXIS);
					field = new JTextField(s);
					field.setMinimumSize(new Dimension(20, 40));
					this.add(field);
				}
			}
			JPanel inner = new JPanel();
			ArrayList<DependencyBox> fields = new ArrayList<>();
			
			public DependencyLinksPanel(ArrayList<String> in) {
				super();
				this.setLayout(new BorderLayout());
				inner.setLayout(new BoxLayout(inner, BoxLayout.PAGE_AXIS));
				for(String s : in)
				{
					makeBox(s);
				}
				this.add(inner,BorderLayout.CENTER);
				JButton add = new JButton("add");
				add.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						makeBox("");
					}
				});
				this.add(add, BorderLayout.SOUTH);				
			}
			
			public void makeBox(String s)
			{
				DependencyBox b = new DependencyBox(s);
				JButton del = new JButton("Delete");
				del.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						inner.remove(b);
						fields.remove(b);
						inner.repaint();
						inner.revalidate();
					}
				});
				b.add(del);
				fields.add(b);
				inner.add(b);
				inner.repaint();
				inner.revalidate();
			}
		}
	}
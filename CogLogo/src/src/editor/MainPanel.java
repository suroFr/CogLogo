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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import src.CognitiveScheme;
import src.CognitonExtension;
import src.myutils.StreamToText;

@SuppressWarnings("serial")
public class MainPanel extends JPanel{
	
	CognitiveStructureEditor cogPanel ;	
	CulturonStructureEditor cultPanel ;
	JScrollPane consoleScrollPane;
	AgentWatcher agPanel ;
	MainPanel panelAdress = this;
	JFrame mainFrame;

	JMenuItem MI_GEN_SAVEALL = new JMenuItem("Save Model");
	
	JRadioButtonMenuItem MI_VIE_COGSCH = new JRadioButtonMenuItem("Cognitive Scheme editor");
	JRadioButtonMenuItem MI_VIE_CULTSCH = new JRadioButtonMenuItem("Groups and roles editor (Culturons)");
	
	JMenuItem MI_SEL_NEW = new JMenuItem("Create new cognitive scheme");

	JMenuItem MI_COG_SETT = new JMenuItem("Settings of current cognitive scheme");
	JMenuItem MI_COG_SAV = new JMenuItem("Save current cognitive scheme");
	JMenuItem MI_COG_SAVAS = new JMenuItem("Save as current cognitive scheme");
	JMenuItem MI_COG_DEL = new JMenuItem("Delete current cognitive scheme");

	JMenuItem MI_OBS_SING = new JMenuItem("Observe a single agent");
	
	JMenuItem MI_HLP_CONS = new JMenuItem("Console");
	JMenuItem MI_HLP_HELP = new JMenuItem("Help");
	JMenuItem MI_HLP_ABOUT = new JMenuItem("About");
	
	JMenu selMenu;
	JMenu viewMenu;
	
	JFrame consoleFrame = null;
	
	public MainPanel(JFrame fr)
	{
		mainFrame = fr;
		this.setDoubleBuffered(true);
		JMenuBar mb = new JMenuBar();
		mainFrame.setJMenuBar(mb);
		mainFrame.setLayout(new BorderLayout());
		this.setFocusable(false);
		this.setLayout(new BorderLayout());

		MFListener listener = new MFListener(mainFrame);
				
		JMenu gen = new JMenu("General");
		mb.add(gen);

		MI_GEN_SAVEALL.addActionListener(listener);
		gen.add(MI_GEN_SAVEALL);
		
		
		viewMenu = new JMenu("View");
		mb.add(viewMenu);
		
		MI_VIE_COGSCH.addActionListener(listener);
		viewMenu.add(MI_VIE_COGSCH);
		MI_VIE_CULTSCH.addActionListener(listener);
		viewMenu.add(MI_VIE_CULTSCH);

		selMenu = new JMenu("Select");
		mb.add(selMenu);
		
		MI_SEL_NEW.addActionListener(listener);
		selMenu.add(MI_SEL_NEW);
		selMenu.addSeparator();

		JMenu cog = new JMenu("Options");
		mb.add(cog);

		MI_COG_SETT.addActionListener(listener);
		cog.add(MI_COG_SETT);
		MI_COG_SAV.addActionListener(listener);
		cog.add(MI_COG_SAV);
		MI_COG_DEL.addActionListener(listener);
		cog.add(MI_COG_DEL);

		JMenu obs = new JMenu("Observe");
		mb.add(obs);

		MI_OBS_SING.addActionListener(listener);
		obs.add(MI_OBS_SING);
		
		cogPanel = new CognitiveStructureEditor(this);
		cogPanel.setVisible(false);
		cultPanel = new CulturonStructureEditor(this);
		cultPanel.setVisible(false);		
		displayPanel(cogPanel);
		selectMenuItem(viewMenu, MI_VIE_COGSCH.getText());
		
		JMenu hlp = new JMenu("Help");
		mb.add(hlp);

		MI_HLP_HELP.addActionListener(listener);
		hlp.add(MI_HLP_HELP);
		MI_HLP_CONS.addActionListener(listener);
		hlp.add(MI_HLP_CONS);
		MI_HLP_ABOUT.addActionListener(listener);
		hlp.add(MI_HLP_ABOUT);
		
		JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        consoleScrollPane = new JScrollPane(textArea);

        CognitonExtension.myConsole = new PrintStream(new StreamToText(textArea));
        CognitonExtension.myConsole.println("INIT : console started");
	}
	
	public void displayPanel(CognitiveStructurePanel p)
	{
		this.removeAll();
		this.add(p,BorderLayout.CENTER);
		this.add(p.toolBar,BorderLayout.NORTH);
		this.repaint();this.revalidate();
	}

	public CognitiveScheme newCogScheme(String schemeName, String decisionMaker) {
		CognitiveScheme cs = new CognitiveScheme(schemeName);
		cs.myDecisionMaker = decisionMaker;
		CognitonExtension.cognitiveSchemes.put(schemeName.toLowerCase(), cs);
		newCogSchemeMenuItem(cs);
		return cs;
	}
	
	public void newCogScheme(String schemeName, String decisionMaker, ArrayList<String> breeds) {
		CognitiveScheme cs = newCogScheme(schemeName, decisionMaker);
		cs.linkedBreeds = breeds;
	}
	
	public void newCogScheme(String schemeName, String decisionMaker, double d, ArrayList<String> breeds) {
		CognitiveScheme cs = newCogScheme(schemeName, decisionMaker);
		cs.linkedBreeds = breeds;
		cs.DecisionMakerBias = d;
	}
	
	public void newCogSchemeMenuItem(CognitiveScheme schemeName) {
		JRadioButtonMenuItem n = new JRadioButtonMenuItem(schemeName.Name);
		n.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switchCogScheme(((JRadioButtonMenuItem)e.getSource()).getText());
			}
		});
		selMenu.add(n);
		switchCogScheme(schemeName.Name);
	}

	public void switchCogScheme(String schemeName) {
		cogPanel.offsetX = 0;
		cogPanel.offsetY = 0;
		cultPanel.offsetX = 0;
		cultPanel.offsetY = 0;
		cogPanel.setCurrentCognitiveScheme(CognitonExtension.cognitiveSchemes.get(schemeName.toLowerCase()));
		cultPanel.setCurrentCognitiveScheme(CognitonExtension.cognitiveSchemes.get(schemeName.toLowerCase()));
		selectMenuItem(selMenu,schemeName);
	}
		
	private void selectMenuItem(JMenu Menu, String schemeName) {
		for(int i = 0 ; i < Menu.getItemCount(); i++)
		{
			if(Menu.getItem(i) != null)
			{
				if (Menu.getItem(i).getText().toLowerCase().equals(schemeName.toLowerCase()))
					Menu.getItem(i).setSelected(true);
				else
					Menu.getItem(i).setSelected(false);				
			}
		}
	}

	// classes
	private class MFListener implements ActionListener {

		public MFListener(JFrame inwin) {}

		@Override
		public void actionPerformed(ActionEvent e) {
			JMenuItem source = (JMenuItem) e.getSource();

			if (source.equals(MI_COG_DEL)) {
				JLabel lab = new JLabel("Are you sure you want to delete the current cognitive scheme? (can't be undone!)");
				Object[] array = {lab};
				int sel = JOptionPane.showConfirmDialog(panelAdress, array, "Delete cognitive scheme",JOptionPane.OK_CANCEL_OPTION);
				if (sel == JOptionPane.OK_OPTION) 
			    {
					int i = 0;
					while(i < selMenu.getItemCount())
					{
						if(selMenu.getItem(i) != null)
						{
							if(selMenu.getItem(i).getText().equals(cogPanel.currentCognitiveScheme.Name))
								selMenu.remove(i);
						}
						i++;
					}
					CognitonExtension.deleteCognitiveScheme(cogPanel.currentCognitiveScheme);
					if(CognitonExtension.cognitiveSchemes.keySet().size() > 0)
						switchCogScheme( (String) CognitonExtension.cognitiveSchemes.keySet().toArray()[0]);
					else
						cogPanel.setCurrentCognitiveScheme(null);
			    }
			}else if (source.equals(MI_SEL_NEW)) {
				LinkedBreedsPanel breeds = new LinkedBreedsPanel(new ArrayList<String>());
				JLabel lab = new JLabel("Cognitive scheme name :");
			    JTextField field = new JTextField();
			    JLabel labDM = new JLabel("Decision maker :");
			    JComboBox<String> dec = new JComboBox<String>(CognitiveScheme.DecisionMakers);
			    dec.setSelectedItem(CognitiveScheme.DecisionMakers[0]);
			    JLabel labBias = new JLabel("Decision maker bias (1 = none):");
			    SpinnerNumberModel model  = new SpinnerNumberModel(1,1,1000,0.1);
			    JSpinner jspin  = new JSpinner(model);
			    JScrollPane scr = new JScrollPane(breeds);
				Object[] array = {lab,field,labDM,dec,labBias,jspin,scr};
				int sel = JOptionPane.showConfirmDialog(panelAdress, array, "Create new cognitive scheme",JOptionPane.OK_CANCEL_OPTION);
				if (sel == JOptionPane.OK_OPTION) 
			    {
					if(CognitonExtension.cognitiveSchemes.get(field.getText().toLowerCase())==null)
	                	panelAdress.newCogScheme(field.getText().toLowerCase(),dec.getSelectedItem().toString(),(double)jspin.getValue(),breeds.getList());	 
	            	else
	            		panelAdress.switchCogScheme(field.getText().toLowerCase());
			    }
			}else if(source.equals(MI_COG_SAV)){
				CognitonExtension.saveCognitiveScheme(cogPanel.currentCognitiveScheme);
			}else if(source.equals(MI_COG_SETT)){
				LinkedBreedsPanel breeds = new LinkedBreedsPanel(cogPanel.currentCognitiveScheme.linkedBreeds);
				JLabel lab = new JLabel("Cognitive scheme name :");
			    JTextField field = new JTextField(cogPanel.currentCognitiveScheme.Name);
			    JLabel labDM = new JLabel("Decision maker :");
			    JComboBox<String> dec = new JComboBox<String>(CognitiveScheme.DecisionMakers);
			    dec.setSelectedItem(cogPanel.currentCognitiveScheme.myDecisionMaker);
			    JLabel labBias = new JLabel("Decision maker bias (1 = none):");
			    SpinnerNumberModel model  = new SpinnerNumberModel(cogPanel.currentCognitiveScheme.DecisionMakerBias,1,1000,0.1);
			    JSpinner jspin  = new JSpinner(model);
			    JScrollPane scr = new JScrollPane(breeds);
				Object[] array = {lab,field,labDM,dec,labBias,jspin,scr};
				int sel = JOptionPane.showConfirmDialog(panelAdress, array, "Cognitive scheme settings",JOptionPane.OK_CANCEL_OPTION);
				if (sel == JOptionPane.OK_OPTION) 
			    {
					if(cogPanel.currentCognitiveScheme.Name.equals(field.getText().toLowerCase()) || 
							CognitonExtension.cognitiveSchemes.get(field.getText().toLowerCase())==null)
	            	{	            		
	                	cogPanel.currentCognitiveScheme.Name = field.getText().toLowerCase();	            		
	                	cogPanel.currentCognitiveScheme.myDecisionMaker =  dec.getSelectedItem().toString();
	                	cogPanel.currentCognitiveScheme.linkedBreeds = breeds.getList();
	                	cogPanel.currentCognitiveScheme.DecisionMakerBias = (double) jspin.getValue();
	            		panelAdress.switchCogScheme(field.getText().toLowerCase());
	            	}
	            	else
	            	{
	            		panelAdress.switchCogScheme(field.getText().toLowerCase());	            		
	            	}
			    }
			}else if(source.equals(MI_OBS_SING)){
				JAnimFrame obs = new JAnimFrame(new AgentWatcher(panelAdress));
				obs.setResizable(true);
				obs.setSize(new Dimension(800, 600));
				obs.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				obs.setVisible(true);
			}else if(source.equals(MI_VIE_COGSCH)){
				displayPanel(cogPanel);
				selectMenuItem(viewMenu, MI_VIE_COGSCH.getText());
			}else if(source.equals(MI_VIE_CULTSCH)){
				displayPanel(cultPanel);
				selectMenuItem(viewMenu, MI_VIE_CULTSCH.getText());
			}else if(source.equals(MI_HLP_CONS)){
				if(consoleFrame == null)
				{
					consoleFrame = new JFrame("Console");
					consoleFrame.add(consoleScrollPane);
					consoleFrame.setResizable(true);
					consoleFrame.setSize(new Dimension(800, 600));
					consoleFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
					consoleFrame.setVisible(true);
				}
				else
					consoleFrame.setVisible(true);
					
				
			}else if(source.equals(MI_HLP_HELP)){
				JFrame fr = new JFrame("Help");
				InputStream in = getClass().getResourceAsStream("/readMe.txt"); 
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				JTextArea textArea = new JTextArea();
				JScrollPane ScrollPane = new JScrollPane(textArea);
				String line;
		        try {
					while ((line = reader.readLine()) != null) {
						textArea.append(line+"\n");
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
		        textArea.setEditable(false);
		        textArea.setCaretPosition(0);
		        fr.add(ScrollPane);
		        fr.setResizable(true);
		        fr.setSize(new Dimension(800, 600));
		        fr.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		        fr.setVisible(true);
			}else if(source.equals(MI_HLP_ABOUT)){
				JLabel lab1 = new JLabel("CogLogo : an implementation of the Cogniton architecture");
				JLabel lab2 = new JLabel("Copyright (C) 2017  SURO François");
				JLabel lab3 = new JLabel("GNU GENERAL PUBLIC LICENSE");
				JLabel lab4 = new JLabel("Version 3, 29 June 2007");
				JLabel lab5 = new JLabel("");
				JLabel lab6 = new JLabel("This program comes with ABSOLUTELY NO WARRANTY");
				JLabel lab7 = new JLabel("This is free software, and you are welcome to redistribute it");
				JLabel lab8 = new JLabel("under certain conditions");
		
				Object[] array = {lab1,lab2,lab3,lab4,lab5,lab5,lab6,lab7,lab8};
				JOptionPane.showMessageDialog(panelAdress, array,"About",JOptionPane.INFORMATION_MESSAGE);
			}else if(source.equals(MI_GEN_SAVEALL)){
				CognitonExtension.saveAllCognitiveScheme();
			}
		}
		
		public class JAnimFrame extends JFrame
		{
			AgentWatcher agentWatcher;
			
			public JAnimFrame(AgentWatcher aw)
			{
				super("Agent Watcher");
				agentWatcher = aw;
				this.setLayout(new BorderLayout());
				this.add(agentWatcher,BorderLayout.CENTER);
				this.add(agentWatcher.toolBar,BorderLayout.NORTH);
				setDoubleBuffered(true);
			}
		}
		
		class LinkedBreedsPanel extends JPanel
		{
			class LinkBox extends Box
			{
				JTextField field;
				public LinkBox(int axis) {
					super(axis);
				}
				public LinkBox()
				{
					super(BoxLayout.LINE_AXIS);
				}
				public LinkBox(String s) {
					super(BoxLayout.LINE_AXIS);
					field = new JTextField(s);
					field.setMinimumSize(new Dimension(20, 40));
					this.add(field);
				}
			}
			JPanel inner = new JPanel();
			ArrayList<LinkBox> fields = new ArrayList<>();
			
			public LinkedBreedsPanel(ArrayList<String> in) {
				super();
				this.setLayout(new BorderLayout());
				this.add(new JLabel("Linked breeds :"),BorderLayout.NORTH);
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
				LinkBox b = new LinkBox(s);
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
			
			public ArrayList<String> getList()
			{
				ArrayList<String> ret = new ArrayList<>();
				for(LinkBox l : fields)
				{
					if(!l.field.getText().equals(""))
						ret.add(l.field.getText().toLowerCase());
				}
				return ret;
			}
		}
	}
}

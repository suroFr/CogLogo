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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.nlogo.api.Agent;
import org.nlogo.api.PerspectiveJ;

import src.CognitiveScheme;
import src.CognitonExtension;
import src.CognitiveScheme.Cogniton;
import src.myutils.MyButton;
import src.myutils.myHashset;

@SuppressWarnings("serial")
public class AgentWatcher extends CognitiveStructurePanel{

	/// very agile development ....
	
	DecimalFormat decFormat = new DecimalFormat("#.####");
	HashMap<String, Double> plans;
	HashMap<String, Double> cognitons;
	HashMap<String, Double> groupInvolvment;
	HashMap<String, Double> groupCogVal;
	private static myHashset agentValues = new myHashset();
	JComboBox<AgentInfo> currentAgent = new JComboBox<AgentInfo>();
	long prevUpdateTime = 0;
	long prevListUpdateTime = 0;
	public final static int INTERVAL = 200;
	Timer timer;
	public static boolean clearDispPlan = false;
	public static boolean dispPlan = false;
	
	public AgentWatcher(MainPanel mainPanel) {
		super(mainPanel);
		decFormat.setMinimumFractionDigits(4);
		DefaultComboBoxModel<AgentInfo> model = new DefaultComboBoxModel<AgentInfo>(agentValues.toAgentInfoArray());
		currentAgent.setModel( model );
		MyButton watchAgent = new MyButton("Watch");
		watchAgent.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(currentAgent.getSelectedItem() != null)
					CognitonExtension.modelContext.world().observer().setPerspective(PerspectiveJ.create(PerspectiveJ.WATCH, 
						((AgentInfo) currentAgent.getSelectedItem()).agent));
			}
		});
		
		MyButton followAgent = new MyButton("Follow");
		followAgent.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(currentAgent.getSelectedItem() != null)
					CognitonExtension.modelContext.world().observer().setPerspective(PerspectiveJ.create(PerspectiveJ.FOLLOW, 
						((AgentInfo) currentAgent.getSelectedItem()).agent));
			}
		});
		
		MyButton resetAgent = new MyButton("Reset");
		resetAgent.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CognitonExtension.modelContext.world().observer().setPerspective(PerspectiveJ.create(0));
			}
		});
		MyButton planNames = new MyButton("Disp.Plan");
		planNames.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				clearDispPlan = false;
				dispPlan = !dispPlan;
			}
			@Override
			public void mousePressed(MouseEvent e) {
				clearDispPlan = true;
			}
			@Override
			public void mouseExited(MouseEvent e) {}			
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseClicked(MouseEvent e) {}
		});
		
		toolBar.addSeparator();
		toolBar.add(currentAgent);
        toolBar.addSeparator();
        toolBar.add(watchAgent);
        toolBar.add(followAgent);
        toolBar.add(resetAgent);
        toolBar.add(planNames);
        toolBar.addSeparator();
        toolBar.add(Box.createHorizontalGlue());
        if(agentValues.size()>0)
        	currentAgent.setSelectedItem(agentValues.toArray()[0]);
        currentAgent.addPopupMenuListener(new PopupMenuListener() {
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				   Long curr = new Long(0);
	            	if(currentAgent.getSelectedItem() != null)
	            	   curr = ((AgentInfo) currentAgent.getSelectedItem()).id;
	            	DefaultComboBoxModel<AgentInfo> model = new DefaultComboBoxModel<AgentInfo>(agentValues.toAgentInfoArray());
	       			currentAgent.setModel( model );
	       			if(curr != null && agentValues.get(curr) != null)
	       				currentAgent.setSelectedItem(agentValues.get(curr));	
			}
			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				if(currentAgent.getSelectedItem() != null && agentValues.get(((AgentInfo) currentAgent.getSelectedItem()).id) != null)
					displayAgent(agentValues.get(((AgentInfo) currentAgent.getSelectedItem()).id));
			}
			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
			}
		});
        currentAgent.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if(currentAgent.getItemCount() > 0)
				{
					if(e.getWheelRotation()<0)
					{
						currentAgent.setSelectedIndex(Math.max(currentAgent.getSelectedIndex() - 1,0)); 
						displayAgent(agentValues.get(((AgentInfo) currentAgent.getSelectedItem()).id));
					}
					else if (e.getWheelRotation()>0)
					{
						currentAgent.setSelectedIndex(Math.min(currentAgent.getSelectedIndex() + 1,currentAgent.getItemCount()-1)); 					
						displayAgent(agentValues.get(((AgentInfo) currentAgent.getSelectedItem()).id));
					}					
				}
			}
		});
        timer = new Timer(INTERVAL, new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
        	
        	if(currentAgent.getSelectedItem() != null)
			{
        		AgentInfo cur = (AgentInfo) currentAgent.getSelectedItem();
				if(CognitonExtension.cognitiveSchemes.get(cur.breed).modified>=prevUpdateTime)
				{
            	   displayAgent(agentValues.get(cur.id));
               }else{
            	   
            	   updateAgent(agentValues.get(cur.id));            	   
               }
				prevUpdateTime = System.currentTimeMillis();
            }  
        }
        });
        timer.start();
	}
	
	public GPlan MakeGPlan(String p , double posX , double posY){
		String msg;
		if(plans.get(p) != null)
			msg = decFormat.format(plans.get(p));					
		else
			msg = "No Active Link";					
		GPlan curr = super.MakeGPlan(p , posX, posY,msg);
		curr.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
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
		GCogniton cur;
		if(c.isCulturon)
		{
			cur = new GCogniton(this,posX,posY,60,25, c, c.customTag + " -- V:" + groupCogVal.get(c.customTag+c.Name)+" P:" +groupInvolvment.get(c.customTag));
		}
		else
		{
			if(cognitons.get(c.Name) == null)
				cur = new GCogniton(this,posX,posY,60,25, c, "Inactive");
			else			
				cur = new GCogniton(this,posX,posY,60,25, c, "" + decFormat.format(cognitons.get(c.Name)));
		}
		cur.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
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
	
	public void displayAgent(AgentInfo ag)
	{
		if(ag != null)
		{
			CognitiveScheme cogScheme = CognitonExtension.cognitiveSchemes.get(ag.cogScheme);
			currentCognitiveScheme = cogScheme;
			cognitons = new HashMap<>();
			plans = new HashMap<>();
			groupInvolvment = new HashMap<String, Double>();
			groupCogVal = new HashMap<String, Double>();
			for(int i = 0 ; i < cogScheme.cognitonNames.size(); i++)
			{
				cognitons.put(cogScheme.cognitonNames.get(i), ag.cognitonValues.get(i));
				if(ag.cognitonValues.get(i)!=null)
					for(String p : cogScheme.cognitons.get(i).dependencyLinks)
						plans.put(p, 0.0);					
			}
			for(int i = 0; i < cogScheme.groupNames.size(); i++)
			{
				if((Double)ag.culturonsGRPID.get(i) != -1)
				{
					for(Cogniton c : cogScheme.getCulturonsForGroupRole(cogScheme.groupNames.get(i), (String)ag.culturonsROLE.get((i))))
						for(String p : c.dependencyLinks)
							plans.put(p, 0.0);	
				}
			}
			for(int i = 0 ; i < cogScheme.cognitonNames.size(); i++)
			{
				for(String p : cogScheme.cognitons.get(i).influencelinks.keySet())
				{
					if(plans.get(p)!=null && ag.cognitonValues.get(i)!= null)
						plans.put(p, plans.get(p)+(cogScheme.cognitons.get(i).influencelinks.get(p)*ag.cognitonValues.get(i)));											
				}
			}
			for(int i = 0; i < cogScheme.groupNames.size(); i++)
			{
				if((Double)ag.culturonsGRPID.get(i) != -1)
				{
					for(Cogniton c : cogScheme.getCulturonsForGroupRole(cogScheme.groupNames.get(i), (String)ag.culturonsROLE.get((i))))
					{
						groupInvolvment.put(c.customTag,(Double)ag.culturonsVALUE.get(i));
						groupCogVal.put(c.customTag+c.Name,cogScheme.getCulturonValueForGroupInstance((Double)ag.culturonsGRPID.get(i), c.Name));
						for(String p : c.influencelinks.keySet())
							if(plans.get(p)!=null)
								plans.put(p, plans.get(p)+(c.influencelinks.get(p)*(Double)ag.culturonsVALUE.get(i)*cogScheme.getCulturonValueForGroupInstance((Double)ag.culturonsGRPID.get(i), c.Name)));							
					}
				}
			}			
			clearDisplayAndData();
			int space = 0;
			planCount = 0;
			for (CognitiveScheme.Cogniton c: cogScheme.cognitons){		
				if(c.posX == null)
				{
					MakeGCognitonAndLinkedElements(c,80,40+espacement*space);
					space+=2;					
				}
				else
					MakeGCognitonAndLinkedElements(c,c.posX,c.posY);
			}
			for(int i = 0; i < cogScheme.groupNames.size(); i++)
			{
				if((Double)ag.culturonsGRPID.get(i) != -1)
				{
					for(Cogniton c : cogScheme.getCulturonsForGroupRole(cogScheme.groupNames.get(i), (String)ag.culturonsROLE.get((i))))
					{
						if(c.posX == null)
						{
							MakeGCognitonAndLinkedElements(c,80,40+espacement*space);
							space+=2;					
						}
						else
							MakeGCognitonAndLinkedElements(c,c.posX,c.posY);
					}
				}
			}		
			displayGStructure();
			this.repaint();
			this.revalidate();
		}
	}
	
	public void updateAgent(AgentInfo ag)
	{
		if(ag != null)
		{
			CognitiveScheme cogScheme = CognitonExtension.cognitiveSchemes.get(ag.cogScheme);
			cognitons = new HashMap<>();
			plans = new HashMap<>();
			groupInvolvment = new HashMap<String, Double>();
			groupCogVal = new HashMap<String, Double>();
			for(int i = 0 ; i < cogScheme.cognitonNames.size(); i++)
			{
				cognitons.put(cogScheme.cognitonNames.get(i), ag.cognitonValues.get(i));
				if(ag.cognitonValues.get(i)!=null)
					for(String p : cogScheme.cognitons.get(i).dependencyLinks)
						plans.put(p, 0.0);					
			}
			for(int i = 0; i < cogScheme.groupNames.size(); i++)
			{
				if((Double)ag.culturonsGRPID.get(i) != -1)
				{
					for(Cogniton c : cogScheme.getCulturonsForGroupRole(cogScheme.groupNames.get(i), (String)ag.culturonsROLE.get((i))))
						for(String p : c.dependencyLinks)
							plans.put(p, 0.0);	
				}
			}
			for(int i = 0 ; i < cogScheme.cognitonNames.size(); i++)
			{
				for(String p : cogScheme.cognitons.get(i).influencelinks.keySet())
				{
					if(plans.get(p)!=null && ag.cognitonValues.get(i)!= null)
						plans.put(p, plans.get(p)+(cogScheme.cognitons.get(i).influencelinks.get(p)*ag.cognitonValues.get(i)));											
				}
			}
			for(int i = 0; i < cogScheme.groupNames.size(); i++)
			{
				if((Double)ag.culturonsGRPID.get(i) != -1)
				{
					for(Cogniton c : cogScheme.getCulturonsForGroupRole(cogScheme.groupNames.get(i), (String)ag.culturonsROLE.get((i))))
					{
						groupInvolvment.put(c.customTag,(Double)ag.culturonsVALUE.get(i));
						groupCogVal.put(c.customTag+c.Name,cogScheme.getCulturonValueForGroupInstance((Double)ag.culturonsGRPID.get(i), c.Name));
						for(String p : c.influencelinks.keySet())
							if(plans.get(p)!=null)
								plans.put(p, plans.get(p)+(c.influencelinks.get(p)*(Double)ag.culturonsVALUE.get(i)*cogScheme.getCulturonValueForGroupInstance((Double)ag.culturonsGRPID.get(i), c.Name)));												
					}
				}
			}			
			for(String p : gPlan.keySet())
			{
				
					if(plans.get(p) != null)
						gPlan.get(p).customMsg = decFormat.format(plans.get(p));					
					else
						gPlan.get(p).customMsg = "No Active Link";					
			
			}
			for(GCogniton gc : gCognitons)
			{
				if(gc.cogniton.isCulturon)
				{
					gc.customMessage = gc.cogniton.customTag
										+" -- V:" + groupCogVal.get(gc.cogniton.customTag+gc.cogniton.Name)
											+" P:" +groupInvolvment.get(gc.cogniton.customTag);
				}
				else
				{
					if(cognitons.get(gc.cogniton.Name) == null)
						gc.customMessage = "Inactive";
					else			
						gc.customMessage = decFormat.format(cognitons.get(gc.cogniton.Name));
				}				
			}
			this.repaint();
		}
	}
	
	synchronized public static void reportAgentValues(long id, String breedname, String cs, ArrayList<Double> values, ArrayList<Double> culturonsGRPID, ArrayList<String> culturonsROLE, ArrayList<Double> culturonsVALUE, Agent agent) 
	{
		AgentInfo ag = new AgentInfo(id, breedname,cs,values, culturonsGRPID,culturonsROLE,culturonsVALUE, agent);
		agentValues.add(ag);	
	}

	synchronized public static void reinit()
	{
		agentValues.clear();
	}

	public void displayGeneralPopup(MouseEvent e){
		JPopupMenu popupGeneral = new JPopupMenu(("Menu"));
		JMenuItem affichageCustom = new JMenuItem("Toggle Type colors");
		affichageCustom.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				displayCustomColor = !displayCustomColor;
				panelAdress.repaint();
			}
		});
		popupGeneral.add(affichageCustom);
		

		JRadioButtonMenuItem influenceLink = new JRadioButtonMenuItem("Toggle Influence Link display");
		influenceLink.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				displayInfluenceLink = !displayInfluenceLink;
				displayAgent(agentValues.get(((AgentInfo) currentAgent.getSelectedItem()).id));
			}
		});
		popupGeneral.add(influenceLink);
		
		JRadioButtonMenuItem conditionalLink = new JRadioButtonMenuItem("Toggle Conditional Link display");
		conditionalLink.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				displayConditionalLink = !displayConditionalLink;
				displayAgent(agentValues.get(((AgentInfo) currentAgent.getSelectedItem()).id));
			}
		});
		popupGeneral.add(conditionalLink);
		
		JRadioButtonMenuItem reinforcementLink = new JRadioButtonMenuItem("Toggle Reinforcement Link display");
		reinforcementLink.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				displayReinforcementLink = !displayReinforcementLink;
				displayAgent(agentValues.get(((AgentInfo) currentAgent.getSelectedItem()).id));
			}
		});
		popupGeneral.add(reinforcementLink);
		
		popupGeneral.show(this, (int)e.getX(), (int) e.getY());
	}
}

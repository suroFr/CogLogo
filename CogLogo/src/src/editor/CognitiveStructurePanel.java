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
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.MouseInputListener;

import src.CognitiveScheme;

@SuppressWarnings("serial")
public class CognitiveStructurePanel extends JPanel{

	CognitiveStructurePanel panelAdress = this;
	protected double espacement = 40;
	protected double espaceCognitonsPlans = 350;
	int compteur = 0;
	int initXTrigger = 12;
	double offsetX,offsetY = 0;
	int planCount = 0;
	double zoomLevel = 1.0;
	

	public CognitiveScheme currentCognitiveScheme = null;
	protected ArrayList<GCogniton> gCognitons = new ArrayList<>();
	protected HashMap<String,GPlan> gPlan = new HashMap<String, GPlan>();
	protected ArrayList<GLink> gLiens = new ArrayList<>();
	protected ArrayList<GLink> gLiensReinf = new ArrayList<>();
	protected ArrayList<GLink> gLiensConditionnels = new ArrayList<>();
	protected ArrayList<GLabel> gLabels = new ArrayList<>();

	JPanel mainPanel;
	JPopupMenu popupGPlans;
	JPopupMenu popupGCognitons;
	JLabel currenCog = new JLabel("No Cognitive Scheme");
	public JToolBar toolBar;
	private double mPrevX =0;
	private double mPrevY =0;
	boolean rightClick = false;
	boolean leftClick = false;
	/*
	int refreshMinTime = 50;
	Timer refreshTimer = new Timer(refreshMinTime, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			superRevalidate();
			refreshTimer.stop();
		}
	});
	*/
	public JComponent selectedElement = null;
	
	boolean displayInfluenceLink = true;
	boolean displayConditionalLink = true;
	boolean displayReinforcementLink = true;
	boolean displayCustomColor = true;
	boolean mouseIn = false;
	long prevUpdateTime = 0;
	long prevUpdateTimer = 0;
	
	public CognitiveStructurePanel()
	{
		super();
		this.setSize(800, 600);
		toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setRollover(true);
        toolBar.add(Box.createHorizontalGlue());
        MouseInputListener mouseListener = new MouseInputListener() {
        	@Override
			public void mouseMoved(MouseEvent e) {}			
			@Override
			public void mouseDragged(MouseEvent e) {
				if(leftClick && System.currentTimeMillis() - prevUpdateTime > 60)
				{
						prevUpdateTime = System.currentTimeMillis();
						offsetX +=(e.getX()-mPrevX)/zoomLevel;
						offsetY +=(e.getY()-mPrevY)/zoomLevel;
						mPrevX = e.getX();
						mPrevY = e.getY();
						panelAdress.repaint();
						panelAdress.revalidate();	
				}				
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				if(SwingUtilities.isRightMouseButton(e) || (SwingUtilities.isLeftMouseButton(e)&&e.isControlDown()))
					rightClick = false;
				else if(SwingUtilities.isLeftMouseButton(e))
					leftClick = false;
			}			
			@Override
			public void mousePressed(MouseEvent e) {
				mPrevX = e.getX();
				mPrevY = e.getY();		
				if(SwingUtilities.isRightMouseButton(e) || (SwingUtilities.isLeftMouseButton(e)&&e.isControlDown()))
					rightClick = true;
				else if(SwingUtilities.isLeftMouseButton(e))
					leftClick = true;
			}			
			@Override
			public void mouseExited(MouseEvent e) {
				mouseIn = false;
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				mouseIn = true;
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				if(SwingUtilities.isRightMouseButton(e) || (SwingUtilities.isLeftMouseButton(e)&&e.isControlDown()))
					displayGeneralPopup(e);
				else
					switchSelectedElement(null);
			}
		};
		this.addMouseListener(mouseListener);
		this.addMouseMotionListener(mouseListener);
		
		this.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				zoomLevel += (e.getPreciseWheelRotation()*-(0.02 + (zoomLevel*0.1)));
				
				if(zoomLevel < 0.1)
					zoomLevel = 0.1;
				else if(zoomLevel > 3)
					zoomLevel = 3;
				panelAdress.repaint();
				panelAdress.revalidate();		
			}
		}); 
	}
	
	protected void switchSelectedElement(JComponent object) {
		selectedElement = object;
		gLabels.clear();
		this.removeAll();
		if(selectedElement!=null)
		{
			if(selectedElement.getClass().equals(GCogniton.class))
			{
				GCogniton cog = (GCogniton) selectedElement;
				if(displayInfluenceLink)
				{
					for(String dPlan : cog.cogniton.influencelinks.keySet())
					{
						GPlan target = gPlan.get(dPlan);
						if(target != null)
							makeGLab(cog,target,cog.cogniton.influencelinks.get(dPlan),0.66);
					}
				}
				if(displayReinforcementLink)
				{
					for(String dPlan : cog.cogniton.feedBackLinks.keySet())
					{
						GPlan target = gPlan.get(dPlan);
						if(target != null)
							makeGLab(cog,target,cog.cogniton.feedBackLinks.get(dPlan),0.33);
					}
				}
			}
			else if(selectedElement.getClass().equals(GPlan.class))
			{
				GPlan pl = (GPlan) selectedElement;
				if(displayInfluenceLink)
				{
					for(GLink l : gLiens)
					{
						if(l.plan.equals(pl))
							makeGLab(l.cogniton,pl,l.cogniton.cogniton.influencelinks.get(pl.plan),0.66);
					}					
				}
				if(displayReinforcementLink)
				{
					for(GLink l : gLiensReinf)
					{
						if(l.plan.equals(pl))
							makeGLab(l.cogniton,pl,l.cogniton.cogniton.feedBackLinks.get(pl.plan),0.33);
					}					
				}
			}			
		}
		displayGStructure();
		this.repaint();
		this.revalidate();
	}

	private void makeGLab(GCogniton cog, GPlan target, Double val, double pos) {
		gLabels.add(new GLabel(this, cog,target, val.toString(), pos));
	}

	public CognitiveStructurePanel(JPanel panelPrincipal) {
		this();
		this.mainPanel = panelPrincipal;
	}

	protected void clearDisplayAndData() {
		gCognitons.clear();
		gPlan.clear();
		gLiens.clear();
		gLiensReinf.clear();
		gLiensConditionnels.clear();
		this.removeAll();
	}

	public GCogniton makeGCogniton(CognitiveScheme.Cogniton c , double posX , double posY)
	{
		GCogniton cur = new GCogniton(this,posX,posY,60,25, c);
		return cur;
	}
	
	public void MakeGCognitonAndLinkedElements(CognitiveScheme.Cogniton c , double posX , double posY){
		GCogniton cur = makeGCogniton(c, posX, posY);
		gCognitons.add(cur);

		for(String dPlan : c.influencelinks.keySet())
		{
			GPlan target = gPlan.get(dPlan);
			if(target == null)
				target = displayPlan(dPlan, espaceCognitonsPlans,40+espacement*planCount);
			if(displayInfluenceLink)
				makeGLinkInfluence(cur,target,c.influencelinks.get(dPlan));
		}
		
		for(String dPlan : c.dependencyLinks)
		{
			GPlan target = gPlan.get(dPlan);
			if(target == null)
				target = displayPlan(dPlan, espaceCognitonsPlans,40+espacement*planCount);
			if(displayConditionalLink)
				makeGLinkConditional(cur,target);
		}
		
		for(String dPlan : c.feedBackLinks.keySet())
		{
			GPlan target = gPlan.get(dPlan);
			if(target == null)
				target = displayPlan(dPlan, espaceCognitonsPlans,40+espacement*planCount);
			if(displayReinforcementLink)
				makeGLinkReinforcement(cur,target,c.feedBackLinks.get(dPlan));
		}
	}
	
	public void makeGLinkInfluence(GCogniton cog, GPlan pl, double Val)
	{
		gLiens.add(new GLink(this,cog,pl,Val , true));
	}
	
	public void makeGLinkConditional(GCogniton cog, GPlan pl)
	{
		gLiensConditionnels.add(new GLink(this,cog,pl,-1 , false));
	}
	
	public void makeGLinkInfluence(GCogniton cog, String pl, double Val)
	{
		GPlan target = gPlan.get(pl);
		if(target == null)
			target = displayPlan(pl, espaceCognitonsPlans,40+espacement*planCount);
		gLiens.add(new GLink(this,cog,target,Val , true));
	}
	
	public void makeGLinkConditional(GCogniton cog, String pl)
	{
		GPlan target = gPlan.get(pl);
		if(target == null)
			target = displayPlan(pl, espaceCognitonsPlans,40+espacement*planCount);
		gLiensConditionnels.add(new GLink(this,cog,target,-1 , false));
	}
	
	public void makeGLinkReinforcement(GCogniton cog, String pl, double Val)
	{
		GPlan target = gPlan.get(pl);
		if(target == null)
			target = displayPlan(pl, espaceCognitonsPlans,40+espacement*planCount);
		gLiensReinf.add(new GArcLink(this,cog,target,Val , true));
	}
	
	public void makeGLinkReinforcement(GCogniton cog, GPlan pl, double Val)
	{
		gLiensReinf.add(new GArcLink(this,cog,pl,Val , true));
	}
	
	public void displayGStructure()
	{
		int i = this.getComponentCount();

		for(GLabel l : gLabels)
		{
			this.add(l);
			this.setComponentZOrder(l, i);
			i++;			
		}
		for(GCogniton gc : gCognitons)
		{
			this.add(gc);
			this.setComponentZOrder(gc, i);
			i++;
		}
		for(GPlan p: gPlan.values())
		{
			this.add(p);
			this.setComponentZOrder(p, i);
			i++;
		}
		if(displayConditionalLink)
		{
			for(GLink l : gLiensConditionnels)
			{
				this.add(l);
				this.setComponentZOrder(l,i);
				i++;
			}			
		}
		if(displayInfluenceLink)
		{
			for(GLink l : gLiens)
			{
				this.add(l);
				this.setComponentZOrder(l,i);
				i++;
			}			
		}
		if(displayReinforcementLink)
		{
			for(GLink l : gLiensReinf)
			{
				this.add(l);
				this.setComponentZOrder(l,i);
				i++;
			}			
		}
	}
	
	public GPlan MakeGPlan(String p , double posX , double posY){
		GPlan curr = new GPlan(this,posX,posY,60,25, p);
		return curr;			
	}
		

	public GPlan displayPlan(String p , double posX , double posY){
		GPlan curr;
		if (currentCognitiveScheme.planSavedPosition.get(p) != null)
			curr = MakeGPlan(p, currentCognitiveScheme.planSavedPosition.get(p).x, currentCognitiveScheme.planSavedPosition.get(p).y);
		else
			curr = MakeGPlan(p, posX, posY);
		gPlan.put(p,curr);
		planCount++;
		return curr;
	}



	public int getRelativeXPos(int pos)
	{
		return (int)((pos+offsetX)*zoomLevel);
	}

	public int getRelativeYPos(int pos)
	{
		return (int)((pos+offsetY)*zoomLevel);
	}

	public GPlan MakeGPlan(String p, double posX, double posY, String msg) {
		GPlan curr = new GPlan(this,posX,posY,60,25, p,msg);
		return curr;	
	}

	public void updateDisplayedElements() {
	}

	public void displayGeneralPopup(MouseEvent e){
		
	}
		
	/*
	@Override
	public void revalidate()
	{
		if(System.currentTimeMillis()-prevUpdateTimer > refreshMinTime)
		{		
			if(refreshTimer != null)
			{
				refreshTimer.start();
				prevUpdateTimer = System.currentTimeMillis();				
			}
			else
				superRevalidate();
		}		
	}	

	public void superRevalidate()
	{
		super.revalidate();
	}
	*/
}

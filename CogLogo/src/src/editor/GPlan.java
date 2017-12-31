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

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;

@SuppressWarnings("serial")
public class GPlan extends JComponent{
	private CognitiveStructurePanel panelAdress;
	Font planFont;
	float margeEcriture = 15;
	String plan;
	String customMsg = "";
	double sizeX,sizeY;
	int X,Y;
	int clickX,clickY;
	
	public GPlan(CognitiveStructurePanel parent , double x, double y, double w, double h, String p) {
		super();
		plan = p;
		X = (int) x;
		Y = (int) y;
		panelAdress = parent;
		planFont = new Font("default", Font.BOLD, 15);
		this.setToolTipText(plan.toString());
		MouseInputListener mouseListener = new MouseInputListener() {
			@Override
			public void mouseMoved(MouseEvent e) {}
			@Override
			public void mouseDragged(MouseEvent e) {
				if(panelAdress.mouseIn && System.currentTimeMillis() - panelAdress.prevUpdateTime > 10)
				{
					panelAdress.prevUpdateTime = System.currentTimeMillis();
					X+=((e.getXOnScreen()- clickX)/panelAdress.zoomLevel);
					Y+=((e.getYOnScreen()- clickY)/panelAdress.zoomLevel);
					clickX = e.getXOnScreen();
					clickY = e.getYOnScreen();
					panelAdress.repaint();
					//panelAdress.revalidate();
				}
			}
			@Override
			public void mouseClicked(MouseEvent e) {
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				panelAdress.mouseIn = true;
			}
			@Override
			public void mouseExited(MouseEvent e) {
				panelAdress.mouseIn = false;
			}
			@Override
			public void mousePressed(MouseEvent e) {		
				clickX = e.getXOnScreen();
				clickY = e.getYOnScreen();
				panelAdress.setComponentZOrder(e.getComponent(),0);
			}
			@Override
			public void mouseReleased(MouseEvent e) {	
				//panelAdress.repaint();
				//panelAdress.revalidate();
			}
		};
		this.addMouseListener(mouseListener);
		this.addMouseMotionListener(mouseListener);
	}

	
	public GPlan(CognitiveStructurePanel parent , double x, double y, double w, double h, String p, String msg) {
		this(parent,x,y,w,h,p);
		customMsg = msg;
	}

	@Override
	public void paintComponent(Graphics g) 
    {    
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.scale(panelAdress.zoomLevel, panelAdress.zoomLevel);
        Color backgroundColor = Color.GRAY;
    	g2d.setColor(Color.BLACK);
    	String displayedString = plan;
    	if(customMsg != "")
    		displayedString = displayedString + " = " + customMsg;
    	g2d.setFont(planFont);
    	FontMetrics fm = g2d.getFontMetrics();
    	this.setBounds((int)(panelAdress.getRelativeXPos((int) (X))),
    			panelAdress.getRelativeYPos((int)Y),
    			(int) (((fm.stringWidth(displayedString) + (2*margeEcriture)))*panelAdress.zoomLevel),(int) (2*fm.getHeight()*panelAdress.zoomLevel));
    	sizeX = fm.stringWidth(displayedString) + (2*margeEcriture);
    	sizeY = 2*fm.getHeight();
    	g2d.fill(new Rectangle2D.Double(0,0,fm.stringWidth(displayedString) + (2*margeEcriture),2*fm.getHeight()));
    	g2d.setColor(backgroundColor);
    	g2d.fill(new Rectangle2D.Double(2,2,(int) (fm.stringWidth(displayedString) + (2*margeEcriture))-4,2*fm.getHeight()-4));
        g2d.setColor(Color.BLACK);
    	g2d.drawString(displayedString, margeEcriture, (float) (fm.getHeight()*1.3));
    }

	public String getPlan() {
		return plan;
	}

	public int getCentreX() {
		return (int) (X + (sizeX/2));
	}

	public int getCentreY() {
		return (int) (Y + (sizeY/2));
	}
	
	public double getDistanceToEdge(double ang)
	{
		double ret;
		double cosT = Math.cos(ang);
		ret = Math.abs(((double)-(sizeX/2.0)) / cosT);
		if(Math.abs(((double)(sizeX/2.0)) / cosT)< ret)
			ret = Math.abs(((double)(sizeX/2.0)) / cosT);
		double sinT = Math.sin(ang);
		if(Math.abs(((double)(sizeY/2.0)) / sinT)< ret)
			ret = Math.abs(((double)(sizeY/2.0)) / sinT);
		if(Math.abs(((double)-(sizeY/2.0)) / sinT)< ret)
			ret = Math.abs(((double)-(sizeY/2.0)) / sinT);		
		return ret;
	}
}

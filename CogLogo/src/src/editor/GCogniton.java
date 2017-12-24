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
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;

import javax.swing.JComponent;

import src.CognitiveScheme;

@SuppressWarnings("serial")
public class GCogniton extends JComponent{
	private CognitiveStructurePanel panelAdress;
	CognitiveScheme.Cogniton cogniton;
	private Boolean DisplayCustomColor;
	float margeEcriture = 15;
	Font cognitonFont;
	double sizeX,sizeY;
	int X,Y;
	int groupIndex = -1;
	
	String customMessage = "";
	
	public GCogniton(CognitiveStructurePanel parent , double x, double y, double w, double h, CognitiveScheme.Cogniton c) {
		super();
		cogniton = c;
		X = (int) x;
		Y = (int) y;
		panelAdress = parent;
		this.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {}
			@Override
			public void mouseDragged(MouseEvent e) {
				X+=(e.getX()/panelAdress.zoomLevel);
				Y+=(e.getY()/panelAdress.zoomLevel);
				panelAdress.repaint();
				panelAdress.revalidate();
			}
		});
		cognitonFont = new Font("default", Font.BOLD, 15);
		this.DisplayCustomColor = true;
		this.setVisible(true);
	}
	
	public GCogniton(CognitiveStructurePanel parent , double x, double y, double w, double h, CognitiveScheme.Cogniton c,String message) {
		this(parent,x,y,w,h,c);
		customMessage = message;
	}

	@Override
	public void paintComponent(Graphics g) 
    {    
		int height,width;
        Graphics2D g2d = (Graphics2D) g.create();
        String displayedString = cogniton.Name;
        g2d.scale(panelAdress.zoomLevel, panelAdress.zoomLevel);
        g2d.setFont(cognitonFont);
    	FontMetrics fm = g2d.getFontMetrics();
    	height = 2*fm.getHeight();
    	width = Math.max(fm.stringWidth(displayedString),fm.stringWidth(customMessage));
    	if(customMessage != "")
    		height = 4*fm.getHeight();
    	this.setBounds(panelAdress.getRelativeXPos((int)(X)),
    			panelAdress.getRelativeYPos((int)Y),
    			(int) ( (width+ (2*margeEcriture))*panelAdress.zoomLevel ),(int)(height*panelAdress.zoomLevel));
    	
    	g2d.fill(new Ellipse2D.Double(0 ,0,width + (2*margeEcriture),height));
    	sizeX = width + (2*margeEcriture);
    	sizeY = height;
    	
    	if(DisplayCustomColor)
    		g2d.setColor(cogniton.getDisplayColor());
    	else
    	{
    		if(cogniton.isCulturon)
    			g2d.setColor(Color.yellow);    		
    		else
    			g2d.setColor(Color.LIGHT_GRAY);    		    			
    	}
    	g2d.fill(new Ellipse2D.Double(2,2,(int) (width + (2*margeEcriture) )-4,height-4));
    	g2d.setColor(Color.BLACK);
    	if(customMessage == "")
    		g2d.drawString(displayedString, margeEcriture, (float) (fm.getHeight()*1.3));
    	else
    	{
    		g2d.drawString(displayedString, (int)((width + (2*margeEcriture))/2.0)-(fm.stringWidth(displayedString)/2), (float) (fm.getHeight()*1.8));    		
    		g2d.drawString(customMessage, (int)((width + (2*margeEcriture))/2.0)-(fm.stringWidth(customMessage)/2), (float) (fm.getHeight()*3.0));    		
    	}
    }

	public void setCouleur(Color c)
	{
		cogniton.setDisplayColor(c);
	}
	
	public void switchDisplayColor()
	{
		this.DisplayCustomColor = !this.DisplayCustomColor;
	}
	
	public void displayCustomColor()
	{
		this.DisplayCustomColor = true;
	}
	
	public void displayTypeColor()
	{
		this.DisplayCustomColor = false;
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

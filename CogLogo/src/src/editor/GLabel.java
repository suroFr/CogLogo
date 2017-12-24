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
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

@SuppressWarnings("serial")
public class GLabel extends JComponent {
	private CognitiveStructurePanel panelAdress;
	Font labFont;
	float margeEcriture = 3;
	String Text;
	double sizeX,sizeY;
	int X,Y;
	GCogniton cog;
	GPlan target;
	double percent;
	
	public GLabel(CognitiveStructurePanel parent , GCogniton c, GPlan t,String text , double p) {
		super();
		Text = text;
		cog = c;
		target = t;
		percent = p;
		X = (int) (((cog.getCentreX() * percent) + ((1.0-percent)*target.getCentreX()) ));
		Y = (int) (((cog.getCentreY() * percent) + ((1.0-percent)*target.getCentreY()) ));
		panelAdress = parent;
		labFont = new Font("default", Font.BOLD,10);
	}

	@Override
	public void paintComponent(Graphics g) 
    {    
		X = (int) (((cog.getCentreX() * percent) + ((1.0-percent)*target.getCentreX()) ));
		Y = (int) (((cog.getCentreY() * percent) + ((1.0-percent)*target.getCentreY()) ));
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.scale(panelAdress.zoomLevel, panelAdress.zoomLevel);
        Color backgroundColor = Color.WHITE;
    	g2d.setColor(Color.BLACK);
    	g2d.setFont(labFont);
    	FontMetrics fm = g2d.getFontMetrics();
    	this.setBounds((int)(panelAdress.getRelativeXPos((int) (X))),
    			panelAdress.getRelativeYPos((int)Y),
    			(int) (((fm.stringWidth(Text) + (2*margeEcriture)))*panelAdress.zoomLevel),(int) (2*fm.getHeight()*panelAdress.zoomLevel));
    	sizeX = fm.stringWidth(Text) + (2*margeEcriture);
    	sizeY = 2*fm.getHeight();
    	g2d.fill(new Rectangle2D.Double(0,0,fm.stringWidth(Text) + (2*margeEcriture),2*fm.getHeight()));
    	g2d.setColor(backgroundColor);
    	g2d.fill(new Rectangle2D.Double(2,2,(int) (fm.stringWidth(Text) + (2*margeEcriture))-4,2*fm.getHeight()-4));
        g2d.setColor(Color.BLACK);
    	g2d.drawString(Text, margeEcriture, (float) (fm.getHeight()*1.3));
    }
}

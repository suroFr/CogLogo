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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

@SuppressWarnings("serial")
public class GLink extends JComponent {
	protected CognitiveStructurePanel panelAdress;
	public GCogniton cogniton;
	public GPlan plan;
	public boolean isInfluence;
	public double poids;
	Color color;
	int[] arrowX = new int[3];
	int[] arrowY = new int[3];

	public GLink(CognitiveStructurePanel parent, GCogniton cog, GPlan pla, double poids, boolean t) {
		super();
		panelAdress = parent;
		this.cogniton = cog;
		this.plan = pla;
		this.poids = poids;
		this.isInfluence = t;
		this.setFocusable(false);
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		if (isInfluence) {
			if (poids > 0.0)
				color = new Color(0.0f, 0.6f, 0.1f);
			else
				color = new Color(0.9f, 0.0f, 0.0f);
			g2d.setStroke(new BasicStroke((int)(7 * panelAdress.zoomLevel)));
		} else {
			color = Color.BLACK;
			g2d.setStroke(new BasicStroke((int)(2* panelAdress.zoomLevel)));
		}
		this.setBounds(0, 0, 5000, 5000);
		g2d.setColor(color);
		g2d.drawLine((int) (panelAdress.getRelativeXPos(cogniton.getCentreX())),
				(int) (panelAdress.getRelativeYPos(cogniton.getCentreY())),
				(int) (panelAdress.getRelativeXPos(plan.getCentreX())),
				(int) panelAdress.getRelativeYPos(plan.getCentreY()));
		
	
		double ang = Math.atan2(cogniton.getCentreY() - plan.getCentreY(), cogniton.getCentreX() - plan.getCentreX());
		
		arrowX[0] = (int) (panelAdress.getRelativeXPos(plan.getCentreX()) + (plan.getDistanceToEdge(ang) * Math.cos(ang)* panelAdress.zoomLevel));
		arrowY[0] =(int) (panelAdress.getRelativeYPos(plan.getCentreY()) + (plan.getDistanceToEdge(ang) * Math.sin(ang)* panelAdress.zoomLevel));

		arrowX[1] = (int) ((arrowX[0]) + ((15 * Math.cos(ang + 0.30))* panelAdress.zoomLevel));
		arrowY[1] =(int) ((arrowY[0]) + ((15 * Math.sin(ang + 0.30))* panelAdress.zoomLevel));

		arrowX[2] = (int) ((arrowX[0]) + ((15 * Math.cos(ang - 0.30))* panelAdress.zoomLevel));
		arrowY[2] = (int) ((arrowY[0]) + ((15 * Math.sin(ang - 0.30))* panelAdress.zoomLevel));

		g2d.drawPolygon(arrowX, arrowY, 3);
	}
}

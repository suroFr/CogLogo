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
import java.awt.geom.QuadCurve2D;

@SuppressWarnings("serial")
public class GArcLink extends GLink {

	public GArcLink(CognitiveStructurePanel parent, GCogniton cog, GPlan pla, double poids, boolean t) {
		super( parent,  cog,  pla,  poids,  t);
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
			if (poids > 0.0)
				color = new Color(0.0f, 0.3f, 0.f);
			else
				color = new Color(0.3f, 0.0f, 0.0f);
			g2d.setStroke(new BasicStroke((int)(3* panelAdress.zoomLevel)));
		this.setBounds(0, 0, 5000, 5000);
		g2d.setColor(color);
		
		double ang = Math.atan2(plan.getCentreY() - cogniton.getCentreY(), plan.getCentreX() - cogniton.getCentreX());
		
		QuadCurve2D q = new QuadCurve2D.Double();
		double x = panelAdress.getRelativeXPos((int) (((plan.getCentreX() + cogniton.getCentreX())/2.0)+(40*Math.cos(ang + (Math.PI/2.0)))));
		double y = panelAdress.getRelativeYPos((int) (((plan.getCentreY() + cogniton.getCentreY())/2.0)+(40*Math.sin(ang + (Math.PI/2.0)))));
		q.setCurve(panelAdress.getRelativeXPos(plan.getCentreX()),
					panelAdress.getRelativeYPos(plan.getCentreY()),
					x,
					y,
					panelAdress.getRelativeXPos(cogniton.getCentreX()),
					panelAdress.getRelativeYPos(cogniton.getCentreY()));
		g2d.draw(q);
		
		QuadCurve2D q1 = new QuadCurve2D.Double();
		QuadCurve2D q2 = new QuadCurve2D.Double();
		q.subdivide(q1, q2);
		
		arrowX[0] = (int) (q2.getX1());
		arrowY[0] =(int) (q2.getY1());

		arrowX[1] = (int) ((arrowX[0]) + (15 * Math.cos(ang + 0.40)* panelAdress.zoomLevel));
		arrowY[1] =(int) ((arrowY[0]) + (15 * Math.sin(ang + 0.40)* panelAdress.zoomLevel));

		arrowX[2] = (int) ((arrowX[0]) + (15 * Math.cos(ang - 0.40)* panelAdress.zoomLevel));
		arrowY[2] = (int) ((arrowY[0]) + (15 * Math.sin(ang - 0.40)* panelAdress.zoomLevel));

		g2d.drawPolygon(arrowX, arrowY, 3);
	}
}

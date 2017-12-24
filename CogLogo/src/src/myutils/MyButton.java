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

package src.myutils;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.border.Border;

@SuppressWarnings("serial")
public class MyButton extends JButton{

	public static Border b = BorderFactory.createCompoundBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createEmptyBorder(3, 3, 3, 3),
	        BorderFactory.createLineBorder(Color.GRAY, 2)), 
	        BorderFactory.createEmptyBorder(5, 5, 5, 5));
	
	public MyButton(String txt)
	{
		super(txt);
		this.setBorder(b);
		this.setBackground(Color.LIGHT_GRAY);
	}
}

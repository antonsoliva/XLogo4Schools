/* XLogo4Schools - A Logo Interpreter specialized for use in schools, based on XLogo by Loic Le Coq
 * Copyright (C) 2013 Marko Zivkovic
 * 
 * Contact Information: marko88zivkovic at gmail dot com
 * 
 * This program is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by the Free 
 * Software Foundation; either version 2 of the License, or (at your option) 
 * any later version.  This program is distributed in the hope that it will be 
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General 
 * Public License for more details.  You should have received a copy of the 
 * GNU General Public License along with this program; if not, write to the Free 
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA 02110-1301, USA.
 * 
 * 
 * This Java source code belongs to XLogo4Schools, written by Marko Zivkovic
 * during his Bachelor thesis at the computer science department of ETH Zurich,
 * in the year 2013 and/or during future work.
 * 
 * It is a reengineered version of XLogo written by Loic Le Coq, published
 * under the GPL License at http://xlogo.tuxfamily.org/
 * 
 * Contents of this file were initially written by Loic Le Coq,
 * modifications, extensions, refactorings might have been applied by Marko Zivkovic 
 */

package xlogo.kernel.gui;

import java.awt.event.ActionEvent;
import java.awt.Point;
import java.awt.event.ActionListener;
import javax.swing.JComponent;

public abstract class GuiComponent implements ActionListener
{
	public boolean			hasAction	= false;
	protected int			originalWidth;
	protected int			originalHeight;
	private String			id;
	protected JComponent	guiObject;
	protected boolean		actionFinished;
	
	public String getId()
	{
		return id;
	}

	protected void setId(String id)
	{
		this.id = id;
	}
	
	public boolean hasAction()
	{
		return hasAction;
	}

	public JComponent getGuiObject()
	{
		return guiObject;
	}
	
	public void setLocation(int x, int y)
	{
		guiObject.setLocation(x, y);
	}
	
	public Point getLocation()
	{
		return guiObject.getLocation();
	}
	
	public void setSize(int width, int height)
	{
		guiObject.setSize(width, height);
	}

	public int getOriginalWidth()
	{
		return originalWidth;
	}

	public int getOriginalHeight()
	{
		return originalHeight;
	}
	
	public abstract void actionPerformed(ActionEvent e);
	
	public abstract boolean isButton();
	
	public abstract boolean isMenu();
}

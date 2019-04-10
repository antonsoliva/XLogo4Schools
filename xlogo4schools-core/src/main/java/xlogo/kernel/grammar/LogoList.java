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

/**
 * Title : XLogo
 * Description : XLogo is an interpreter for the Logo
 * programming language
 * Licence : GPL
 * 
 * @author Loïc Le Coq
 */
package xlogo.kernel.grammar;

import java.util.Vector;

public class LogoList extends LogoType
{
	private Vector<LogoType>	vector;
	
	LogoList(Vector<LogoType> vector)
	{
		this.vector = vector;
	}
	
	LogoList()
	{
		vector = new Vector<LogoType>();
	}
	
	public boolean isList()
	{
		return true;
	}
	
	public void add(LogoType type)
	{
		vector.add(type);
	}
	
	public Vector<LogoType> getVector()
	{
		return vector;
	}
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("[ ");
		for (int i = 0; i < vector.size(); i++)
		{
			sb.append(vector.get(i).toString());
			sb.append(" ");
		}
		sb.append("]");
		return sb.toString();
	}
	
	@Override
	public String toDebug()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("(LIST) ");
		sb.append(toString());
		return sb.toString();
		
	}
	
}

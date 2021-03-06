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
 * Contents of this file were entirely written by Marko Zivkovic
 */

package xlogo.storage.workspace;

public enum NumberOfBackups {
	NO_BACKUPS(0),
	ONE(1),
	THREE(3),
	TEN(10),
	FIFTY(50),
	INFINITE(-1);
	
	private int number;
	
	private NumberOfBackups(int number) {
		this.number = number;
	}
	
	public int getNumber()
	{
		return number;
	}
	
	public String toString()
	{
		if (number == -1)
		{
			return "infinite";
		}
		return Integer.toString(number);
	}
	
	public int getValue(){
		return number;
	}
	
	public static NumberOfBackups valueOf(int value){
		switch(value){
			case 0: return NumberOfBackups.NO_BACKUPS;
			case 1: return NumberOfBackups.ONE;
			case 3: return NumberOfBackups.THREE;
			case 10: return NumberOfBackups.TEN;
			case 50: return NumberOfBackups.FIFTY;
			default: return NumberOfBackups.INFINITE;
		}
	}
}

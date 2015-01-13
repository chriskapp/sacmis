/**
 * sacmis
 * An application wich executes PHP code and displays the result. Useful for
 * testing and debugging PHP scripts.
 * 
 * Copyright (c) 2010-2015 Christoph Kappestein <k42b3.x@gmail.com>
 * 
 * This file is part of sacmis. sacmis is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation, 
 * either version 3 of the License, or at any later version.
 * 
 * sacmis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with sacmis. If not, see <http://www.gnu.org/licenses/>.
 */

package com.k42b3.sacmis;

/**
 * ExecutableDetector
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/sacmis
 */
public class ExecutableDetector
{
	protected String argument;
	protected String indicator;

	public ExecutableDetector(String argument, String indicator)
	{
		this.argument = argument;
		this.indicator = indicator;
	}

	public String getArgument()
	{
		return argument;
	}

	public void setArgument(String argument)
	{
		this.argument = argument;
	}

	public String getIndicator()
	{
		return indicator;
	}

	public void setIndicator(String indicator)
	{
		this.indicator = indicator;
	}
}

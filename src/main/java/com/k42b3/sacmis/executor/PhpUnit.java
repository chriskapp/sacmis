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

package com.k42b3.sacmis.executor;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import com.k42b3.sacmis.ExecutableDetector;
import com.k42b3.sacmis.ExecutorAbstract;

/**
 * PhpUnit
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/sacmis
 */
public class PhpUnit extends ExecutorAbstract
{
	public PhpUnit(String cmd, RSyntaxTextArea textArea)
	{
		super(cmd, textArea);
	}

	protected String getName()
	{
		return "phpunit";
	}

	protected String[] getExecutables()
	{
		String[] executables = {"phpunit", "phpunit.bat", "vendor/bin/phpunit", "vendor/bin/phpunit.bat", "php phpunit.phar"};

		return executables;
	}

	protected ExecutableDetector getDetector()
	{
		return new ExecutableDetector("--version", "PHPUnit");
	}
}

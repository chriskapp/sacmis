/**
 * sacmis
 * An application wich writes an script from an textarea to a file and executes 
 * it with a selected processor. The result is displayed in another textfield.
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

/**
 * ExecutorAbstract
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/sacmis
 */
abstract public class ExecutorAbstract implements Runnable
{
	protected Logger logger = Logger.getLogger("com.k42b3.sacmis");

	protected String cmd;
	protected RSyntaxTextArea textArea;

	public ExecutorAbstract(String cmd, RSyntaxTextArea textArea)
	{
		this.cmd = cmd;
		this.textArea = textArea;
	}

	public void run()
	{
		try
		{
			// clear text
			this.textArea.setText("");

			CommandLine commandLine = CommandLine.parse(this.getExecutable() + " " + this.cmd);
			ExecuteWatchdog watchdog = new ExecuteWatchdog(ExecuteWatchdog.INFINITE_TIMEOUT);

			// create executor
			DefaultExecutor executor = new DefaultExecutor();

			executor.setStreamHandler(new PumpStreamHandler(new TextAreaOutputStream(textArea)));
			executor.setWatchdog(watchdog);
			executor.execute(commandLine);
		}
		catch(FoundNoExecutableException e)
		{
			JOptionPane.showMessageDialog(null, e.getMessage(), "Information", JOptionPane.ERROR_MESSAGE);
		}
		catch(IOException e)
		{
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Returns whether the command returns an response that contains the given 
	 * string
	 * 
	 * @param String cmd
	 * @param String contains
	 * @return boolean
	 * @throws IOException 
	 */
	public static boolean hasExecutable(String cmd, String contains) throws IOException
	{
		Process process = Runtime.getRuntime().exec(cmd);
		BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line;
		StringBuilder response = new StringBuilder();
		while((line = input.readLine()) != null)
		{
			response.append(line);
		}
		input.close();

		return response.toString().indexOf(contains) != -1;
	}

	/**
	 * Tries to find the fitting executable
	 * 
	 * @return String
	 * @throws IOException
	 */
	protected String getExecutable() throws FoundNoExecutableException
	{
		String[] executables = this.getExecutables();

		if(executables != null)
		{
			for(int i = 0; i < executables.length; i++)
			{
				try
				{
					ExecutableDetector detector = this.getDetector();

					if(hasExecutable(executables[i] + " " + detector.getArgument(), detector.getIndicator()))
					{
						return executables[i];
					}
				}
				catch(IOException e)
				{
					// an exception gets thrown if the executable does not exist
				}
			}
		}

		throw new FoundNoExecutableException("Could not find executable " + this.getName());
	}

	/**
	 * Returns the name of the executable
	 * 
	 * @return String
	 */
	abstract protected String getName();

	/**
	 * Returns the executables which can be possible used by this executor
	 * 
	 * @return String[]
	 */
	abstract protected String[] getExecutables();

	/**
	 * Returns the informations for the executor howto detect the correct 
	 * executable
	 * 
	 * @return ExecutableDetector
	 */
	abstract protected ExecutableDetector getDetector();
	
	/**
	 * @see http://stackoverflow.com/a/5693905
	 */
	class TextAreaOutputStream extends OutputStream
	{
		protected final RSyntaxTextArea textArea;
		protected final StringBuilder sb = new StringBuilder();

		public TextAreaOutputStream(RSyntaxTextArea textArea)
		{
			this.textArea = textArea;
		}

		@Override
		public void flush()
		{
		}

		@Override
		public void close()
		{
		}

		@Override
		public void write(int b) throws IOException
		{
			if(b == '\r')
			{
			}
			else if(b == '\n')
			{
				final String text = sb.toString();

				SwingUtilities.invokeLater(new Runnable(){

					public void run()
					{
						textArea.append(text + "\n");
					}

				});

				sb.setLength(0);
			}
			else
			{
				sb.append((char) b);
			}
		}
	}
}

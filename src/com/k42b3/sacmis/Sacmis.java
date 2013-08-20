/**
 * sacmis
 * An application wich writes an script from an textarea to a file and executes 
 * it with a selected processor. The result is displayed in another textfield.
 * 
 * Copyright (c) 2010-2013 Christoph Kappestein <k42b3.x@gmail.com>
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.ScrollPaneConstants;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.k42b3.sacmis.MenuBar.MenuBarActionListener;

/**
 * Sacmis
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/sacmis
 */
public class Sacmis extends JFrame
{
	public static String ver = "0.0.7 beta";

	private Logger logger;

	private int exitCode = 0;
	private boolean writeStdIn = false;
	private long timeout = 4000;
	private String inputCache = "input.cache";

	private JComboBox<String> args;
	private In in;
	private Out out;

	private ByteArrayOutputStream baos;
	private ByteArrayOutputStream baosErr;
	private ByteArrayInputStream bais;
	
	private Queue<String> commandQueue;
	
	public Sacmis() throws Exception
	{
		this.logger = Logger.getLogger("com.k42b3.sacmis");

		// settings
		this.setTitle("Sacmis (version: " + ver + ")");
		this.setLocation(100, 100);
		this.setSize(600, 500);
		this.setMinimumSize(this.getSize());

		// set toolbar
		this.setJMenuBar(this.buildMenuBar());
		
		// arguments
		JPanel panelNorth = new JPanel();
		panelNorth.setLayout(new BorderLayout());
		panelNorth.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

		String[] calls = {
			"php %file%",
			"python %file%"
		};

		this.args = new JComboBox<String>(calls);
		this.args.setEditable(true);
		
		panelNorth.add(this.args, BorderLayout.CENTER);

		this.add(panelNorth, BorderLayout.NORTH);
		
		
		// main panel
		JPanel panelMain = new JPanel();
		panelMain.setLayout(new BorderLayout());
		panelMain.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

		JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		
		
		this.in  = new In();
		
		RTextScrollPane scrIn = new RTextScrollPane(this.in);
		scrIn.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		scrIn.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrIn.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);	
		scrIn.setPreferredSize(new Dimension(600, 200));

		sp.add(scrIn);
		
		
		this.out = new Out();
		
		RTextScrollPane scrOut = new RTextScrollPane(this.out);
		scrOut.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		scrOut.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrOut.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);	

		sp.add(scrOut);


		panelMain.add(sp, BorderLayout.CENTER);

		this.add(panelMain, BorderLayout.CENTER);

		
		// toolbar	
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// load file
		this.loadFile();
	}
	
	private JMenuBar buildMenuBar()
	{
		MenuBar menuBar = new MenuBar();
		menuBar.setActionListener(new MenuBarActionListener(){

			public void onActionRun()
			{
				onRun();
			}

			public void onActionReset()
			{
				onReset();
			}

			public void onActionSave()
			{
				saveFile();
			}
			
			public void onActionLoad()
			{
				loadFile();
			}
			
			public void onActionAbout()
			{
				onAbout();
			}

			public void onActionExit()
			{
				onExit();
			}

		});

		return menuBar;
	}
	
	private void loadFile()
	{
		File fIn = new File(inputCache);
		FileInputStream fileIn;
	
		in.setText("");
		out.setText("");
	
		if(fIn.exists())
		{
			logger.info("Load file " + inputCache);

			try
			{
				fileIn = new FileInputStream(inputCache);

				BufferedReader brIn = new BufferedReader(new InputStreamReader(fileIn));

				String line = null;

				while((line = brIn.readLine()) != null)
				{
					in.append(line + "\n");
				}

				brIn.close();
			}
			catch(IOException e)
			{
				logger.warning(e.getMessage());

				out.setText(e.getMessage());
			}
		}
	}

	private void saveFile()
	{
		try
		{
			FileOutputStream fileOut;
			fileOut = new FileOutputStream(inputCache);

		    new PrintStream(fileOut).print(in.getText());

		    fileOut.close();
		}
		catch(Exception e)
		{
			logger.warning(e.getMessage());

			out.setText(e.getMessage());
		}
	}

	private void executeCommand(String input)
	{
		out.setText("");
		String cmd = commandQueue.poll();

		try
		{
			// replace input cache file
			cmd = cmd.replaceAll("%file%", inputCache);

			logger.info("Execute: " + cmd);

			// save file
			saveFile();

			// parse cmd
			CommandLine commandLine = CommandLine.parse(cmd);

			// set timeout
			ExecuteWatchdog watchdog = new ExecuteWatchdog(timeout);

			// create executor
			DefaultExecutor executor = new DefaultExecutor();
			executor.setExitValue(this.exitCode);

			this.baos = new ByteArrayOutputStream();
			this.baosErr = new ByteArrayOutputStream();

			if(this.writeStdIn)
			{
				this.bais = new ByteArrayInputStream(input.getBytes());

				executor.setStreamHandler(new PumpStreamHandler(this.baos, this.baosErr, this.bais));
			}
			else
			{
				executor.setStreamHandler(new PumpStreamHandler(this.baos, this.baosErr));
			}

			executor.setWatchdog(watchdog);
			executor.execute(commandLine, new ExecuteResultHandler(){

				public void onProcessComplete(int e) 
				{
					if(commandQueue.size() > 0)
					{
						executeCommand(baos.toString());
					}
					else
					{
						out.setText(baos.toString());
					}
				}

				public void onProcessFailed(ExecuteException e) 
				{
					out.setText(baosErr.toString());
				}

			});
		}
		catch(Exception e)
		{
			logger.warning(e.getMessage());

			out.setText(e.getMessage());
		}
	}

	private void onRun()
	{
		String cmd;
		if(args.getSelectedIndex() != -1)
		{
			cmd = args.getSelectedItem().toString();
		}
		else
		{
			cmd = args.getEditor().getItem().toString();
		}
		
		commandQueue = new LinkedList<String>();

		String[] commands = cmd.split(">");

		for(int i = 0; i < commands.length; i++)
		{
			commandQueue.add(commands[i]);
		}

		logger.info("Added " + commandQueue.size() + " commands to the queue");

		executeCommand(in.getText());
	}
	
	private void onReset()
	{
		in.setText("");
		out.setText("");

		loadFile();
	}
	
	private void onAbout()
	{
		StringBuilder text = new StringBuilder();
		text.append("Version: sacmis " + ver + "\n");
		text.append("Author: Christoph \"k42b3\" Kappestein" + "\n");
		text.append("Website: https://github.com/k42b3/sacmis" + "\n");
		text.append("License: GPLv3 <http://www.gnu.org/licenses/gpl-3.0.html>" + "\n");
		text.append("\n");
		text.append("An application wich writes an script from an textarea to a file and executes" + "\n");
		text.append("it with a selected processor. The result is displayed in another textfield." + "\n");

		out.setText(text.toString());
	}
	
	private void onExit()
	{
		saveFile();

		System.exit(0);
	}
}

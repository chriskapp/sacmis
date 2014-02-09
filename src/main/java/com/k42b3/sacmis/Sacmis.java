/**
 * sacmis
 * An application wich writes an script from an textarea to a file and executes 
 * it with a selected processor. The result is displayed in another textfield.
 * 
 * Copyright (c) 2010-2014 Christoph Kappestein <k42b3.x@gmail.com>
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
import java.awt.Container;
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

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.log4j.Logger;
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
	public static final String VERSION = "0.0.8";

	protected Logger logger = Logger.getLogger("com.k42b3.sacmis");

	protected int exitCode = 0;
	protected boolean writeStdIn = false;
	protected long timeout = 4000;
	protected String inputCache = "input-%num%.cache";

	protected JTabbedPane tp;

	protected ByteArrayOutputStream baos;
	protected ByteArrayOutputStream baosErr;
	protected ByteArrayInputStream bais;

	private Queue<String> commandQueue;
	
	public Sacmis() throws Exception
	{
		// settings
		this.setTitle("Sacmis (version: " + VERSION + ")");
		this.setLocation(100, 100);
		this.setSize(600, 500);
		this.setMinimumSize(this.getSize());

		// set toolbar
		this.setJMenuBar(this.buildMenuBar());

		// main panel
		tp = new JTabbedPane();
		tp.addChangeListener(new ChangeListener(){

			public void stateChanged(ChangeEvent e)
			{
				getActiveIn().requestFocusInWindow();
			}

		});

		this.add(tp, BorderLayout.CENTER);

		// toolbar	
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// add tab
		this.newTab();
	}

	protected JMenuBar buildMenuBar()
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

			public void onActionNewTab()
			{
				newTab();
			}

			public void onActionCloseTab()
			{
				closeTab();
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
	
	protected JComponent buildMainPanel()
	{
		// processor
		JPanel panelNorth = new JPanel();
		panelNorth.setLayout(new BorderLayout());
		panelNorth.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

		String[] calls = {
			"php %file%",
			"python %file%"
		};

		JComboBox<String> args = new JComboBox<String>(calls);
		args.setEditable(true);

		panelNorth.add(args, BorderLayout.CENTER);

		// textareas
		JPanel panelMain = new JPanel();
		panelMain.setLayout(new BorderLayout());
		panelMain.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

		JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		
		// in textarea
		RTextScrollPane scrIn = new RTextScrollPane(new InTextArea());
		scrIn.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		scrIn.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrIn.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);	
		scrIn.setPreferredSize(new Dimension(600, 200));

		sp.add(scrIn);
		
		// out textarea
		RTextScrollPane scrOut = new RTextScrollPane(new OutTextArea());
		scrOut.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		scrOut.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrOut.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);	

		sp.add(scrOut);

		panelMain.add(sp, BorderLayout.CENTER);
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(panelNorth, BorderLayout.NORTH);
		panel.add(panelMain, BorderLayout.CENTER);

		return panel;
	}
	
	protected Container getActivePanel()
	{
		return (Container) tp.getSelectedComponent();
	}

	protected JComboBox<String> getActiveArgs()
	{
		Container comp = (Container) this.getActivePanel().getComponent(0);
		JComboBox<String> args = (JComboBox<String>) comp.getComponent(0);

		return args;
	}

	protected InTextArea getActiveIn()
	{
		Container comp = (Container) this.getActivePanel().getComponent(1);
		JSplitPane sp = (JSplitPane) comp.getComponent(0);
		RTextScrollPane scp = (RTextScrollPane) sp.getComponent(1);
		JViewport vp = (JViewport) scp.getComponent(0);
		InTextArea in = (InTextArea) vp.getComponent(0);

		return in;
	}
	
	protected OutTextArea getActiveOut()
	{
		Container comp = (Container) this.getActivePanel().getComponent(1);
		JSplitPane sp = (JSplitPane) comp.getComponent(0);
		RTextScrollPane scp = (RTextScrollPane) sp.getComponent(2);
		JViewport vp = (JViewport) scp.getComponent(0);
		OutTextArea out = (OutTextArea) vp.getComponent(0);

		return out;
	}

	protected String getInputFile()
	{
		return inputCache.replaceAll("%num%", "" + tp.getSelectedIndex());
	}

	protected void loadFile()
	{
		File fIn = new File(this.getInputFile());
		FileInputStream fileIn;
	
		this.getActiveIn().setText("");
		this.getActiveOut().setText("");
	
		if(fIn.exists())
		{
			logger.info("Load file " + fIn.getName());

			try
			{
				fileIn = new FileInputStream(fIn);
				BufferedReader brIn = new BufferedReader(new InputStreamReader(fileIn));
				String line = null;

				while((line = brIn.readLine()) != null)
				{
					this.getActiveIn().append(line + "\n");
				}

				brIn.close();
			}
			catch(IOException e)
			{
				logger.error(e.getMessage(), e);

				this.getActiveOut().setText(e.getMessage());
			}
		}
	}

	protected void saveFile()
	{
		try
		{
			FileOutputStream fileOut;
			fileOut = new FileOutputStream(this.getInputFile());

		    new PrintStream(fileOut).print(this.getActiveIn().getText());

		    fileOut.close();
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(), e);

			this.getActiveOut().setText(e.getMessage());
		}
	}

	protected void newTab()
	{
		tp.addTab("Tab-" + (tp.getTabCount()), this.buildMainPanel());
		tp.setSelectedIndex(tp.getTabCount() - 1);

		// load file
		this.loadFile();
		
		// focus
		getActiveIn().requestFocusInWindow();
	}

	protected void closeTab()
	{
		tp.remove(tp.getSelectedIndex());
	}

	protected void executeCommand(String input)
	{
		this.getActiveOut().setText("");
		String cmd = commandQueue.poll();

		try
		{
			// replace input cache file
			cmd = cmd.replaceAll("%file%", this.getInputFile());

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
						getActiveOut().setText(baos.toString());
					}
				}

				public void onProcessFailed(ExecuteException e) 
				{
					getActiveOut().setText(baosErr.toString());
				}

			});
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(), e);

			this.getActiveOut().setText(e.getMessage());
		}
	}

	protected void onRun()
	{
		String cmd;
		
		if(this.getActiveArgs().getSelectedIndex() != -1)
		{
			cmd = this.getActiveArgs().getSelectedItem().toString();
		}
		else
		{
			cmd = this.getActiveArgs().getEditor().getItem().toString();
		}
		
		commandQueue = new LinkedList<String>();

		String[] commands = cmd.split(">");

		for(int i = 0; i < commands.length; i++)
		{
			commandQueue.add(commands[i].trim());
		}

		logger.info("Added " + commandQueue.size() + " commands to the queue");

		executeCommand(this.getActiveIn().getText());
	}
	
	protected void onReset()
	{
		this.getActiveIn().setText("");
		this.getActiveOut().setText("");

		loadFile();
	}
	
	protected void onAbout()
	{
		StringBuilder text = new StringBuilder();
		text.append("Version: sacmis " + VERSION + "\n");
		text.append("Author: Christoph \"k42b3\" Kappestein" + "\n");
		text.append("Website: https://github.com/k42b3/sacmis" + "\n");
		text.append("License: GPLv3 <http://www.gnu.org/licenses/gpl-3.0.html>" + "\n");
		text.append("\n");
		text.append("An application wich writes an script from an textarea to a file and executes" + "\n");
		text.append("it with a selected processor. The result is displayed in another textfield." + "\n");

		this.getActiveOut().setText(text.toString());
	}
	
	protected void onExit()
	{
		saveFile();

		System.exit(0);
	}
}

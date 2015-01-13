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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 * MenuBar
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/sacmis
 */
public class MenuBar extends JMenuBar
{
	protected MenuBarActionListener listener;

	public MenuBar()
	{
		super();

		this.buildAction();
		this.buildProcess();
		this.buildTemplate();
	}

	public void setActionListener(MenuBarActionListener listener)
	{
		this.listener = listener;
	}

	protected void buildAction()
	{
		JMenu menu = new JMenu("Action");

		JMenuItem itemRun = new JMenuItem("Run");
		itemRun.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
		itemRun.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onActionRun();
			}

		});
		menu.add(itemRun);

		JMenuItem itemReset = new JMenuItem("Reset");
		itemReset.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		itemReset.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onActionReset();
			}

		});
		menu.add(itemReset);

		JMenuItem itemSave = new JMenuItem("Save");
		itemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		itemSave.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onActionSave();
			}

		});
		menu.add(itemSave);
		
		JMenuItem itemLoad = new JMenuItem("Load");
		itemLoad.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
		itemLoad.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onActionLoad();
			}

		});
		menu.add(itemLoad);
		
		JMenuItem itemNewTab = new JMenuItem("New Tab");
		itemNewTab.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
		itemNewTab.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onActionNewTab();
			}

		});
		menu.add(itemNewTab);

		JMenuItem itemCloseTab = new JMenuItem("Close Tab");
		itemCloseTab.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
		itemCloseTab.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onActionCloseTab();
			}

		});
		menu.add(itemCloseTab);
		
		JMenuItem itemAbout = new JMenuItem("About");
		itemAbout.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onActionAbout();
			}

		});
		menu.add(itemAbout);
		
		JMenuItem itemExit = new JMenuItem("Exit");
		itemExit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onActionExit();
			}

		});
		menu.add(itemExit);
		
		this.add(menu);
	}

	protected void buildProcess()
	{
		JMenu menu = new JMenu("Process");

		JMenu itemComposer = new JMenu("Composer");
		this.buildComposer(itemComposer);

		menu.add(itemComposer);

		JMenuItem itemTest = new JMenuItem("PHPUnit");
		itemTest.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
		itemTest.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onPhpUnitTest();
			}

		});
		menu.add(itemTest);

		JMenuItem itemOpcode = new JMenuItem("Opcodes");
		itemOpcode.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.CTRL_MASK));
		itemOpcode.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onPhpOpcode();
			}

		});
		menu.add(itemOpcode);

		this.add(menu);
	}

	protected void buildComposer(JMenu menu)
	{
		JMenuItem itemOpen = new JMenuItem("Open");
		itemOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		itemOpen.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onComposerOpen();
			}

		});
		menu.add(itemOpen);

		JMenuItem itemUpdate = new JMenuItem("Update");
		itemUpdate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.CTRL_MASK));
		itemUpdate.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onComposerUpdate();
			}

		});
		menu.add(itemUpdate);

		JMenuItem itemRequire = new JMenuItem("Require");
		itemRequire.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
		itemRequire.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onComposerRequire();
			}

		});
		menu.add(itemRequire);
	}

	protected void buildTemplate()
	{
		TemplateManager manager = new TemplateManager();
		ArrayList<String> templates = null;

		try
		{
			templates = manager.getTemplates();
		}
		catch(ParserConfigurationException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch(SAXException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch(IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if(templates != null && templates.size() > 0)
		{
			JMenu menu = new JMenu("Template");

			for(int i = 0; i < templates.size(); i++)
			{
				JMenuItem itemTemplate = new JMenuItem(templates.get(i));
				itemTemplate.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent e) 
					{
						JMenuItem item = (JMenuItem) e.getSource();
						listener.onTemplateLoad(item.getText());
					}

				});
				menu.add(itemTemplate);
			}

			this.add(menu);
		}
	}

	public interface MenuBarActionListener
	{
		public void onActionRun();
		public void onActionReset();
		public void onActionSave();
		public void onActionLoad();
		public void onActionNewTab();
		public void onActionCloseTab();
		public void onActionAbout();
		public void onActionExit();
		public void onComposerOpen();
		public void onComposerUpdate();
		public void onComposerRequire();
		public void onPhpUnitTest();
		public void onPhpOpcode();
		public void onTemplateLoad(String name);
	}
}

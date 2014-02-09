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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

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

		buildAction();
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
	}
}

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

package xlogo.gui.welcome.settings.tabs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import xlogo.AppSettings;
import xlogo.AppSettings.AppProperty;
import xlogo.gui.components.X4SComponent;
import xlogo.interfaces.Observable.PropertyChangeListener;
import xlogo.messages.async.dialog.DialogMessenger;
import xlogo.storage.Storable;
import xlogo.storage.WSManager;
import xlogo.storage.global.GlobalConfig;
import xlogo.storage.workspace.WorkspaceConfig;

public abstract class AbstractWorkspacePanel extends X4SComponent{

	private PropertyChangeListener enterWorkspaceListener;
	private PropertyChangeListener workspaceListChangeListener;
		
	protected abstract JComboBox getWorkspaceSelection();

	private boolean ignoreGuiEvents = false;
	
	@Override
	protected void initEventListeners()
	{
		getWorkspaceSelection().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (ignoreGuiEvents)
					return;
				new Thread(new Runnable() {
					public void run() {
						String wsName = (String) getWorkspaceSelection().getSelectedItem();
						if (wsName != null){
							enterWorkspace(wsName);
						}
					}
				}).run();
			}
		});
		
		enterWorkspaceListener = new PropertyChangeListener(){
			
			@Override
			public void propertyChanged() {
				ignoreGuiEvents = true;
				if (WSManager.getWorkspaceConfig() != null){
					enableComponents();
				} else {
					disableComponents();
				}
				selectCurrentWorkspace();
				ignoreGuiEvents = false;
			}
		};
		
		workspaceListChangeListener = new PropertyChangeListener(){
			
			@Override
			public void propertyChanged() {
				ignoreGuiEvents = true;
				populateWorkspaceList();
				ignoreGuiEvents = false;
			}
		};
		AppSettings as = AppSettings.getInstance();		
		as.addPropertyChangeListener(AppProperty.WORKSPACE_LIST, workspaceListChangeListener);
		as.addPropertyChangeListener(AppProperty.WORKSPACE, enterWorkspaceListener);
	}

	@Override
	public void stopEventListeners()
	{
		super.stopEventListeners();
		AppSettings as = AppSettings.getInstance();		
		as.removePropertyChangeListener(AppProperty.WORKSPACE_LIST, workspaceListChangeListener);
		as.removePropertyChangeListener(AppProperty.WORKSPACE, enterWorkspaceListener);	
	}
	
	protected abstract void setValues();
	
	protected abstract void enableComponents();
	
	protected abstract void disableComponents();

	protected void populateWorkspaceList() {
		GlobalConfig gc = WSManager.getGlobalConfig();
		String[] workspaces = gc.getAllWorkspaces();
		getWorkspaceSelection().setModel(new DefaultComboBoxModel(workspaces));
		selectCurrentWorkspace();
	}
	
	protected void selectCurrentWorkspace(){
		GlobalConfig gc = WSManager.getGlobalConfig();
		String lastUsed = gc.getLastUsedWorkspace();
		getWorkspaceSelection().setSelectedItem(lastUsed);
		setValues();
	}

	protected void deleteWorkspace() {
		WSManager wsManager = WSManager.getInstance();
		GlobalConfig gc = wsManager.getGlobalConfigInstance();
	
		String wsName = (String) getWorkspaceSelection().getSelectedItem();
		String wsLocation = gc.getWorkspaceDirectory(wsName).toString();
		String message = 
				translate("ws.settings.want.delete.dir.1") 
				+ wsLocation 
				+ translate("ws.settings.want.delete.dir.2");
		
		boolean ans = getUserYesOrNo(message, translate("ws.settings.delete.from.fs"));
		
		wsManager.deleteWorkspace(wsName, ans);
		
		populateWorkspaceList();
	}

	protected void importWorkspace() {
		File dir = getUserSelectedDirectory();
		if (dir == null)
			return;
		
		WSManager wsManager = WSManager.getInstance();
		if (!WSManager.isWorkspaceDirectory(dir))
		{
			DialogMessenger.getInstance().dispatchMessage(
					translate("i.am.sorry"),
					dir.toString() + translate("ws.settings.not.legal.ws.dir"));
			return;
		}
		String newName = dir.getName();
		
		if (dir.equals(WSManager.getGlobalConfig().getWorkspaceDirectory(newName)))
		{
			DialogMessenger.getInstance().dispatchMessage("The workspace was already in the list.");
			return;
		}
		
		File newDir = new File(dir.toString());
	
		if (WSManager.getGlobalConfig().existsWorkspace(newName) || !Storable.checkLegalName(newName))
		{
			do
			{
				String msg = WSManager.getGlobalConfig().existsWorkspace(newName) ? 
						"The workspace name " + newName + " already exists. Please choose a new name"
						: newDir.exists() ? newDir.toString() + " already exists. Please choose a new name."
						: "The chosen name contains illegal characters.";
				
				newName = getUserText(msg, "Name Conflict");
				if (newName == null)
					return;
				
				newDir = new File(dir.getParent() + File.separator + newName);
			}
			while(WSManager.getGlobalConfig().existsWorkspace(newName) 
					|| !Storable.checkLegalName(newName) 
					|| newDir.exists());
		}
		
		if(!newDir.equals(dir))
			dir.renameTo(newDir);
		
		wsManager.importWorkspace(newDir, newName);
		populateWorkspaceList();
	}

	protected void enterWorkspace(String wsName) {
		// enter workspace
		WSManager.getInstance().enterWorkspace(wsName);
		WorkspaceConfig wc = WSManager.getInstance().getWorkspaceConfigInstance();
		if (wc == null)
		{
			disableComponents();
			DialogMessenger.getInstance().dispatchMessage(
					translate("ws.error.title"),
					translate("ws.settings.could.not.enter.wp"));
			return;
		}
		enableComponents();
		setValues();		
	}

	protected void addWorkspace() {
		WorkspaceCreationPanel wscPanel = new WorkspaceCreationPanel();
		
		int option = JOptionPane.showConfirmDialog(
	            getComponent(),
	            wscPanel.getComponent(),
	            translate("ws.settings.create.new.wp"),
	            JOptionPane.OK_CANCEL_OPTION,
	            JOptionPane.PLAIN_MESSAGE);
	
		if (option != JOptionPane.OK_OPTION)
			return;
		
		String wsName = wscPanel.wsNameField.getText();
		String location = wscPanel.locationField.getText();
		File dir = new File(location);
		
		GlobalConfig gc = WSManager.getGlobalConfig();
		
		// Make sure that the specified workspace name is non-empty and that it does not exist already
		if (wsName.length() == 0){
			DialogMessenger.getInstance().dispatchMessage(
					translate("i.am.sorry"),
					translate("ws.settings.wp.name.non.empty"));
			return;
		}
		if (gc.existsWorkspace(wsName)){
			DialogMessenger.getInstance().dispatchMessage(
					translate("i.am.sorry"),
					translate("ws.settings.wp.exists.already"));
			return;
		}
		
		// Make sure dir is an existing directory
		if(!dir.exists()){
			if (!dir.mkdirs()){
				DialogMessenger.getInstance().dispatchMessage(
						translate("ws.error.title"),
						translate("ws.settings.could.not.create.directory"));
				return;
			}
		}else if (!dir.isDirectory()){
				DialogMessenger.getInstance().dispatchMessage(
					translate("ws.error.title"),
					translate("ws.settings.need.dir.not.file"));
				return;
		}
		// dir exists & wsName doesn't exist yet => fine to create WS now
		//try {
			WSManager.getInstance().createWorkspace(dir, wsName);
			populateWorkspaceList();
		/*} catch (IOException e) {
			DialogMessenger.getInstance().dispatchMessage(
				translate("ws.error.title"),
				translate("ws.settings.could.not.create.ws"));
			return;
		}*/
			
	}

}

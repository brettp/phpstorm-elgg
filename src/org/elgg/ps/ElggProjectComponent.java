/*
 * Copyright (c) 2016 Brett Profitt.
 *
 * This file is part of the Elgg support plugin for JetBrains PhpStorm IDE.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.elgg.ps;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.HyperlinkEvent;

public class ElggProjectComponent implements ProjectComponent {
	private Project project;

	public ElggProjectComponent(Project project) {
		this.project = project;
	}

	public void initComponent() {
		//System.out.println("initComponent");
	}

	public void disposeComponent() {
		//System.out.println("disposeComponent");
	}

	@NotNull
	public String getComponentName() {
		return "ElggProjectComponent";
	}


	public void projectOpened() {
		if (Settings.getInstance(project).dismissEnableNotification) {
			return;
		}

		if (VfsUtil.findRelativeFile(this.project.getBaseDir(), "engine", "start.php") != null ||
		    VfsUtil.findRelativeFile(this.project.getBaseDir(), "engine", "classes", "Elgg", "Application.php") != null ||

		    // composer install
		    VfsUtil.findRelativeFile(this.project.getBaseDir(), "vendor", "elgg", "elgg") != null) {
			notifyEnableMessage();
		}
	}

	public void projectClosed() {

	}

	public void notifyEnableMessage() {
		Project project = this.project;

		Notification notification = new Notification("Elgg Plugin",
		                                             "Elgg Plugin",
		                                             "Elgg project found. <br><a href=\"autoconf\">Auto configure</a>, view <a href=\"config\">settings</a> or <a href=\"ignore\">ignore</a>?",
		                                             NotificationType.INFORMATION,
		                                             new NotificationListener() {
			                                             @Override
			                                             public void hyperlinkUpdate(@NotNull Notification notification,
			                                                                         @NotNull HyperlinkEvent event) {

				                                             // handle html click events
				                                             switch (event.getDescription()) {
					                                             case "autoconf":
						                                             autoConf(project);
						                                             break;

					                                             case "config":
						                                             SettingsForm.show(project);
					                                             case "ignore":
						                                             Settings.getInstance(project).dismissEnableNotification = true;
						                                             break;
				                                             }

				                                             notification.expire();
			                                             }

		                                             });

		Notifications.Bus.notify(notification, project);
	}

	public void autoConf(Project project) {
		Settings s = Settings.getInstance(project);
		String error = "";
		// find root
		VirtualFile start = VfsUtil.findRelativeFile(this.project.getBaseDir(), "engine", "start.php");
		VirtualFile app = VfsUtil.findRelativeFile(this.project.getBaseDir(), "engine", "classes", "Elgg", "Application.php");
		VirtualFile composer = VfsUtil.findRelativeFile(this.project.getBaseDir(), "vendor", "elgg", "elgg");
		VirtualFile installDir = null;

		if (start != null) {
			// elgg/engine/start.php
			installDir = start.getParent().getParent();
		}
		else if (app != null) {
			// elgg/engine/classes/Elgg/Application.php
			installDir = app.getParent().getParent().getParent().getParent();
		}
		else if (composer != null) {
			// vendor/elgg/elgg/
			installDir = composer;
		}
		else {
			error = "installation directory";
		}

		// find mods
		if (installDir != null) {
			s.elggDir = installDir.getPath();

			VirtualFile rootMods = VfsUtil.findRelativeFile(this.project.getBaseDir(), "mod");
			VirtualFile installedMods = VfsUtil.findRelativeFile(installDir, "mod");

			if (rootMods != null) {
				s.modDir = rootMods.getPath();
			}
			else if (installedMods != null) {
				s.modDir = installedMods.getPath();
			}
			else {
				error = "mod directory";
			}
		}

		if (error != "") {
			Notification notice = new Notification("Elgg Plugin",
			                                       "Elgg Plugin",
			                                       "Error configuring Elgg " + error + ". <a href='settings'>Configure manually</a>.",
			                                       NotificationType.ERROR,
			                                       new NotificationListener() {
				                                       @Override
				                                       public void hyperlinkUpdate(@NotNull Notification notification,
				                                                                   @NotNull HyperlinkEvent event) {
					                                       if ("settings".equals(event.getDescription())) {
						                                       SettingsForm.show(project);
					                                       }

					                                       notification.expire();
				                                       }
			                                       });

			Notifications.Bus.notify(notice, project);
		}
		else {
			Notification notice = new Notification("Elgg Plugin", "Elgg Plugin", "Plugin configured: <br>" +
			                                                                     "Elgg dir: " + s.elggDir + "<br>" +
			                                                                     "Mod dir: " + s.modDir + "<br><br>" +
			                                                                     "<a href='settings'>View settings</a>",

			                                       NotificationType.INFORMATION, new NotificationListener() {
				@Override
				public void hyperlinkUpdate(@NotNull Notification notification, @NotNull HyperlinkEvent event) {
					if ("settings".equals(event.getDescription())) {
						SettingsForm.show(project);
					}

					notification.expire();
				}
			});

			Notifications.Bus.notify(notice, project);
			Settings.getInstance(project).dismissEnableNotification = true;
		}
	}
}


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

import com.intellij.ide.actions.ShowSettingsUtilImpl;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class SettingsForm implements Configurable {
	private Project project;
	private JPanel panel1;

	private TextFieldWithBrowseButton elggDir;
	private TextFieldWithBrowseButton modDir;
	private TextFieldWithBrowseButton dataDir;

	public SettingsForm(@NotNull final Project project) {
		this.project = project;
	}

	@Nls
	@Override
	public String getDisplayName() {
		return "Elgg Plugin";
	}

	@Nullable
	@Override
	public String getHelpTopic() {
		return null;
	}

	public JComponent createComponent() {
		elggDir.getButton()
		       .addMouseListener(createPathButtonMouseListener(elggDir.getTextField(),
		                                                       FileChooserDescriptorFactory.createSingleFolderDescriptor()));

		modDir.getButton()
		      .addMouseListener(createPathButtonMouseListener(modDir.getTextField(),
		                                                      FileChooserDescriptorFactory.createSingleFolderDescriptor()));

		dataDir.getButton()
		       .addMouseListener(createPathButtonMouseListener(dataDir.getTextField(),
		                                                       FileChooserDescriptorFactory.createSingleFolderDescriptor()));

		return (JComponent)panel1;
	}

	@Override
	public boolean isModified() {
		return !elggDir.getText().equals(getSettings().elggDir) ||
		       !modDir.getText().equals(getSettings().modDir) ||
		       !dataDir.getText().equals(getSettings().dataDir);
	}

	@Override
	public void apply() throws ConfigurationException {
		getSettings().elggDir = elggDir.getText();
		getSettings().modDir = modDir.getText();
		getSettings().dataDir = dataDir.getText();
	}

	@Override
	public void reset() {
		updateUIFromSettings();
	}

	@Override
	public void disposeUIResources() {
	}

	private Settings getSettings() {
		return Settings.getInstance(project);
	}

	private void updateUIFromSettings() {
		elggDir.setText(getSettings().elggDir);
		modDir.setText(getSettings().modDir);
		dataDir.setText(getSettings().dataDir);
	}

	private MouseListener createPathButtonMouseListener(final JTextField textField, final FileChooserDescriptor fileChooserDescriptor) {
		return new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
			}

			@Override
			public void mousePressed(MouseEvent mouseEvent) {
				VirtualFile projectDirectory = project.getBaseDir();
				VirtualFile selectedFile =
					FileChooser.chooseFile(fileChooserDescriptor, project, VfsUtil.findRelativeFile(textField.getText(), projectDirectory));

				if (null == selectedFile) {
					return;
				}
				;
				textField.setText(selectedFile.getPath());
			}

			@Override
			public void mouseReleased(MouseEvent mouseEvent) {
			}

			@Override
			public void mouseEntered(MouseEvent mouseEvent) {
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent) {
			}
		};
	}

	public static void show(@NotNull Project project) {
		ShowSettingsUtilImpl.showSettingsDialog(project, "Elgg.SettingsForm", null);
	}
}

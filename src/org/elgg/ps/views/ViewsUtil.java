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
package org.elgg.ps.views;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileVisitor;
import org.elgg.ps.Util;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.elgg.ps.Util.viewsPath;

public class ViewsUtil {
	/**
	 * A list of function name => view argument index
	 */
	public static Map<String, List<Integer>> viewFuncs = new HashMap<String, List<Integer>>() {{
		put("elgg_view", new ArrayList<Integer>() {{
			add(0);
		}});
		put("elgg_extend_view", new ArrayList<Integer>() {{
			add(0);
			add(1);
		}});
		put("elgg_unextend_view", new ArrayList<Integer>() {{
			add(0);
			add(1);
		}});
		put("elgg_view_exists", new ArrayList<Integer>() {{
			add(0);
		}});
		put("elgg_view_layout", new ArrayList<Integer>() {{
			add(0);
		}});
		put("elgg_view_form", new ArrayList<Integer>() {{
			add(0);
		}});

		// 2.X
		put("elgg_view_resource", new ArrayList<Integer>() {{
			add(0);
		}});
		put("elgg_view_input", new ArrayList<Integer>() {{
			add(0);
		}});
	}};

	public static Map<String, String> viewPrefixes = new HashMap<String, String>() {{
		put("elgg_view_layout", "page/layouts/");
		put("elgg_view_form", "forms/");
		put("elgg_view_resource", "resources/");
		put("elgg_view_input", "input/");
	}};

	public static List<String> getAllViews(Project project) {
		List<String> views = new ArrayList<>();
		visitAllViewFiles(project, views, false);

		return views;
	}

	public static  List<VirtualFile> getAllViewFiles(Project project) {
		List<VirtualFile> views = new ArrayList<>();
		visitAllViewFiles(project, views, true);

		return views;
	}

	public static  void visitAllViewFiles(Project project, List viewFiles, Boolean asFile) {
		//final List<VirtualFile> viewFiles = new ArrayList<>();
		final VirtualFile baseDir = project.getBaseDir();
		final List<VirtualFile> viewDirs = new ArrayList<>();

		// add core views dir
		VirtualFile rootViews = baseDir.findFileByRelativePath(viewsPath);
		viewDirs.add(rootViews);

		// add mod views dirs
		for (VirtualFile mod : Util.getMods(project)) {
			VirtualFile viewDir = mod.findFileByRelativePath(viewsPath);

			if (viewDir == null) {
				continue;
			}

			viewDirs.add(viewDir);
		}

		// go through each view dir finding view files
		for (VirtualFile viewDir : viewDirs) {
			if (viewDir == null) {
				continue;
			}

			for (VirtualFile viewType : viewDir.getChildren()) {
				VfsUtil.visitChildrenRecursively(viewType, new VirtualFileVisitor() {
					@Override
					public boolean visitFile(@NotNull VirtualFile file) {
						if (file != null && !file.isDirectory()) {
							if (asFile) {
								viewFiles.add(file);
							} else {
								viewFiles.add(file.getPath().replace(viewType.getPath() + "/", "")
								                  // extensions other than php are valid
								                  .replace(".php", ""));
							}
						}

						return super.visitFile(file);
					}
				});
			}
		}
	}
}

/*
 * Copyright (c) 2016 Brett Profitt.
 *
 * This file is part of the Elgg support plugin for JetBrains PhpStorm IDE.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General public static License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General public static License for more details.
 *
 * You should have received a copy of the GNU General public static License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.elgg.ps;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileVisitor;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.Function;
import com.jetbrains.php.lang.psi.elements.FunctionReference;
import com.jetbrains.php.lang.psi.elements.ParameterList;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import com.jetbrains.php.lang.psi.elements.impl.StringLiteralExpressionImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Util {
	public static boolean pluginEnabled = false;

	public static String dataDir = "";
	public static String viewsPath = "views";
	public static String modsPaths = "mod";
	public static String actionsPaths = "actions";

	public static Boolean inFuncParam(PsiElement e, String funcName) {
		PsiElement parent = e;

		// first need to find param list, then need to find func
		while (parent instanceof PsiElement) {
			if (parent instanceof ParameterList) {
				FunctionReference f = getFuncRef(e);
				if (f != null) {
					return (((Function)f.resolve()).getName().equals(funcName));
				}
			}

			parent = parent.getParent();
		}

		return false;
	}

	@Nullable
	public static FunctionReference getFuncRef(PsiElement e) {
		PsiElement parent = e;
		while (parent instanceof PsiElement) {
			if (parent instanceof FunctionReference) {
				return (FunctionReference)parent;
			}

			parent = parent.getParent();
		}

		return null;
	}

	@Nullable
	public static PsiElement[] getFuncParams(PsiElement e) {
		FunctionReference f = getFuncRef(e);

		if (f instanceof FunctionReference) {
			return f.getParameters();
		}

		return null;
	}


	@Nullable
	public static Integer getParameterIndex(PsiElement param) {
		int i;
		PsiElement[] params = getFuncParams(param);

		if (params == null) {
			return null;
		}

		String curParam = param.getText();
		for (i = 0; i < params.length; i++) {
			if (!(params[i] instanceof StringLiteralExpression)) {
				continue;
			}
			if (curParam.equals(((StringLiteralExpressionImpl)params[i]).getContents())) {
				return i;
			}
		}

		return null;
	}


	/**
	 * Get an array of all mod dirs.
	 * <p>
	 * Because mods can be stored about anywhere, this finds dirs with a manifest.xml file in them.
	 *
	 * @param project
	 * @return
	 */
	public static List<VirtualFile> getMods(Project project) {
		final List<VirtualFile> mods = new ArrayList<>();
		final Settings s = Settings.getInstance(project);

		// only look at the mods dir for now.
		// eventually will need to check composer
		final VirtualFile modDir = VfsUtil.findRelativeFile(s.modDir, project.getBaseDir());

		if (modDir == null) {
			return mods;
		}

		for (VirtualFile mod : modDir.getChildren()) {
			if (!mod.isDirectory()) {
				continue;
			}

			// has to have manifest.xml and start.php files
			if (mod.findChild("manifest.xml") == null || VfsUtil.findRelativeFile(mod, "start.php") == null) {
				continue;
			}

			mods.add(mod);
		}

		return mods;
	}

	/**
	 * Visit all files in root and mods with a common structure (like actions and views)
	 *
	 * @param project
	 * @param files
	 * @param suffix
	 * @param asFile
	 */
	public static void visitCommonFiles(Project project, List files) {
		visitCommonFiles(project, files, null, null);
	}

	public static void visitCommonFiles(Project project, String suffix, VirtualFileVisitor visitor) {
		visitCommonFiles(project, null, suffix, visitor);
	}

	public static void visitCommonFiles(Project project, VirtualFileVisitor visitor) {
		visitCommonFiles(project, null, null, visitor);
	}

	public static void visitCommonFiles(Project project, List files, String suffix) {
		visitCommonFiles(project, files, suffix, null);
	}

	protected static void visitCommonFiles(Project project, @Nullable List files, @Nullable String suffix, @Nullable VirtualFileVisitor visitor) {
		final Settings s = Settings.getInstance(project);
		final VirtualFile projectDir = project.getBaseDir();

		final List<VirtualFile> dirs = new ArrayList<>();

		// add core views dir
		final VirtualFile rootFiles = VfsUtil.findRelativeFile(s.elggDir + "/" + suffix, projectDir);
		dirs.add(rootFiles);

		// add mod views dirs
		for (VirtualFile mod : getMods(project)) {
			VirtualFile modDir = mod.findFileByRelativePath(suffix);

			if (modDir == null) {
				continue;
			}

			dirs.add(modDir);
		}


		if (visitor == null) {
			visitor = new VirtualFileVisitor() {
				@Override
				public boolean visitFile(@NotNull VirtualFile file) {
					if (file != null && !file.isDirectory() && files != null) {
						files.add(file);
					}

					return super.visitFile(file);
				}
			};
		}

		// go through each view dir finding view files
		for (VirtualFile dir : dirs) {
			if (dir == null) {
				continue;
			}

			for (VirtualFile viewType : dir.getChildren()) {
				VfsUtil.visitChildrenRecursively(viewType, visitor);
			}
		}
	}
}
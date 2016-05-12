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

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.jetbrains.php.lang.psi.elements.FunctionReference;
import org.elgg.ps.Util;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static org.elgg.ps.views.ViewsUtil.isInViewParam;
import static org.elgg.ps.views.ViewsUtil.viewPrefixes;

public class ViewsGoToDeclarationHandler implements GotoDeclarationHandler {

	@Nullable
	@Override
	public PsiElement[] getGotoDeclarationTargets(PsiElement e, int i, Editor editor) {
		Collection<PsiFile> psiFiles = new HashSet<PsiFile>();
		List<VirtualFile> viewFiles = ViewsUtil.getAllViewFiles(e.getProject());
		final String viewText;

		// prepend any stripped out views for funcs like elgg_view_input()
		FunctionReference function = Util.getFuncRef(e);

		if (function == null) {
			return psiFiles.toArray(new PsiFile[psiFiles.size()]);
		}

		if (!isInViewParam(e)) {
			return psiFiles.toArray(new PsiFile[psiFiles.size()]);
		}

		if (viewPrefixes.containsKey(function.getName())) {
			String add = viewPrefixes.get(function.getName());
			 viewText = add + e.getText();
		} else {
			viewText = e.getText();
		}

		viewFiles.removeIf(file -> !(file.getPath().endsWith(viewText) || file.getPath().endsWith(viewText + ".php")));

		for (VirtualFile file : viewFiles) {
			PsiFile psiFile = PsiManager.getInstance(e.getProject()).findFile(file);
			if (psiFile instanceof PsiFile) {
				psiFiles.add(psiFile);
			}
		}

		return psiFiles.toArray(new PsiFile[psiFiles.size()]);
	}


	@Nullable
	@Override
	public String getActionText(DataContext dataContext) {
		return null;
	}
}
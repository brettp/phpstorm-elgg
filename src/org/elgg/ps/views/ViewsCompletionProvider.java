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

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.Function;
import org.elgg.ps.Util;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.elgg.ps.Util.viewsPath;

public class ViewsCompletionProvider extends CompletionProvider<CompletionParameters> {
	/**
	 * A list of function name => view argument index
	 */
	protected Map<String, List<Integer>> viewFuncs = new HashMap<String, List<Integer>>() {{
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
	}};

	private static final InsertHandler<LookupElement> INSERT_HANDLER = new InsertHandler<LookupElement>() {
		@Override
		public void handleInsert(InsertionContext context, LookupElement item) {
			context.getEditor().getCaretModel().moveCaretRelatively(1, 0, false, false, true);
		}
	};

	protected boolean isViewFunc(@NotNull Function func) {
		return viewFuncs.containsKey(func.getName());
	}

	protected boolean isInViewParam(Function func, PsiElement param) {
		Integer i = Util.getParameterIndex(param);

		return viewFuncs.get(func.getName()).contains(i);
	}

	@Override
	public void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
		final Project project = parameters.getPosition().getProject();

		// I'm sure there's a way to do this within the capture, but haven't figured it out yet
		PsiElement func = parameters.getPosition().getParent().getParent().getParent();
		if (!(func instanceof PsiElement)) {
			return;
		}

		PsiReference funcRef = func.getReference();
		if (!(funcRef instanceof PsiReference)) {
			return;
		}

		PsiElement resolvedReference = func.getReference().resolve();
		if (!(resolvedReference instanceof Function)) {
			return;
		}

		Function function = (Function)resolvedReference;
		if (!isViewFunc(function)) {
			return;
		}

		if (!isInViewParam(function, parameters.getPosition())) {
			return;
		}

		List<String> views = getAllViews(project);

		for (String view : views) {
			LookupElementBuilder builder = LookupElementBuilder.create(view)
			                                                   .withCaseSensitivity(false)
			                                                   .withPresentableText(view)
			                                                   .withInsertHandler(INSERT_HANDLER);
			result.addElement(builder);
		}
	}

	public List<String> getAllViews(Project project) {
		final List<String> viewFiles = new ArrayList<>();
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

							viewFiles.add(file.getPath().replace(viewType.getPath() + "/", "")
							                  // extensions other than php are valid
							                  .replace(".php", ""));
						}
						return super.visitFile(file);
					}
				});
			}
		}

		return viewFiles;
	}
}
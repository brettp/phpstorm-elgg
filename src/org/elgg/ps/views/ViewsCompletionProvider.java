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

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.FunctionReference;
import org.elgg.ps.Util;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static org.elgg.ps.views.ViewsUtil.*;

public class ViewsCompletionProvider extends CompletionProvider<CompletionParameters> {
	@Override
	public void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
		final Project project = parameters.getPosition().getProject();
		FunctionReference function = Util.getFuncRef(parameters.getPosition());

		if (!isInViewParam(parameters.getPosition())) {
			return;
		}

		List<String> views = getAllViews(project);

		if (viewPrefixes.containsKey(function.getName())) {
			String strip = viewPrefixes.get(function.getName());

			views.removeIf(s -> !s.contains(strip));

			// @todo can you use an interator?
			List<String> newViews = new ArrayList<>();
			for (String view : views) {
				newViews.add(view.replaceFirst(strip, ""));
			}

			views = newViews;
		}

		for (String view : views) {
			LookupElementBuilder builder = LookupElementBuilder.create(view).withCaseSensitivity(false).withPresentableText(view);
			result.addElement(builder);
		}
	}
}
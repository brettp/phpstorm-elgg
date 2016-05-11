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


import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.patterns.PhpPatterns;
import org.jetbrains.annotations.NotNull;

public class ViewsCompletionContributor extends CompletionContributor {
	public ViewsCompletionContributor() {
		extend(
		 CompletionType.BASIC,
		 getViewAttributePatterns(),
		 new ViewsCompletionProvider());
	}

	/**
	 * Function filtering is done in the contributor because it's too messy to do here.
	 * @return
	 */
	@NotNull
	private static PsiElementPattern.Capture<PsiElement> getViewAttributePatterns() {
		return PlatformPatterns.psiElement()
		                       .inside(PlatformPatterns.psiElement(PhpElementTypes.PARAMETER_LIST))
		                       .inside(PlatformPatterns.psiElement(PhpElementTypes.FUNCTION_CALL))
		                       .inFile(PhpPatterns.psiFile());
	}
}
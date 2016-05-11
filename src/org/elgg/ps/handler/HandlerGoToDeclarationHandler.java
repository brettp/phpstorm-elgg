package org.elgg.ps.handler;

import com.intellij.codeInsight.completion.PlainPrefixMatcher;
import com.intellij.codeInsight.completion.PrefixMatcher;
import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.elements.ParameterList;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.elgg.ps.ElggFuncs;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;


public class HandlerGoToDeclarationHandler implements GotoDeclarationHandler {

	@Nullable
	@Override
	public PsiElement[] getGotoDeclarationTargets(PsiElement e, int i, Editor editor) {

		//if (!Settings.pluginEnabled) {
		//	return null;
		//}

		if (!(e.getContainingFile() instanceof PhpFile) || !(e.getContext() instanceof StringLiteralExpression)) {
			return new PsiElement[0];
		}

		if (!(e.getParent().getParent() instanceof ParameterList)) {
			return new PsiElement[0];
		}

		String registerFuncText = e.getParent().getParent().getParent().getText();

		if (!registerFuncText.startsWith(ElggFuncs.REGISTER_EVENT) && !registerFuncText.startsWith(ElggFuncs.REGISTER_HOOK)) {
			return new PsiElement[0];
		}

		String handler = e.getText();

		PrefixMatcher matcher = new PlainPrefixMatcher(handler);
		Collection<String> matches = PhpIndex.getInstance(e.getProject()).getAllFunctionNames(matcher);

		if (matches.size() > 0) {
			//return matches[0];
			String a = "a";
		}



		//if (!PlatformPatterns.or(PlatformPatterns.psiElement(StringLiteralExpression.class)
		//                          .withText(PlatformPatterns.or(PlatformPatterns.string().endsWith("twig'"),
		//                                                        PlatformPatterns.string().endsWith("twig\"")))
		//                          .withLanguage(PhpLanguage.INSTANCE),
		//                         PlatformPatterns.psiElement(StringLiteralExpression.class)
		//                          .withText(PlatformPatterns.or(PlatformPatterns.string().endsWith("twig'"),
		//                                                        PlatformPatterns.string().endsWith("twig\"")))
		//                          .withLanguage(PhpLanguage.INSTANCE)).accepts(psiElement.getContext())) {
		//
		//	return new PsiElement[0];
		//}
		//
		//String templateName = PsiElementUtils.getText(psiElement);
		//if (StringUtils.isBlank(templateName)) {
		//	return new PsiElement[0];
		//}
		//
		//return TwigHelper.getTemplatePsiElements(psiElement.getProject(), templateName);
		return null;
	}

	@Nullable
	@Override
	public String getActionText(DataContext dataContext) {
		return null;
	}

}


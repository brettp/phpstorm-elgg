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
package org.elgg.ps.psi;

import com.intellij.lang.ASTNode;
import com.jetbrains.php.lang.psi.elements.impl.StringLiteralExpressionImpl;
import org.jetbrains.annotations.NotNull;

public class PsiNamedElggView extends StringLiteralExpressionImpl {
	public PsiNamedElggView(@NotNull ASTNode node) {
		super(node);
	}
	//
	//public static String getName(SimpleProperty element) {
	//	return getKey(element);
	//}
	//
	//public static PsiElement setName(SimpleProperty element, String newName) {
	//	ASTNode keyNode = element.getNode().findChildByType(SimpleTypes.KEY);
	//	if (keyNode != null) {
	//
	//		SimpleProperty property = SimpleElementFactory.createProperty(element.getProject(), newName);
	//		ASTNode newKeyNode = property.getFirstChild().getNode();
	//		element.getNode().replaceChild(keyNode, newKeyNode);
	//	}
	//	return element;
	//}
	//
	//public static PsiElement getNameIdentifier(SimpleProperty element) {
	//	ASTNode keyNode = element.getNode().findChildByType(SimpleTypes.KEY);
	//	if (keyNode != null) {
	//		return keyNode.getPsi();
	//	}
	//	else {
	//		return null;
	//	}
	//}

}

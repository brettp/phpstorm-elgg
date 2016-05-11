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

import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.elgg.ps.Util;

import java.util.*;

public class TypeProvider implements PhpTypeProvider2 {
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

	@Override
	public char getKey() {
		return 'ブ';
	}

	/**
	 * Just needs to find if this is a view call and then return a unique ID (elgg_view.view_name)
	 * Find if it's a view by checking the function map
	 * It would be awesome if we could use the PHPDoc annotations with something like ElggView
	 *
	 * @param psiElement
	 * @return
	 */
	@Nullable
	@Override
	public String getType(PsiElement psiElement) {
		if (DumbService.getInstance(psiElement.getProject()).isDumb()) {
			return null;
		}

		//FunctionReference funcRef = (FunctionReference)psiElement;
		//if (funcRef == null) {
		//	return null;
		//}

		if (!(psiElement instanceof FunctionReference)) {
			return null;
		}

		FunctionReference funcRef = (FunctionReference)psiElement;

		Collection<? extends PhpNamedElement> funcs = funcRef.resolveGlobal(true);
		if (funcs.isEmpty()) {
			return null;
		}

		PhpNamedElement func = funcs.iterator().next();
		if (!(func instanceof Function) || !isViewFunc((Function)func)) {
			return null;
		}

		Integer paramsIndex = Util.getParameterIndex(funcRef);

		//if (viewFuncs.get(func.getName()).contains(paramsIndex)) {
		//	return func.getName() + ':' + paramsIndex.toString();
		//}

		return func.getName() + ":" + "views";

		// find index we're one

		//
		//PhpClassAdapter phpClassAdapter = getPhpClassAdapterForMethod(method);
		//if (phpClassAdapter != null) {
		//	if (phpClassAdapter.isSubclassOf(CLASS_PHP_UNIT_MOCK_BUILDER)) {
		//		return getTypeForMockBuilder(methodReference);
		//	}
		//
		//	if (phpClassAdapter.isSubclassOf(CLASS_PHP_UNIT_TEST_CASE)) {
		//		return getTypeForTestCase(methodReference);
		//	}
		//}
		//
	}

	@Override
	public Collection<? extends PhpNamedElement> getBySignature(String s, Project project) {
		Collection<PhpClass> collection = new ArrayList<PhpClass>();
		String[] views = s.split(":");

		if (!views[1].equals("views")) {
			return collection;
		}

		if (!viewFuncs.containsKey(views[0])) {
			return collection;
		}


		PhpIndex phpIndex = PhpIndex.getInstance(project);

		collection.addAll(phpIndex.getAnyByFQN(views[0]));

		return collection;
	}

	protected boolean isViewFunc(@NotNull Function func) {
		return viewFuncs.containsKey(func.getName());
	}
	//
	//@Nullable
	//protected String getTypeForMockBuilder(@NotNull MethodReference methodReference) {
	//	MethodReference mockBuilderMethodReference = (new PhpMethodChain(methodReference)).findMethodReference("getMockBuilder");
	//	if (mockBuilderMethodReference == null) {
	//		return null;
	//	}
	//
	//	PhpClass phpClass =
	//		(new PhpClassResolver()).resolveByMethodReferenceContainingParameterListWithClassReference(mockBuilderMethodReference);
	//	if (phpClass == null) {
	//		return null;
	//	}
	//
	//	return phpClass.getFQN();
	//}
	//
	//@Nullable
	//protected String getTypeForTestCase(@NotNull MethodReference methodReference) {
	//	PhpClass phpClass = (new PhpClassResolver()).resolveByMethodReferenceContainingParameterListWithClassReference(methodReference);
	//	if (phpClass == null) {
	//		return null;
	//	}
	//
	//	return phpClass.getFQN();
	//}
	//
	//@Nullable
	//protected PhpClassAdapter getPhpClassAdapterForMethod(@NotNull Method method) {
	//	PhpClass phpClass = method.getContainingClass();
	//	if (phpClass == null) {
	//		return null;
	//	}
	//
	//	return new PhpClassAdapter(phpClass);
	//}
}

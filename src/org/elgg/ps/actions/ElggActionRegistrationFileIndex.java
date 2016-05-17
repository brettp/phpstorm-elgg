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
package org.elgg.ps.actions;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.PhpFileImpl;
import com.jetbrains.php.lang.psi.elements.FunctionReference;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import com.jetbrains.php.lang.psi.stubs.indexes.PhpConstantNameIndex;
import gnu.trove.THashMap;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ElggActionRegistrationFileIndex extends FileBasedIndexExtension<String, Void> {

	public static final ID<String, Void> KEY = ID.create("org.elgg.ps.action_registrations");
	private final KeyDescriptor<String> myKeyDescriptor = new EnumeratorStringDescriptor();
	private static int MAX_FILE_BYTE_SIZE = 2097152;

	public static Set<String> registerActionFunctions = new HashSet<String>() {{
		add("elgg_register_action");
	}};

	@NotNull
	@Override
	public ID<String, Void> getName() {
		return KEY;
	}

	@NotNull
	@Override
	public DataIndexer<String, Void, FileContent> getIndexer() {
		return new DataIndexer<String, Void, FileContent>() {
			@NotNull
			@Override
			public Map<String, Void> map(@NotNull FileContent inputData) {
				final Map<String, Void> map = new THashMap<String, Void>();

				PsiFile psiFile = inputData.getPsiFile();

				if (!(inputData.getPsiFile() instanceof PhpFile) && isValidForIndex(inputData)) {
					return map;
				}

				psiFile.accept(new PsiRecursiveElementWalkingVisitor() {
					@Override
					public void visitElement(PsiElement element) {
						if (element instanceof FunctionReference) {
							visitFuncRef((FunctionReference)element);
						}
						super.visitElement(element);
					}

					public void visitFuncRef(FunctionReference funcRef) {
						String name = funcRef.getName();
						if (!registerActionFunctions.contains(name)) {
							return;
						}

						// elgg_register_action("action/name", "path/to/php/file.php", "access_level")
						PsiElement[] parameters = funcRef.getParameters();

						if (parameters.length == 0 || !(parameters[0] instanceof StringLiteralExpression)) {
							return;
						}

						String actionName = ((StringLiteralExpression)parameters[0]).getContents();
						if (StringUtils.isBlank(actionName)) {
							return;
						}

						// need to resolve any variables manually because the index isn't available yet
						// only looking through the current file
						PsiFile[] roots = ((PhpFileImpl)funcRef.getContainingFile()).getPsiRoots();

						for (PsiFile root : roots) {
							if (!(root instanceof PhpFileImpl)) {
								continue;
							}
						}

						// @todo need to figure out how to resolve vars in the string
						if (!(parameters[1] instanceof StringLiteralExpression)) {
							return;
						}

						String actionFile = ((StringLiteralExpression)parameters[1]).getContents();
						if (StringUtils.isBlank(actionFile)) {
							return;
						}

						//map.put(actionFile, null);

					}

				});

				return map;
			}
		};
	}

	@NotNull
	@Override
	public KeyDescriptor<String> getKeyDescriptor() {
		return this.myKeyDescriptor;
	}

	@NotNull
	@Override
	public DataExternalizer<Void> getValueExternalizer() {
		return ScalarIndexExtension.VOID_DATA_EXTERNALIZER;
	}

	@NotNull
	@Override
	public FileBasedIndex.InputFilter getInputFilter() {
		return PhpConstantNameIndex.PHP_INPUT_FILTER;
	}


	@Override
	public boolean dependsOnFileContent() {
		return true;
	}

	@Override
	public int getVersion() {
		return 1;
	}

	public static boolean isValidForIndex(FileContent inputData) {
		return inputData.getFile().getLength() < MAX_FILE_BYTE_SIZE;
	}

}




package br.ic.ufal.refactoring.detections.duplication.subclasses.methods;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.detections.BadSmell;
import br.ic.ufal.util.OperationsUtil;

public class DownMethods extends BadSmell {

	private final OperationsUtil operationsUtil = new OperationsUtil();
	private final List<MethodDeclaration> methodsToBeDown = new ArrayList<MethodDeclaration>();

	/**
	 * @param project
	 */
	public DownMethods(Project project) {
		super(project);
	}

	@Override
	public boolean check() {

		for (Clazz superclass : super.getProject().getClasses()) {

			List<Clazz> subClasses = operationsUtil.getSubclasses(superclass,
					super.getProject().getClasses());

			for (Clazz subclass : subClasses) {

				for (MethodDeclaration methodDeclaration : subclass
						.getTypeDeclaration().getMethods()) {

					int count = operationsUtil.countMethodsInClasses(
							methodDeclaration, subClasses);

					if (count == 1) {
						if (superclassContainMethod(methodDeclaration,
								superclass)
								&& !methodDeclaration.isConstructor()) {
							this.methodsToBeDown.add(methodDeclaration);
						}
					}
				}
			}
		}
		return this.methodsToBeDown.size() > 0;
	}

	/**
	 * @param methodDeclaration
	 * @param superclass
	 * @return
	 */
	private boolean superclassContainMethod(
			MethodDeclaration methodDeclaration, Clazz superclass) {

		for (MethodDeclaration method : superclass.getTypeDeclaration()
				.getMethods()) {
			if (methodDeclaration.resolveBinding().isSubsignature(
					method.resolveBinding())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @return
	 */
	public List<MethodDeclaration> getMethodsToBeUp() {
		return methodsToBeDown;
	}
}

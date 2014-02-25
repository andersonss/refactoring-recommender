package br.ic.ufal.refactoring.detections.duplication.subclasses.methods.up;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.detections.BadSmell;
import br.ic.ufal.util.OperationsUtil;

public class UpMethods extends BadSmell {

	private final OperationsUtil operationsUtil = new OperationsUtil();
	private final List<UpMethodsDesc> methodsToBeUp = new ArrayList<UpMethodsDesc>();

	/**
	 * @param project
	 */
	public UpMethods(Project project) {
		super(project);
	}

	@Override
	public boolean check() {

		for (Clazz superclass : super.getProject().getClasses()) {
			System.out.println("Analysing Class: "
					+ superclass.getTypeDeclaration().getName());
			boolean upMethods = false;
			UpMethodsDesc upMethodsDesc = new UpMethodsDesc();

			upMethodsDesc.setSuperclass(superclass);

			List<Clazz> subClasses = operationsUtil.getSubclasses(superclass,
					super.getProject().getClasses());

			for (Clazz subclass : subClasses) {

				upMethodsDesc.addSubclass(subclass);

				for (MethodDeclaration methodDeclaration : subclass
						.getTypeDeclaration().getMethods()) {

					int count = operationsUtil.countMethodsInClasses(
							methodDeclaration, subClasses);

					if (count == subClasses.size()) {

						if (!classContainMethod(methodDeclaration, superclass)) {
							upMethodsDesc.addMethodToBeUp(methodDeclaration);
							upMethods = true;
						}
					}
				}
			}
			if (upMethods) {
				this.methodsToBeUp.add(upMethodsDesc);
			}

		}
		return this.methodsToBeUp.size() > 0;
	}

	/**
	 * @param methodDeclaration
	 * @param clazz
	 * @return
	 */
	private boolean classContainMethod(MethodDeclaration methodDeclaration,
			Clazz clazz) {

		for (MethodDeclaration method : clazz.getTypeDeclaration().getMethods()) {
			if (methodDeclaration != null && method != null) {
				if (methodDeclaration.resolveBinding() != null
						&& method.resolveBinding() != null) {
					if (methodDeclaration.resolveBinding().isSubsignature(
							method.resolveBinding())) {
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * @return
	 */
	public List<UpMethodsDesc> getMethodsToBeUp() {
		return methodsToBeUp;
	}
}

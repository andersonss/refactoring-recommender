package br.ic.ufal.refactoring.corrections.movingfeaturesbetwenobjects;

import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.corrections.Correction;
import br.ic.ufal.refactoring.detections.duplication.subclasses.methods.down.DownMethodsDesc;

public class PushDownMethods extends Correction {

	private final DownMethodsDesc downMethodsDesc;

	/**
	 * 
	 * @param downMethodsDesc
	 * @param project
	 */
	public PushDownMethods(DownMethodsDesc downMethodsDesc, Project project) {
		super(project);
		this.downMethodsDesc = downMethodsDesc;

	}

	@Override
	public void apply() {

		System.out.println("Pushing Down Methods");

		Clazz superclass = downMethodsDesc.getSuperclass();

		List<Clazz> subclasses = downMethodsDesc.getSubclasses();

		List<MethodDeclaration> methodsTobeDown = downMethodsDesc
				.getMethodsToBeDown();

		for (Clazz subclazz : subclasses) {

			for (MethodDeclaration methodTobeDown : methodsTobeDown) {
				RemoveMethod removeMethod = new RemoveMethod(superclass,
						subclazz, methodTobeDown,
						new HashMap<MethodInvocation, MethodDeclaration>(),
						false, methodTobeDown.getName().getIdentifier(),
						getProject());
				removeMethod.apply();
			}

		}

		System.out.println("Pushed Down Methods");

	}

}

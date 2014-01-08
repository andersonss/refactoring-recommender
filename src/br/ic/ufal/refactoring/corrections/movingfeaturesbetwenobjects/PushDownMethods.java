package br.ic.ufal.refactoring.corrections.movingfeaturesbetwenobjects;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.corrections.Correction;
import br.ic.ufal.refactoring.detections.duplication.subclasses.methods.down.DownMethodsDesc;

public class PushDownMethods extends Correction {

	private DownMethodsDesc downMethodsDesc; 
	
	public PushDownMethods(DownMethodsDesc downMethodsDesc, Project project) {
		super(project);
		this.downMethodsDesc = downMethodsDesc;
		
		
	}

	@Override
	public void apply() {
		
			System.out.println("Pushing Down Methods" );
		
			Clazz superclass = downMethodsDesc.getSuperclass();
			
			for (MethodDeclaration methodDeclaration : downMethodsDesc.getMethodsToBeDown()) {
			
				//if (operationsUtil.useMethod(methodDeclaration, superclass, getProject()) == 0) {
					RemoveMethod removeMethod = new RemoveMethod(superclass, methodDeclaration, super.getProject());
					removeMethod.apply();
				//} 
			}
			
		
		
		System.out.println("Pushed Down Methods" );
		
		
	}

}

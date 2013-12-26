package br.ic.ufal.refactoring.corrections.movingfeaturesbetwenobjects;

import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.corrections.Correction;
import br.ic.ufal.refactoring.detections.duplication.subclasses.methods.down.DownMethodsDesc;
import br.ic.ufal.util.OperationsUtil;

public class PushDownMethods extends Correction {

	private List<DownMethodsDesc> downMethodsDescs; 
	private OperationsUtil operationsUtil = new OperationsUtil();
	
	public PushDownMethods(List<DownMethodsDesc> downMethodsDescs, Project project) {
		super(project);
		this.downMethodsDescs = downMethodsDescs;
		
		
	}

	@Override
	public void apply() {
		
		System.out.println("Pushing Down Methods" );
		
		int count = 1;
		
		for (DownMethodsDesc downMethodsDesc : this.downMethodsDescs) {
			
			System.out.println("Push Down Method " + count + " of " + this.downMethodsDescs.size());
			
			//System.out.println(downMethodsDesc);
			
			Clazz superclass = downMethodsDesc.getSuperclass();
			
			for (MethodDeclaration methodDeclaration : downMethodsDesc.getMethodsToBeDown()) {
			
				//if (operationsUtil.useMethod(methodDeclaration, superclass, getProject()) == 0) {
					RemoveMethod removeMethod = new RemoveMethod(superclass, methodDeclaration, super.getProject());
					removeMethod.apply();
				//} 
			}
			
			count++;
			
		}
		
		System.out.println("Pushed Down Methods" );
		
		
	}

}

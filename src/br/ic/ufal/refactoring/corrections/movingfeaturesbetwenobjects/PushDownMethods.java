package br.ic.ufal.refactoring.corrections.movingfeaturesbetwenobjects;

import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.corrections.Correction;
import br.ic.ufal.refactoring.detections.duplication.subclasses.methods.down.DownMethodsDesc;

public class PushDownMethods extends Correction {

	private List<DownMethodsDesc> downMethodsDescs; 
	
	public PushDownMethods(List<DownMethodsDesc> downMethodsDescs, Project project) {
		super(project);
		this.downMethodsDescs = downMethodsDescs;
		
		
	}

	@Override
	public void execute() {
		
		for (DownMethodsDesc downMethodsDesc : this.downMethodsDescs) {
			
			System.out.println(downMethodsDesc);
			
			for (MethodDeclaration methodDeclaration : downMethodsDesc.getMethodsToBeDown()) {
				RemoveMethod removeMethod = new RemoveMethod(downMethodsDesc.getSuperclass(), methodDeclaration, super.getProject());
				removeMethod.execute();
			}
			
		}
		
		
	}

}

package br.ic.ufal.refactoring.corrections.movingfeaturesbetwenobjects;

import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.corrections.Correction;
import br.ic.ufal.refactoring.detections.duplication.subclasses.methods.up.UpMethodsDesc;

public class PullUpMethods extends Correction {

	private UpMethodsDesc upMethodsDesc = null;
	
	public PullUpMethods(UpMethodsDesc upMethodsDesc, Project project) {
		super(project);
		
		this.upMethodsDesc = upMethodsDesc;
	}

	@Override
	public void apply() {
		
		System.out.println("Applying Pull Up Methods");
		
			
			Clazz superclazz = upMethodsDesc.getSuperclass();
			List<Clazz> subclasses = upMethodsDesc.getSubclasses();
			List<MethodDeclaration> methodsToBeUp = upMethodsDesc.getMethodsToBeUp();
			
			for (MethodDeclaration methodDeclaration : methodsToBeUp) {
				Clazz sourceClazz = clazzContainMethod(methodDeclaration, subclasses);
				
			//	System.out.println("Moving Method: " + methodDeclaration.getName());
			
				if (sourceClazz != null &&superclazz != null && methodDeclaration != null) {
					if (methodDeclaration.getName().getIdentifier() != null) {
						MoveMethod moveMethod = new MoveMethod(sourceClazz, superclazz, methodDeclaration, new HashMap<MethodInvocation, MethodDeclaration>(), false, methodDeclaration.getName().getIdentifier(), getProject());
						moveMethod.apply();
						
					}
				}
				
				
			//	System.out.println("Moved Method: " + methodDeclaration.getName());
				
			}
		
		
		
		System.out.println("Applyed Pull Up Methods");
		
	}
	
	private Clazz clazzContainMethod(MethodDeclaration method, List<Clazz> subclassess){
		
		for (Clazz clazz : subclassess) {
			for (MethodDeclaration methodDeclaration : clazz.getTypeDeclaration().getMethods()) {
				if (method.resolveBinding().isEqualTo(methodDeclaration.resolveBinding())) {
					return clazz;
				}
			}
		}
		
		return null;
	}

}

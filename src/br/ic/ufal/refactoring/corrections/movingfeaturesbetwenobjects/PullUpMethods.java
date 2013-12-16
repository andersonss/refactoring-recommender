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

	private List<UpMethodsDesc> upMethodsDescs = null;
	
	public PullUpMethods(List<UpMethodsDesc> upMethodsDescs, Project project) {
		super(project);
		
		this.upMethodsDescs = upMethodsDescs;
	}

	@Override
	public void execute() {
		
		for (UpMethodsDesc desc : this.upMethodsDescs) {
			Clazz superclazz = desc.getSuperclass();
			List<Clazz> subclasses = desc.getSubclasses();
			List<MethodDeclaration> methodsToBeUp = desc.getMethodsToBeUp();
			
			for (MethodDeclaration methodDeclaration : methodsToBeUp) {
				Clazz sourceClazz = clazzContainMethod(methodDeclaration, subclasses);
				
				System.out.println("Moving Method: " + methodDeclaration.getName());
				
				MoveMethod moveMethod = new MoveMethod(sourceClazz, superclazz, methodDeclaration, new HashMap<MethodInvocation, MethodDeclaration>(), false, methodDeclaration.getName().getIdentifier(), getProject());
				moveMethod.execute();
				
				System.out.println("Moved Method: " + methodDeclaration.getName());
				
			}
			
		}
		
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

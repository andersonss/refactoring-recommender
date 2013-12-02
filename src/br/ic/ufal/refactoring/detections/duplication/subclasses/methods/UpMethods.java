package br.ic.ufal.refactoring.detections.duplication.subclasses.methods;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.detections.BadSmell;
import br.ic.ufal.util.OperationsUtil;

public class UpMethods extends BadSmell {

	private OperationsUtil operationsUtil = new OperationsUtil();
	private List<MethodDeclaration> methodsToBeUp = new ArrayList<MethodDeclaration>();
	
	public UpMethods(Project project) {
		super(project);
	}

	@Override
	public boolean check() {
	
		for (Clazz superclass : super.getProject().getClasses()) {
			
			List<Clazz> subClasses = operationsUtil.getSubclasses(superclass, super.getProject().getClasses());
			
			for (Clazz subclass : subClasses) {
				
				for (MethodDeclaration methodDeclaration : subclass.getTypeDeclaration().getMethods()) {
					
					int count = operationsUtil.countMethodsInClasses(methodDeclaration, subClasses);
					
					if (count == subClasses.size() ) {
						if (!superclassContainMethod(methodDeclaration, superclass)) {
							this.methodsToBeUp.add(methodDeclaration);
						}
					}
				}
			}
		}
		return this.methodsToBeUp.size() > 0;
	}
	
	private boolean superclassContainMethod(MethodDeclaration methodDeclaration, Clazz superclass){
		
		for (MethodDeclaration method : superclass.getTypeDeclaration().getMethods()) {
			if (methodDeclaration.resolveBinding().isSubsignature(method.resolveBinding())) {
				return true;
			}
		}
		
		return false;
	}

	public List<MethodDeclaration> getMethodsToBeUp() {
		return methodsToBeUp;
	}
}

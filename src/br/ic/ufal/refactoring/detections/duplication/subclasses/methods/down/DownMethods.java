package br.ic.ufal.refactoring.detections.duplication.subclasses.methods.down;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.detections.BadSmell;
import br.ic.ufal.util.OperationsUtil;

public class DownMethods extends BadSmell {

	private OperationsUtil operationsUtil = new OperationsUtil();
	private List<DownMethodsDesc> downMethodsDescs = new ArrayList<DownMethodsDesc>();
	
	public DownMethods(Project project) {
		super(project);
	}

	@Override
	public boolean check() {
	
		for (Clazz superclass : super.getProject().getClasses()) {
			
			List<Clazz> subClasses = operationsUtil.getSubclasses(superclass, super.getProject().getClasses());
			
			for (Clazz subclass : subClasses) {
				
				for (MethodDeclaration methodDeclaration : subclass.getTypeDeclaration().getMethods()) {
					if (!methodDeclaration.isConstructor()) {
						int count = operationsUtil.countMethodsInClasses(methodDeclaration, subClasses);
						
						if (count == 1 ) {
							
							
							DownMethodsDesc desc = retrieveDownMethodDesc(superclass);
							
							if (desc != null) {
								desc.getMethodsToBeDown().add(methodDeclaration);
							}else{
								DownMethodsDesc downMethodsDesc = new DownMethodsDesc();
								
								downMethodsDesc.setSuperclass(superclass);
								
								for (Clazz sub : subClasses) {
									if (classContainMethod(methodDeclaration, sub)) {
										downMethodsDesc.addSubclass(sub);
									}
								}
								
								downMethodsDesc.addMethod(methodDeclaration);
								
								this.downMethodsDescs.add(downMethodsDesc);
							}
							
							
							/*if (classContainMethod(methodDeclaration, superclass) &&
								!methodDeclaration.isConstructor()) {
								this.methodsToBeDown.add(methodDeclaration);
							}*/
						}
					}
					
				}
			}
		}
		return this.downMethodsDescs.size() > 0;
	}
	
	private boolean classContainMethod(MethodDeclaration methodDeclaration, Clazz clazz){
		
		for (MethodDeclaration method : clazz.getTypeDeclaration().getMethods()) {
			if (methodDeclaration.resolveBinding().isSubsignature(method.resolveBinding())) {
				return true;
			}
		}
		
		return false;
	}
	
	private DownMethodsDesc retrieveDownMethodDesc(Clazz superclass){
		
		for (DownMethodsDesc desc : this.downMethodsDescs) {
			if (superclass.getTypeDeclaration().resolveBinding().isEqualTo(desc.getSuperclass().getTypeDeclaration().resolveBinding())) {
				return desc;
			}
		}
		
		return null;
	}

	public List<DownMethodsDesc> getMethodsToBeDown() {
		return this.downMethodsDescs;
	}
}

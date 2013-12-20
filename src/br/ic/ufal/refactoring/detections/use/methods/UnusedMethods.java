package br.ic.ufal.refactoring.detections.use.methods;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.detections.BadSmell;
import br.ic.ufal.util.OperationsUtil;

public class UnusedMethods extends BadSmell {
	
	private List<MethodDeclaration> unusedMethods = new ArrayList<MethodDeclaration>();
	private OperationsUtil operationsUtil = new OperationsUtil();
	
	public UnusedMethods(Project project) {
		super(project);
	}

	@Override
	public boolean check() {
			for (int i = 0; i < super.getProject().getClasses().size(); i++) {
				Clazz clazz = super.getProject().getClasses().get(i);
				
				System.out.println("Analysing Unused Method in Class: " + clazz.getTypeDeclaration().getName() + " Position: " + i);
				
				for (MethodDeclaration method : clazz.getTypeDeclaration().getMethods()) {
					if (operationsUtil.useMethod(method, super.getProject()) == 0 && 
						!method.isConstructor()) {
						this.unusedMethods.add(method);
					}
				}
			}
			
		
		
		return unusedMethods.size() > 0;
	}

	public List<MethodDeclaration> getUnusedMethods() {
		return unusedMethods;
	}
}

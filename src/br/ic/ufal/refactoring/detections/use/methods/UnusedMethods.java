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
	
	private int threshold = 0;
	
	public UnusedMethods(Project project, int threshold) {
		super(project);
		this.threshold = threshold;
	}

	@Override
	public boolean check() {
		
		System.out.println("Check Unused Methods");
		
		for (int i = 0; i < super.getProject().getClasses().size(); i++) {
				Clazz clazz = super.getProject().getClasses().get(i);
				
				//System.out.println("Analysing Unused Methods in Class: " + clazz.getTypeDeclaration().getName() + " Position: " + i +  " of " +super.getProject().getClasses().size());
				//System.out.println("Number of Methods: " + clazz.getTypeDeclaration().getMethods().length);
				int count = 0;
				for (MethodDeclaration method : clazz.getTypeDeclaration().getMethods()) {
					if (operationsUtil.useMethod(method, super.getProject()) == this.threshold && 
						!method.isConstructor()) {
						count ++;
						this.unusedMethods.add(method);
					}
				}
				//System.out.println("Amount of Unused Methods: " + count);
			}
			
		
		
		return unusedMethods.size() > 0;
	}

	public List<MethodDeclaration> getUnusedMethods() {
		return unusedMethods;
	}
}

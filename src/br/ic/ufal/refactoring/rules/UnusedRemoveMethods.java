package br.ic.ufal.refactoring.rules;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.corrections.Correction;
import br.ic.ufal.refactoring.corrections.makingmethodcallsimpler.RemoveMethods;
import br.ic.ufal.refactoring.detections.BadSmellType;
import br.ic.ufal.refactoring.detections.use.methods.UnusedMethods;

public class UnusedRemoveMethods extends Rule {

	private int threshold = 0;
	
	public UnusedRemoveMethods(Project project, int threshold) {
		super(project);
		this.threshold = threshold;
	}

	@Override
	public void execute() {
		
		System.out.println("Analysing Unused Methods");
		UnusedMethods unusedMethods = new UnusedMethods(getProject(), this.threshold);
		
		if (unusedMethods.check()) {
			
			getProject().countDetectedBadSmells(BadSmellType.UnsuedMethods, unusedMethods.getUnusedMethods().size());
			
			for (int i = 0; i < unusedMethods.getUnusedMethods().size(); i++) {
				MethodDeclaration method = unusedMethods.getUnusedMethods().get(i);
				
				System.out.println("Correct Unused Method: " + i + " of "+ unusedMethods.getUnusedMethods().size());
				
				Correction removeMethods = new RemoveMethods(getProject(), method);
				removeMethods.apply();
				
			}
			
			
		}else{
			System.out.println("Not Exist Unused Methods");
		}
	}

}

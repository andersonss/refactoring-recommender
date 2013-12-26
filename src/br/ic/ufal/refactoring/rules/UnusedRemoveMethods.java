package br.ic.ufal.refactoring.rules;

import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.corrections.Correction;
import br.ic.ufal.refactoring.corrections.makingmethodcallsimpler.RemoveMethods;
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
			
			Correction removeMethods = new RemoveMethods(getProject(), unusedMethods.getUnusedMethods());
			removeMethods.apply();
		}else{
			System.out.println("Not Exist Unused Methods");
		}
	}

}

package br.ic.ufal.refactoring.rules;

import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.corrections.Correction;
import br.ic.ufal.refactoring.corrections.movingfeaturesbetwenobjects.PushDownMethods;
import br.ic.ufal.refactoring.detections.duplication.subclasses.methods.down.DownMethods;

public class DownPushDownMethods extends Rule {

	private int threshold = 1;
	
	public DownPushDownMethods(Project project, int threshold) {
		super(project);
		this.threshold = threshold;
	}

	@Override
	public void execute() {
		System.out.println("Verifying Down Methods");
		DownMethods downMethods = new DownMethods(getProject(), this.threshold);
		
		if (downMethods.check()) {
			
			Correction pushDownMethods = new PushDownMethods(downMethods.getMethodsToBeDown(), getProject());
			pushDownMethods.apply();
		}

	}

}

package br.ic.ufal.refactoring.rules;

import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.corrections.Correction;
import br.ic.ufal.refactoring.corrections.movingfeaturesbetwenobjects.PullUpMethods;
import br.ic.ufal.refactoring.detections.duplication.subclasses.methods.up.UpMethods;

public class UpPullUpMethods extends Rule {

	public UpPullUpMethods(Project project) {
		super(project);
	}

	@Override
	public void execute() {
		System.out.println("Verifying Up Methods");
		UpMethods upMethods = new UpMethods(getProject());
		
		if (upMethods.check()) {
			
			Correction pullUpMethods = new PullUpMethods(upMethods.getMethodsToBeUp(), getProject());
			pullUpMethods.apply();
		}

	}

}

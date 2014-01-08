package br.ic.ufal.refactoring.rules;

import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.corrections.Correction;
import br.ic.ufal.refactoring.corrections.movingfeaturesbetwenobjects.PullUpMethods;
import br.ic.ufal.refactoring.detections.BadSmellType;
import br.ic.ufal.refactoring.detections.duplication.subclasses.methods.up.UpMethods;
import br.ic.ufal.refactoring.detections.duplication.subclasses.methods.up.UpMethodsDesc;

public class UpPullUpMethods extends Rule {

	public UpPullUpMethods(Project project) {
		super(project);
	}

	@Override
	public void execute() {
		System.out.println("Verifying Up Methods");
		UpMethods upMethods = new UpMethods(getProject());
		
		if (upMethods.check()) {
			
			getProject().countDetectedBadSmells(BadSmellType.UpMethods, upMethods.getMethodsToBeUp().size());
			
			for (int i = 0; i < upMethods.getMethodsToBeUp().size(); i++) {
				
				UpMethodsDesc upMethodsDesc = upMethods.getMethodsToBeUp().get(i);
				
				System.out.println("Correct Up Method: " + i + " of " + upMethods.getMethodsToBeUp().size());
				
				Correction pullUpMethods = new PullUpMethods(upMethodsDesc, getProject());
				pullUpMethods.apply();
				
			}
			
			
		}

	}

}

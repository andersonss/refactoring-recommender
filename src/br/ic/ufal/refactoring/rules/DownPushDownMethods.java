package br.ic.ufal.refactoring.rules;

import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.corrections.Correction;
import br.ic.ufal.refactoring.corrections.movingfeaturesbetwenobjects.PushDownMethods;
import br.ic.ufal.refactoring.detections.BadSmellType;
import br.ic.ufal.refactoring.detections.duplication.subclasses.methods.down.DownMethods;
import br.ic.ufal.refactoring.detections.duplication.subclasses.methods.down.DownMethodsDesc;

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
			
			getProject().countDetectedBadSmells(BadSmellType.DownMethods, downMethods.getMethodsToBeDown().size());
			
			for (int i = 0; i < downMethods.getMethodsToBeDown().size(); i++) {
				DownMethodsDesc downMethodsDesc = downMethods.getMethodsToBeDown().get(i);
			
				System.out.println("Correct Down Methods: " + i + " of " + downMethods.getMethodsToBeDown().size());
				
				Correction pushDownMethods = new PushDownMethods(downMethodsDesc, getProject());
				pushDownMethods.apply();
				
			}
		}else{
			System.out.println("Not Exist Down Methods");
		}

	}

}

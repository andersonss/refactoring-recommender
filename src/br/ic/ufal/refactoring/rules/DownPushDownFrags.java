package br.ic.ufal.refactoring.rules;

import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.corrections.Correction;
import br.ic.ufal.refactoring.corrections.movingfeaturesbetwenobjects.PushDownFragments;
import br.ic.ufal.refactoring.detections.duplication.subclasses.fields.down.DownFragments;

public class DownPushDownFrags extends Rule {

	private int threshold = 0;
	
	public DownPushDownFrags(Project project, int threshold) {
		super(project);
		this.threshold = threshold;
	}

	@Override
	public void execute() {
		System.out.println("Verifying Down Fragments");
		DownFragments downFields = new DownFragments(getProject(), this.threshold);
		
		if (downFields.check()) {
			
				Correction pushDownFragments = new PushDownFragments(downFields.getDownFragmentsDescs(), getProject());
				pushDownFragments.apply();
		}
	}

}

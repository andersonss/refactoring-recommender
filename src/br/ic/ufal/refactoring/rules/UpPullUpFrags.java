package br.ic.ufal.refactoring.rules;

import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.corrections.Correction;
import br.ic.ufal.refactoring.corrections.movingfeaturesbetwenobjects.PullUpFragments;
import br.ic.ufal.refactoring.detections.duplication.subclasses.fields.up.UpFragments;

public class UpPullUpFrags extends Rule {
	
	private int threshold = 1;

	public UpPullUpFrags(Project project, int threshold) {
		super(project);
		this.threshold = threshold;
	}

	@Override
	public void execute() {
		System.out.println("Verifying Up Fragments");
		UpFragments upFragments = new UpFragments(getProject(), this.threshold);
		
		if (upFragments.check()) {
			
			Correction pullUpFragments = new PullUpFragments(upFragments.getDuplicatedFragments(), getProject());
			pullUpFragments.apply();
			
		}

	}

}

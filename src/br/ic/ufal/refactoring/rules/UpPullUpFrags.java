package br.ic.ufal.refactoring.rules;

import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.corrections.Correction;
import br.ic.ufal.refactoring.corrections.movingfeaturesbetwenobjects.PullUpFragments;
import br.ic.ufal.refactoring.detections.BadSmellType;
import br.ic.ufal.refactoring.detections.dataclumps.fields.DuplicatedFragments;
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
			
			getProject().countDetectedBadSmells(BadSmellType.UpFragments, upFragments.getDuplicatedFragments().size());
			
			for (int i = 0; i < upFragments.getDuplicatedFragments().size(); i++) {
				
				DuplicatedFragments duplicatedFragments = upFragments.getDuplicatedFragments().get(i);
				
				System.out.println("Correct Duplicated Fragments: " + i + " of " + upFragments.getDuplicatedFragments().size());
				
				Correction pullUpFragments = new PullUpFragments(duplicatedFragments, getProject());
				pullUpFragments.apply();
				
			}
		}else{
			System.out.println("Not Exist Up Fragments");
		}

	}

}

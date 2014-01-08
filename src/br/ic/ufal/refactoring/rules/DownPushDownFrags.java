package br.ic.ufal.refactoring.rules;

import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.corrections.Correction;
import br.ic.ufal.refactoring.corrections.movingfeaturesbetwenobjects.PushDownFragments;
import br.ic.ufal.refactoring.detections.BadSmellType;
import br.ic.ufal.refactoring.detections.duplication.subclasses.fields.down.DownFragments;
import br.ic.ufal.refactoring.detections.duplication.subclasses.fields.down.DownFragmentsDesc;

public class DownPushDownFrags extends Rule {

	private int threshold = 0;
	
	public DownPushDownFrags(Project project, int threshold) {
		super(project);
		this.threshold = threshold;
	}

	@Override
	public void execute() {
		System.out.println("Verifying Down Fragments");
		DownFragments downFragments = new DownFragments(getProject(), this.threshold);
		
		if (downFragments.check()) {
			
			
			for (int i = 0; i < downFragments.getDownFragmentsDescs().size(); i++) {
				
				DownFragmentsDesc fragToBeDown = downFragments.getDownFragmentsDescs().get(i);
				
				System.out.println("Correct Duplicated Fragments: " + i + " of " + downFragments.getDownFragmentsDescs().size());
				
				Correction pushDownFragments = new PushDownFragments(fragToBeDown, getProject());
				pushDownFragments.apply();
				
			}
			
			getProject().countSolvedBadSmells(BadSmellType.DownFragments, downFragments.getDownFragmentsDescs().size());
			
		}
	}

}

package br.ic.ufal.refactoring.corrections.movingfeaturesbetwenobjects;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.corrections.Correction;
import br.ic.ufal.refactoring.detections.duplication.subclasses.fields.down.DownFragmentsDesc;

public class PushDownFragments extends Correction {

	private DownFragmentsDesc downFragmentsDesc = null;
	
	public PushDownFragments(DownFragmentsDesc downFragmentsDesc, 
			 				 Project project) {
		super(project);
		this.downFragmentsDesc = downFragmentsDesc;
		
	}

	@Override
	public void apply() {
		
		System.out.println("Applying Push Down Fragments");
		
		
			for (Clazz subclass : downFragmentsDesc.getSubclasses()) {
			
				MoveField moveField = new MoveField(downFragmentsDesc.getSuperclass(), subclass, downFragmentsDesc.getFragmentsToBeDown(), super.getProject());
				moveField.apply();
			}	
				
		
		System.out.println("Applied Push Down Fragments");
		
		
	}

}

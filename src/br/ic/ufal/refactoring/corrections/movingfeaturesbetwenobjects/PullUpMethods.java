package br.ic.ufal.refactoring.corrections.movingfeaturesbetwenobjects;

import java.util.List;

import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.corrections.Correction;
import br.ic.ufal.refactoring.detections.duplication.subclasses.methods.up.UpMethodsDesc;

public class PullUpMethods extends Correction {

	private List<UpMethodsDesc> upMethodsDescs = null;
	
	public PullUpMethods(List<UpMethodsDesc> upMethodsDescs, Project project) {
		super(project);
		
		this.upMethodsDescs = upMethodsDescs;
	}

	@Override
	public void execute() {
		
	}

}

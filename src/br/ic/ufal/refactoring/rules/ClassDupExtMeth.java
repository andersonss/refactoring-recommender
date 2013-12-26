package br.ic.ufal.refactoring.rules;

import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.corrections.Correction;
import br.ic.ufal.refactoring.corrections.composingmethods.ExtractMethod;
import br.ic.ufal.refactoring.detections.duplication.clazz.ClazzDuplication;

public class ClassDupExtMeth extends Rule {

	private int threshold = 2;
	
	public ClassDupExtMeth(Project project, int threshold) {
		super(project);
		this.threshold = threshold;
	}

	@Override
	public void execute() {
		ClazzDuplication clazzDuplication = new ClazzDuplication(getProject(), this.threshold);
		
		System.out.println("Verifying Clazz Duplication Statements");
		if (clazzDuplication.check()) {
			Correction extractMethod = new ExtractMethod(clazzDuplication.getDuplicatedStatements(), getProject());
			extractMethod.apply();
		}else{
			System.out.println("Not Exist Duplication in Clazz");
		}

	}

}

package br.ic.ufal.refactoring.rules;

import java.util.List;

import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.corrections.Correction;
import br.ic.ufal.refactoring.corrections.composingmethods.ExtractMethod;
import br.ic.ufal.refactoring.detections.BadSmellType;
import br.ic.ufal.refactoring.detections.duplication.clazz.ClassDuplication;
import br.ic.ufal.refactoring.detections.duplication.clazz.DuplicatedStatements;

public class ClassDupExtMeth extends Rule {

	private int threshold = 2;
	
	public ClassDupExtMeth(Project project, int threshold) {
		super(project);
		this.threshold = threshold;
	}

	@Override
	public void execute() {
		ClassDuplication clazzDuplication = new ClassDuplication(getProject(), this.threshold);
		
		System.out.println("Verifying Class Duplication Statements");
		if (clazzDuplication.check()) {
			
			List<DuplicatedStatements> duplicatedStatementsList = clazzDuplication.getDuplicatedStatements();
			
			getProject().countDetectedBadSmells(BadSmellType.ClassDuplication, duplicatedStatementsList.size());
			
			for (int i = 0; i < duplicatedStatementsList.size(); i++) {
				
				System.out.println("Solving Duplicated Statements: "+ i + " of " + duplicatedStatementsList.size());
				
				Correction extractMethod = new ExtractMethod(duplicatedStatementsList.get(i), getProject());
				extractMethod.apply();
			
			}
			
			
		}else{
			System.out.println("Not Exist Duplication in Clazz");
		}

	}

}

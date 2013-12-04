package br.ic.ufal.refactoring.corrections.movingfeaturesbetwenobjects;

import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.corrections.Correction;

public class PushDownFragments extends Correction {

	 private Clazz sourceClass;
	 private List<Clazz> targetClasses;
	 private Set<VariableDeclaration> extractedFieldFragments; 
	
	
	public PushDownFragments(Clazz sourceClass,
			 				 List<Clazz> targetClasses,
			 				 Set<VariableDeclaration> extractedFieldFragments, 
			 				 Project project) {
		super(project);
		this.sourceClass = sourceClass;
		this.targetClasses = targetClasses;
		this.extractedFieldFragments = extractedFieldFragments;
		
		
	}

	@Override
	public void execute() {
		
		for (Clazz subclass : this.targetClasses) {
			MoveField moveField = new MoveField(this.sourceClass, subclass, this.extractedFieldFragments, super.getProject());
			moveField.execute();
		}
		
		
	}

}

package br.ic.ufal.refactoring.corrections.movingfeaturesbetwenobjects;

import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.corrections.Correction;
import br.ic.ufal.refactoring.detections.duplication.subclasses.fields.down.DownFragmentsDesc;

public class PushDownFragments extends Correction {

	private List<DownFragmentsDesc> descriptions = null;
	
	public PushDownFragments(List<DownFragmentsDesc> descriptions, 
			 				 Project project) {
		super(project);
		this.descriptions = descriptions;
		
	}

	@Override
	public void apply() {
		
		System.out.println("Applying Push Down Fragments");
		
		int position = 1;
		
		for (DownFragmentsDesc downFragmentsDesc : this.descriptions) {
			System.out.println("Applying Push Down Fragment in: " + position + " of " + this.descriptions.size());
		
			for (Clazz subclass : downFragmentsDesc.getSubclasses()) {
			
				System.out.println("Applying Down Fragments in Class: " + subclass.getTypeDeclaration().getName()+" Position: " + position);
			
				MoveField moveField = new MoveField(downFragmentsDesc.getSuperclass(), subclass, downFragmentsDesc.getFragmentsToBeDown(), super.getProject());
				moveField.apply();
			}	
				
			position++;
		}
		
		System.out.println("Applied Push Down Fragments");
		
		
	}

}

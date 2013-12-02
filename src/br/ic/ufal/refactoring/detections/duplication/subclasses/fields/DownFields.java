package br.ic.ufal.refactoring.detections.duplication.subclasses.fields;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.detections.BadSmell;
import br.ic.ufal.util.OperationsUtil;

public class DownFields extends BadSmell {

	private OperationsUtil operationsUtil = new OperationsUtil();
	private List<VariableDeclaration> fragmentsToBeDown = new ArrayList<VariableDeclaration>();
	
	public DownFields(Project project) {
		super(project);
	}

	@Override
	public boolean check() {
	
		for (Clazz superclass : super.getProject().getClasses()) {
			
			List<Clazz> subClasses = operationsUtil.getSubclasses(superclass, super.getProject().getClasses());
			
			if (subClasses.size() > 0) {
				for (FieldDeclaration fieldDeclaration : superclass.getTypeDeclaration().getFields()) {
					List<VariableDeclaration> fragments = fieldDeclaration.fragments();
				
					for (VariableDeclaration fragment : fragments) {
						int count = operationsUtil.countFragmentsInClasses(fragment, subClasses);
					
						if (count == 1 ) {
							this.fragmentsToBeDown.add(fragment);
						}
						
					}
				}
			}
			
		}
		return this.fragmentsToBeDown.size() > 0;
	}
	
	public List<VariableDeclaration> getFragmentsToBeDown() {
		return fragmentsToBeDown;
	}
}

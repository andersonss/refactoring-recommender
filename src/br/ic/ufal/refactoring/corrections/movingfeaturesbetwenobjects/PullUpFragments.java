package br.ic.ufal.refactoring.corrections.movingfeaturesbetwenobjects;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.VariableDeclaration;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.corrections.Correction;
import br.ic.ufal.refactoring.detections.dataclumps.fields.DuplicatedFragments;
import br.ic.ufal.util.ParseUtil;

public class PullUpFragments extends Correction {

	private List<DuplicatedFragments> duplicatedFragmentsList = null;
	
	public PullUpFragments(List<DuplicatedFragments> duplicatedFragmentsList, Project project) {
		super(project);
		
		this.duplicatedFragmentsList = duplicatedFragmentsList;
	}

	@Override
	public void execute() {
		
		MoveField moveField = null;
		
		for (DuplicatedFragments duplicatedFragments : this.duplicatedFragmentsList) {
			
			Set<VariableDeclaration> fragments = new HashSet<VariableDeclaration>();
			
			for (VariableDeclaration fragment : duplicatedFragments.getFragments()) {
				fragments.add(fragment);
			}
			
			for (int j = 0; j < duplicatedFragments.getClasses().size(); j++) {
				
				Clazz subclass = duplicatedFragments.getClasses().get(j);
			
				if (j == duplicatedFragments.getClasses().size()-1) {
					
					Clazz superclass = ParseUtil.getClazz(subclass.getTypeDeclaration().getSuperclassType(), super.getProject().getClasses());
					System.out.println("Superclass" + superclass.getTypeDeclaration().getName() + " Subclass: " + subclass.getTypeDeclaration().getName());
					moveField = new MoveField(subclass, superclass, fragments, super.getProject());
					moveField.execute();
					
				}else{
					
					System.out.println("Subclass: " + subclass.getTypeDeclaration().getName());
					moveField = new MoveField(subclass, null, fragments, super.getProject());
					moveField.execute();
					
				}
			}
			
		}
	
	}

}
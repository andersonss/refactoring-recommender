package br.ic.ufal.refactoring.corrections.movingfeaturesbetwenobjects;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.VariableDeclaration;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.corrections.Correction;
import br.ic.ufal.refactoring.detections.dataclumps.fields.DuplicatedFragments;
import br.ic.ufal.util.ParseUtil;

public class PullUpFragments extends Correction {

	private DuplicatedFragments duplicatedFragments = null;
	
	public PullUpFragments(DuplicatedFragments duplicatedFragments, Project project) {
		super(project);
		this.duplicatedFragments = duplicatedFragments;
	}

	@Override
	public void apply() {
		
		System.out.println("Applying Pull Up Fragments");
		
		MoveField moveField = null;
		
			Set<VariableDeclaration> fragments = new HashSet<VariableDeclaration>();
			
			for (VariableDeclaration fragment : duplicatedFragments.getFragments()) {
				fragments.add(fragment);
			}
			
			for (int j = 0; j < duplicatedFragments.getClasses().size(); j++) {
				
				Clazz subclass = duplicatedFragments.getClasses().get(j);
				
				System.out.println("Moving Fragments from Class: " + subclass.getTypeDeclaration().getName() + " Class: " + j + " of: " + duplicatedFragments.getClasses().size());
			
				//System.out.println("Subclass: " + subclass.getTypeDeclaration().getName());
				moveField = new MoveField(subclass, null, fragments, super.getProject());
				moveField.apply();
				
				if (j == 0) {
					
					Clazz superclass = ParseUtil.getClazz(subclass.getTypeDeclaration().getSuperclassType(), super.getProject().getClasses());
					
					if (superclass != null) {
						//System.out.println("Superclass" + superclass.getTypeDeclaration().getName() + " Subclass: " + subclass.getTypeDeclaration().getName());
						moveField = new MoveField(subclass, superclass, fragments, super.getProject());
						moveField.apply();
					}
					
				}else{
					
					//System.out.println("Subclass: " + subclass.getTypeDeclaration().getName());
					RemoveFragments removeFragments = new RemoveFragments(subclass, fragments, super.getProject());
					removeFragments.apply();
					
					/*moveField = new MoveField(subclass, null, fragments, super.getProject());
					moveField.execute();*/
					
				}
			}
			
		
	
		System.out.println("Applied Pull Up Fragments");
	}

}

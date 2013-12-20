package br.ic.ufal.refactoring.detections.duplication.subclasses.fields.up;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclaration;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.detections.BadSmell;
import br.ic.ufal.refactoring.detections.dataclumps.fields.DuplicatedFragments;
import br.ic.ufal.util.OperationsUtil;
import br.ic.ufal.util.ParseUtil;

public class UpFragments extends BadSmell {

	private List<DuplicatedFragments> duplicatedFragmentsList = new ArrayList<DuplicatedFragments>();
	private OperationsUtil operationsUtil = new OperationsUtil();
	
	public UpFragments(Project project) {
		super(project);
	}

	@Override
	public boolean check() {
		
		System.out.println("Retrieving Duplicated Fragments");
		this.duplicatedFragmentsList = operationsUtil.retrieveDuplicatedFragments(super.getProject());
		
		System.out.println("Identifying Related Fragments Duplication");
		this.duplicatedFragmentsList = operationsUtil.identifyRelatedFragmentsDuplication(duplicatedFragmentsList);
		
		
		for (DuplicatedFragments duplicatedFragments : duplicatedFragmentsList) {
			List<VariableDeclaration> fragments = new ArrayList<>();
			
			for (VariableDeclaration fragment : duplicatedFragments.getFragments()) {
				
				Type supertype = duplicatedFragments.getClasses().get(0).getTypeDeclaration().getSuperclassType();
				Clazz superclass = ParseUtil.getClazz(supertype, super.getProject().getClasses());
				
				if (!operationsUtil.classContainFragment(fragment,superclass)) {
					fragments.add(fragment);
				}
			}
			
			duplicatedFragments.setFragments(fragments);
		}
		
		return this.duplicatedFragmentsList.size() > 0;
	}
	
	
	
	public List<DuplicatedFragments> getDuplicatedFragments() {
		return duplicatedFragmentsList;
	}

}

package br.ic.ufal.refactoring.detections.duplication.subclasses.fields.up;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.detections.BadSmell;
import br.ic.ufal.refactoring.detections.dataclumps.fields.DuplicatedFragments;
import br.ic.ufal.util.OperationsUtil;
import br.ic.ufal.util.ParseUtil;

public class UpFields extends BadSmell {

	private List<DuplicatedFragments> duplicatedFragmentsList = new ArrayList<DuplicatedFragments>();
	private OperationsUtil operationsUtil = new OperationsUtil();
	
	public UpFields(Project project) {
		super(project);
	}

	@Override
	public boolean check() {
		
		this.duplicatedFragmentsList = operationsUtil.retrieveDuplicatedFragments(super.getProject());
		this.duplicatedFragmentsList = operationsUtil.identifyRelatedFragmentsDuplication(duplicatedFragmentsList);
		
		for (DuplicatedFragments duplicatedFragments : duplicatedFragmentsList) {
			List<VariableDeclarationFragment> fragments = new ArrayList<>();
			
			for (VariableDeclarationFragment fragment : duplicatedFragments.getFragments()) {
				
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

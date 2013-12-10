package br.ic.ufal.refactoring.detections.use.fields;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.detections.BadSmell;
import br.ic.ufal.util.OperationsUtil;

public class UnusedFragments extends BadSmell {
	
	private List<UnusedFragmentsDesc> unusedFragments = new ArrayList<UnusedFragmentsDesc>();
	private OperationsUtil operationsUtil = new OperationsUtil();
	
	public UnusedFragments(Project project) {
		super(project);
	}

	@Override
	public boolean check() {
			for (int i = 0; i < super.getProject().getClasses().size(); i++) {
				Clazz clazz = super.getProject().getClasses().get(i);
				
				UnusedFragmentsDesc unusedFragmentsDesc = new UnusedFragmentsDesc();
				unusedFragmentsDesc.setClazz(clazz);
				
				System.out.println("Checking Class: " + clazz.getTypeDeclaration().getName());
				for (FieldDeclaration field : clazz.getTypeDeclaration().getFields()) {
					List<VariableDeclaration> fragments = field.fragments();
					for (VariableDeclaration fragment : fragments) {
						if (operationsUtil.useFragment(fragment, super.getProject()) == 0) {
							System.out.println("Unused Fragments: " + fragment);
							unusedFragmentsDesc.addFragment(fragment);
						}
					}
				}
				
				if (unusedFragmentsDesc.getFragmentsToBeRemoved().size() > 0) {
					this.unusedFragments.add(unusedFragmentsDesc);
				}
			}
		
		return unusedFragments.size() > 0;
	}

	public List<UnusedFragmentsDesc> getUnusedFragments() {
		return unusedFragments;
	}
}

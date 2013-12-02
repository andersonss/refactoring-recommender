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
	
	private List<VariableDeclaration> unusedFragments = new ArrayList<VariableDeclaration>();
	private OperationsUtil operationsUtil = new OperationsUtil();
	
	public UnusedFragments(Project project) {
		super(project);
	}

	@Override
	public boolean check() {
		System.out.println("Checking Unused Fragments");
			for (int i = 0; i < super.getProject().getClasses().size(); i++) {
				Clazz clazz = super.getProject().getClasses().get(i);
				System.out.println("Checking Class: " + clazz.getTypeDeclaration().getName() + " Position: " + i);
				for (FieldDeclaration field : clazz.getTypeDeclaration().getFields()) {
					List<VariableDeclaration> fragments = field.fragments();
					for (VariableDeclaration fragment : fragments) {
						if (operationsUtil.useFragment(fragment, super.getProject()) == 0) {
							System.out.println("Unused Fragments: " + fragment);
							this.unusedFragments.add(fragment);
						}
					}
				}
			}
		
		return unusedFragments.size() > 0;
	}

	public List<VariableDeclaration> getUnusedFragments() {
		return unusedFragments;
	}
}

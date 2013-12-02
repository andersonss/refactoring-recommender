package br.ic.ufal.refactoring.detections.use.parameters;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.detections.BadSmell;
import br.ic.ufal.util.OperationsUtil;

public class UnusedParameters extends BadSmell {
	
	private List<VariableDeclaration> unusedParameters = new ArrayList<VariableDeclaration>();
	private OperationsUtil operationsUtil = new OperationsUtil();
	
	public UnusedParameters(Project project) {
		super(project);
	}

	@Override
	public boolean check() {
		System.out.println("Checking Unused Parameters");
			for (int i = 0; i < super.getProject().getClasses().size(); i++) {
				Clazz clazz = super.getProject().getClasses().get(i);
				System.out.println("Checking Class: " + clazz.getTypeDeclaration().getName() + " Position: " + i);
				for (MethodDeclaration method : clazz.getTypeDeclaration().getMethods()) {
					System.out.println("Method: " + method.getName());
					
					List<SingleVariableDeclaration> parameters = method.parameters();
					for (SingleVariableDeclaration parameter : parameters) {
						if (operationsUtil.useParameter(parameter, method, super.getProject()) == 0) {
							System.out.println("Unused Parameters: " + parameter);
							this.unusedParameters.add(parameter);
						}
					}
				}
			}
		
		return unusedParameters.size() > 0;
	}

	public List<VariableDeclaration> getUnusedFragments() {
		return unusedParameters;
	}
}

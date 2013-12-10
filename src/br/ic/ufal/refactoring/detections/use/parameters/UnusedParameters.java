package br.ic.ufal.refactoring.detections.use.parameters;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.detections.BadSmell;
import br.ic.ufal.util.OperationsUtil;

public class UnusedParameters extends BadSmell {
	
	private List<UnusedParametersDesc> unusedParameters = new ArrayList<UnusedParametersDesc>();
	private OperationsUtil operationsUtil = new OperationsUtil();
	
	public UnusedParameters(Project project) {
		super(project);
	}

	@Override
	public boolean check() {
			for (int i = 0; i < super.getProject().getClasses().size(); i++) {
				Clazz clazz = super.getProject().getClasses().get(i);
				
				
				for (MethodDeclaration method : clazz.getTypeDeclaration().getMethods()) {
				
					UnusedParametersDesc unusedParametersDesc = new UnusedParametersDesc();
					
					List<VariableDeclaration> parameters = method.parameters();
					for (VariableDeclaration parameter : parameters) {
						if (operationsUtil.useParameter(parameter, method, super.getProject()) == 0) {
							unusedParametersDesc.addParameters(parameter);
						}
					}
					
					if (unusedParametersDesc.getParametersToBeRemoved().size() > 0) {
						unusedParametersDesc.setClazz(clazz);
						unusedParametersDesc.setMethod(method);
						unusedParameters.add(unusedParametersDesc);
					}
				}
			}
		
		return unusedParameters.size() > 0;
	}

	public List<UnusedParametersDesc> getUnusedParameters() {
		return unusedParameters;
	}
}

package br.ic.ufal.refactoring.detections.speculativegenerality.methods;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class SpeculativeParameters {

	private MethodDeclaration method = null;
	private List<SingleVariableDeclaration> parameters = new ArrayList<SingleVariableDeclaration>();
	
	public SpeculativeParameters() {
		// TODO Auto-generated constructor stub
	}

	public MethodDeclaration getMethod() {
		return method;
	}

	public void setMethod(MethodDeclaration method) {
		this.method = method;
	}

	public List<SingleVariableDeclaration> getParameters() {
		return parameters;
	}

	public void setParameters(List<SingleVariableDeclaration> parameters) {
		this.parameters = parameters;
	}
	
	public void addParameterSpec(SingleVariableDeclaration parameter){
		this.parameters.add(parameter);
	}

	@Override
	public String toString() {
		return "SpeculativeParametersList [method=" + method.getName() + ", parameters="
				+ parameters + "]";
	}

	
}

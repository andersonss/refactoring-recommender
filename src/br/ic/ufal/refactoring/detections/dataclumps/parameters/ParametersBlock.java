package br.ic.ufal.refactoring.detections.dataclumps.parameters;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

public class ParametersBlock {
	
	private List<SingleVariableDeclaration> paramters = new ArrayList<SingleVariableDeclaration>();
	
	public ParametersBlock() {
		// TODO Auto-generated constructor stub
	}

	public List<SingleVariableDeclaration> getParamters() {
		return paramters;
	}

	public void setParamters(List<SingleVariableDeclaration> paramters) {
		this.paramters = paramters;
	}

	public void addParameter(SingleVariableDeclaration parameter){
		this.paramters.add(parameter);
	}

	@Override
	public boolean equals(Object obj) {
		
		ParametersBlock block = (ParametersBlock) obj;
		
		for (SingleVariableDeclaration parameter : block.getParamters()) {
			if (!existParameter(parameter)) {
				return false;
			}
		}
		
		if (this.paramters.size() != block.getParamters().size()) {
			return false;
		}
		
		return true;
	}
	
	private boolean existParameter(SingleVariableDeclaration parameter){
		for (SingleVariableDeclaration p : this.paramters) {
			if (parameter.getType().toString().equalsIgnoreCase(p.getType().toString()) &&
				parameter.getName().getFullyQualifiedName().equalsIgnoreCase(p.getName().getFullyQualifiedName()))  {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "ParametersBlock [fragments=" + paramters + "]";
	}
}

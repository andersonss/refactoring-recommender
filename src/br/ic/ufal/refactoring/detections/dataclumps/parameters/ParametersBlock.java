package br.ic.ufal.refactoring.detections.dataclumps.parameters;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;

public class ParametersBlock {
	
	private List<VariableDeclaration> paramters = new ArrayList<VariableDeclaration>();
	
	public ParametersBlock() {
		// TODO Auto-generated constructor stub
	}

	public List<VariableDeclaration> getParamters() {
		return paramters;
	}

	public void setParamters(List<VariableDeclaration> paramters) {
		this.paramters = paramters;
	}

	public void addParameter(VariableDeclaration parameter){
		this.paramters.add(parameter);
	}

	@Override
	public boolean equals(Object obj) {
		
		ParametersBlock block = (ParametersBlock) obj;
		
		for (VariableDeclaration parameter : block.getParamters()) {
			if (!existParameter(parameter)) {
				return false;
			}
		}
		
		if (this.paramters.size() != block.getParamters().size()) {
			return false;
		}
		
		return true;
	}
	
	private boolean existParameter(VariableDeclaration parameter){
		SingleVariableDeclaration sparameter = (SingleVariableDeclaration)parameter;
		
		for (VariableDeclaration p : this.paramters) {
			SingleVariableDeclaration sp = (SingleVariableDeclaration)p;
			if (sparameter.getType().toString().equalsIgnoreCase(sp.getType().toString()) &&
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

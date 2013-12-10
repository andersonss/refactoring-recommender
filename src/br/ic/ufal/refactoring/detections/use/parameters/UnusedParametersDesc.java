package br.ic.ufal.refactoring.detections.use.parameters;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;

import br.ic.ufal.parser.Clazz;

public class UnusedParametersDesc {

	private Clazz clazz = null;
	private MethodDeclaration method = null;
	private Set<VariableDeclaration> parametersToBeRemoved = new HashSet<VariableDeclaration>();


	public UnusedParametersDesc() {
		// TODO Auto-generated constructor stub
	}


	public Clazz getClazz() {
		return clazz;
	}


	public void setClazz(Clazz clazz) {
		this.clazz = clazz;
	}

	public Set<VariableDeclaration> getParametersToBeRemoved() {
		return parametersToBeRemoved;
	}

	public MethodDeclaration getMethod() {
		return method;
	}
	
	public void setMethod(MethodDeclaration method) {
		this.method = method;
	}
	
	public void addParameters(VariableDeclaration fragment){
		this.parametersToBeRemoved.add(fragment);
	}


	@Override
	public String toString() {
		return "UnusedParametersDesc [clazz=" + clazz.getTypeDeclaration().getName() +
				", method=" + method.getName()
				+ ", parametersToBeRemoved=" + parametersToBeRemoved + "]";
	}


	
	
	
}

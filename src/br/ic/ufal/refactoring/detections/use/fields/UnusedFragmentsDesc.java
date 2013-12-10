package br.ic.ufal.refactoring.detections.use.fields;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.VariableDeclaration;

import br.ic.ufal.parser.Clazz;

public class UnusedFragmentsDesc {

	private Clazz clazz = null;
	private Set<VariableDeclaration> fragmentsToBeRemoved = new HashSet<VariableDeclaration>();


	public UnusedFragmentsDesc() {
		// TODO Auto-generated constructor stub
	}


	public Clazz getClazz() {
		return clazz;
	}


	public void setClazz(Clazz clazz) {
		this.clazz = clazz;
	}


	public Set<VariableDeclaration> getFragmentsToBeRemoved() {
		return fragmentsToBeRemoved;
	}


	public void setFragmentsToBeRemoved(Set<VariableDeclaration> fragmentsToBeRemoved) {
		this.fragmentsToBeRemoved = fragmentsToBeRemoved;
	}
	
	public void addFragment(VariableDeclaration fragment){
		this.fragmentsToBeRemoved.add(fragment);
	}


	@Override
	public String toString() {
		return "UnusedFragmentsDesc [clazz=" + clazz.getTypeDeclaration().getName()
				+ ", fragmentsToBeRemoved=" + fragmentsToBeRemoved + "]";
	}
	
	
}

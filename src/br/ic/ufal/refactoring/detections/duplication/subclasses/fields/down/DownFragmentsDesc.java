package br.ic.ufal.refactoring.detections.duplication.subclasses.fields.down;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.VariableDeclaration;

import br.ic.ufal.parser.Clazz;

public class DownFragmentsDesc {

	private Clazz superclass = null;
	private List<Clazz> subclasses = new ArrayList<Clazz>();
	private Set<VariableDeclaration> fragmentsToBeDown = new HashSet<VariableDeclaration>();
 
	public DownFragmentsDesc() {
		// TODO Auto-generated constructor stub
	}

	public Clazz getSuperclass() {
		return superclass;
	}

	public void setSuperclass(Clazz superclass) {
		this.superclass = superclass;
	}

	public List<Clazz> getSubclasses() {
		return subclasses;
	}

	public void setSubclasses(List<Clazz> subclasses) {
		this.subclasses = subclasses;
	}
	
	public void addSubclass(Clazz subclass){
		this.subclasses.add(subclass);
	}

	public Set<VariableDeclaration> getFragmentsToBeDown() {
		return fragmentsToBeDown;
	}

	public void setFragmentsToBeDown(Set<VariableDeclaration> fragmentsToBeDown) {
		this.fragmentsToBeDown = fragmentsToBeDown;
	}
	
	public void addFragment(VariableDeclaration fragment){
		this.fragmentsToBeDown.add(fragment);
	}

	@Override
	public String toString() {
		
		String sclasses = new String();
		for (Clazz subclass : this.subclasses) {
			sclasses = sclasses + subclass.getTypeDeclaration().getName()+" ,"; 
		}
		
		return "DownFragmentsDesc ["
				+ "superclass=" + superclass.getTypeDeclaration().getName() + 
				", subclasses="+ sclasses + 
				", fragmentsToBeDown=" + fragmentsToBeDown + "]";
	}
	
	
	
}

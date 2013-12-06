package br.ic.ufal.refactoring.detections.duplication.subclasses.methods.down;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import br.ic.ufal.parser.Clazz;

public class DownMethodsDesc {

	private Clazz superclass = null;
	private List<Clazz> subclasses = new ArrayList<Clazz>();
	private List<MethodDeclaration> methodsToBeDown = new ArrayList<MethodDeclaration>();
 
	public DownMethodsDesc() {
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

	public List<MethodDeclaration> getMethodsToBeDown() {
		return methodsToBeDown;
	}

	public void setMethodsToBeDown(List<MethodDeclaration> methodsToBeDown) {
		this.methodsToBeDown = methodsToBeDown;
	}
	
	public void addMethod(MethodDeclaration method){
		this.methodsToBeDown.add(method);
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
				", methodsToBeDown=" + methodsToBeDown + "]";
	}
	
	
	
}

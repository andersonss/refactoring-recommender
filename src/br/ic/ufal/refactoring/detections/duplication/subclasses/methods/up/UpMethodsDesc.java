package br.ic.ufal.refactoring.detections.duplication.subclasses.methods.up;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import br.ic.ufal.parser.Clazz;

public class UpMethodsDesc {

	private Clazz superclass = null;
	private List<Clazz> subclasses = new ArrayList<Clazz>();
	private List<MethodDeclaration> methodsToBeUp = new ArrayList<MethodDeclaration>();
	
	public UpMethodsDesc() {
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

	public List<MethodDeclaration> getMethodsToBeUp() {
		return methodsToBeUp;
	}

	public void setMethodsToBeUp(List<MethodDeclaration> methodsToBeUp) {
		this.methodsToBeUp = methodsToBeUp;
	}
	
	public void addMethodToBeUp(MethodDeclaration methodDeclaration){
		this.methodsToBeUp.add(methodDeclaration);
	}

	@Override
	public String toString() {
		
		String subcs = new String();
		
		for (Clazz subclass : subclasses) {
			subcs = subcs + subclass.getTypeDeclaration().getName() + ", ";
		}
		
		return "UpMethodsDesc [superclass=" + superclass.getTypeDeclaration().getName() + ", subclasses="
				+ subcs + ", methodsToBeUp=" + methodsToBeUp + "]";
	}
	
	
}

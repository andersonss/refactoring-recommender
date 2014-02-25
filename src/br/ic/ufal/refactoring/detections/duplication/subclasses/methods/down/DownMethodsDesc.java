package br.ic.ufal.refactoring.detections.duplication.subclasses.methods.down;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import br.ic.ufal.parser.Clazz;

public class DownMethodsDesc {

	private Clazz superclass = null;
	private List<Clazz> subclasses = new ArrayList<Clazz>();
	private List<MethodDeclaration> methodsToBeDown = new ArrayList<MethodDeclaration>();

	/**
	 * 
	 */
	public DownMethodsDesc() {
	}

	/**
	 * 
	 * @return superclass
	 */
	public Clazz getSuperclass() {
		return superclass;
	}

	/**
	 * 
	 * @param superclass
	 */
	public void setSuperclass(Clazz superclass) {
		this.superclass = superclass;
	}

	/**
	 * @return
	 */
	public List<Clazz> getSubclasses() {
		return subclasses;
	}

	/**
	 * 
	 * @param subclasses
	 */
	public void setSubclasses(List<Clazz> subclasses) {
		this.subclasses = subclasses;
	}

	/**
	 * 
	 * @param subclass
	 */
	public void addSubclass(Clazz subclass) {
		this.subclasses.add(subclass);
	}

	/**
	 * 
	 * @return
	 */
	public List<MethodDeclaration> getMethodsToBeDown() {
		return methodsToBeDown;
	}

	/**
	 * 
	 * @param methodsToBeDown
	 */
	public void setMethodsToBeDown(List<MethodDeclaration> methodsToBeDown) {
		this.methodsToBeDown = methodsToBeDown;
	}

	/**
	 * 
	 * @param method
	 */
	public void addMethod(MethodDeclaration method) {
		this.methodsToBeDown.add(method);
	}

	@Override
	public String toString() {

		String sclasses = new String();
		for (Clazz subclass : this.subclasses) {
			sclasses = sclasses + subclass.getTypeDeclaration().getName()
					+ " ,";
		}

		return "DownFragmentsDesc [" + "superclass="
				+ superclass.getTypeDeclaration().getName() + ", subclasses="
				+ sclasses + ", methodsToBeDown=" + methodsToBeDown + "]";
	}

}

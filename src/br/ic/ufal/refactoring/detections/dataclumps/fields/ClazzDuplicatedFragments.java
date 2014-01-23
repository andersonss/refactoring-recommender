package br.ic.ufal.refactoring.detections.dataclumps.fields;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.VariableDeclaration;

import br.ic.ufal.parser.Clazz;

public class ClazzDuplicatedFragments {

	private List<Clazz> classes = new ArrayList<Clazz>();
	private List<VariableDeclaration> fragments = new ArrayList<VariableDeclaration>();
	
	public ClazzDuplicatedFragments() {
		// TODO Auto-generated constructor stub
	}

	public List<Clazz> getClasses() {
		return classes;
	}

	public void setClasses(List<Clazz> classes) {
		this.classes = classes;
	}

	public List<VariableDeclaration> getFragments() {
		return fragments;
	}

	public void setFragments(List<VariableDeclaration> fragments) {
		this.fragments = fragments;
	}

	@Override
	public String toString() {
		return "ClazzDuplicatedFragments [classes=" + classes + ", fragments="
				+ fragments + "]";
	}
	
	

}

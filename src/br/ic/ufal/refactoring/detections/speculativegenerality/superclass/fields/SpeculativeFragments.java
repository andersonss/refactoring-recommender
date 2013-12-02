package br.ic.ufal.refactoring.detections.speculativegenerality.superclass.fields;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class SpeculativeFragments {

	private TypeDeclaration clazz = null; 
	private List<VariableDeclarationFragment> speculativeFragments = new ArrayList<VariableDeclarationFragment>();
	
	public SpeculativeFragments() {
	
	}

	public TypeDeclaration getClazz() {
		return clazz;
	}

	public void setClazz(TypeDeclaration clazz) {
		this.clazz = clazz;
	}

	public List<VariableDeclarationFragment> getSpeculativeFragments() {
		return speculativeFragments;
	}

	public void setSpeculativeFragments(
			List<VariableDeclarationFragment> speculativeFragments) {
		this.speculativeFragments = speculativeFragments;
	}

	public void addSpeculativeFragment(VariableDeclarationFragment fragment){
		this.speculativeFragments.add(fragment);
	}

	@Override
	public String toString() {
		return "SpeculativeFields [clazz=" + clazz.getName() + ", speculativeFragments="
				+ speculativeFragments + "]";
	}
	
}

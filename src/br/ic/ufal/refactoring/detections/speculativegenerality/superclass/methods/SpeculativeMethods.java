package br.ic.ufal.refactoring.detections.speculativegenerality.superclass.methods;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class SpeculativeMethods {

	private TypeDeclaration clazz = null;
	private List<MethodDeclaration> speculativeMethods = new ArrayList<MethodDeclaration>();	
			
	public SpeculativeMethods() {
		// TODO Auto-generated constructor stub
	}

	public TypeDeclaration getClazz() {
		return clazz;
	}

	public void setClazz(TypeDeclaration clazz) {
		this.clazz = clazz;
	}

	public List<MethodDeclaration> getSpeculativeMethods() {
		return speculativeMethods;
	}

	public void setSpeculativeMethods(List<MethodDeclaration> speculativeMethods) {
		this.speculativeMethods = speculativeMethods;
	}

	public void addSpeculativeMethod(MethodDeclaration method){
		this.speculativeMethods.add(method);
	}

	@Override
	public String toString() {
		
		String methods = new String();
		for (MethodDeclaration method : this.speculativeMethods) {
			methods = methods + method.getName() + ", ";
		}
		
		return "SpeculativeMethods [clazz=" + clazz.getName() + ", speculativeMethods="
				+ methods + "]";
	}
	
	
}

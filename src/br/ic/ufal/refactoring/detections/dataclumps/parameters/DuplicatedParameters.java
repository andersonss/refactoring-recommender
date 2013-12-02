package br.ic.ufal.refactoring.detections.dataclumps.parameters;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import br.ic.ufal.parser.Clazz;


public class DuplicatedParameters {
	
	private List<Clazz> duplicatedClasses = new ArrayList<Clazz>();
	private List<MethodDeclaration> duplicatedMethods = new ArrayList<MethodDeclaration>();
	private ParametersBlock parametersBlock = new ParametersBlock();
	
	public DuplicatedParameters() {
		// TODO Auto-generated constructor stub
	}

	public List<Clazz> getDuplicatedClasses() {
		return duplicatedClasses;
	}

	public void setDuplicatedClasses(List<Clazz> duplicatedClasses) {
		this.duplicatedClasses = duplicatedClasses;
	}

	public void addDuplicatedClass(Clazz c){
		this.duplicatedClasses.add(c);
	}
	
	public List<MethodDeclaration> getDuplicatedMethods() {
		return duplicatedMethods;
	}

	public void setDuplicatedMethods(List<MethodDeclaration> duplicatedMethods) {
		this.duplicatedMethods = duplicatedMethods;
	}

	public ParametersBlock getBlock() {
		return parametersBlock;
	}

	public void setBlock(ParametersBlock parametersBlock) {
		this.parametersBlock = parametersBlock;
	}

	public void addDuplicatedMethod(MethodDeclaration method){
		this.duplicatedMethods.add(method);
	}
	
	@Override
	public boolean equals(Object obj) {
		
		DuplicatedParameters description = (DuplicatedParameters)obj;
		
		boolean existDescription = true;
		
		for (Clazz clazz : description.getDuplicatedClasses()) {
			if (!existClasse(clazz)) {
				existDescription = false;
			}
		}
		
		if (this.duplicatedClasses.size()!= description.getDuplicatedClasses().size()) {
			return false;
		}
		
		for (MethodDeclaration method : description.getDuplicatedMethods()) {
			if (!existMethod(method)) {
				existDescription = false;
			}
		}
		
		if (this.duplicatedMethods.size()!= description.getDuplicatedMethods().size()) {
			return false;
		}
		
		if (!this.parametersBlock.equals(description.getBlock())) {
			return false;
		}
		
		return existDescription;
	}
	
	private boolean existMethod(MethodDeclaration method){
		for (MethodDeclaration m : this.duplicatedMethods) {
			if (m.resolveBinding().isEqualTo(method.resolveBinding())) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean existClasse(Clazz clazz){
		for (Clazz c : this.duplicatedClasses) {
			if (c.getTypeDeclaration().resolveBinding().isEqualTo(c.getTypeDeclaration().resolveBinding())) {
				return true;
			}
		}
		
		return false;
	}
	
	
	@Override
	public String toString() {
		
		String dcs = new String();
		for (Clazz c : this.duplicatedClasses) {
			dcs = dcs + c.getTypeDeclaration().getName() + ", ";
		}
		
		String dms = new String();
		for (MethodDeclaration method : duplicatedMethods) {
			dms = dms + method.getName() + ", ";
		}
		
		return "\n DuplicatedParameters [duplicatedClasses=" + dcs
				+ ", duplicatedMethods= " + dms
				+ ", Parameters Block = " + this.parametersBlock + "]";
		
		
		
	}

}

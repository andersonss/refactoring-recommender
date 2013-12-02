package br.ic.ufal.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import br.ic.ufal.parser.Clazz;

public class DuplicatedStatements {

	private List<Clazz> duplicatedClasses = new ArrayList<Clazz>();
	private List<MethodDeclaration> duplicatedMethods = new ArrayList<MethodDeclaration>();
	private StatementsBlock statementsBlocks = new StatementsBlock();

	public DuplicatedStatements() {
		// TODO Auto-generated constructor stub
	}
	
	public List<Clazz> getDuplicatedClasses() {
		return duplicatedClasses;
	}
	
	public void setDuplicatedClasses(List<Clazz> duplicatedClasses) {
		this.duplicatedClasses = duplicatedClasses;
	}
	
	public void addDuplicatedClass(Clazz clazz){
		this.duplicatedClasses.add(clazz);
	}
	
	public List<MethodDeclaration> getDuplicatedMethods() {
		return duplicatedMethods;
	}

	public void setDuplicatedMethods(List<MethodDeclaration> duplicatedMethods) {
		this.duplicatedMethods = duplicatedMethods;
	}

	public StatementsBlock getBlocks() {
		return statementsBlocks;
	}

	public void setBlocks(StatementsBlock statementsBlocks) {
		this.statementsBlocks = statementsBlocks;
	}

	public void addDuplicatedMethod(MethodDeclaration method){
		this.duplicatedMethods.add(method);
	}
	
	@Override
	public boolean equals(Object obj) {
		
		DuplicatedStatements description = (DuplicatedStatements)obj;
		
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
		
		if (!this.statementsBlocks.equals(description.getBlocks())) {
			return false;
		}
		
		return existDescription;
	}
	
	private boolean existMethod(MethodDeclaration method){
		for (MethodDeclaration m : this.duplicatedMethods) {
			if (m.getName().getFullyQualifiedName().equalsIgnoreCase(method.getName().getFullyQualifiedName())) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean existClasse(Clazz clazz){
		for (Clazz c : this.duplicatedClasses) {
			if (c.getTypeDeclaration().getName().getFullyQualifiedName().equalsIgnoreCase(clazz.getTypeDeclaration().getName().getFullyQualifiedName())) {
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
		
			return "DuplicatedStatements [duplicatedClasses= " + dcs
					+ "\n duplicatedMethods= " + dms + "\n statementsBlocks=" + statementsBlocks + "]";
		
	}

	
	
}

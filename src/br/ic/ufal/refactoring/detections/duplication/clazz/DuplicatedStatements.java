package br.ic.ufal.refactoring.detections.duplication.clazz;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import br.ic.ufal.parser.Clazz;

public class DuplicatedStatements {

	private List<Clazz> duplicatedClasses = new ArrayList<Clazz>();
	private List<MethodDeclaration> duplicatedMethods = new ArrayList<MethodDeclaration>();
	private StatementsBlock statementsBlock = new StatementsBlock();

	/**
	 * 
	 */
	public DuplicatedStatements() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @return
	 */
	public List<Clazz> getDuplicatedClasses() {
		return duplicatedClasses;
	}

	/**
	 * @param duplicatedClasses
	 */
	public void setDuplicatedClasses(List<Clazz> duplicatedClasses) {
		this.duplicatedClasses = duplicatedClasses;
	}

	/**
	 * @param c
	 */
	public void addDuplicatedClass(Clazz c) {
		this.duplicatedClasses.add(c);
	}

	/**
	 * @return
	 */
	public List<MethodDeclaration> getDuplicatedMethods() {
		return duplicatedMethods;
	}

	/**
	 * @param duplicatedMethods
	 */
	public void setDuplicatedMethods(List<MethodDeclaration> duplicatedMethods) {
		this.duplicatedMethods = duplicatedMethods;
	}

	/**
	 * @return
	 */
	public StatementsBlock getBlock() {
		return statementsBlock;
	}

	/**
	 * @param parametersBlock
	 */
	public void setBlock(StatementsBlock parametersBlock) {
		this.statementsBlock = parametersBlock;
	}

	/**
	 * @param method
	 */
	public void addDuplicatedMethod(MethodDeclaration method) {
		this.duplicatedMethods.add(method);
	}

	@Override
	public boolean equals(Object obj) {

		DuplicatedStatements description = (DuplicatedStatements) obj;

		boolean existDescription = true;

		for (Clazz clazz : description.getDuplicatedClasses()) {
			if (!existClasse(clazz)) {
				existDescription = false;
			}
		}

		if (this.duplicatedClasses.size() != description.getDuplicatedClasses()
				.size()) {
			return false;
		}

		for (MethodDeclaration method : description.getDuplicatedMethods()) {
			if (!existMethod(method)) {
				existDescription = false;
			}
		}

		if (this.duplicatedMethods.size() != description.getDuplicatedMethods()
				.size()) {
			return false;
		}

		if (!this.statementsBlock.equals(description.getBlock())) {
			return false;
		}

		return existDescription;
	}

	/**
	 * @param method
	 * @return
	 */
	private boolean existMethod(MethodDeclaration method) {
		for (MethodDeclaration m : this.duplicatedMethods) {
			if (m.resolveBinding().isEqualTo(method.resolveBinding())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @param clazz
	 * @return
	 */
	private boolean existClasse(Clazz clazz) {
		for (Clazz c : this.duplicatedClasses) {
			if (c.getTypeDeclaration().resolveBinding()
					.isEqualTo(c.getTypeDeclaration().resolveBinding())) {
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

		return "\n DuplicatedStatements [duplicatedClasses=" + dcs
				+ ", duplicatedMethods= " + dms + ", Statements Block = "
				+ this.statementsBlock + "]";

	}

}

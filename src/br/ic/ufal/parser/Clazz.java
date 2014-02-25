package br.ic.ufal.parser;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jface.text.Document;

public class Clazz {

	private CompilationUnit compilationUnit = null;
	private ICompilationUnit iCompilationUnit = null;
	private TypeDeclaration typeDeclaration = null;
	private Document document = null;

	/**
	 * 
	 */
	public Clazz() {

	}

	/**
	 * 
	 * @return
	 */
	public CompilationUnit getCompilationUnit() {

		return compilationUnit;
	}

	/**
	 * 
	 * @param compilationUnit
	 */
	public void setCompilationUnit(CompilationUnit compilationUnit) {
		this.compilationUnit = compilationUnit;
	}

	/**
	 * 
	 * @return
	 */
	public ICompilationUnit getICompilationUnit() {
		return iCompilationUnit;
	}

	/**
	 * 
	 * @param iCompilationUnit
	 */
	public void setICompilationUnit(ICompilationUnit iCompilationUnit) {
		this.iCompilationUnit = iCompilationUnit;
	}

	/**
	 * 
	 * @return
	 */
	public TypeDeclaration getTypeDeclaration() {
		return typeDeclaration;
	}

	/**
	 * 
	 * @param typeDeclaration
	 */
	public void setTypeDeclaration(TypeDeclaration typeDeclaration) {
		this.typeDeclaration = typeDeclaration;
	}

	/**
	 * 
	 * @return
	 */
	public Document getDocument() {
		return document;
	}

	/**
	 * 
	 * @param document
	 */
	public void setDocument(Document document) {
		this.document = document;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Clazz: " + this.typeDeclaration.getName();
	}

}

package br.ic.ufal.util;

import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jface.text.Document;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.parser.Project;

public class ParseUtil {

	public ParseUtil() {
		
	}
	
	public static void updateClazz(Document document, Clazz clazz, Project proj) throws JavaModelException{
		for (Clazz c : proj.getClasses()) {
			if (c.getTypeDeclaration().getName().getIdentifier().equalsIgnoreCase(clazz.getTypeDeclaration().getName().getIdentifier())) {
				c.setDocument(document);
				c.getICompilationUnit().getBuffer().setContents(c.getDocument().get());
				c.setCompilationUnit(toCompilationUnit(c.getICompilationUnit()));
				c.setTypeDeclaration(getTypeDeclaration(c.getCompilationUnit()));
			}
		}
	}

	public static CompilationUnit toCompilationUnit(ICompilationUnit unit) {
		
		System.out.println(unit.getElementName());
	    ASTParser parser = ASTParser.newParser(AST.JLS4);
	    parser.setKind(ASTParser.K_COMPILATION_UNIT);
	    parser.setSource(unit);
	    parser.setResolveBindings(true);
	    
	    return (CompilationUnit) parser.createAST(null);
	}
	
	public static Clazz getClazz(ITypeBinding iTypeBinding, List<Clazz> classes){
		
		for (Clazz clazz : classes) {
			if (clazz.getTypeDeclaration() != null) {
				if (clazz.getTypeDeclaration().resolveBinding().isEqualTo(iTypeBinding)) {
					return clazz;
				}
			}
			
		}
		
		return null;
	}
	
	public static TypeDeclaration getTypeDeclaration(ITypeBinding iTypeBinding, List<Clazz> classes){
		
		for (Clazz clazz : classes) {
			if (clazz.getTypeDeclaration() != null) {
				if (clazz.getTypeDeclaration().resolveBinding().isEqualTo(iTypeBinding)) {
					return clazz.getTypeDeclaration();
				}
			}
			
		}
		
		return null;
	}
	
	public static Clazz getClazz(Class c, List<Clazz> classes){
		
		for (Clazz clazz : classes) {
			if (clazz.getTypeDeclaration() != null) {
				if (clazz.getTypeDeclaration().getClass().equals(c)) {
					return clazz;
				}
			}
			
		}
		
		return null;
	}
	
	
	public static Clazz getClazz(CompilationUnit compilationUnit, List<Clazz> classes){
		
		TypeDeclaration typeDeclaration = getTypeDeclaration(compilationUnit);
		
		for (Clazz clazz : classes) {
			if (typeDeclaration.resolveBinding().isEqualTo(clazz.getTypeDeclaration().resolveBinding())) {
				return clazz;
			}
		}
		return null;
	}
	
	public static Clazz getClazz(TypeDeclaration typeDeclaration, List<Clazz> classes){
		
		for (Clazz clazz : classes) {
			if (typeDeclaration.resolveBinding().isEqualTo(clazz.getTypeDeclaration().resolveBinding())) {
				return clazz;
			}
		}
		return null;
	}
	
	public static Clazz getClazz(Type type, List<Clazz> classes){
		
		for (Clazz clazz : classes) {
			if (type.resolveBinding().isEqualTo(clazz.getTypeDeclaration().resolveBinding())) {
				return clazz;
			}
		}
		return null;
	}
	
	public static TypeDeclaration getTypeDeclaration(CompilationUnit compilationUnit){
		
		
			List<AbstractTypeDeclaration> types = compilationUnit.types();
			
			for (AbstractTypeDeclaration abstracttype : types) {
				
				if (abstracttype instanceof TypeDeclaration) {
					TypeDeclaration typedecl = (TypeDeclaration) abstracttype;
					
					return typedecl;
				}
			}
		
		return null;
		
	}
	
	
	
	
	
	public static Document getDocument(ICompilationUnit iCompilationUnit){
		Document sourceDocument = null;
		
		try {
			sourceDocument = new Document(iCompilationUnit.getBuffer().getContents());
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return sourceDocument;
	}
}

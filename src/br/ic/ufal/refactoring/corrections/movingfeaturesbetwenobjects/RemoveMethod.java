package br.ic.ufal.refactoring.corrections.movingfeaturesbetwenobjects;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.corrections.Correction;

public class RemoveMethod extends Correction {
	
	private CompilationUnit sourceCompilationUnit = null;
	private ICompilationUnit sourceICompilationUnit = null;
	private TypeDeclaration sourceTypeDeclaration = null;
	private Document sourceDocument = null;
	
	private MethodDeclaration methodToBeRemoved = null;
	
	private MultiTextEdit sourceMultiTextEdit = null;
	
	public RemoveMethod(Clazz sourceClass, MethodDeclaration methodDeclaration, Project project) {
		super(project);
		
		this.sourceCompilationUnit = sourceClass.getCompilationUnit();
		this.sourceICompilationUnit = sourceClass.getICompilationUnit();
		this.sourceTypeDeclaration = sourceClass.getTypeDeclaration();
		this.sourceDocument = sourceClass.getDocument();
		
		this.methodToBeRemoved = methodDeclaration;
		
		this.sourceMultiTextEdit = new MultiTextEdit();
		
	}

	@Override
	public void execute() {
		
		removeMethod();
		
		try {
			
			this.sourceMultiTextEdit.apply(this.sourceDocument);
			
			this.sourceICompilationUnit.getBuffer().setContents(this.sourceDocument.get());
			
			System.out.println(this.sourceDocument.get());
			
		} catch (MalformedTreeException e) {
			e.printStackTrace();
		} catch (BadLocationException e) {
			e.printStackTrace();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}
	
	private void removeMethod() {
		ASTRewrite sourceRewriter = ASTRewrite.create(sourceCompilationUnit.getAST());
		ListRewrite classBodyRewrite = sourceRewriter.getListRewrite(sourceTypeDeclaration, TypeDeclaration.BODY_DECLARATIONS_PROPERTY);
		
		for (MethodDeclaration methodDeclaration : this.sourceTypeDeclaration.getMethods()) {
			
			if (methodToBeRemoved.resolveBinding().isSubsignature(methodDeclaration.resolveBinding()) ) {
				classBodyRewrite.remove(methodDeclaration, null);
			}
			
		}
		
		TextEdit sourceEdit = sourceRewriter.rewriteAST(this.sourceDocument, this.sourceICompilationUnit.getJavaProject().getOptions(true));
		sourceMultiTextEdit.addChild(sourceEdit);
	
		
		
	}

}

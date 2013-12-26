package br.ic.ufal.refactoring.corrections.movingfeaturesbetwenobjects;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
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

public class RemoveParameters extends Correction {
	
	private TypeDeclaration sourceTypeDeclaration = null;
	private ICompilationUnit sourceICompilationUnit = null;
	private Document sourceDocument = null;
	
	private MethodDeclaration sourceMethod = null;
	
	private Set<VariableDeclaration> parametersToBeRemoved = null;
	
	private MultiTextEdit sourceMultiTextEdit = null;
	
	public RemoveParameters(Clazz clazz, MethodDeclaration methodDeclaration, Set<VariableDeclaration> parametersToBeRemoved, Project project) {
		super(project);
		
		this.sourceTypeDeclaration = clazz.getTypeDeclaration();
		this.sourceICompilationUnit = clazz.getICompilationUnit();
		this.sourceDocument = clazz.getDocument();
		
		this.sourceMethod = methodDeclaration;
		
		this.parametersToBeRemoved = parametersToBeRemoved;
		
		this.sourceMultiTextEdit = new MultiTextEdit();
	}

	@Override
	public void apply() {
		
		removeParametersInSourceMethod();
		
		try {
			this.sourceMultiTextEdit.apply(this.sourceDocument);
			this.sourceICompilationUnit.getBuffer().setContents(this.sourceDocument.get());
			
			System.out.println("Code Source ");
	        System.out.println(this.sourceDocument.get());
			
		} catch (MalformedTreeException e) {
			e.printStackTrace();
		} catch (BadLocationException e) {
			e.printStackTrace();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}

	}
	
	private void removeParametersInSourceMethod( ) {
		
			if(parametersToBeRemoved.size() > 0) {
				ASTRewrite sourceRewriter = ASTRewrite.create(this.sourceTypeDeclaration.getAST());
				
				ListRewrite parameterRewrite = sourceRewriter.getListRewrite(this.sourceMethod, MethodDeclaration.PARAMETERS_PROPERTY);
					
				for(VariableDeclaration parameter : parametersToBeRemoved) {
					parameterRewrite.remove(parameter, null);
				}
				
				TextEdit sourceEdit = sourceRewriter.rewriteAST(this.sourceDocument, null);
				this.sourceMultiTextEdit.addChild(sourceEdit);
			}
		}

}

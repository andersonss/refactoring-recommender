package br.ic.ufal.refactoring.corrections.movingfeaturesbetwenobjects;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
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
import br.ic.ufal.util.ParseUtil;

public class RemoveFragments extends Correction {
	
	private Clazz sourceClass = null;
	private TypeDeclaration sourceTypeDeclaration = null;
	private ICompilationUnit sourceICompilationUnit = null;
	private Document sourceDocument = null;
	
	private Set<VariableDeclaration> fragmentsToBeRemoved = null;
	
	private MultiTextEdit sourceMultiTextEdit = null;
	
	public RemoveFragments(Clazz clazz, Set<VariableDeclaration> fragments, Project project) {
		super(project);
		
		this.sourceClass = clazz;
		
		this.sourceTypeDeclaration = clazz.getTypeDeclaration();
		this.sourceICompilationUnit = clazz.getICompilationUnit();
		this.sourceDocument = clazz.getDocument();
		
		this.fragmentsToBeRemoved = fragments;
		
		this.sourceMultiTextEdit = new MultiTextEdit();
	}

	@Override
	public void apply() {
		
		removeFieldFragmentsInSourceClass();
		
		try {
			this.sourceMultiTextEdit.apply(this.sourceDocument);
			//this.sourceICompilationUnit.getBuffer().setContents(this.sourceDocument.get());
			
			ParseUtil.updateClazz(this.sourceDocument, this.sourceClass, getProject());
			
			//System.out.println("Code Source ");
	        //System.out.println(this.sourceDocument.get());
			
		} catch (MalformedTreeException e) {
			e.printStackTrace();
		} catch (BadLocationException e) {
			e.printStackTrace();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}

	}
	
	private void removeFieldFragmentsInSourceClass( ) {
		
		FieldDeclaration[] fieldDeclarations = this.sourceTypeDeclaration.getFields();
		for(FieldDeclaration fieldDeclaration : fieldDeclarations) {
			List<VariableDeclarationFragment> fragments = fieldDeclaration.fragments();
			int actualNumberOfFragments = fragments.size();
			Set<VariableDeclaration> fragmentsToBeRemoved = new LinkedHashSet<VariableDeclaration>();
			for(VariableDeclarationFragment fragment : fragments) {
				/*if(fieldFragments.contains(fragment)) {
					fragmentsToBeRemoved.add(fragment);
				}*/
				for (VariableDeclaration fieldFragment : this.fragmentsToBeRemoved) {
					if (fieldFragment.getInitializer() != null && fragment.getInitializer() != null) {
						if (fieldFragment.getName().toString().equalsIgnoreCase(fragment.getName().toString()) &&
							fieldFragment.getInitializer().toString().equalsIgnoreCase(fragment.getInitializer().toString())) {
							fragmentsToBeRemoved.add(fragment);
						}
					}if (fieldFragment.getInitializer() == null && fragment.getInitializer() == null) {
						if (fieldFragment.getName().toString().equalsIgnoreCase(fragment.getName().toString()) ) {
							fragmentsToBeRemoved.add(fragment);
						}
					}
						
				}
				
				
			}
			if(fragmentsToBeRemoved.size() > 0) {
				ASTRewrite sourceRewriter = ASTRewrite.create(this.sourceTypeDeclaration.getAST());
				ListRewrite contextBodyRewrite = sourceRewriter.getListRewrite(this.sourceTypeDeclaration, TypeDeclaration.BODY_DECLARATIONS_PROPERTY);
				if(actualNumberOfFragments == fragmentsToBeRemoved.size()) {
					contextBodyRewrite.remove(fieldDeclaration, null);
				}
				else if(fragmentsToBeRemoved.size() < actualNumberOfFragments) {
					ListRewrite fragmentRewrite = sourceRewriter.getListRewrite(fieldDeclaration, FieldDeclaration.FRAGMENTS_PROPERTY);
					for(VariableDeclaration fragment : fragmentsToBeRemoved) {
						fragmentRewrite.remove(fragment, null);
					}
				}
				TextEdit sourceEdit = sourceRewriter.rewriteAST(this.sourceDocument, null);
				this.sourceMultiTextEdit.addChild(sourceEdit);
			}
		}
	}

}

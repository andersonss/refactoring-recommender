package br.ic.ufal.refactoring.corrections.composingmethods;

import java.util.Random;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.corrections.Correction;
import br.ic.ufal.refactoring.detections.duplication.clazz.DuplicatedStatements;
import br.ic.ufal.util.ParseUtil;

//TODO Extract Method
public class ExtractMethod extends Correction {

	private DuplicatedStatements duplicatedStatements = null;
	
	private MultiTextEdit targetMultiTextEdit = null;
	
	public ExtractMethod(DuplicatedStatements duplicatedStatements, Project project) {
		super(project);
		this.duplicatedStatements = duplicatedStatements;
	}

	@Override
	public void apply() {
		
			System.out.println("Applying Extract Method");
		
			this.targetMultiTextEdit = new MultiTextEdit();
			
			Clazz clazz = this.duplicatedStatements.getDuplicatedClasses().get(0);
			
			createMethod(clazz);
				
			try {
				this.targetMultiTextEdit.apply(clazz.getDocument());
				ParseUtil.updateClazz(clazz.getDocument(), clazz, getProject());
			} catch (MalformedTreeException e) {
				e.printStackTrace();
			} catch (BadLocationException e) {
				e.printStackTrace();
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
				
			System.out.println("Applied Extract Method");
	}
	
	private void createMethod(Clazz clazz){
		
		ASTRewrite targetRewriter = ASTRewrite.create(clazz.getTypeDeclaration().getAST());
		AST ast = clazz.getTypeDeclaration().getAST();
		MethodDeclaration newMethodDeclaration = ast.newMethodDeclaration();
		
		//TODO Modificar geracao de novos methods
		targetRewriter.set(newMethodDeclaration, MethodDeclaration.NAME_PROPERTY, ast.newSimpleName("test"+(new Random()).nextInt()), null);
		ListRewrite extractedClassConstructorModifiersRewrite = targetRewriter.getListRewrite(newMethodDeclaration, MethodDeclaration.MODIFIERS2_PROPERTY);
		extractedClassConstructorModifiersRewrite.insertLast(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD), null);
    	
		Block extractedClassConstructorBody = ast.newBlock();
		targetRewriter.set(newMethodDeclaration, MethodDeclaration.BODY_PROPERTY, extractedClassConstructorBody, null);
    	
		ListRewrite targetClassBodyRewrite = targetRewriter.getListRewrite(clazz.getTypeDeclaration(), TypeDeclaration.BODY_DECLARATIONS_PROPERTY);
		targetClassBodyRewrite.insertLast(newMethodDeclaration, null);
		TextEdit targetEdit = targetRewriter.rewriteAST(clazz.getDocument(), clazz.getICompilationUnit().getJavaProject().getOptions(true));
		targetMultiTextEdit.addChild(targetEdit);
	}

}

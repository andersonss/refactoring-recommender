package br.ic.ufal.refactoring.corrections.composingmethods;

import java.util.ArrayList;
import java.util.List;

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
import br.ic.ufal.refactoring.detections.duplication.clazz.StatementsBlock;
import br.ic.ufal.util.ParseUtil;

//TODO Extract Method
public class ExtractMethod extends Correction {

	private List<DuplicatedStatements> duplicatedStatements = null;
	
	private MultiTextEdit targetMultiTextEdit = null;
	
	public ExtractMethod(List<DuplicatedStatements> duplicatedStatements, Project project) {
		super(project);
		
		this.duplicatedStatements = duplicatedStatements;
	}

	@Override
	public void execute() {
		
		for (int i = 0; i < this.duplicatedStatements.size(); i++) {
			
			System.out.println("Solving Duplication Statements: " + i + " of: " + this.duplicatedStatements.size());
			
			this.targetMultiTextEdit = new MultiTextEdit();
			
			DuplicatedStatements duplicatedStatement = this.duplicatedStatements.get(i);
			
			Clazz clazz = duplicatedStatement.getDuplicatedClasses().get(0);
			List<MethodDeclaration> methods = duplicatedStatement.getDuplicatedMethods();
			StatementsBlock statementsBlock = duplicatedStatement.getBlock();
			
				createMethod(clazz, i);
				
				try {
					
					this.targetMultiTextEdit.apply(clazz.getDocument());
					
					ParseUtil.updateClazz(clazz.getDocument(), clazz, getProject());
				
					//System.out.println(ParseUtil.getClazz(clazz.getTypeDeclaration(), getProject().getClasses()).getDocument().get());

				} catch (MalformedTreeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	}
	
	
	private void createMethod(Clazz clazz, int i){
		
		ASTRewrite targetRewriter = ASTRewrite.create(clazz.getTypeDeclaration().getAST());
		AST ast = clazz.getTypeDeclaration().getAST();
		MethodDeclaration newMethodDeclaration = ast.newMethodDeclaration();
		
		targetRewriter.set(newMethodDeclaration, MethodDeclaration.NAME_PROPERTY, ast.newSimpleName("test"+i), null);
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

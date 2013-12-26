package br.ic.ufal.refactoring.corrections.makingmethodcallsimpler;

import java.util.List;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.MethodDeclaration;
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
import br.ic.ufal.util.ParseUtil;

public class RemoveMethods extends Correction {

	
	private List<MethodDeclaration> methodsToBeRemoved = null;
	
	public RemoveMethods(Project project, List<MethodDeclaration> methodsToBeRemoved) {
		super(project);
		this.methodsToBeRemoved = methodsToBeRemoved;
	}
	
	@Override
	public void apply() {
		
		System.out.println("Removing Unused Methods");
		
		
		int count = 0;
		
		for (Clazz clazz : super.getProject().getClasses()) {
			
			System.out.println("Removing Unsued Methods in Class: " + count + " of " + super.getProject().getClasses().size());
			
			MultiTextEdit sourceMultiTextEdit = new MultiTextEdit();
			ASTRewrite sourceRewriter = ASTRewrite.create(clazz.getCompilationUnit().getAST());
			ListRewrite classBodyRewrite = sourceRewriter.getListRewrite(clazz.getTypeDeclaration(), TypeDeclaration.BODY_DECLARATIONS_PROPERTY);
		
			for (MethodDeclaration methodToBeRemoved : this.methodsToBeRemoved) {
				for (MethodDeclaration method : clazz.getTypeDeclaration().getMethods()) {
					if (methodToBeRemoved != null && method != null) {
						if (methodToBeRemoved.resolveBinding() != null && method.resolveBinding() != null) {
							if (methodToBeRemoved.resolveBinding().isEqualTo(method.resolveBinding())) {
								classBodyRewrite.remove(method, null);
							}else{
								if (methodToBeRemoved.resolveBinding().isSubsignature(method.resolveBinding())) {
									classBodyRewrite.remove(method, null);
								}
							}
						}
					}
				}
			}
			
			TextEdit sourceEdit = sourceRewriter.rewriteAST(clazz.getDocument(), clazz.getICompilationUnit().getJavaProject().getOptions(true));
			sourceMultiTextEdit.addChild(sourceEdit);
			
			try {
				sourceMultiTextEdit.apply(clazz.getDocument());
				
				ParseUtil.updateClazz(clazz.getDocument(), clazz, getProject());
				
				
			} catch (MalformedTreeException e) {
				e.printStackTrace();
			} catch (BadLocationException e) {
				e.printStackTrace();
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
			
			count++;
		
		}
		
		System.out.println("Removed Unused Methods");
		
		
	}

}

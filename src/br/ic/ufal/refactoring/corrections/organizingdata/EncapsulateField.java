package br.ic.ufal.refactoring.corrections.organizingdata;

import gr.uom.java.ast.util.ExpressionExtractor;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
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

//FieldVisibility
public class EncapsulateField extends Correction {
	
	private CompilationUnit sourceCompilationUnit = null;
	private TypeDeclaration sourceTypeDeclaration = null;
	private Document sourceDocument = null;
	private ICompilationUnit sourceICompilationUnit = null;
	
	private List<FieldDeclaration> fields = null;
	
	private MultiTextEdit sourceMultiTextEdit;

	public EncapsulateField(List<FieldDeclaration> fields, Project project) {
		super(project);
		this.fields = fields;
		
	}

	@Override
	public void apply() {
		
		System.out.println("Applying Encapsulate Fields");
		
		int count = 0;
		
		for (FieldDeclaration fieldDeclaration : fields) {
			
			System.out.println("Encapsulate Field: " + count + " of " + fields.size());
			
			this.sourceMultiTextEdit = new MultiTextEdit();
			
			TypeDeclaration td = ParseUtil.getTypeDeclaration((CompilationUnit)fieldDeclaration.getRoot());
			
			
			Clazz sourceClass = ParseUtil.getClazz(td, super.getProject().getClasses());
			
			this.sourceCompilationUnit = sourceClass.getCompilationUnit();
			this.sourceTypeDeclaration = sourceClass.getTypeDeclaration();
			this.sourceDocument = sourceClass.getDocument();
			this.sourceICompilationUnit = sourceClass.getICompilationUnit();
			
			createSetterMethodInSourceClass(fieldDeclaration);
			createGetterMethodInSourceClass(fieldDeclaration);
			updateModifier( );
			
			try {
				this.sourceMultiTextEdit.apply(this.sourceDocument);
				
				ParseUtil.updateClazz(this.sourceDocument, sourceClass, getProject());
				
				/*clazz.getICompilationUnit().getBuffer().setContents(clazz.getDocument().get());
				
				CompilationUnit compilationUnit = ParseUtil.toCompilationUnit(clazz.getICompilationUnit());
				
				clazz.setCompilationUnit(compilationUnit);
				
				TypeDeclaration typeDeclaration = ParseUtil.getTypeDeclaration(compilationUnit);
				if (typeDeclaration != null) {
					clazz.setTypeDeclaration(typeDeclaration);
				}
				
				Document document = null;
				
				try {
					document = new Document(clazz.getICompilationUnit().getBuffer().getContents());
					clazz.setDocument(document);
				} catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				
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
			
		
			count++;
		}
		
		System.out.println("Applied Encapsulate Fields");
		
		
	}
	
	private void removeFieldFragmentsInSourceClass(List<VariableDeclaration> fieldFragments) {
		
		FieldDeclaration[] fieldDeclarations = sourceTypeDeclaration.getFields();
		for(FieldDeclaration fieldDeclaration : fieldDeclarations) {
			List<VariableDeclarationFragment> fragments = fieldDeclaration.fragments();
			int actualNumberOfFragments = fragments.size();
			Set<VariableDeclaration> fragmentsToBeRemoved = new LinkedHashSet<VariableDeclaration>();
			for(VariableDeclarationFragment fragment : fragments) {
				if(fieldFragments.contains(fragment)) {
					fragmentsToBeRemoved.add(fragment);
				}
			}
			if(fragmentsToBeRemoved.size() > 0) {
				ASTRewrite sourceRewriter = ASTRewrite.create(sourceTypeDeclaration.getAST());
				ListRewrite contextBodyRewrite = sourceRewriter.getListRewrite(sourceTypeDeclaration, TypeDeclaration.BODY_DECLARATIONS_PROPERTY);
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
				/*this.sourceICompilationUnit = (ICompilationUnit)this.sourceCompilationUnit.getJavaElement();
				CompilationUnitChange change = compilationUnitChanges.get(this.sourceICompilationUnit);
				change.getEdit().addChild(sourceEdit);
				change.addTextEditGroup(new TextEditGroup("Remove extracted field", new TextEdit[] {sourceEdit}));*/
			}
		}
	}

	private void updateModifier() {
		
		ASTRewrite sourceRewriter = ASTRewrite.create(this.sourceCompilationUnit.getAST());
		AST ast = this.sourceTypeDeclaration.getAST();
		
		ListRewrite classBodyRewrite = sourceRewriter.getListRewrite(this.sourceTypeDeclaration, TypeDeclaration.BODY_DECLARATIONS_PROPERTY);
		
		ExpressionExtractor expressionExtractor = new ExpressionExtractor();
        Set<VariableDeclaration> finalFieldFragments = new LinkedHashSet<VariableDeclaration>();
        Set<VariableDeclaration> finalFieldFragmentsWithoutInitializer = new LinkedHashSet<VariableDeclaration>();
        
        for (FieldDeclaration fieldDeclaration : this.sourceTypeDeclaration.getFields()) {
        	
        		if (isPublic(fieldDeclaration.modifiers())) {
        			List<VariableDeclaration> fragments = fieldDeclaration.fragments();
        			removeFieldFragmentsInSourceClass(fragments);
        			
        			for (VariableDeclaration fieldFragment : fragments) {
        				List<Expression> initializerThisExpressions = expressionExtractor.getThisExpressions(fieldFragment.getInitializer());
        				FieldDeclaration extractedFieldDeclaration = null;
        	        
        				if(initializerThisExpressions.isEmpty()) {
        	        			extractedFieldDeclaration = ast.newFieldDeclaration((VariableDeclarationFragment)ASTNode.copySubtree(ast, fieldFragment));
        				} else {
        	        			VariableDeclarationFragment fragment = ast.newVariableDeclarationFragment();
        	        			sourceRewriter.set(fragment, VariableDeclarationFragment.NAME_PROPERTY, ast.newSimpleName(fieldFragment.getName().getIdentifier()), null);
        	        			extractedFieldDeclaration = ast.newFieldDeclaration(fragment);
        	        		}
        				
        				FieldDeclaration originalFieldDeclaration = (FieldDeclaration)fieldFragment.getParent();
        	        		sourceRewriter.set(extractedFieldDeclaration, FieldDeclaration.TYPE_PROPERTY, originalFieldDeclaration.getType(), null);
        	    		
        	        		ListRewrite extractedFieldDeclarationModifiersRewrite = sourceRewriter.getListRewrite(extractedFieldDeclaration, FieldDeclaration.MODIFIERS2_PROPERTY);
        	        		extractedFieldDeclarationModifiersRewrite.insertLast(ast.newModifier(Modifier.ModifierKeyword.PRIVATE_KEYWORD), null);
        	        		
        	        		List<IExtendedModifier> originalModifiers = originalFieldDeclaration.modifiers();
        	        		for(IExtendedModifier extendedModifier : originalModifiers) {
        	        			if(extendedModifier.isModifier()) {
        	        				Modifier modifier = (Modifier)extendedModifier;
        	        				if(modifier.isFinal()) {
        	        					extractedFieldDeclarationModifiersRewrite.insertLast(ast.newModifier(Modifier.ModifierKeyword.FINAL_KEYWORD), null);
        	        					finalFieldFragments.add(fieldFragment);
        	        					if(fieldFragment.getInitializer() == null)
        	        						finalFieldFragmentsWithoutInitializer.add(fieldFragment);
        	        				}
        	        				else if(modifier.isStatic()) {
        	        					extractedFieldDeclarationModifiersRewrite.insertLast(ast.newModifier(Modifier.ModifierKeyword.STATIC_KEYWORD), null);
        	        				}
        	        				else if(modifier.isTransient()) {
        	        					extractedFieldDeclarationModifiersRewrite.insertLast(ast.newModifier(Modifier.ModifierKeyword.TRANSIENT_KEYWORD), null);
        	        				}
        	        				else if(modifier.isVolatile()) {
        	        					extractedFieldDeclarationModifiersRewrite.insertLast(ast.newModifier(Modifier.ModifierKeyword.VOLATILE_KEYWORD), null);
        	        				}
        	        			}
        	        		}
        	        		
        	        		classBodyRewrite.insertFirst(extractedFieldDeclaration, null);
        	        		//classBodyRewrite.insertLast(extractedFieldDeclaration, null);
        	        }	
			}
        	}
        
        TextEdit sourceEdit = sourceRewriter.rewriteAST(this.sourceDocument, null);
		this.sourceMultiTextEdit.addChild(sourceEdit);
        
	}
	
	private void createSetterMethodInSourceClass(FieldDeclaration fieldDeclaration) {
			
			if (!isFinal(fieldDeclaration.modifiers())) {
				List<VariableDeclarationFragment> fragments = fieldDeclaration.fragments();
				for(VariableDeclarationFragment fragment : fragments) {
						ASTRewrite sourceRewriter = ASTRewrite.create(sourceTypeDeclaration.getAST());
						AST contextAST = sourceTypeDeclaration.getAST();
						MethodDeclaration newMethodDeclaration = contextAST.newMethodDeclaration();
						sourceRewriter.set(newMethodDeclaration, MethodDeclaration.RETURN_TYPE2_PROPERTY, contextAST.newPrimitiveType(PrimitiveType.VOID), null);
						ListRewrite methodDeclarationModifiersRewrite = sourceRewriter.getListRewrite(newMethodDeclaration, MethodDeclaration.MODIFIERS2_PROPERTY);
						methodDeclarationModifiersRewrite.insertLast(contextAST.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD), null);
						String methodName = fragment.getName().getIdentifier();
						methodName = "set" + methodName.substring(0,1).toUpperCase() + methodName.substring(1,methodName.length());
						sourceRewriter.set(newMethodDeclaration, MethodDeclaration.NAME_PROPERTY, contextAST.newSimpleName(methodName), null);
						ListRewrite methodDeclarationParametersRewrite = sourceRewriter.getListRewrite(newMethodDeclaration, MethodDeclaration.PARAMETERS_PROPERTY);
						SingleVariableDeclaration parameter = contextAST.newSingleVariableDeclaration();
						sourceRewriter.set(parameter, SingleVariableDeclaration.TYPE_PROPERTY, fieldDeclaration.getType(), null);
						sourceRewriter.set(parameter, SingleVariableDeclaration.NAME_PROPERTY, fragment.getName(), null);
						methodDeclarationParametersRewrite.insertLast(parameter, null);
						Block methodDeclarationBody = contextAST.newBlock();
						ListRewrite methodDeclarationBodyStatementsRewrite = sourceRewriter.getListRewrite(methodDeclarationBody, Block.STATEMENTS_PROPERTY);
						Assignment assignment = contextAST.newAssignment();
						sourceRewriter.set(assignment, Assignment.RIGHT_HAND_SIDE_PROPERTY, fragment.getName(), null);
						sourceRewriter.set(assignment, Assignment.OPERATOR_PROPERTY, Assignment.Operator.ASSIGN, null);
						FieldAccess fieldAccess = contextAST.newFieldAccess();
						sourceRewriter.set(fieldAccess, FieldAccess.EXPRESSION_PROPERTY, contextAST.newThisExpression(), null);
						sourceRewriter.set(fieldAccess, FieldAccess.NAME_PROPERTY, fragment.getName(), null);
						sourceRewriter.set(assignment, Assignment.LEFT_HAND_SIDE_PROPERTY, fieldAccess, null);
						ExpressionStatement expressionStatement = contextAST.newExpressionStatement(assignment);
						methodDeclarationBodyStatementsRewrite.insertLast(expressionStatement, null);
						sourceRewriter.set(newMethodDeclaration, MethodDeclaration.BODY_PROPERTY, methodDeclarationBody, null);
						ListRewrite contextBodyRewrite = sourceRewriter.getListRewrite(sourceTypeDeclaration, TypeDeclaration.BODY_DECLARATIONS_PROPERTY);
						contextBodyRewrite.insertLast(newMethodDeclaration, null);
						TextEdit sourceEdit = sourceRewriter.rewriteAST(this.sourceDocument, null);
						this.sourceMultiTextEdit.addChild(sourceEdit);
					
				}
			}
			
			
		
	}

	private void createGetterMethodInSourceClass(FieldDeclaration fieldDeclaration) {
		
			List<VariableDeclarationFragment> fragments = fieldDeclaration.fragments();
			for(VariableDeclarationFragment fragment : fragments) {
					ASTRewrite sourceRewriter = ASTRewrite.create(sourceTypeDeclaration.getAST());
					AST contextAST = sourceTypeDeclaration.getAST();
					MethodDeclaration newMethodDeclaration = contextAST.newMethodDeclaration();
					sourceRewriter.set(newMethodDeclaration, MethodDeclaration.RETURN_TYPE2_PROPERTY, fieldDeclaration.getType(), null);
					ListRewrite methodDeclarationModifiersRewrite = sourceRewriter.getListRewrite(newMethodDeclaration, MethodDeclaration.MODIFIERS2_PROPERTY);
					methodDeclarationModifiersRewrite.insertLast(contextAST.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD), null);
					String methodName = fragment.getName().getIdentifier();
					methodName = "get" + methodName.substring(0,1).toUpperCase() + methodName.substring(1,methodName.length());
					sourceRewriter.set(newMethodDeclaration, MethodDeclaration.NAME_PROPERTY, contextAST.newSimpleName(methodName), null);
					Block methodDeclarationBody = contextAST.newBlock();
					ListRewrite methodDeclarationBodyStatementsRewrite = sourceRewriter.getListRewrite(methodDeclarationBody, Block.STATEMENTS_PROPERTY);
					ReturnStatement returnStatement = contextAST.newReturnStatement();
					sourceRewriter.set(returnStatement, ReturnStatement.EXPRESSION_PROPERTY, fragment.getName(), null);
					methodDeclarationBodyStatementsRewrite.insertLast(returnStatement, null);
					sourceRewriter.set(newMethodDeclaration, MethodDeclaration.BODY_PROPERTY, methodDeclarationBody, null);
					ListRewrite contextBodyRewrite = sourceRewriter.getListRewrite(sourceTypeDeclaration, TypeDeclaration.BODY_DECLARATIONS_PROPERTY);
					contextBodyRewrite.insertLast(newMethodDeclaration, null);
					TextEdit sourceEdit = sourceRewriter.rewriteAST(this.sourceDocument, null);
					this.sourceMultiTextEdit.addChild(sourceEdit);
				
			}
		
	}
	
	private boolean isPublic(List modifiers){
		
		for (Object obj : modifiers) {
			if (obj instanceof Modifier) {
				Modifier modifier = (Modifier) obj;
				if (modifier.getKeyword().equals(ModifierKeyword.PUBLIC_KEYWORD)) {
					return true;
				}
				
			}
		}
		
		return false;
	}
	
	private boolean isFinal(List<Modifier> modifiers){
		
		for (Object obj : modifiers) {
			if (obj instanceof Modifier) {
				Modifier modifier = (Modifier) obj;
				if (modifier.getKeyword().equals(ModifierKeyword.FINAL_KEYWORD)) {
					return true;
				}
				
			}
		}
		
		return false;
	}
	
	
}

package br.ic.ufal.refactoring.corrections.movingfeaturesbetwenobjects;

import gr.uom.java.ast.util.ExpressionExtractor;
import gr.uom.java.ast.util.MethodDeclarationUtility;
import gr.uom.java.ast.util.TypeVisitor;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.core.refactoring.CompilationUnitChange;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.corrections.Correction;
//Shotgun Surgery
//Parallel Inheritance Hierarchies
//Inappropriate Intimacy
public class MoveField extends Correction {
	
	private CompilationUnit sourceCompilationUnit;
	private ICompilationUnit sourceICompilationUnit;
	private Document sourceDocument;
	private TypeDeclaration sourceTypeDeclaration;
	private CompilationUnit targetCompilationUnit;
	private ICompilationUnit targetICompilationUnit;
	private Document targetDocument;
	private TypeDeclaration targetTypeDeclaration = null;
	private Set<ITypeBinding> requiredImportDeclarationsInExtractedClass;
	private Map<MethodDeclaration, Set<String>> additionalArgumentsAddedToExtractedMethods;
	private Map<MethodDeclaration, Set<SingleVariableDeclaration>> additionalParametersAddedToExtractedMethods;
	private Set<String> sourceFieldBindingsWithCreatedGetterMethod;
	private Set<FieldDeclaration> fieldDeclarationsChangedWithPublicModifier;
	private Set<VariableDeclaration> extractedFieldFragments;
	private String extractedTypeName;
	private Map<Statement, ASTRewrite> statementRewriteMap;
	private Map<MethodDeclaration, Map<VariableDeclaration, Assignment>> constructorFinalFieldAssignmentMap;
	private Set<VariableDeclaration> extractedFieldsWithThisExpressionInTheirInitializer;
	private MultiTextEdit sourceMultiTextEdit;

	public MoveField(Clazz sourceClass,
					 
			 		 Clazz targetClass,
					 
					 Set<VariableDeclaration> extractedFieldFragments, 
					 Project project) {
		
		super(project);
		this.sourceCompilationUnit = sourceClass.getCompilationUnit();
		this.sourceTypeDeclaration = sourceClass.getTypeDeclaration();
		this.sourceICompilationUnit = sourceClass.getICompilationUnit();
		this.sourceDocument = sourceClass.getDocument();
		
		
		this.sourceMultiTextEdit = new MultiTextEdit();
		
		if (targetClass != null) {
			this.targetCompilationUnit = targetClass.getCompilationUnit();
			this.targetTypeDeclaration = targetClass.getTypeDeclaration();
			this.targetICompilationUnit = targetClass.getICompilationUnit();
			this.targetDocument = targetClass.getDocument();
			
			this.extractedTypeName = targetTypeDeclaration.getName().toString();
		}
		
		
		
		this.requiredImportDeclarationsInExtractedClass = new LinkedHashSet<ITypeBinding>();
		this.additionalArgumentsAddedToExtractedMethods = new LinkedHashMap<MethodDeclaration, Set<String>>();
		this.additionalParametersAddedToExtractedMethods = new LinkedHashMap<MethodDeclaration, Set<SingleVariableDeclaration>>();
		this.sourceFieldBindingsWithCreatedGetterMethod = new LinkedHashSet<String>();
		this.fieldDeclarationsChangedWithPublicModifier = new LinkedHashSet<FieldDeclaration>();
		this.extractedFieldFragments = extractedFieldFragments;
		this.statementRewriteMap = new LinkedHashMap<Statement, ASTRewrite>();
		this.constructorFinalFieldAssignmentMap = new LinkedHashMap<MethodDeclaration, Map<VariableDeclaration, Assignment>>();
		this.extractedFieldsWithThisExpressionInTheirInitializer = new LinkedHashSet<VariableDeclaration>();
		
		
	}

	@Override
	public void execute() {
		
		removeFieldFragmentsInSourceClass(extractedFieldFragments);
		
		if (targetTypeDeclaration != null) {
			Set<ITypeBinding> typeBindings = new LinkedHashSet<ITypeBinding>();
			TypeVisitor typeVisitor = new TypeVisitor();
			for(VariableDeclaration fieldFragment : extractedFieldFragments) {
				fieldFragment.getParent().accept(typeVisitor);
				for(ITypeBinding typeBinding : typeVisitor.getTypeBindings()) {
					typeBindings.add(typeBinding);
				}
			}
			
			getSimpleTypeBindings(typeBindings, requiredImportDeclarationsInExtractedClass);
			
			moveField();
			
			
		}
		
		//handleInitializationOfExtractedFieldsWithThisExpressionInTheirInitializer();
		
		for(Statement statement : statementRewriteMap.keySet()) {
			ASTRewrite sourceRewriter = statementRewriteMap.get(statement);
			TextEdit sourceEdit = sourceRewriter.rewriteAST(this.sourceDocument, null);
			this.sourceMultiTextEdit.addChild(sourceEdit);
		}
		
		
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
	
	private void handleInitializationOfExtractedFieldsWithThisExpressionInTheirInitializer() {
		String modifiedExtractedTypeName = extractedTypeName.substring(0,1).toLowerCase() + extractedTypeName.substring(1,extractedTypeName.length());
		for(VariableDeclaration fieldFragment : extractedFieldsWithThisExpressionInTheirInitializer) {
			String originalFieldName = fieldFragment.getName().getIdentifier();
			String modifiedFieldName = originalFieldName.substring(0,1).toUpperCase() + originalFieldName.substring(1,originalFieldName.length());
			for(MethodDeclaration methodDeclaration : sourceTypeDeclaration.getMethods()) {
				if(methodDeclaration.isConstructor()) {
					ASTRewrite sourceRewriter = ASTRewrite.create(sourceCompilationUnit.getAST());
					ListRewrite constructorBodyRewrite = sourceRewriter.getListRewrite(methodDeclaration.getBody(), Block.STATEMENTS_PROPERTY);
					AST contextAST = sourceTypeDeclaration.getAST();
					MethodInvocation setterMethodInvocation = contextAST.newMethodInvocation();
					sourceRewriter.set(setterMethodInvocation, MethodInvocation.NAME_PROPERTY, contextAST.newSimpleName("set" + modifiedFieldName), null);
					ListRewrite setterMethodInvocationArgumentsRewrite = sourceRewriter.getListRewrite(setterMethodInvocation, MethodInvocation.ARGUMENTS_PROPERTY);
					setterMethodInvocationArgumentsRewrite.insertLast(fieldFragment.getInitializer(), null);
					if((fieldFragment.resolveBinding().getModifiers() & Modifier.STATIC) != 0) {
						sourceRewriter.set(setterMethodInvocation, MethodInvocation.EXPRESSION_PROPERTY, contextAST.newSimpleName(extractedTypeName), null);
					}
					else {
						sourceRewriter.set(setterMethodInvocation, MethodInvocation.EXPRESSION_PROPERTY, contextAST.newSimpleName(modifiedExtractedTypeName), null);
					}
					ExpressionStatement expressionStatement = contextAST.newExpressionStatement(setterMethodInvocation);
					constructorBodyRewrite.insertLast(expressionStatement, null);
					TextEdit sourceEdit = sourceRewriter.rewriteAST(this.sourceDocument, null);
					this.sourceMultiTextEdit.addChild(sourceEdit);
					/*this.sourceICompilationUnit = (ICompilationUnit)this.sourceCompilationUnit.getJavaElement();
					CompilationUnitChange change = compilationUnitChanges.get(this.sourceICompilationUnit);
					change.getEdit().addChild(sourceEdit);
					change.addTextEditGroup(new TextEditGroup("Initialize extracted field " + fieldFragment.getName().getIdentifier(), new TextEdit[] {sourceEdit}));*/
				}
			}
		}
	}

	private void moveField(){
		
		ASTRewrite targetRewriter = ASTRewrite.create(this.targetCompilationUnit.getAST());
		AST ast = this.targetTypeDeclaration.getAST();
		
		if(this.sourceCompilationUnit.getPackage() != null) {
        	//extractedClassRewriter.set(extractedClassCompilationUnit, CompilationUnit.PACKAGE_PROPERTY, sourceCompilationUnit.getPackage(), null);
        	targetRewriter.set(this.targetCompilationUnit, CompilationUnit.PACKAGE_PROPERTY, this.sourceCompilationUnit.getPackage(), null);
        	
        }
		
		ListRewrite extractedClassBodyRewrite = targetRewriter.getListRewrite(this.targetTypeDeclaration, TypeDeclaration.BODY_DECLARATIONS_PROPERTY);
        
        ExpressionExtractor expressionExtractor = new ExpressionExtractor();
        Set<VariableDeclaration> finalFieldFragments = new LinkedHashSet<VariableDeclaration>();
        Set<VariableDeclaration> finalFieldFragmentsWithoutInitializer = new LinkedHashSet<VariableDeclaration>();
        
        for(VariableDeclaration fieldFragment : extractedFieldFragments) {
        	
        	List<Expression> initializerThisExpressions = expressionExtractor.getThisExpressions(fieldFragment.getInitializer());
        	FieldDeclaration extractedFieldDeclaration = null;
        	
        	if(initializerThisExpressions.isEmpty()) {
        		extractedFieldDeclaration = ast.newFieldDeclaration((VariableDeclarationFragment)ASTNode.copySubtree(ast, fieldFragment));
        	} else {
        		this.extractedFieldsWithThisExpressionInTheirInitializer.add(fieldFragment);
        		VariableDeclarationFragment fragment = ast.newVariableDeclarationFragment();
        		targetRewriter.set(fragment, VariableDeclarationFragment.NAME_PROPERTY, ast.newSimpleName(fieldFragment.getName().getIdentifier()), null);
        		extractedFieldDeclaration = ast.newFieldDeclaration(fragment);
        	}
        	
        	FieldDeclaration originalFieldDeclaration = (FieldDeclaration)fieldFragment.getParent();
        	targetRewriter.set(extractedFieldDeclaration, FieldDeclaration.TYPE_PROPERTY, originalFieldDeclaration.getType(), null);
    		
        	ListRewrite extractedFieldDeclarationModifiersRewrite = targetRewriter.getListRewrite(extractedFieldDeclaration, FieldDeclaration.MODIFIERS2_PROPERTY);
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
    		extractedClassBodyRewrite.insertLast(extractedFieldDeclaration, null);
        }
        
   
        for(VariableDeclaration fieldFragment : extractedFieldFragments) {
        	MethodDeclaration getterMethodDeclaration = createGetterMethodDeclaration(fieldFragment, ast, targetRewriter);
        	extractedClassBodyRewrite.insertLast(getterMethodDeclaration, null);
        	if(!finalFieldFragments.contains(fieldFragment)) {
        		MethodDeclaration setterMethodDeclaration = createSetterMethodDeclaration(fieldFragment, ast, targetRewriter);
        		extractedClassBodyRewrite.insertLast(setterMethodDeclaration, null);
        	}
        }
        
        try {
        	for(ITypeBinding typeBinding : requiredImportDeclarationsInExtractedClass) {
        		addImportDeclaration(typeBinding, this.targetCompilationUnit, targetRewriter);
        	}
        	
        TextEdit extractedClassEdit = targetRewriter.rewriteAST(this.targetDocument, null);
        
        
        extractedClassEdit.apply(this.targetDocument);
        	
        this.targetICompilationUnit.getBuffer().setContents(this.targetDocument.get());
        
        
        System.out.println("Code Target ");
        System.out.println(this.targetDocument.get());
        
        
        }catch (MalformedTreeException e) {
        	e.printStackTrace();
        } catch (BadLocationException e) {
        	e.printStackTrace();
        } catch (JavaModelException e) {
			e.printStackTrace();
		}
        
	}

	

	private boolean variableBindingCorrespondsToExtractedField(IVariableBinding variableBinding) {
		for(VariableDeclaration extractedFieldFragment : extractedFieldFragments) {
			if(extractedFieldFragment.resolveBinding().isEqualTo(variableBinding))
				return true;
		}
		return false;
	}

	

	private boolean isParentAnonymousClassDeclaration(ASTNode node) {
		if(node.getParent() instanceof AnonymousClassDeclaration) {
			return true;
		}
		else if(node.getParent() instanceof CompilationUnit) {
			return false;
		}
		else {
			return isParentAnonymousClassDeclaration(node.getParent());
		}
	}

	private IMethodBinding findSetterMethodInSourceClass(IVariableBinding fieldBinding) {
		MethodDeclaration[] contextMethods = sourceTypeDeclaration.getMethods();
		for(MethodDeclaration methodDeclaration : contextMethods) {
			SimpleName simpleName = MethodDeclarationUtility.isSetter(methodDeclaration);
			if(simpleName != null && simpleName.resolveBinding().isEqualTo(fieldBinding)) {
				return methodDeclaration.resolveBinding();
			}
		}
		return null;
	}

	private IMethodBinding findGetterMethodInSourceClass(IVariableBinding fieldBinding) {
		MethodDeclaration[] contextMethods = sourceTypeDeclaration.getMethods();
		for(MethodDeclaration methodDeclaration : contextMethods) {
			SimpleName simpleName = MethodDeclarationUtility.isGetter(methodDeclaration);
			if(simpleName != null && simpleName.resolveBinding().isEqualTo(fieldBinding)) {
				return methodDeclaration.resolveBinding();
			}
		}
		return null;
	}

	private void createSetterMethodInSourceClass(IVariableBinding variableBinding) {
		FieldDeclaration[] fieldDeclarations = sourceTypeDeclaration.getFields();
		for(FieldDeclaration fieldDeclaration : fieldDeclarations) {
			List<VariableDeclarationFragment> fragments = fieldDeclaration.fragments();
			for(VariableDeclarationFragment fragment : fragments) {
				if(variableBinding.isEqualTo(fragment.resolveBinding())) {
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
					/*this.sourceICompilationUnit = (ICompilationUnit)this.sourceCompilationUnit.getJavaElement();
					CompilationUnitChange change = compilationUnitChanges.get(this.sourceICompilationUnit);
					change.getEdit().addChild(sourceEdit);
					change.addTextEditGroup(new TextEditGroup("Create setter method for field " + variableBinding.getName(), new TextEdit[] {sourceEdit}));*/
				}
			}
		}
	}

	private void createGetterMethodInSourceClass(IVariableBinding variableBinding) {
		FieldDeclaration[] fieldDeclarations = sourceTypeDeclaration.getFields();
		for(FieldDeclaration fieldDeclaration : fieldDeclarations) {
			List<VariableDeclarationFragment> fragments = fieldDeclaration.fragments();
			for(VariableDeclarationFragment fragment : fragments) {
				if(variableBinding.isEqualTo(fragment.resolveBinding())) {
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
					/*this.sourceICompilationUnit = (ICompilationUnit)this.sourceCompilationUnit.getJavaElement();
					CompilationUnitChange change = compilationUnitChanges.get(this.sourceICompilationUnit);
					change.getEdit().addChild(sourceEdit);
					change.addTextEditGroup(new TextEditGroup("Create getter method for field " + variableBinding.getName(), new TextEdit[] {sourceEdit}));*/
				}
			}
		}
	}



	private void handleAccessedFieldNotHavingSetterMethod(MethodDeclaration sourceMethod,
			MethodDeclaration newMethodDeclaration, ASTRewrite targetRewriter,
			Map<String, SingleVariableDeclaration> fieldParameterMap, SimpleName newAccessedVariable) {
		Set<String> additionalArgumentsAddedToMovedMethod = additionalArgumentsAddedToExtractedMethods.get(sourceMethod);
		Set<SingleVariableDeclaration> additionalParametersAddedToMovedMethod = additionalParametersAddedToExtractedMethods.get(sourceMethod);
		if(newAccessedVariable.getParent() instanceof FieldAccess) {
			FieldAccess fieldAccess = (FieldAccess)newAccessedVariable.getParent();
			if(fieldAccess.getExpression() instanceof ThisExpression) {
				targetRewriter.replace(newAccessedVariable.getParent(), newAccessedVariable, null);
				if(!additionalArgumentsAddedToMovedMethod.contains(newAccessedVariable.getIdentifier())) {
					SingleVariableDeclaration fieldParameter = addParameterToMovedMethod(newMethodDeclaration, newAccessedVariable, targetRewriter);
					additionalArgumentsAddedToMovedMethod.add(newAccessedVariable.getIdentifier());
					additionalParametersAddedToMovedMethod.add(fieldParameter);
					fieldParameterMap.put(newAccessedVariable.getIdentifier(), fieldParameter);
				}
			}
		}
		else if(!additionalArgumentsAddedToMovedMethod.contains(newAccessedVariable.getIdentifier())) {
			SingleVariableDeclaration fieldParameter = addParameterToMovedMethod(newMethodDeclaration, newAccessedVariable, targetRewriter);
			additionalArgumentsAddedToMovedMethod.add(newAccessedVariable.getIdentifier());
			additionalParametersAddedToMovedMethod.add(fieldParameter);
			fieldParameterMap.put(newAccessedVariable.getIdentifier(), fieldParameter);
		}
	}

	private SingleVariableDeclaration handleAccessedFieldHavingSetterMethod(MethodDeclaration sourceMethod,
			MethodDeclaration newMethodDeclaration, ASTRewrite targetRewriter,
			AST ast, SingleVariableDeclaration sourceClassParameter,
			String modifiedSourceTypeName, SimpleName newAccessedVariable, IVariableBinding accessedVariableBinding) {
		IMethodBinding getterMethodBinding = findGetterMethodInSourceClass(accessedVariableBinding);
		Set<String> additionalArgumentsAddedToMovedMethod = additionalArgumentsAddedToExtractedMethods.get(sourceMethod);
		Set<SingleVariableDeclaration> additionalParametersAddedToMovedMethod = additionalParametersAddedToExtractedMethods.get(sourceMethod);
		if(!additionalArgumentsAddedToMovedMethod.contains("this")) {
			sourceClassParameter = addSourceClassParameterToMovedMethod(newMethodDeclaration, targetRewriter);
			additionalArgumentsAddedToMovedMethod.add("this");
			additionalParametersAddedToMovedMethod.add(sourceClassParameter);
		}
		MethodInvocation getterMethodInvocation = ast.newMethodInvocation();
		if(getterMethodBinding != null) {
			targetRewriter.set(getterMethodInvocation, MethodInvocation.NAME_PROPERTY, ast.newSimpleName(getterMethodBinding.getName()), null);
		}
		else {
			if(!sourceFieldBindingsWithCreatedGetterMethod.contains(accessedVariableBinding.getKey())) {
				createGetterMethodInSourceClass(accessedVariableBinding);
				sourceFieldBindingsWithCreatedGetterMethod.add(accessedVariableBinding.getKey());
			}
			String originalFieldName = accessedVariableBinding.getName();
			String modifiedFieldName = originalFieldName.substring(0,1).toUpperCase() + originalFieldName.substring(1,originalFieldName.length());
			targetRewriter.set(getterMethodInvocation, MethodInvocation.NAME_PROPERTY, ast.newSimpleName("get" + modifiedFieldName), null);
		}
		targetRewriter.set(getterMethodInvocation, MethodInvocation.EXPRESSION_PROPERTY, ast.newSimpleName(modifiedSourceTypeName), null);
		if(newAccessedVariable.getParent() instanceof FieldAccess) {
			FieldAccess newFieldAccess = (FieldAccess)newAccessedVariable.getParent();
			if(newFieldAccess.getExpression() instanceof ThisExpression) {
				targetRewriter.replace(newFieldAccess, getterMethodInvocation, null);
			}
		}
		else {
			targetRewriter.replace(newAccessedVariable, getterMethodInvocation, null);
		}
		return sourceClassParameter;
	}

	private SingleVariableDeclaration addSourceClassParameterToMovedMethod(MethodDeclaration newMethodDeclaration, ASTRewrite targetRewriter) {
		AST ast = newMethodDeclaration.getAST();
		SingleVariableDeclaration parameter = ast.newSingleVariableDeclaration();
		SimpleName typeName = ast.newSimpleName(sourceTypeDeclaration.getName().getIdentifier());
		Type parameterType = ast.newSimpleType(typeName);
		targetRewriter.set(parameter, SingleVariableDeclaration.TYPE_PROPERTY, parameterType, null);
		String sourceTypeName = sourceTypeDeclaration.getName().getIdentifier();
		String modifiedSourceTypeName = sourceTypeName.substring(0,1).toLowerCase() + sourceTypeName.substring(1,sourceTypeName.length());
		SimpleName parameterName = ast.newSimpleName(modifiedSourceTypeName);
		targetRewriter.set(parameter, SingleVariableDeclaration.NAME_PROPERTY, parameterName, null);
		ListRewrite parametersRewrite = targetRewriter.getListRewrite(newMethodDeclaration, MethodDeclaration.PARAMETERS_PROPERTY);
		parametersRewrite.insertLast(parameter, null);
		Set<ITypeBinding> typeBindings = new LinkedHashSet<ITypeBinding>();
		typeBindings.add(sourceTypeDeclaration.resolveBinding());
		getSimpleTypeBindings(typeBindings, requiredImportDeclarationsInExtractedClass);
		return parameter;
	}

	private SingleVariableDeclaration addParameterToMovedMethod(MethodDeclaration newMethodDeclaration, SimpleName fieldName, ASTRewrite targetRewriter) {
		AST ast = newMethodDeclaration.getAST();
		SingleVariableDeclaration parameter = ast.newSingleVariableDeclaration();
		Type fieldType = null;
		FieldDeclaration[] fields = sourceTypeDeclaration.getFields();
		for(FieldDeclaration field : fields) {
			List<VariableDeclarationFragment> fragments = field.fragments();
			for(VariableDeclarationFragment fragment : fragments) {
				if(fragment.getName().getIdentifier().equals(fieldName.getIdentifier())) {
					fieldType = field.getType();
					break;
				}
			}
		}
		targetRewriter.set(parameter, SingleVariableDeclaration.TYPE_PROPERTY, fieldType, null);
		targetRewriter.set(parameter, SingleVariableDeclaration.NAME_PROPERTY, ast.newSimpleName(fieldName.getIdentifier()), null);
		ListRewrite parametersRewrite = targetRewriter.getListRewrite(newMethodDeclaration, MethodDeclaration.PARAMETERS_PROPERTY);
		parametersRewrite.insertLast(parameter, null);
		Set<ITypeBinding> typeBindings = new LinkedHashSet<ITypeBinding>();
		typeBindings.add(fieldType.resolveBinding());
		getSimpleTypeBindings(typeBindings, requiredImportDeclarationsInExtractedClass);
		return parameter;
	}

	private SingleVariableDeclaration addParameterToMovedMethod(MethodDeclaration newMethodDeclaration, IVariableBinding variableBinding, ASTRewrite targetRewriter) {
		AST ast = newMethodDeclaration.getAST();
		SingleVariableDeclaration parameter = ast.newSingleVariableDeclaration();
		ITypeBinding typeBinding = variableBinding.getType();
		Type fieldType = null;
		if(typeBinding.isClass()) {
			fieldType = ast.newSimpleType(ast.newSimpleName(typeBinding.getName()));
		}
		else if(typeBinding.isPrimitive()) {
			String primitiveType = typeBinding.getName();
			if(primitiveType.equals("int"))
				fieldType = ast.newPrimitiveType(PrimitiveType.INT);
			else if(primitiveType.equals("double"))
				fieldType = ast.newPrimitiveType(PrimitiveType.DOUBLE);
			else if(primitiveType.equals("byte"))
				fieldType = ast.newPrimitiveType(PrimitiveType.BYTE);
			else if(primitiveType.equals("short"))
				fieldType = ast.newPrimitiveType(PrimitiveType.SHORT);
			else if(primitiveType.equals("char"))
				fieldType = ast.newPrimitiveType(PrimitiveType.CHAR);
			else if(primitiveType.equals("long"))
				fieldType = ast.newPrimitiveType(PrimitiveType.LONG);
			else if(primitiveType.equals("float"))
				fieldType = ast.newPrimitiveType(PrimitiveType.FLOAT);
			else if(primitiveType.equals("boolean"))
				fieldType = ast.newPrimitiveType(PrimitiveType.BOOLEAN);
		}
		else if(typeBinding.isArray()) {
			ITypeBinding elementTypeBinding = typeBinding.getElementType();
			Type elementType = ast.newSimpleType(ast.newSimpleName(elementTypeBinding.getName()));
			fieldType = ast.newArrayType(elementType, typeBinding.getDimensions());
		}
		else if(typeBinding.isParameterizedType()) {
			fieldType = createParameterizedType(ast, typeBinding, targetRewriter);
		}
		targetRewriter.set(parameter, SingleVariableDeclaration.TYPE_PROPERTY, fieldType, null);
		targetRewriter.set(parameter, SingleVariableDeclaration.NAME_PROPERTY, ast.newSimpleName(variableBinding.getName()), null);
		ListRewrite parametersRewrite = targetRewriter.getListRewrite(newMethodDeclaration, MethodDeclaration.PARAMETERS_PROPERTY);
		parametersRewrite.insertLast(parameter, null);
		Set<ITypeBinding> typeBindings = new LinkedHashSet<ITypeBinding>();
		typeBindings.add(variableBinding.getType());
		getSimpleTypeBindings(typeBindings, requiredImportDeclarationsInExtractedClass);
		return parameter;
	}

	private ParameterizedType createParameterizedType(AST ast, ITypeBinding typeBinding, ASTRewrite targetRewriter) {
		ITypeBinding erasure = typeBinding.getErasure();
		ITypeBinding[] typeArguments = typeBinding.getTypeArguments();
		ParameterizedType parameterizedType = ast.newParameterizedType(ast.newSimpleType(ast.newSimpleName(erasure.getName())));
		ListRewrite typeArgumentsRewrite = targetRewriter.getListRewrite(parameterizedType, ParameterizedType.TYPE_ARGUMENTS_PROPERTY);
		for(ITypeBinding typeArgument : typeArguments) {
			if(typeArgument.isClass() || typeArgument.isInterface())
				typeArgumentsRewrite.insertLast(ast.newSimpleType(ast.newSimpleName(typeArgument.getName())), null);
			else if(typeArgument.isParameterizedType()) {
				typeArgumentsRewrite.insertLast(createParameterizedType(ast, typeArgument, targetRewriter), null);
			}
		}
		return parameterizedType;
	}

	private void setPublicModifierToSourceMethod(MethodDeclaration methodDeclaration) {
		ASTRewrite sourceRewriter = ASTRewrite.create(sourceCompilationUnit.getAST());
		ListRewrite modifierRewrite = sourceRewriter.getListRewrite(methodDeclaration, MethodDeclaration.MODIFIERS2_PROPERTY);
		Modifier publicModifier = methodDeclaration.getAST().newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);
		boolean modifierFound = false;
		List<IExtendedModifier> modifiers = methodDeclaration.modifiers();
		for(IExtendedModifier extendedModifier : modifiers) {
			if(extendedModifier.isModifier()) {
				Modifier modifier = (Modifier)extendedModifier;
				if(modifier.getKeyword().equals(Modifier.ModifierKeyword.PUBLIC_KEYWORD)) {
					modifierFound = true;
				}
				else if(modifier.getKeyword().equals(Modifier.ModifierKeyword.PRIVATE_KEYWORD)) {
					modifierFound = true;
					modifierRewrite.replace(modifier, publicModifier, null);
					TextEdit sourceEdit = sourceRewriter.rewriteAST(this.sourceDocument, null);
					this.sourceMultiTextEdit.addChild(sourceEdit);
					/*this.sourceICompilationUnit = (ICompilationUnit)this.sourceCompilationUnit.getJavaElement();
					CompilationUnitChange change = compilationUnitChanges.get(this.sourceICompilationUnit);
					change.getEdit().addChild(sourceEdit);
					change.addTextEditGroup(new TextEditGroup("Change access level to public", new TextEdit[] {sourceEdit}));*/
				}
				else if(modifier.getKeyword().equals(Modifier.ModifierKeyword.PROTECTED_KEYWORD)) {
					modifierFound = true;
				}
			}
		}
		if(!modifierFound) {
			modifierRewrite.insertFirst(publicModifier, null);
			TextEdit sourceEdit = sourceRewriter.rewriteAST(this.sourceDocument, null);
			this.sourceMultiTextEdit.addChild(sourceEdit);
			this.sourceICompilationUnit = (ICompilationUnit)this.sourceCompilationUnit.getJavaElement();
			
		}
	}

	private void modifySourceStaticFieldInstructionsInTargetClass(MethodDeclaration sourceMethod,
			MethodDeclaration newMethodDeclaration, ASTRewrite targetRewriter) {
		ExpressionExtractor extractor = new ExpressionExtractor();
		List<Expression> sourceVariableInstructions = extractor.getVariableInstructions(sourceMethod.getBody());
		List<Expression> newVariableInstructions = extractor.getVariableInstructions(newMethodDeclaration.getBody());
		int i = 0;
		for(Expression expression : sourceVariableInstructions) {
			SimpleName simpleName = (SimpleName)expression;
			IBinding binding = simpleName.resolveBinding();
			if(binding.getKind() == IBinding.VARIABLE) {
				IVariableBinding variableBinding = (IVariableBinding)binding;
				if(variableBinding.isField() && (variableBinding.getModifiers() & Modifier.STATIC) != 0) {
					if(sourceTypeDeclaration.resolveBinding().isEqualTo(variableBinding.getDeclaringClass())) {
						AST ast = newMethodDeclaration.getAST();
						SimpleName qualifier = ast.newSimpleName(sourceTypeDeclaration.getName().getIdentifier());
						if(simpleName.getParent() instanceof FieldAccess) {
							FieldAccess fieldAccess = (FieldAccess)newVariableInstructions.get(i).getParent();
							targetRewriter.set(fieldAccess, FieldAccess.EXPRESSION_PROPERTY, qualifier, null);
						}
						else if(!(simpleName.getParent() instanceof QualifiedName)) {
							SimpleName newSimpleName = ast.newSimpleName(simpleName.getIdentifier());
							QualifiedName newQualifiedName = ast.newQualifiedName(qualifier, newSimpleName);
							targetRewriter.replace(newVariableInstructions.get(i), newQualifiedName, null);
						}
						setPublicModifierToSourceField(variableBinding);
					}
					else {
						AST ast = newMethodDeclaration.getAST();
						SimpleName qualifier = null;
						if((variableBinding.getModifiers() & Modifier.PUBLIC) != 0) {
							qualifier = ast.newSimpleName(variableBinding.getDeclaringClass().getName());
							Set<ITypeBinding> typeBindings = new LinkedHashSet<ITypeBinding>();
							typeBindings.add(variableBinding.getDeclaringClass());
							getSimpleTypeBindings(typeBindings, requiredImportDeclarationsInExtractedClass);
						}
						else {
							qualifier = ast.newSimpleName(sourceTypeDeclaration.getName().getIdentifier());
						}
						if(simpleName.getParent() instanceof FieldAccess) {
							FieldAccess fieldAccess = (FieldAccess)newVariableInstructions.get(i).getParent();
							targetRewriter.set(fieldAccess, FieldAccess.EXPRESSION_PROPERTY, qualifier, null);
						}
						else if(!(simpleName.getParent() instanceof QualifiedName)) {
							SimpleName newSimpleName = ast.newSimpleName(simpleName.getIdentifier());
							QualifiedName newQualifiedName = ast.newQualifiedName(qualifier, newSimpleName);
							targetRewriter.replace(newVariableInstructions.get(i), newQualifiedName, null);
						}
					}
				}
			}
			i++;
		}
	}

	private void setPublicModifierToSourceField(IVariableBinding variableBinding) {
		FieldDeclaration[] fieldDeclarations = sourceTypeDeclaration.getFields();
		for(FieldDeclaration fieldDeclaration : fieldDeclarations) {
			List<VariableDeclarationFragment> fragments = fieldDeclaration.fragments();
			for(VariableDeclarationFragment fragment : fragments) {
				boolean modifierIsReplaced = false;
				if(variableBinding.isEqualTo(fragment.resolveBinding())) {
					ASTRewrite sourceRewriter = ASTRewrite.create(sourceTypeDeclaration.getAST());
					ListRewrite modifierRewrite = sourceRewriter.getListRewrite(fieldDeclaration, FieldDeclaration.MODIFIERS2_PROPERTY);
					Modifier publicModifier = fieldDeclaration.getAST().newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);
					boolean modifierFound = false;
					List<IExtendedModifier> modifiers = fieldDeclaration.modifiers();
					for(IExtendedModifier extendedModifier : modifiers) {
						if(extendedModifier.isModifier()) {
							Modifier modifier = (Modifier)extendedModifier;
							if(modifier.getKeyword().equals(Modifier.ModifierKeyword.PUBLIC_KEYWORD)) {
								modifierFound = true;
							}
							else if(modifier.getKeyword().equals(Modifier.ModifierKeyword.PRIVATE_KEYWORD)) {
								if(!fieldDeclarationsChangedWithPublicModifier.contains(fieldDeclaration)) {
									fieldDeclarationsChangedWithPublicModifier.add(fieldDeclaration);
									modifierFound = true;
									modifierRewrite.replace(modifier, publicModifier, null);
									modifierIsReplaced = true;
									TextEdit sourceEdit = sourceRewriter.rewriteAST(this.sourceDocument, null);
									this.sourceMultiTextEdit.addChild(sourceEdit);
									/*this.sourceICompilationUnit = (ICompilationUnit)this.sourceCompilationUnit.getJavaElement();
									CompilationUnitChange change = compilationUnitChanges.get(this.sourceICompilationUnit);
									change.getEdit().addChild(sourceEdit);
									change.addTextEditGroup(new TextEditGroup("Change access level to public", new TextEdit[] {sourceEdit}));*/
								}
							}
							else if(modifier.getKeyword().equals(Modifier.ModifierKeyword.PROTECTED_KEYWORD)) {
								modifierFound = true;
							}
						}
					}
					if(!modifierFound) {
						if(!fieldDeclarationsChangedWithPublicModifier.contains(fieldDeclaration)) {
							fieldDeclarationsChangedWithPublicModifier.add(fieldDeclaration);
							modifierRewrite.insertFirst(publicModifier, null);
							modifierIsReplaced = true;
							TextEdit sourceEdit = sourceRewriter.rewriteAST(this.sourceDocument, null);
							this.sourceMultiTextEdit.addChild(sourceEdit);
							
							/*this.sourceICompilationUnit = (ICompilationUnit)this.sourceCompilationUnit.getJavaElement();
							CompilationUnitChange change = compilationUnitChanges.get(this.sourceICompilationUnit);
							change.getEdit().addChild(sourceEdit);
							change.addTextEditGroup(new TextEditGroup("Set access level to public", new TextEdit[] {sourceEdit}));*/
						}
					}
				}
				if(modifierIsReplaced)
					break;
			}
		}
	}

	private MethodDeclaration createSetterMethodDeclaration(VariableDeclaration fieldFragment, AST extractedClassAST, ASTRewrite extractedClassRewriter) {
		String originalFieldName = fieldFragment.getName().getIdentifier();
		String modifiedFieldName = originalFieldName.substring(0,1).toUpperCase() + originalFieldName.substring(1,originalFieldName.length());
		MethodDeclaration setterMethodDeclaration = extractedClassAST.newMethodDeclaration();
		extractedClassRewriter.set(setterMethodDeclaration, MethodDeclaration.NAME_PROPERTY, extractedClassAST.newSimpleName("set" + modifiedFieldName), null);
		PrimitiveType type = extractedClassAST.newPrimitiveType(PrimitiveType.VOID);
		extractedClassRewriter.set(setterMethodDeclaration, MethodDeclaration.RETURN_TYPE2_PROPERTY, type, null);
		ListRewrite setterMethodModifiersRewrite = extractedClassRewriter.getListRewrite(setterMethodDeclaration, MethodDeclaration.MODIFIERS2_PROPERTY);
		setterMethodModifiersRewrite.insertLast(extractedClassAST.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD), null);
		SingleVariableDeclaration parameter = extractedClassAST.newSingleVariableDeclaration();
		extractedClassRewriter.set(parameter, SingleVariableDeclaration.NAME_PROPERTY, fieldFragment.getName(), null);
		FieldDeclaration originalFieldDeclaration = (FieldDeclaration)fieldFragment.getParent();
		extractedClassRewriter.set(parameter, SingleVariableDeclaration.TYPE_PROPERTY, originalFieldDeclaration.getType(), null);
		ListRewrite setterMethodParametersRewrite = extractedClassRewriter.getListRewrite(setterMethodDeclaration, MethodDeclaration.PARAMETERS_PROPERTY);
		setterMethodParametersRewrite.insertLast(parameter, null);
		if((originalFieldDeclaration.getModifiers() & Modifier.STATIC) != 0) {
			setterMethodModifiersRewrite.insertLast(extractedClassAST.newModifier(Modifier.ModifierKeyword.STATIC_KEYWORD), null);
		}
		
		Assignment assignment = extractedClassAST.newAssignment();
		FieldAccess fieldAccess = extractedClassAST.newFieldAccess();
		if((originalFieldDeclaration.getModifiers() & Modifier.STATIC) != 0) {
			extractedClassRewriter.set(fieldAccess, FieldAccess.EXPRESSION_PROPERTY, extractedClassAST.newSimpleName(extractedTypeName), null);
		}
		else {
			ThisExpression thisExpression = extractedClassAST.newThisExpression();
			extractedClassRewriter.set(fieldAccess, FieldAccess.EXPRESSION_PROPERTY, thisExpression, null);
		}
		extractedClassRewriter.set(fieldAccess, FieldAccess.NAME_PROPERTY, fieldFragment.getName(), null);
		extractedClassRewriter.set(assignment, Assignment.LEFT_HAND_SIDE_PROPERTY, fieldAccess, null);
		extractedClassRewriter.set(assignment, Assignment.OPERATOR_PROPERTY, Assignment.Operator.ASSIGN, null);
		extractedClassRewriter.set(assignment, Assignment.RIGHT_HAND_SIDE_PROPERTY, fieldFragment.getName(), null);
		ExpressionStatement expressionStatement = extractedClassAST.newExpressionStatement(assignment);
		Block setterMethodBody = extractedClassAST.newBlock();
		ListRewrite setterMethodBodyRewrite = extractedClassRewriter.getListRewrite(setterMethodBody, Block.STATEMENTS_PROPERTY);
		setterMethodBodyRewrite.insertLast(expressionStatement, null);
		extractedClassRewriter.set(setterMethodDeclaration, MethodDeclaration.BODY_PROPERTY, setterMethodBody, null);
		return setterMethodDeclaration;
	}

	private MethodDeclaration createGetterMethodDeclaration(VariableDeclaration fieldFragment, AST extractedClassAST, ASTRewrite extractedClassRewriter) {
		String originalFieldName = fieldFragment.getName().getIdentifier();
		String modifiedFieldName = originalFieldName.substring(0,1).toUpperCase() + originalFieldName.substring(1,originalFieldName.length());
		MethodDeclaration getterMethodDeclaration = extractedClassAST.newMethodDeclaration();
		extractedClassRewriter.set(getterMethodDeclaration, MethodDeclaration.NAME_PROPERTY, extractedClassAST.newSimpleName("get" + modifiedFieldName), null);
		FieldDeclaration originalFieldDeclaration = (FieldDeclaration)fieldFragment.getParent();
		extractedClassRewriter.set(getterMethodDeclaration, MethodDeclaration.RETURN_TYPE2_PROPERTY, originalFieldDeclaration.getType(), null);
		ListRewrite getterMethodModifiersRewrite = extractedClassRewriter.getListRewrite(getterMethodDeclaration, MethodDeclaration.MODIFIERS2_PROPERTY);
		getterMethodModifiersRewrite.insertLast(extractedClassAST.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD), null);
		if((originalFieldDeclaration.getModifiers() & Modifier.STATIC) != 0) {
			getterMethodModifiersRewrite.insertLast(extractedClassAST.newModifier(Modifier.ModifierKeyword.STATIC_KEYWORD), null);
		}
		ReturnStatement returnStatement = extractedClassAST.newReturnStatement();
		extractedClassRewriter.set(returnStatement, ReturnStatement.EXPRESSION_PROPERTY, fieldFragment.getName(), null);
		Block getterMethodBody = extractedClassAST.newBlock();
		ListRewrite getterMethodBodyRewrite = extractedClassRewriter.getListRewrite(getterMethodBody, Block.STATEMENTS_PROPERTY);
		getterMethodBodyRewrite.insertLast(returnStatement, null);
		extractedClassRewriter.set(getterMethodDeclaration, MethodDeclaration.BODY_PROPERTY, getterMethodBody, null);
		return getterMethodDeclaration;
	}

	private void getSimpleTypeBindings(Set<ITypeBinding> typeBindings, Set<ITypeBinding> finalTypeBindings) {
		for(ITypeBinding typeBinding : typeBindings) {
			if(typeBinding.isPrimitive()) {

			}
			else if(typeBinding.isArray()) {
				ITypeBinding elementTypeBinding = typeBinding.getElementType();
				Set<ITypeBinding> typeBindingList = new LinkedHashSet<ITypeBinding>();
				typeBindingList.add(elementTypeBinding);
				getSimpleTypeBindings(typeBindingList, finalTypeBindings);
			}
			else if(typeBinding.isParameterizedType()) {
				Set<ITypeBinding> typeBindingList = new LinkedHashSet<ITypeBinding>();
				typeBindingList.add(typeBinding.getTypeDeclaration());
				ITypeBinding[] typeArgumentBindings = typeBinding.getTypeArguments();
				for(ITypeBinding typeArgumentBinding : typeArgumentBindings)
					typeBindingList.add(typeArgumentBinding);
				getSimpleTypeBindings(typeBindingList, finalTypeBindings);
			}
			else if(typeBinding.isWildcardType()) {
				Set<ITypeBinding> typeBindingList = new LinkedHashSet<ITypeBinding>();
				typeBindingList.add(typeBinding.getBound());
				getSimpleTypeBindings(typeBindingList, finalTypeBindings);
			}
			else {
				if(typeBinding.isNested()) {
					finalTypeBindings.add(typeBinding.getDeclaringClass());
				}
				finalTypeBindings.add(typeBinding);
			}
		}
	}

	private void addImportDeclaration(ITypeBinding typeBinding, CompilationUnit targetCompilationUnit, ASTRewrite targetRewriter) {
		String qualifiedName = typeBinding.getQualifiedName();
		String qualifiedPackageName = "";
		if(qualifiedName.contains("."))
			qualifiedPackageName = qualifiedName.substring(0,qualifiedName.lastIndexOf("."));
		PackageDeclaration sourcePackageDeclaration = sourceCompilationUnit.getPackage();
		String sourcePackageDeclarationName = "";
		if(sourcePackageDeclaration != null)
			sourcePackageDeclarationName = sourcePackageDeclaration.getName().getFullyQualifiedName();     
		if(!qualifiedPackageName.equals("") && !qualifiedPackageName.equals("java.lang") &&
				!qualifiedPackageName.equals(sourcePackageDeclarationName) && !typeBinding.isNested()) {
			List<ImportDeclaration> importDeclarationList = targetCompilationUnit.imports();
			boolean found = false;
			for(ImportDeclaration importDeclaration : importDeclarationList) {
				if(!importDeclaration.isOnDemand()) {
					if(qualifiedName.equals(importDeclaration.getName().getFullyQualifiedName())) {
						found = true;
						break;
					}
				}
				else {
					if(qualifiedPackageName.equals(importDeclaration.getName().getFullyQualifiedName())) {
						found = true;
						break;
					}
				}
			}
			if(!found) {
				AST ast = targetCompilationUnit.getAST();
				ImportDeclaration importDeclaration = ast.newImportDeclaration();
				targetRewriter.set(importDeclaration, ImportDeclaration.NAME_PROPERTY, ast.newName(qualifiedName), null);
				ListRewrite importRewrite = targetRewriter.getListRewrite(targetCompilationUnit, CompilationUnit.IMPORTS_PROPERTY);
				importRewrite.insertLast(importDeclaration, null);
			}
		}
	}

	private void createExtractedTypeFieldReferenceInSourceClass() {
		ASTRewrite sourceRewriter = ASTRewrite.create(sourceTypeDeclaration.getAST());
		AST contextAST = sourceTypeDeclaration.getAST();
		ListRewrite contextBodyRewrite = sourceRewriter.getListRewrite(sourceTypeDeclaration, TypeDeclaration.BODY_DECLARATIONS_PROPERTY);
		VariableDeclarationFragment extractedReferenceFragment = contextAST.newVariableDeclarationFragment();
		String modifiedExtractedTypeName = extractedTypeName.substring(0,1).toLowerCase() + extractedTypeName.substring(1,extractedTypeName.length());
		sourceRewriter.set(extractedReferenceFragment, VariableDeclarationFragment.NAME_PROPERTY, contextAST.newSimpleName(modifiedExtractedTypeName), null);
		if(constructorFinalFieldAssignmentMap.isEmpty()) {
			ClassInstanceCreation initializer = contextAST.newClassInstanceCreation();
			Type targetType = contextAST.newSimpleType(contextAST.newName(extractedTypeName));
			sourceRewriter.set(initializer, ClassInstanceCreation.TYPE_PROPERTY, targetType, null);
			sourceRewriter.set(extractedReferenceFragment, VariableDeclarationFragment.INITIALIZER_PROPERTY, initializer, null);
		}
		else {
			ExpressionExtractor expressionExtractor = new ExpressionExtractor();
			for(MethodDeclaration constructor : constructorFinalFieldAssignmentMap.keySet()) {
				ASTRewrite constructorRewriter = ASTRewrite.create(sourceTypeDeclaration.getAST());
				ListRewrite constructorBodyStatementsRewrite = constructorRewriter.getListRewrite(constructor.getBody(), Block.STATEMENTS_PROPERTY);
				
				Assignment extractedTypeFieldReferenceAssignment = contextAST.newAssignment();
				FieldAccess extractedTypeFieldAccess = contextAST.newFieldAccess();
				constructorRewriter.set(extractedTypeFieldAccess, FieldAccess.NAME_PROPERTY, contextAST.newSimpleName(modifiedExtractedTypeName), null);
				constructorRewriter.set(extractedTypeFieldAccess, FieldAccess.EXPRESSION_PROPERTY, contextAST.newThisExpression(), null);
				constructorRewriter.set(extractedTypeFieldReferenceAssignment, Assignment.LEFT_HAND_SIDE_PROPERTY, extractedTypeFieldAccess, null);
				constructorRewriter.set(extractedTypeFieldReferenceAssignment, Assignment.OPERATOR_PROPERTY, Assignment.Operator.ASSIGN, null);
				ClassInstanceCreation classInstanceCreation = contextAST.newClassInstanceCreation();
				Type targetType = contextAST.newSimpleType(contextAST.newName(extractedTypeName));
				constructorRewriter.set(classInstanceCreation, ClassInstanceCreation.TYPE_PROPERTY, targetType, null);
				constructorRewriter.set(extractedTypeFieldReferenceAssignment, Assignment.RIGHT_HAND_SIDE_PROPERTY, classInstanceCreation, null);
				ExpressionStatement assignmentStatement = contextAST.newExpressionStatement(extractedTypeFieldReferenceAssignment);
				
				Map<VariableDeclaration, Assignment> finalFieldAssignmentMap = constructorFinalFieldAssignmentMap.get(constructor);
				ListRewrite classInstanceCreationArgumentsRewrite = constructorRewriter.getListRewrite(classInstanceCreation, ClassInstanceCreation.ARGUMENTS_PROPERTY);
				Set<SingleVariableDeclaration> extractedClassConstructorParameters = new LinkedHashSet<SingleVariableDeclaration>();
	        	for(VariableDeclaration fieldFragment : finalFieldAssignmentMap.keySet()) {
	        		Assignment fieldAssignment = finalFieldAssignmentMap.get(fieldFragment);
	        		List<Expression> variableInstructions = expressionExtractor.getVariableInstructions(fieldAssignment.getRightHandSide());
	        		for(Expression expression : variableInstructions) {
	        			SimpleName simpleName = (SimpleName)expression;
	        			List<SingleVariableDeclaration> originalConstructorParameters = constructor.parameters();
	        			for(SingleVariableDeclaration originalConstructorParameter : originalConstructorParameters) {
	        				if(originalConstructorParameter.resolveBinding().isEqualTo(simpleName.resolveBinding())) {
	        					if(!extractedClassConstructorParameters.contains(originalConstructorParameter)) {
	        						classInstanceCreationArgumentsRewrite.insertLast(simpleName, null);
	        						extractedClassConstructorParameters.add(originalConstructorParameter);
	        					}
	        				}
	        			}
	        		}
	        	}
	        	constructorBodyStatementsRewrite.insertFirst(assignmentStatement, null);
	        	TextEdit sourceEdit = constructorRewriter.rewriteAST(this.sourceDocument, null);
	        	this.sourceMultiTextEdit.addChild(sourceEdit);
			/*	this.sourceICompilationUnit = (ICompilationUnit)this.sourceCompilationUnit.getJavaElement();
				CompilationUnitChange change = compilationUnitChanges.get(this.sourceICompilationUnit);
				change.getEdit().addChild(sourceEdit);
				change.addTextEditGroup(new TextEditGroup("Initialize field holding a reference to the extracted class", new TextEdit[] {sourceEdit}));*/
			}
		}
		FieldDeclaration extractedReferenceFieldDeclaration = contextAST.newFieldDeclaration(extractedReferenceFragment);
		sourceRewriter.set(extractedReferenceFieldDeclaration, FieldDeclaration.TYPE_PROPERTY, contextAST.newSimpleName(extractedTypeName), null);
		ListRewrite typeFieldDeclarationModifiersRewrite = sourceRewriter.getListRewrite(extractedReferenceFieldDeclaration, FieldDeclaration.MODIFIERS2_PROPERTY);
		typeFieldDeclarationModifiersRewrite.insertLast(contextAST.newModifier(Modifier.ModifierKeyword.PRIVATE_KEYWORD), null);
		contextBodyRewrite.insertFirst(extractedReferenceFieldDeclaration, null);
		
		TextEdit sourceEdit = sourceRewriter.rewriteAST(this.sourceDocument, this.sourceICompilationUnit.getJavaProject().getOptions(true));
		this.sourceMultiTextEdit.addChild(sourceEdit);
		/*this.sourceICompilationUnit = (ICompilationUnit)this.sourceCompilationUnit.getJavaElement();
		CompilationUnitChange change = compilationUnitChanges.get(this.sourceICompilationUnit);
		change.getEdit().addChild(sourceEdit);
		change.addTextEditGroup(new TextEditGroup("Create field holding a reference to the extracted class", new TextEdit[] {sourceEdit}));*/
	}

	private void removeFieldFragmentsInSourceClass(Set<VariableDeclaration> fieldFragments) {
		System.out.println("Source Type Declaration: " + this.sourceTypeDeclaration.getName());
		
		FieldDeclaration[] fieldDeclarations = sourceTypeDeclaration.getFields();
		for(FieldDeclaration fieldDeclaration : fieldDeclarations) {
			List<VariableDeclarationFragment> fragments = fieldDeclaration.fragments();
			int actualNumberOfFragments = fragments.size();
			Set<VariableDeclaration> fragmentsToBeRemoved = new LinkedHashSet<VariableDeclaration>();
			for(VariableDeclarationFragment fragment : fragments) {
				/*if(fieldFragments.contains(fragment)) {
					fragmentsToBeRemoved.add(fragment);
				}*/
				for (VariableDeclaration fieldFragment : fieldFragments) {
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
			}
		}
	}

	private void modifyExtractedFieldAssignmentsInSourceClass(Set<VariableDeclaration> fieldFragments) {
		
		ExpressionExtractor expressionExtractor = new ExpressionExtractor();
		Set<MethodDeclaration> contextMethods = getAllMethodDeclarationsInSourceClass();
		String modifiedExtractedTypeName = extractedTypeName.substring(0,1).toLowerCase() + extractedTypeName.substring(1,extractedTypeName.length());
		for(MethodDeclaration methodDeclaration : contextMethods) {
				Block methodBody = methodDeclaration.getBody();
				if(methodBody != null) {
					List<Statement> statements = methodBody.statements();
					for(Statement statement : statements) {
						ASTRewrite sourceRewriter = null;
						if(statementRewriteMap.containsKey(statement)) {
							sourceRewriter = statementRewriteMap.get(statement);
						}
						else {
							sourceRewriter = ASTRewrite.create(sourceTypeDeclaration.getAST());
						}
						AST contextAST = sourceTypeDeclaration.getAST();
						boolean rewriteAST = false;
						
						List<Expression> assignments = expressionExtractor.getAssignments(statement);
						for(Expression expression : assignments) {
							Assignment assignment = (Assignment)expression;
							Expression leftHandSide = assignment.getLeftHandSide();
							SimpleName assignedVariable = null;
							if(leftHandSide instanceof SimpleName) {
								assignedVariable = (SimpleName)leftHandSide;
							}
							else if(leftHandSide instanceof FieldAccess) {
								FieldAccess fieldAccess = (FieldAccess)leftHandSide;
								assignedVariable = fieldAccess.getName();
							}
							Expression rightHandSide = assignment.getRightHandSide();
							List<Expression> accessedVariables = expressionExtractor.getVariableInstructions(rightHandSide);
							List<Expression> arrayAccesses = expressionExtractor.getArrayAccesses(leftHandSide);
							for(VariableDeclaration fieldFragment : fieldFragments) {
								String originalFieldName = fieldFragment.getName().getIdentifier();
								String modifiedFieldName = originalFieldName.substring(0,1).toUpperCase() + originalFieldName.substring(1,originalFieldName.length());
								if(assignedVariable != null) {
									IBinding leftHandBinding = assignedVariable.resolveBinding();
									if(leftHandBinding.getKind() == IBinding.VARIABLE) {
										IVariableBinding assignedVariableBinding = (IVariableBinding)leftHandBinding;
										if(assignedVariableBinding.isField() && fieldFragment.resolveBinding().isEqualTo(assignedVariableBinding)) {
											if(methodDeclaration.isConstructor() && (assignedVariableBinding.getModifiers() & Modifier.FINAL) != 0) {
												if(assignment.getParent() instanceof ExpressionStatement) {
													ExpressionStatement assignmentStatement = (ExpressionStatement)assignment.getParent();
													ListRewrite constructorStatementsRewrite = sourceRewriter.getListRewrite(methodDeclaration.getBody(), Block.STATEMENTS_PROPERTY);
													constructorStatementsRewrite.remove(assignmentStatement, null);
													if(constructorFinalFieldAssignmentMap.containsKey(methodDeclaration)) {
														Map<VariableDeclaration, Assignment> finalFieldAssignmentMap = constructorFinalFieldAssignmentMap.get(methodDeclaration);
														finalFieldAssignmentMap.put(fieldFragment, assignment);
													}
													else {
														Map<VariableDeclaration, Assignment> finalFieldAssignmentMap = new LinkedHashMap<VariableDeclaration, Assignment>();
														finalFieldAssignmentMap.put(fieldFragment, assignment);
														constructorFinalFieldAssignmentMap.put(methodDeclaration, finalFieldAssignmentMap);
													}
												}
											}
											else {
												MethodInvocation setterMethodInvocation = contextAST.newMethodInvocation();
												sourceRewriter.set(setterMethodInvocation, MethodInvocation.NAME_PROPERTY, contextAST.newSimpleName("set" + modifiedFieldName), null);
												ListRewrite setterMethodInvocationArgumentsRewrite = sourceRewriter.getListRewrite(setterMethodInvocation, MethodInvocation.ARGUMENTS_PROPERTY);
												setterMethodInvocationArgumentsRewrite.insertLast(assignment.getRightHandSide(), null);
												if((assignedVariableBinding.getModifiers() & Modifier.STATIC) != 0) {
													sourceRewriter.set(setterMethodInvocation, MethodInvocation.EXPRESSION_PROPERTY, contextAST.newSimpleName(extractedTypeName), null);
												}
												else {
													sourceRewriter.set(setterMethodInvocation, MethodInvocation.EXPRESSION_PROPERTY, contextAST.newSimpleName(modifiedExtractedTypeName), null);
												}
												sourceRewriter.replace(assignment, setterMethodInvocation, null);
											}
											rewriteAST = true;
										}
									}
								}
								for(Expression expression2 : arrayAccesses) {
									ArrayAccess arrayAccess = (ArrayAccess)expression2;
									Expression arrayExpression = arrayAccess.getArray();
									SimpleName arrayVariable = null;
									if(arrayExpression instanceof SimpleName) {
										arrayVariable = (SimpleName)arrayExpression;
									}
									else if(arrayExpression instanceof FieldAccess) {
										FieldAccess fieldAccess = (FieldAccess)arrayExpression;
										arrayVariable = fieldAccess.getName();
									}
									if(arrayVariable != null) {
										IBinding arrayBinding = arrayVariable.resolveBinding();
										if(arrayBinding.getKind() == IBinding.VARIABLE) {
											IVariableBinding arrayVariableBinding = (IVariableBinding)arrayBinding;
											if(arrayVariableBinding.isField() && fieldFragment.resolveBinding().isEqualTo(arrayVariableBinding)) {
												MethodInvocation getterMethodInvocation = contextAST.newMethodInvocation();
												sourceRewriter.set(getterMethodInvocation, MethodInvocation.NAME_PROPERTY, contextAST.newSimpleName("get" + modifiedFieldName), null);
												if((arrayVariableBinding.getModifiers() & Modifier.STATIC) != 0) {
													sourceRewriter.set(getterMethodInvocation, MethodInvocation.EXPRESSION_PROPERTY, contextAST.newSimpleName(extractedTypeName), null);
												}
												else {
													sourceRewriter.set(getterMethodInvocation, MethodInvocation.EXPRESSION_PROPERTY, contextAST.newSimpleName(modifiedExtractedTypeName), null);
												}
												sourceRewriter.replace(arrayVariable, getterMethodInvocation, null);
												rewriteAST = true;
											}
										}
									}
								}
								for(Expression expression2 : accessedVariables) {
									SimpleName accessedVariable = (SimpleName)expression2;
									IBinding rightHandBinding = accessedVariable.resolveBinding();
									if(rightHandBinding.getKind() == IBinding.VARIABLE) {
										IVariableBinding accessedVariableBinding = (IVariableBinding)rightHandBinding;
										if(accessedVariableBinding.isField() && fieldFragment.resolveBinding().isEqualTo(accessedVariableBinding)) {
											MethodInvocation getterMethodInvocation = contextAST.newMethodInvocation();
											sourceRewriter.set(getterMethodInvocation, MethodInvocation.NAME_PROPERTY, contextAST.newSimpleName("get" + modifiedFieldName), null);
											if((accessedVariableBinding.getModifiers() & Modifier.STATIC) != 0) {
												sourceRewriter.set(getterMethodInvocation, MethodInvocation.EXPRESSION_PROPERTY, contextAST.newSimpleName(extractedTypeName), null);
											}
											else {
												sourceRewriter.set(getterMethodInvocation, MethodInvocation.EXPRESSION_PROPERTY, contextAST.newSimpleName(modifiedExtractedTypeName), null);
											}
											sourceRewriter.replace(accessedVariable, getterMethodInvocation, null);
											rewriteAST = true;
										}
									}
								}
							}
						}
						if(rewriteAST) {
							if(!statementRewriteMap.containsKey(statement))
								statementRewriteMap.put(statement, sourceRewriter);
							/*try {
								TextEdit sourceEdit = sourceRewriter.rewriteAST();
								ICompilationUnit sourceICompilationUnit = (ICompilationUnit)sourceCompilationUnit.getJavaElement();
								CompilationUnitChange change = compilationUnitChanges.get(sourceICompilationUnit);
								change.getEdit().addChild(sourceEdit);
								change.addTextEditGroup(new TextEditGroup("Replace field assignment with invocation of setter method", new TextEdit[] {sourceEdit}));
							} catch (JavaModelException e) {
								e.printStackTrace();
							}*/
						}
					}
				}
			}
		
	}

	private void modifyExtractedFieldAccessesInSourceClass(Set<VariableDeclaration> fieldFragments) {
		ExpressionExtractor expressionExtractor = new ExpressionExtractor();
		Set<MethodDeclaration> contextMethods = getAllMethodDeclarationsInSourceClass();
		String modifiedExtractedTypeName = extractedTypeName.substring(0,1).toLowerCase() + extractedTypeName.substring(1,extractedTypeName.length());
		for(MethodDeclaration methodDeclaration : contextMethods) {
				Block methodBody = methodDeclaration.getBody();
				if(methodBody != null) {
					List<Statement> statements = methodBody.statements();
					for(Statement statement : statements) {
						ASTRewrite sourceRewriter = null;
						if(statementRewriteMap.containsKey(statement)) {
							sourceRewriter = statementRewriteMap.get(statement);
						}
						else {
							sourceRewriter = ASTRewrite.create(sourceTypeDeclaration.getAST());
						}
						AST contextAST = sourceTypeDeclaration.getAST();
						boolean rewriteAST = false;
						List<Expression> accessedVariables = expressionExtractor.getVariableInstructions(statement);
						List<Expression> arrayAccesses = expressionExtractor.getArrayAccesses(statement);
						for(VariableDeclaration fieldFragment : fieldFragments) {
							String originalFieldName = fieldFragment.getName().getIdentifier();
							String modifiedFieldName = originalFieldName.substring(0,1).toUpperCase() + originalFieldName.substring(1,originalFieldName.length());
							for(Expression expression : accessedVariables) {
								SimpleName accessedVariable = (SimpleName)expression;
								IBinding binding = accessedVariable.resolveBinding();
								if(binding.getKind() == IBinding.VARIABLE) {
									IVariableBinding accessedVariableBinding = (IVariableBinding)binding;
									if(accessedVariableBinding.isField() && fieldFragment.resolveBinding().isEqualTo(accessedVariableBinding)) {
										if(!isAssignmentChild(expression)) {
											MethodInvocation getterMethodInvocation = contextAST.newMethodInvocation();
											sourceRewriter.set(getterMethodInvocation, MethodInvocation.NAME_PROPERTY, contextAST.newSimpleName("get" + modifiedFieldName), null);
											if((accessedVariableBinding.getModifiers() & Modifier.STATIC) != 0) {
												sourceRewriter.set(getterMethodInvocation, MethodInvocation.EXPRESSION_PROPERTY, contextAST.newSimpleName(extractedTypeName), null);
											}
											else {
												sourceRewriter.set(getterMethodInvocation, MethodInvocation.EXPRESSION_PROPERTY, contextAST.newSimpleName(modifiedExtractedTypeName), null);
											}
											sourceRewriter.replace(accessedVariable, getterMethodInvocation, null);
											rewriteAST = true;
										}
									}
								}
							}
							for(Expression expression : arrayAccesses) {
								ArrayAccess arrayAccess = (ArrayAccess)expression;
								Expression arrayExpression = arrayAccess.getArray();
								SimpleName arrayVariable = null;
								if(arrayExpression instanceof SimpleName) {
									arrayVariable = (SimpleName)arrayExpression;
								}
								else if(arrayExpression instanceof FieldAccess) {
									FieldAccess fieldAccess = (FieldAccess)arrayExpression;
									arrayVariable = fieldAccess.getName();
								}
								if(arrayVariable != null) {
									IBinding arrayBinding = arrayVariable.resolveBinding();
									if(arrayBinding.getKind() == IBinding.VARIABLE) {
										IVariableBinding arrayVariableBinding = (IVariableBinding)arrayBinding;
										if(arrayVariableBinding.isField() && fieldFragment.resolveBinding().isEqualTo(arrayVariableBinding)) {
											if(!isAssignmentChild(expression)) {
												MethodInvocation getterMethodInvocation = contextAST.newMethodInvocation();
												sourceRewriter.set(getterMethodInvocation, MethodInvocation.NAME_PROPERTY, contextAST.newSimpleName("get" + modifiedFieldName), null);
												if((arrayVariableBinding.getModifiers() & Modifier.STATIC) != 0) {
													sourceRewriter.set(getterMethodInvocation, MethodInvocation.EXPRESSION_PROPERTY, contextAST.newSimpleName(extractedTypeName), null);
												}
												else {
													sourceRewriter.set(getterMethodInvocation, MethodInvocation.EXPRESSION_PROPERTY, contextAST.newSimpleName(modifiedExtractedTypeName), null);
												}
												sourceRewriter.replace(arrayVariable, getterMethodInvocation, null);
												rewriteAST = true;
											}
										}
									}
								}
							}
						}
						if(rewriteAST) {
							if(!statementRewriteMap.containsKey(statement))
								statementRewriteMap.put(statement, sourceRewriter);
							/*try {
								TextEdit sourceEdit = sourceRewriter.rewriteAST();
								ICompilationUnit sourceICompilationUnit = (ICompilationUnit)sourceCompilationUnit.getJavaElement();
								CompilationUnitChange change = compilationUnitChanges.get(sourceICompilationUnit);
								change.getEdit().addChild(sourceEdit);
								change.addTextEditGroup(new TextEditGroup("Replace field access with invocation of getter method", new TextEdit[] {sourceEdit}));
							} catch (JavaModelException e) {
								e.printStackTrace();
							}*/
						}
					}
				}
			}
		
	}

	private Set<MethodDeclaration> getAllMethodDeclarationsInSourceClass() {
		Set<MethodDeclaration> contextMethods = new LinkedHashSet<MethodDeclaration>();
		for(FieldDeclaration fieldDeclaration : sourceTypeDeclaration.getFields()) {
			contextMethods.addAll(getMethodDeclarationsWithinAnonymousClassDeclarations(fieldDeclaration));
		}
		List<MethodDeclaration> methodDeclarationList = Arrays.asList(sourceTypeDeclaration.getMethods());
		contextMethods.addAll(methodDeclarationList);
		/*for(MethodDeclaration methodDeclaration : methodDeclarationList) {
			contextMethods.addAll(getMethodDeclarationsWithinAnonymousClassDeclarations(methodDeclaration));
		}*/
		//get methods of inner classes
		TypeDeclaration[] types = sourceTypeDeclaration.getTypes();
		for(TypeDeclaration type : types) {
			for(FieldDeclaration fieldDeclaration : type.getFields()) {
				contextMethods.addAll(getMethodDeclarationsWithinAnonymousClassDeclarations(fieldDeclaration));
			}
			List<MethodDeclaration> innerMethodDeclarationList = Arrays.asList(type.getMethods());
			contextMethods.addAll(innerMethodDeclarationList);
			/*for(MethodDeclaration methodDeclaration : innerMethodDeclarationList) {
				contextMethods.addAll(getMethodDeclarationsWithinAnonymousClassDeclarations(methodDeclaration));
			}*/
		}
		return contextMethods;
	}

	private Set<MethodDeclaration> getMethodDeclarationsWithinAnonymousClassDeclarations(FieldDeclaration fieldDeclaration) {
		Set<MethodDeclaration> methods = new LinkedHashSet<MethodDeclaration>();
		List<VariableDeclarationFragment> fragments = fieldDeclaration.fragments();
		for(VariableDeclarationFragment fragment : fragments) {
			Expression expression = fragment.getInitializer();
			if(expression != null && expression instanceof ClassInstanceCreation) {
				ClassInstanceCreation classInstanceCreation = (ClassInstanceCreation)expression;
				AnonymousClassDeclaration anonymousClassDeclaration = classInstanceCreation.getAnonymousClassDeclaration();
				if(anonymousClassDeclaration != null) {
					List<BodyDeclaration> bodyDeclarations = anonymousClassDeclaration.bodyDeclarations();
					for(BodyDeclaration bodyDeclaration : bodyDeclarations) {
						if(bodyDeclaration instanceof MethodDeclaration)
							methods.add((MethodDeclaration)bodyDeclaration);
					}
				}
			}
		}
		return methods;
	}

	private boolean isAssignmentChild(ASTNode node) {
		if(node instanceof Assignment)
			return true;
		else if(node instanceof PrefixExpression) {
			PrefixExpression prefixExpression = (PrefixExpression)node;
			if(prefixExpression.getOperator().equals(PrefixExpression.Operator.INCREMENT) ||
					prefixExpression.getOperator().equals(PrefixExpression.Operator.DECREMENT))
				return true;
			else
				return isAssignmentChild(node.getParent());
		}
		else if(node instanceof PostfixExpression) {
			PostfixExpression postfixExpression = (PostfixExpression)node;
			if(postfixExpression.getOperator().equals(PostfixExpression.Operator.INCREMENT) ||
					postfixExpression.getOperator().equals(PostfixExpression.Operator.DECREMENT))
				return true;
			else
				return isAssignmentChild(node.getParent());
		}
		else if(node instanceof Statement)
			return false;
		else
			return isAssignmentChild(node.getParent());
	}
	
	
	
}

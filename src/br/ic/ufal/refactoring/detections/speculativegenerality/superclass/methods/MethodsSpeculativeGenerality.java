package br.ic.ufal.refactoring.detections.speculativegenerality.superclass.methods;

import gr.uom.java.ast.util.ExpressionExtractor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.detections.BadSmell;

public class MethodsSpeculativeGenerality extends BadSmell {

	private List<SpeculativeMethods> speculativeMethodsList = new ArrayList<SpeculativeMethods>();
	
	public MethodsSpeculativeGenerality(Project project) {
		super(project);
		
	}

	@Override
	public boolean check() {
		for (Clazz clazz : super.getProject().getClasses()) {
			
			ICompilationUnit iCompilationUnit = clazz.getICompilationUnit();
			
			CompilationUnit compilationUnit = clazz.getCompilationUnit();
			
			TypeDeclaration typeDeclaration = (TypeDeclaration) compilationUnit.types().get(0);
			
			SpeculativeMethods speculativeMethods = new SpeculativeMethods();
			speculativeMethods.setClazz(typeDeclaration);
			
			List<TypeDeclaration> subClasses = checkSubClasses(clazz);
			if (subClasses.size() > 0) {
				MethodDeclaration[] methods = typeDeclaration.getMethods();
				for (MethodDeclaration methodDeclaration : methods) {
					boolean isMethodUsed = false;
						
					if (isMethodUsed(methodDeclaration, subClasses)) {
						isMethodUsed = true;
					}
						
					if (!isMethodUsed) {
						speculativeMethods.addSpeculativeMethod(methodDeclaration);
					}
					
				}
				
				if (speculativeMethods.getSpeculativeMethods().size() > 0) {
					this.speculativeMethodsList.add(speculativeMethods);
				}
			}
			
		}	
		
		return this.speculativeMethodsList.size() > 0;
	}
	
	public boolean isMethodUsed(MethodDeclaration methodDeclaration, List<TypeDeclaration> subClasses){
		
		ExpressionExtractor expressionExtractor = new ExpressionExtractor();
		for (TypeDeclaration subclass : subClasses) {
			for (MethodDeclaration method : subclass.getMethods()) {
				List<Expression> methodInvocations = expressionExtractor.getMethodInvocations(method.getBody());
				
				if (method.getName().toString().equalsIgnoreCase(methodDeclaration.getName().toString())) {
					return true;
				}
				
				for (Expression expression : methodInvocations) {
					
					if (expression instanceof MethodInvocation) {
						MethodInvocation methodInvocation = (MethodInvocation) expression;
						
						IMethodBinding methodBinding = methodInvocation.resolveMethodBinding();
						//TODO: Precisa verificar o tipo 
						if (methodBinding.getName().equals(methodDeclaration.getName().toString())) {
							return true;
						}
						
						
					}
					
				}
			}
		}
		
		
		
		return false;
	}

	private List<TypeDeclaration> checkSubClasses(Clazz clazz){
		List<Clazz> subclasses = new ArrayList<Clazz>();
		
		for (Clazz c : super.getProject().getClasses()) {
			if (c.getTypeDeclaration().getSuperclassType() != null) {
				if (c.getTypeDeclaration().getSuperclassType().resolveBinding().isEqualTo(clazz.getTypeDeclaration().getSuperclassType().resolveBinding())) {
					subclasses.add(c);
				}
				
			}
		}
		
		List<TypeDeclaration> subTypes = new ArrayList<TypeDeclaration>();
		
		for (Clazz subclass : subclasses) {
			ICompilationUnit iCompilationUnit = subclass.getICompilationUnit();
			
			CompilationUnit compilationUnit = subclass.getCompilationUnit();
			
			TypeDeclaration typeDeclaration = (TypeDeclaration) compilationUnit.types().get(0);
			subTypes.add(typeDeclaration);
		}
		
		
		
		return subTypes;
	}
	
	public List<SpeculativeMethods> getSpeculativeMethodsList() {
		return speculativeMethodsList;
	}
}

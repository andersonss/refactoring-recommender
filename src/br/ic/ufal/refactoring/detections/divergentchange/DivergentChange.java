package br.ic.ufal.refactoring.detections.divergentchange;

import gr.uom.java.ast.util.ExpressionExtractor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.detections.BadSmell;

public class DivergentChange extends BadSmell {

	private List<TypeDeclaration> divergentClasses = new ArrayList<TypeDeclaration>();
	
	public DivergentChange(Project project) {
		super(project);
	}

	@Override
	public boolean check() {
		
		ExpressionExtractor expressionExtractor = new ExpressionExtractor();
		
		for (Clazz clazz : super.getProject().getClasses()) {
			
			System.out.println("Class: " + clazz.getTypeDeclaration().getName().getIdentifier());
			
			ICompilationUnit iCompilationUnit = clazz.getICompilationUnit();
			
			CompilationUnit compilationUnit = clazz.getCompilationUnit();
			
			TypeDeclaration typeDeclaration = (TypeDeclaration)compilationUnit.types().get(0);
			
			Set<String> coupling = new HashSet<String>();
			
			for (MethodDeclaration method : typeDeclaration.getMethods()) {
				
				System.out.println("Method: " + method.getName().getIdentifier());
				
				List<Expression> methodInvocations = expressionExtractor.getMethodInvocations(method.getBody());
				
				for (Expression expression : methodInvocations) {
					
					if (expression instanceof MethodInvocation) {
						MethodInvocation methodInvocation = (MethodInvocation) expression;
						System.out.println("Invoked Method "+methodInvocation.getName().getIdentifier()+" from: " + methodInvocation.resolveMethodBinding().getDeclaringClass().getName());
					}
					
				}
				
				List<Expression> variables = expressionExtractor.getVariableInstructions(method.getBody());
				
				for (Expression expression : variables) {
					
					SimpleName variable = (SimpleName)expression;
					IBinding variableBinding = variable.resolveBinding();
					
					if (variableBinding != null) {
						
						if(variableBinding.getKind() == IBinding.VARIABLE) {
							IVariableBinding accessedVariableBinding = (IVariableBinding)variableBinding;
							
							if(accessedVariableBinding.isField()) {
								System.out.println("Accessed Fields "+accessedVariableBinding.getName()+" from: " + accessedVariableBinding.getDeclaringClass().getName());
							}
						}
					}
				}	
				
			}
			
		}
		return true;
	}
	
	public List<TypeDeclaration> getDivergentClasses() {
		return divergentClasses;
	}

}

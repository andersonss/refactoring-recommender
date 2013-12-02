package br.ic.ufal.refactoring.detections.divergentchange;

import gr.uom.java.ast.util.ExpressionExtractor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

public class DivergentChange extends BadSmell {

	private List<TypeDeclaration> divergentClasses = new ArrayList<TypeDeclaration>();
	
	public DivergentChange(Project project) {
		super(project);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean check() {
		ExpressionExtractor expressionExtractor = new ExpressionExtractor();
		
		for (Clazz clazz : super.getProject().getClasses()) {
			
			ICompilationUnit iCompilationUnit = clazz.getICompilationUnit();
			
			CompilationUnit compilationUnit = clazz.getCompilationUnit();
			
			TypeDeclaration typeDeclaration = (TypeDeclaration)compilationUnit.types().get(0);
			
			Set<String> coupling = new HashSet<String>();
			
			for (MethodDeclaration method : typeDeclaration.getMethods()) {
				
				
				List<Expression> methodInvocations = expressionExtractor.getMethodInvocations(method.getBody());
				
				for (Expression expression : methodInvocations) {
					
					if (expression instanceof MethodInvocation) {
						MethodInvocation methodInvocation = (MethodInvocation) expression;
						
						IMethodBinding methodBinding = methodInvocation.resolveMethodBinding();
					
						coupling.add(methodBinding.getDeclaringClass().getName());
						
					}
					
				}
				
			}
			
			if (coupling.size() > 0) {
				this.divergentClasses.add(typeDeclaration);
			}
			
		}
		return this.divergentClasses.size() > 0;
	}
	
	public List<TypeDeclaration> getDivergentClasses() {
		return divergentClasses;
	}

}

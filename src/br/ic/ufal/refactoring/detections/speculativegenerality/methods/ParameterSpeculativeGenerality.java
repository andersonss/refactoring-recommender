package br.ic.ufal.refactoring.detections.speculativegenerality.methods;

import gr.uom.java.ast.util.ExpressionExtractor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.detections.BadSmell;

public class ParameterSpeculativeGenerality extends BadSmell {

	private List<SpeculativeParameters> speculativeParametersList = new ArrayList<SpeculativeParameters>();
	
	public ParameterSpeculativeGenerality(Project project) {
		super(project);
	}

	public boolean check(){
		
		
		
		for (Clazz clazz : super.getProject().getClasses()) {
			
			ICompilationUnit iCompilationUnit = clazz.getICompilationUnit();
			
			CompilationUnit compilationUnit = clazz.getCompilationUnit();
			
			TypeDeclaration typeDeclaration = (TypeDeclaration) compilationUnit.types().get(0);
			
			for (MethodDeclaration method : typeDeclaration.getMethods()) {
				
				SpeculativeParameters speculativeParameters = new SpeculativeParameters();
				speculativeParameters.setMethod(method);
				
				List<SingleVariableDeclaration> parameters = method.parameters();
				
				for (SingleVariableDeclaration parameter : parameters) {
					if (!isParameterUsed(parameter, method)) {
						speculativeParameters.addParameterSpec(parameter);
					}
				}
				
				if (speculativeParameters.getParameters().size() > 0) {
					this.speculativeParametersList.add(speculativeParameters);
				}
				
			}
			
		}	
		
		return this.speculativeParametersList.size() > 0;
	}
	
	public boolean isParameterUsed(SingleVariableDeclaration parameter, MethodDeclaration method){
		
		ExpressionExtractor expressionExtractor = new ExpressionExtractor();
		
		List<Expression> newVariableInstructions = expressionExtractor.getVariableInstructions(method.getBody());
		
		for (Expression expression : newVariableInstructions) {
			
			SimpleName sn = (SimpleName)expression;
			
			if (sn.toString().equalsIgnoreCase(parameter.getName().toString()) &&
				sn.resolveTypeBinding().getName().toString().equalsIgnoreCase(parameter.getType().toString())) {
				
				return true;
			}
		}
		
		return false;
	}
	
	public List<SpeculativeParameters> getSpeculativeParameters() {
		return speculativeParametersList;
	}
}

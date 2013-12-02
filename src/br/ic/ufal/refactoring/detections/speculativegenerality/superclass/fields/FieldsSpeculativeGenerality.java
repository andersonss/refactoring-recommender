package br.ic.ufal.refactoring.detections.speculativegenerality.superclass.fields;

import gr.uom.java.ast.util.ExpressionExtractor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.detections.BadSmell;

public class FieldsSpeculativeGenerality extends BadSmell {

	private List<SpeculativeFragments> speculativeFragmentsList = new ArrayList<SpeculativeFragments>();
	
	public FieldsSpeculativeGenerality(Project project) {
		super(project);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean check() {
		for (Clazz clazz : super.getProject().getClasses()) {
			
			ICompilationUnit iCompilationUnit = clazz.getICompilationUnit();
			
			CompilationUnit compilationUnit = clazz.getCompilationUnit();
			
			TypeDeclaration typeDeclaration = (TypeDeclaration) compilationUnit.types().get(0);
			
			SpeculativeFragments speculativeFragments = new SpeculativeFragments();
			speculativeFragments.setClazz(typeDeclaration);
			
			List<TypeDeclaration> subClasses = checkSubClasses(clazz);
			if (subClasses.size() > 0) {
				FieldDeclaration[] fields = typeDeclaration.getFields();
				for (FieldDeclaration fieldDeclaration : fields) {
					List<VariableDeclarationFragment> fragments = fieldDeclaration.fragments();
					for (VariableDeclarationFragment fragment : fragments) {
						boolean isFragUsed = false;
						
						
						for (TypeDeclaration subType : subClasses) {
							for (MethodDeclaration method : subType.getMethods()) {
								if (isFragmentUsed(fragment, fieldDeclaration, method)) {
									isFragUsed = true;
								}
							}
						}
						
						if (!isFragUsed) {
							speculativeFragments.addSpeculativeFragment(fragment);
						}
					}
				}
				
				if (speculativeFragments.getSpeculativeFragments().size() > 0) {
					this.speculativeFragmentsList.add(speculativeFragments);
				}
			}
			
			
			
			
		}	
		
		return this.speculativeFragmentsList.size() > 0;
	}
	
	public boolean isFragmentUsed(VariableDeclarationFragment fragment, FieldDeclaration field, MethodDeclaration method){
		
		ExpressionExtractor expressionExtractor = new ExpressionExtractor();
		
		List<Expression> newVariableInstructions = expressionExtractor.getVariableInstructions(method.getBody());
		
		for (Expression expression : newVariableInstructions) {
			
			SimpleName sn = (SimpleName)expression;
			
			if (sn.toString().equalsIgnoreCase(fragment.getName().toString()) &&
				sn.resolveTypeBinding().getName().toString().equalsIgnoreCase(field.getType().toString())) {
				
				return true;
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

	public List<SpeculativeFragments> getSpeculativeFragments() {
		return speculativeFragmentsList;
	}
}

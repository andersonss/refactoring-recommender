package br.ic.ufal.refactoring.corrections.makingmethodcallsimpler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.corrections.Correction;
import br.ic.ufal.refactoring.corrections.movingfeaturesbetwenobjects.MoveField;
import br.ic.ufal.refactoring.corrections.movingfeaturesbetwenobjects.MoveMethod;
import br.ic.ufal.refactoring.detections.speculativegenerality.inheritance.AbstractSpeculativeGenerality;
import br.ic.ufal.util.ParseUtil;

public class CollapseHierarchy extends Correction {

	private List<Clazz> speculatives = null;
	
	public CollapseHierarchy(List<Clazz> speculatives, Project project) {
		super(project);
		this.speculatives = speculatives;
	}

	@Override
	public void execute() {
			for (Clazz clazz : this.speculatives) {
				Type superclazzType = clazz.getTypeDeclaration().getSuperclassType();
			
				Clazz superclazz = ParseUtil.getClazz(superclazzType, getProject().getClasses());
				
				TypeDeclaration typeDeclaration = clazz.getTypeDeclaration();
				
				
				for (FieldDeclaration field : typeDeclaration.getFields()) {
					
					Set<VariableDeclaration> fragments = new HashSet<VariableDeclaration>();
					List<VariableDeclaration> fieldFragments = field.fragments();
					for (VariableDeclaration variableDeclaration : fieldFragments) {
						fragments.add(variableDeclaration);
					}
					
					MoveField moveField = new MoveField(clazz, superclazz, fragments, getProject());
					moveField.execute();
					
				}
				
				for (MethodDeclaration method : typeDeclaration.getMethods()) {
					MoveMethod moveMethod = new MoveMethod(clazz, superclazz, method, new HashMap<MethodInvocation, MethodDeclaration>(), false, method.getName().getIdentifier(),getProject());
					moveMethod.execute();
				}
				
				
			}
		}
	

}

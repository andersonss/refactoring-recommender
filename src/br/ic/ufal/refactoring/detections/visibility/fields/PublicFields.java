package br.ic.ufal.refactoring.detections.visibility.fields;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.detections.BadSmell;

public class PublicFields extends BadSmell {

	private List<FieldDeclaration> publicFields = new ArrayList<FieldDeclaration>();
	
	public PublicFields(Project project) {
		super(project);
	}

	@Override
	public boolean check() {
		
		System.out.println("Checking Public Fields");
		for (Clazz clazz : super.getProject().getClasses()) {
			TypeDeclaration typeDeclaration = clazz.getTypeDeclaration();
			
			for (FieldDeclaration fieldDeclaration : typeDeclaration.getFields()) {
				@SuppressWarnings("rawtypes")
				List modifiers = fieldDeclaration.modifiers();
				if (containModifier(ModifierKeyword.PUBLIC_KEYWORD, modifiers)) {
					this.publicFields.add(fieldDeclaration);
				}
			}
		}
		
		return this.publicFields.size() > 0;
	}
	
	private boolean containModifier(ModifierKeyword modifierKeyword, @SuppressWarnings("rawtypes") List modifiers){
		
		for (Object obj : modifiers) {
			if (obj instanceof Modifier) {
				Modifier modifier = (Modifier) obj;
				if (modifier.getKeyword().equals(modifierKeyword)) {
					return true;
				}
				
			}
		}
		
		return false;
	}
	
	public List<FieldDeclaration> getPublicFields() {
		return publicFields;
	}
}

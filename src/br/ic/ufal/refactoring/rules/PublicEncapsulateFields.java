package br.ic.ufal.refactoring.rules;

import org.eclipse.jdt.core.dom.FieldDeclaration;

import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.corrections.Correction;
import br.ic.ufal.refactoring.corrections.organizingdata.EncapsulateField;
import br.ic.ufal.refactoring.detections.BadSmellType;
import br.ic.ufal.refactoring.detections.duplication.subclasses.fields.down.DownFragments;
import br.ic.ufal.refactoring.detections.visibility.fields.PublicFields;

public class PublicEncapsulateFields extends Rule {

	public PublicEncapsulateFields(Project project) {
		super(project);
	}

	@Override
	public void execute() {
		System.out.println("Analysing Public Fields");
		PublicFields publicFields = new PublicFields(getProject());
		if (publicFields.check()) {
			
			int amountOfBadSmellsBefore = publicFields.getPublicFields().size();
			getProject().countDetectedBadSmells(BadSmellType.PublicFields, amountOfBadSmellsBefore);
			
			for (int i = 0; i < publicFields.getPublicFields().size(); i++) {
				
				FieldDeclaration publicField = publicFields.getPublicFields().get(i);
				
				System.out.println("Correct Public Field: " + i + " of " + publicFields.getPublicFields().size());
				
				Correction encapsulateField = new EncapsulateField(publicField, getProject());
				encapsulateField.apply();
				
			}
			
			publicFields = new PublicFields(getProject());
			
			if (publicFields.check()) {
				
				int amountOfBadSmellsAfter = publicFields.getPublicFields().size();
				
				getProject().countAfterBadSmells(BadSmellType.PublicFields, amountOfBadSmellsAfter);
			}else{
				getProject().countAfterBadSmells(BadSmellType.PublicFields, 0);
			}
			
		}else{
			System.out.println("Not Exist Public Fields");
		}

	}

}

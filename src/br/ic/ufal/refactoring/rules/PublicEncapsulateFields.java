package br.ic.ufal.refactoring.rules;

import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.corrections.Correction;
import br.ic.ufal.refactoring.corrections.organizingdata.EncapsulateField;
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
			Correction encapsulateField = new EncapsulateField(publicFields.getPublicFields(), getProject());
			encapsulateField.apply();
		}

	}

}

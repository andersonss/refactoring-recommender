package br.ic.ufal.refactoring.detections.dataclumps.fields;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.detections.BadSmell;
import br.ic.ufal.refactoring.detections.dataclumps.parameters.DuplicatedParameters;
import br.ic.ufal.refactoring.detections.dataclumps.parameters.ParametersBlock;
import br.ic.ufal.util.ParseUtil;

public class FieldsDataClumps extends BadSmell {

	public FieldsDataClumps(Project project) {
		super(project);
		
	}

	@Override
	public boolean check() {
		return false;
	}
	
	
	
	
	
}

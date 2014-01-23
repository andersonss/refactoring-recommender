package br.ic.ufal.refactoring.rules;

import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.corrections.movingfeaturesbetwenobjects.ExtractParameters;
import br.ic.ufal.refactoring.detections.BadSmellType;
import br.ic.ufal.refactoring.detections.dataclumps.fields.FragmentsDataClumps;
import br.ic.ufal.refactoring.detections.dataclumps.parameters.DuplicatedParameters;
import br.ic.ufal.refactoring.detections.dataclumps.parameters.ParametersDataClumps;
import br.ic.ufal.util.ParseUtil;

public class DataClumpsParametersExtractParameters extends Rule {

	public DataClumpsParametersExtractParameters(Project project) {
		super(project);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute() {
		
		System.out.println("Executing Data Clumps Paramters");
		
		ParametersDataClumps parametersDataClumps = new ParametersDataClumps(getProject(), 1);
		
		if (parametersDataClumps.check()) {
			List<DuplicatedParameters> duplicatedParametersList = parametersDataClumps.getDuplicatedParametersList();
			
			int amountOfBadSmellsBefore = duplicatedParametersList.size();
			getProject().countDetectedBadSmells(BadSmellType.ParametersDataClumps, amountOfBadSmellsBefore);
			
			for (DuplicatedParameters duplicatedParameters : duplicatedParametersList) {
				//System.out.println(duplicatedParameters);
				
				IPackageFragment iPackageFragment = ParseUtil.getPackageFrament(duplicatedParameters.getDuplicatedClasses().get(1).getICompilationUnit(), getProject());
				ICompilationUnit newICompilationUnit = null;
				String name = "NewClass"+getProject().generateUniqueId();
				
				try {
					newICompilationUnit = iPackageFragment.createCompilationUnit(name+".java", "", true, null);
				} catch (JavaModelException e) {
					e.printStackTrace();
				}
				IFile iFile = (IFile)newICompilationUnit.getResource();
				System.out.println("Created File: "+iFile.getName());
				
				ExtractParameters extractParamters = new ExtractParameters(iFile, duplicatedParameters.getDuplicatedClasses(), new HashSet<VariableDeclaration>(duplicatedParameters.getBlock().getParamters()), new HashSet<MethodDeclaration>(), new HashSet<MethodDeclaration>(), name, getProject());
				extractParamters.apply();
			}
			getProject().countAfterBadSmells(BadSmellType.ParametersDataClumps, 0);
			/*parametersDataClumps = new ParametersDataClumps(getProject(), 2);
			
			if (parametersDataClumps.check()) {
				
				int amountOfBadSmellsAfter = parametersDataClumps.getDuplicatedParametersList().size();
				
				getProject().countAfterBadSmells(BadSmellType.ParametersDataClumps, amountOfBadSmellsAfter);
			}else{
				getProject().countAfterBadSmells(BadSmellType.ParametersDataClumps, 0);
			}*/
		}else{
			System.out.println("Not Exist Parameters Data Clumps");
		}

	}

}

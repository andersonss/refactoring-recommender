package br.ic.ufal.refactoring.rules;

import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;

import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.corrections.movingfeaturesbetwenobjects.ExtractFields;
import br.ic.ufal.refactoring.detections.BadSmellType;
import br.ic.ufal.refactoring.detections.dataclumps.fields.DuplicatedFragments;
import br.ic.ufal.refactoring.detections.dataclumps.fields.FragmentsDataClumps;
import br.ic.ufal.util.ParseUtil;

public class DataClumpsFragExtractFields extends Rule {

	public DataClumpsFragExtractFields(Project project) {
		super(project);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute() {
	
	FragmentsDataClumps fragmentsDataClumps = new FragmentsDataClumps(getProject(), 2);
		
		if (fragmentsDataClumps.check()) {
			
			List<DuplicatedFragments> duplicatedFragmentsList = fragmentsDataClumps.getFragsDuplicated();
			
			int amountOfBadSmellsBefore = duplicatedFragmentsList.size();
			getProject().countDetectedBadSmells(BadSmellType.FragmentsDataClumps, amountOfBadSmellsBefore);
			
			for (DuplicatedFragments duplicatedFragments : duplicatedFragmentsList) {
				
				IPackageFragment iPackageFragment = ParseUtil.getPackageFrament(duplicatedFragments.getClasses().get(1).getICompilationUnit(), getProject());
				ICompilationUnit newICompilationUnit = null;
				String name = "NewClass"+getProject().generateUniqueId();
				
				try {
					newICompilationUnit = iPackageFragment.createCompilationUnit(name+".java", "", true, null);
				} catch (JavaModelException e) {
					e.printStackTrace();
				}
				IFile iFile = (IFile)newICompilationUnit.getResource();
				System.out.println(iFile.getName());
				
				ExtractFields extractFields = new ExtractFields(iFile, duplicatedFragments.getClasses(), new HashSet<VariableDeclaration>(duplicatedFragments.getFragments()), new HashSet<MethodDeclaration>(), new HashSet<MethodDeclaration>(), name, getProject());
				extractFields.apply();
			}
			
			
			fragmentsDataClumps = new FragmentsDataClumps(getProject(), 2);
			
			if (fragmentsDataClumps.check()) {
				
				int amountOfBadSmellsAfter = fragmentsDataClumps.getFragsDuplicated().size();
				
				getProject().countAfterBadSmells(BadSmellType.FragmentsDataClumps, amountOfBadSmellsAfter);
			}else{
				getProject().countAfterBadSmells(BadSmellType.FragmentsDataClumps, 0);
			}
			
			
		}else{
			System.out.println("Not Exist Duplication in Clazz");
		}

	}

}

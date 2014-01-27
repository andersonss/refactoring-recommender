package recommender.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import br.ic.ufal.refactoring.engine.Engine;

public class Recommender extends AbstractHandler {
	
	
	public Recommender() {
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		MessageDialog.openInformation(window.getShell(), "Recommender", "Recommender");
		
		
		
		Engine engine = new Engine( );
		//engine.planning("log4j");
		//engine.planning("xerces-java-trunk");
		//engine.planning("HSQLDB");
		//engine.planning("JEdit");
		//engine.planning("ArgoUML");
		//engine.planning("JHotDraw");
		//engine.planning("HSQLDB");
		engine.planning("SweetHome3D");
		//engine.planning("TestProject");
		/*File file = new File("sweethome3D.txt");
		
		FileWriter fw = null;
		BufferedWriter bw = null;
		
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String output = new String();
		
		Project project = Parser.parseProject("TestProject");
		
		Evaluator evaluator = new Evaluator();
		
		Measures measures = evaluator.evaluateProjectQuality(project);
		
		output = output + measures;
		
		QualityFactors factors = new QualityFactors(measures);
		
		output = output + factors;*/
		
		/*System.out.println("Get Extract Method Refactoring Opportunities");
		
		Set<ASTSliceGroup> astSliceGroups = Standalone.getExtractMethodRefactoringOpportunities(project.getJavaProject());
		
		for (ASTSliceGroup astSliceGroup : astSliceGroups) {
			Set<ASTSlice> astSlices = astSliceGroup.getCandidates();
			
			for (ASTSlice astSlice : astSlices) {
				System.out.println(astSlice);
			}
		}*/
		
		
		
		/*ParametersDataClumps parametersDataClumps = new ParametersDataClumps(project, 1);
		
		if (parametersDataClumps.check()) {
			List<DuplicatedParameters> duplicatedParametersList = parametersDataClumps.getDuplicatedParametersList();
			
			for (DuplicatedParameters duplicatedParameters : duplicatedParametersList) {
				output = output + duplicatedParameters;
				System.out.println(duplicatedParameters);
				
				IPackageFragment iPackageFragment = ParseUtil.getPackageFrament(duplicatedParameters.getDuplicatedClasses().get(1).getICompilationUnit(), project);
				ICompilationUnit newICompilationUnit = null;
				String name = "NewClass"+project.generateUniqueId();
				
				try {
					newICompilationUnit = iPackageFragment.createCompilationUnit(name+".java", "", true, null);
				} catch (JavaModelException e) {
					e.printStackTrace();
				}
				IFile iFile = (IFile)newICompilationUnit.getResource();
				System.out.println(iFile.getName());
				
				ExtractParameters extractParamters = new ExtractParameters(iFile, duplicatedParameters.getDuplicatedClasses(), new HashSet<VariableDeclaration>(duplicatedParameters.getBlock().getParamters()), new HashSet<MethodDeclaration>(), new HashSet<MethodDeclaration>(), name, project);
				extractParamters.apply();
			}
			
			System.out.println("ALL Classes");
			for (Clazz clazz : project.getClasses()) {
					System.out.println(clazz.getDocument().get());
				
			}
		}else{
			System.out.println("Not Exist Parameters Data Clumps");
		}*/
		
	/*	FragmentsDataClumps fragmentsDataClumps = new FragmentsDataClumps(project, 2);
		
		if (fragmentsDataClumps.check()) {
			
			List<DuplicatedFragments> duplicatedFragmentsList = fragmentsDataClumps.getFragsDuplicated();
			for (DuplicatedFragments duplicatedFragments : duplicatedFragmentsList) {
				output = output + duplicatedFragments;
				
				
				IPackageFragment iPackageFragment = ParseUtil.getPackageFrament(duplicatedFragments.getClasses().get(1).getICompilationUnit(), project);
				ICompilationUnit newICompilationUnit = null;
				String name = "NewClass"+project.generateUniqueId();
				
				try {
					newICompilationUnit = iPackageFragment.createCompilationUnit(name+".java", "", true, null);
				} catch (JavaModelException e) {
					e.printStackTrace();
				}
				IFile iFile = (IFile)newICompilationUnit.getResource();
				System.out.println(iFile.getName());
				
				ExtractFields extractFields = new ExtractFields(iFile, duplicatedFragments.getClasses(), new HashSet<VariableDeclaration>(duplicatedFragments.getFragments()), new HashSet<MethodDeclaration>(), new HashSet<MethodDeclaration>(), name, project);
				extractFields.apply();
			}
			
			System.out.println("ALL Classes");
			for (Clazz clazz : project.getClasses()) {
					System.out.println(clazz.getDocument().get());
			}
			
		}else{
			System.out.println("Not Exist Duplication in Clazz");
		}*/
		
	/*	try {
			bw.write(output);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
		
	
		return null;
	}
}

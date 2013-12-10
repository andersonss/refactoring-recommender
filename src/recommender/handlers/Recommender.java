package recommender.handlers;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import br.ic.ufal.evaluator.Evaluator;
import br.ic.ufal.evaluator.QualityFactors;
import br.ic.ufal.parser.Parser;
import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.detections.use.parameters.UnusedParameters;
import br.ic.ufal.refactoring.detections.use.parameters.UnusedParametersDesc;
import br.ic.ufal.util.OperationsUtil;

public class Recommender extends AbstractHandler {
	
	private OperationsUtil operationsUtil = new OperationsUtil();
	
	public Recommender() {
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		MessageDialog.openInformation(window.getShell(), "Recommender", "Recommender");
		
		List<Project> projects = null;
		
		try {
			projects = Parser.parseAllProjects();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		
		Evaluator evaluator = new Evaluator();
		
		for (Project proj : projects) {
			System.out.println("//----------- Evlauation -------------------");
			
			System.out.println("Project Name: " + proj.getName());
			System.out.println("Amount of Classes: " + proj.getClasses().size());
			
			QualityFactors factors = new QualityFactors(evaluator.evaluateProjectQuality(proj));
			System.out.println("Reusability: " + factors.evaluateReusability());
			System.out.println("Extendibility: " + factors.evaluateExtendibility());
			System.out.println("Flexibility: " + factors.evaluateFlexibility());
			System.out.println("Effectiveness: " + factors.evaluateEffectiveness());
			
			//------------------------- Unused Methods -----------------------------
			/*System.out.println("Verifying Unused Methods");
			UnusedMethods unusedMethods = new UnusedMethods(proj);
			
			if (unusedMethods.check()) {
				System.out.println("Removing Unused Methods");
				
				RemoveMethods removeMethods = new RemoveMethods(proj, unusedMethods.getUnusedMethods());
				removeMethods.execute();
				System.out.println("Removed Unused Methods");
			}else{
				System.out.println("Not Exist Unused Methods");
			}*/
			
			//-----------------------------------------------------------------------
			
			//------------------------- Unused Fields -----------------------------
			/*System.out.println("Verifying Unused Fragments");
			UnusedFragments unusedFragments = new UnusedFragments(proj);
			if (unusedFragments.check()) {
				System.out.println("Removing Unsued Fragments");
				
				for (UnusedFragmentsDesc unusedFrag : unusedFragments.getUnusedFragments()) {
					RemoveFragments removeFragments = new RemoveFragments(unusedFrag.getClazz(), unusedFrag.getFragmentsToBeRemoved(), proj);
					removeFragments.execute();
				}
				
				System.out.println("Removed Unused Fragments");
			}else{
				System.out.println("Not Exist Unused Fragments");
			}*/
			//-----------------------------------------------------------------------
			
			//------------------------- Unused Parameters -----------------------------
			System.out.println("Verifying Unused Parameters");
			UnusedParameters unusedParameters = new UnusedParameters(proj);
			
			if (unusedParameters.check()) {
				for (UnusedParametersDesc unusedParametersDesc : unusedParameters.getUnusedParameters()) {
					System.out.println(unusedParametersDesc);
				}
			}
			
			
			//-----------------------------------------------------------------------
			
			//------------------------- Up Methods -----------------------------
			
			/*System.out.println("Verifying Up Methods");
			UpMethods upMethods = new UpMethods(proj);
			
			if (upMethods.check()) {
				System.out.println(upMethods.getMethodsToBeUp());
			}
			*/
			//-----------------------------------------------------------------------
			
			//------------------------- Down Methods -------------------------------
			/*System.out.println("Verifying Down Methods");
			DownMethods downMethods = new DownMethods(proj);
			
			if (downMethods.check()) {
				
				PushDownMethods pushDownMethods = new PushDownMethods(downMethods.getMethodsToBeDown(), proj);
				pushDownMethods.execute();
				
			}*/
			//-----------------------------------------------------------------------
			
			//------------------------- Up Fragments -----------------------------
			
			/*System.out.println("Verifying Up Fields");
			UpFragments upFragments = new UpFragments(proj);
			
			if (upFragments.check()) {
				List<DuplicatedFragments> fragmentsToBeUp = upFragments.getDuplicatedFragments();
				
				PullUpFragments pullUpFragments = new PullUpFragments(fragmentsToBeUp, proj);
				pullUpFragments.execute();
				
				System.out.println(fragmentsToBeUp);
			}*/
			
			//-----------------------------------------------------------------------
			//------------------------- Down Fragments ------------------------------
			/*System.out.println("Verifying Down Fragments");
			DownFragments downFields = new DownFragments(proj);
			
			if (downFields.check()) {
				
				List<DownFragmentsDesc> descs = downFields.getDownFragmentsDescs();
				for (DownFragmentsDesc downFragmentsDesc : descs) {
					System.out.println(downFragmentsDesc);
					System.out.println("Applying Push Down Fragments.");
					
					PushDownFragments pushDownFragments = new PushDownFragments(downFragmentsDesc.getSuperclass(), downFragmentsDesc.getSubclasses(), downFragmentsDesc.getFragmentsToBeDown(), proj);
					pushDownFragments.execute();
					
					System.out.println("Applied Push Down Fragments");
				}
			}*/
			//-----------------------------------------------------------------------
			
			/*DataClass dataClass = new DataClass(proj);
			
			if (dataClass.check()) {
				
				System.out.println("Data Class");
				
				for (Clazz clazz : dataClass.getDataClasses()) {
					System.out.println("Data Class: " + clazz.getTypeDeclaration().getName());
				}
				
			}else{
				System.out.println("Not Exist Data Class");
			}*/
			
			/*System.out.println("Verifying Public Fields");
			
			PublicFields publicFields = new PublicFields(proj);
			
			boolean check = publicFields.check();
			
			if (check) {
				System.out.println("Encapsulating Public Fields");
				EncapsulateField encapsulateField = new EncapsulateField(publicFields.getPublicFields(), proj);
				encapsulateField.execute();
				System.out.println("Encapsulated Public Fields");
				
			}else{
				System.out.println("Not Exist Public Field");
			}*/
			
			
			/*System.out.println("Fields Data Clumps");
			
			FieldsDataClumps fieldsDataClumps = new FieldsDataClumps(proj);
			
			List<DuplicatedFragments> duplicatedFragments = fieldsDataClumps.retrieveDuplicatedFragments();
			duplicatedFragments = fieldsDataClumps.review(duplicatedFragments);
			for (DuplicatedFragments dfs : duplicatedFragments) {
				System.out.println(dfs);
			}*/
			
			/*System.out.println("Parameters Data Clumps");
			ParametersDataClumps parametersDataClumps = new ParametersDataClumps(proj);
			
			List<DuplicatedParameters> duplicatedParameters = parametersDataClumps.retrieveDuplicatedParameters(proj);
			duplicatedParameters = parametersDataClumps.review(duplicatedParameters);
			for (DuplicatedParameters dps : duplicatedParameters) {
				System.out.println(dps);
			}*/
			
			/*System.out.println("Subclasses Fields");
			SubclassesFieldsDuplication subFieldsDuplication = new SubclassesFieldsDuplication(proj);
			if (subFieldsDuplication.check()) {
				System.out.println(subFieldsDuplication.getDuplicatedFragments());
			}*/
			
			/*System.out.println("Divergent Change");
			DivergentChange divergentChange = new DivergentChange(proj);
			if (divergentChange.check()) {
				List<TypeDeclaration> divergentClasses = divergentChange.getDivergentClasses();
				for (TypeDeclaration typeDeclaration : divergentClasses) {
					System.out.println("Divergent Class: " + typeDeclaration.getName());
				}
			}*/
			
			System.out.println("//----------- Evlauation -------------------");
			factors = new QualityFactors(evaluator.evaluateProjectQuality(proj));
			System.out.println("Reusability: " + factors.evaluateReusability());
			System.out.println("Extendibility: " + factors.evaluateExtendibility());
			System.out.println("Flexibility: " + factors.evaluateFlexibility());
			System.out.println("Effectiveness: " + factors.evaluateEffectiveness());
		}
		
		
		
		return null;
	}
}

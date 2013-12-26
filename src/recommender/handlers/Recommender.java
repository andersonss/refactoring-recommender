package recommender.handlers;

import java.util.ArrayList;
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
import br.ic.ufal.refactoring.corrections.Correction;
import br.ic.ufal.refactoring.corrections.composingmethods.ExtractMethod;
import br.ic.ufal.refactoring.corrections.makingmethodcallsimpler.RemoveMethods;
import br.ic.ufal.refactoring.corrections.movingfeaturesbetwenobjects.FeatureEnvyCorrection;
import br.ic.ufal.refactoring.corrections.movingfeaturesbetwenobjects.PullUpFragments;
import br.ic.ufal.refactoring.corrections.movingfeaturesbetwenobjects.PullUpMethods;
import br.ic.ufal.refactoring.corrections.movingfeaturesbetwenobjects.PushDownFragments;
import br.ic.ufal.refactoring.corrections.movingfeaturesbetwenobjects.PushDownMethods;
import br.ic.ufal.refactoring.corrections.organizingdata.EncapsulateField;
import br.ic.ufal.refactoring.detections.duplication.clazz.ClazzDuplication;
import br.ic.ufal.refactoring.detections.duplication.subclasses.fields.down.DownFragments;
import br.ic.ufal.refactoring.detections.duplication.subclasses.fields.up.UpFragments;
import br.ic.ufal.refactoring.detections.duplication.subclasses.methods.down.DownMethods;
import br.ic.ufal.refactoring.detections.duplication.subclasses.methods.up.UpMethods;
import br.ic.ufal.refactoring.detections.featureenvy.FeatureEnvy;
import br.ic.ufal.refactoring.detections.use.methods.UnusedMethods;
import br.ic.ufal.refactoring.detections.visibility.fields.PublicFields;
import br.ic.ufal.refactoring.engine.Engine;
import br.ic.ufal.refactoring.rules.ClassDupExtMeth;
import br.ic.ufal.refactoring.rules.FeatureEnvyMoveMeth;
import br.ic.ufal.refactoring.rules.Rule;

public class Recommender extends AbstractHandler {
	
	
	public Recommender() {
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		MessageDialog.openInformation(window.getShell(), "Recommender", "Recommender");
		
		Engine engine = new Engine( );
		engine.planning();
		
		/*Project project = null;
		
		Evaluator evaluator = new Evaluator();
		
		System.out.println("//================ First Strategy ================");
		
		project = Parser.parseProject("SweetHome3D");
		
		System.out.println("//----------- Evlauation -------------------");
		
		System.out.println("Project Name: " + project.getName());
		System.out.println("Amount of Classes: " + project.getClasses().size());
		
		QualityFactors factors = new QualityFactors(evaluator.evaluateProjectQuality(project));
		System.out.println("Reusability: " + factors.evaluateReusability());
		System.out.println("Extendibility: " + factors.evaluateExtendibility());
		System.out.println("Flexibility: " + factors.evaluateFlexibility());
		System.out.println("Effectiveness: " + factors.evaluateEffectiveness());
		
		// Extract Method
		
		(new ClassDupExtMeth(project, 2)).execute();
		
		System.out.println("//----------- Evlauation -------------------");
		factors = new QualityFactors(evaluator.evaluateProjectQuality(project));
		System.out.println("Reusability: " + factors.evaluateReusability());
		System.out.println("Extendibility: " + factors.evaluateExtendibility());
		System.out.println("Flexibility: " + factors.evaluateFlexibility());
		System.out.println("Effectiveness: " + factors.evaluateEffectiveness());
		
		System.out.println("//================ Second Strategy ================");
		
		project = Parser.parseProject("SweetHome3D");
		
		evaluator = new Evaluator();
		
		System.out.println("//----------- Evaluation -------------------");
		
		System.out.println("Project Name: " + project.getName());
		System.out.println("Amount of Classes: " + project.getClasses().size());
		
		factors = new QualityFactors(evaluator.evaluateProjectQuality(project));
		System.out.println("Reusability: " + factors.evaluateReusability());
		System.out.println("Extendibility: " + factors.evaluateExtendibility());
		System.out.println("Flexibility: " + factors.evaluateFlexibility());
		System.out.println("Effectiveness: " + factors.evaluateEffectiveness());
		
		// Extract Method
		
		(new ClassDupExtMeth(project, 2)).execute();
		
		System.out.println("//----------- Evaluation -------------------");
		factors = new QualityFactors(evaluator.evaluateProjectQuality(project));
		System.out.println("Reusability: " + factors.evaluateReusability());
		System.out.println("Extendibility: " + factors.evaluateExtendibility());
		System.out.println("Flexibility: " + factors.evaluateFlexibility());
		System.out.println("Effectiveness: " + factors.evaluateEffectiveness());
		
		// Move Method
		
		(new FeatureEnvyMoveMeth(project, 1)).execute();
		
		System.out.println("//----------- Evlauation -------------------");
		factors = new QualityFactors(evaluator.evaluateProjectQuality(project));
		System.out.println("Reusability: " + factors.evaluateReusability());
		System.out.println("Extendibility: " + factors.evaluateExtendibility());
		System.out.println("Flexibility: " + factors.evaluateFlexibility());
		System.out.println("Effectiveness: " + factors.evaluateEffectiveness());*/
		
		/*List<Project> projects = null;
		
		Evaluator evaluator = new Evaluator();
		
		try {
			projects = Parser.parseAllProjects();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		
		evaluator = new Evaluator();
		
		for (Project proj : projects) {
			System.out.println("//----------- Evlauation -------------------");
			
			System.out.println("Project Name: " + proj.getName());
			System.out.println("Amount of Classes: " + proj.getClasses().size());
			
			QualityFactors factors = new QualityFactors(evaluator.evaluateProjectQuality(proj));
			System.out.println("Reusability: " + factors.evaluateReusability());
			System.out.println("Extendibility: " + factors.evaluateExtendibility());
			System.out.println("Flexibility: " + factors.evaluateFlexibility());
			System.out.println("Effectiveness: " + factors.evaluateEffectiveness());
			
			// Extract Method
			
			ClazzDuplication clazzDuplication = new ClazzDuplication(proj, 2);
			
			System.out.println("Verifying Clazz Duplication Statements");
			if (clazzDuplication.check()) {
				Correction extractMethod = new ExtractMethod(clazzDuplication.getDuplicatedStatements(), proj);
				extractMethod.execute();
			}else{
				System.out.println("Not Exist Duplication in Clazz");
			}
			
			// Move Method
			
			System.out.println("Analysing Feature Envy");
			FeatureEnvy featureEnvy = new FeatureEnvy(proj, 1);
			if (featureEnvy.check()) {     
				Correction featureEnvyCorrection = new FeatureEnvyCorrection(featureEnvy.getDescriptions(), proj);
				featureEnvyCorrection.execute();
			}else{
				System.out.println("Not Exist Feature Envy");
			}
			
			// Move Fragments
			//------------------------- Up Fragments -----------------------------
			
			System.out.println("Verifying Up Fragments");
			UpFragments upFragments = new UpFragments(proj, 5);
			
			if (upFragments.check()) {
				
				Correction pullUpFragments = new PullUpFragments(upFragments.getDuplicatedFragments(), proj);
				pullUpFragments.execute();
				
			}
			
			// Push Down Field
			//-----------------------------------------------------------------------
			//------------------------- Down Fragments ------------------------------
			System.out.println("Verifying Down Fragments");
			DownFragments downFields = new DownFragments(proj, 0);
			
			if (downFields.check()) {
				
					Correction pushDownFragments = new PushDownFragments(downFields.getDownFragmentsDescs(), proj);
					pushDownFragments.execute();
			}
			// Encapsulate Field
			System.out.println("Analysing Public Fields");
			PublicFields publicFields = new PublicFields(proj);
			if (publicFields.check()) {
				Correction encapsulateField = new EncapsulateField(publicFields.getPublicFields(), proj);
				encapsulateField.execute();
			}
			
			// Encapsulate Collection
			
			// Remove Setting Method
			System.out.println("Verifying Unused Methods");
			UnusedMethods unusedMethods = new UnusedMethods(proj, 0);
			
			if (unusedMethods.check()) {
				
				Correction removeMethods = new RemoveMethods(proj, unusedMethods.getUnusedMethods());
				removeMethods.execute();
			}else{
				System.out.println("Not Exist Unused Methods");
			}
			
			// Push Down Method
			// Falta inserir uma verificacao que garanta a remocao do metodo somente 
			//nos casos onde a superclasse nao utilize no body de outros metodos
			System.out.println("Verifying Down Methods");
			DownMethods downMethods = new DownMethods(proj, 1);
			
			if (downMethods.check()) {
				
				Correction pushDownMethods = new PushDownMethods(downMethods.getMethodsToBeDown(), proj);
				pushDownMethods.execute();
			}
			// Pull Up Method
			
			System.out.println("Verifying Up Methods");
			UpMethods upMethods = new UpMethods(proj);
			
			if (upMethods.check()) {
				
				Correction pullUpMethods = new PullUpMethods(upMethods.getMethodsToBeUp(), proj);
				pullUpMethods.execute();
			}
			
			
			
			System.out.println("//----------- Evlauation -------------------");
			factors = new QualityFactors(evaluator.evaluateProjectQuality(proj));
			System.out.println("Reusability: " + factors.evaluateReusability());
			System.out.println("Extendibility: " + factors.evaluateExtendibility());
			System.out.println("Flexibility: " + factors.evaluateFlexibility());
			System.out.println("Effectiveness: " + factors.evaluateEffectiveness());
		}*/
		
		
		
		return null;
	}
}

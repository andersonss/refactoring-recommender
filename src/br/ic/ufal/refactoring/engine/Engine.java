package br.ic.ufal.refactoring.engine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.ic.ufal.evaluator.Evaluator;
import br.ic.ufal.evaluator.Measures;
import br.ic.ufal.evaluator.QualityFactors;
import br.ic.ufal.parser.Parser;
import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.detections.dataclumps.fields.FragmentsDataClumps;
import br.ic.ufal.refactoring.detections.dataclumps.parameters.ParametersDataClumps;
import br.ic.ufal.refactoring.detections.duplication.subclasses.fields.down.DownFragments;
import br.ic.ufal.refactoring.detections.duplication.subclasses.methods.down.DownMethods;
import br.ic.ufal.refactoring.detections.featureenvy.FeatureEnvy;
import br.ic.ufal.refactoring.detections.use.methods.UnusedMethods;
import br.ic.ufal.refactoring.detections.visibility.fields.PublicFields;
import br.ic.ufal.refactoring.rules.DataClumpsFragExtractFields;
import br.ic.ufal.refactoring.rules.DataClumpsParametersExtractParameters;
import br.ic.ufal.refactoring.rules.DivergentChangeExtractClass;
import br.ic.ufal.refactoring.rules.DownPushDownFrags;
import br.ic.ufal.refactoring.rules.DownPushDownMethods;
import br.ic.ufal.refactoring.rules.DuplicatedMethodsPullUpMethods;
import br.ic.ufal.refactoring.rules.FeatureEnvyMoveMeth;
import br.ic.ufal.refactoring.rules.PublicEncapsulateFields;
import br.ic.ufal.refactoring.rules.Rule;
import br.ic.ufal.refactoring.rules.RuleType;
import br.ic.ufal.refactoring.rules.ShotgunSurgeryMoveMethodField;
import br.ic.ufal.refactoring.rules.UnusedRemoveMethods;

public class Engine {

	
	private List<RuleType> rulesTypes = new ArrayList<RuleType>();
	
	public Engine( ) {
		
		rulesTypes.add(RuleType.DataClumpsFragments);
		
		//rulesTypes.add(RuleType.DataClassPublicField);
		/*rulesTypes.add(RuleType.DataClumpsParameters);
		
		rulesTypes.add(RuleType.DataClassPublicField);
		
		rulesTypes.add(RuleType.RefusedBequestDownPushDownFrags);
		rulesTypes.add(RuleType.RefusedBequestDownPushDownMethods);
		
		rulesTypes.add(RuleType.DataClassPublicField);
		rulesTypes.add(RuleType.DataClassUnusedGettersSettersMethods);
		
		rulesTypes.add(RuleType.FeatureEnvyMoveMeth);*/
		
		
		
	}
	
	public void planning(String projectName){
		
		System.out.println("Planning");
	
		String configuration = new String();
		
		Project project = new Project();
		project	= Parser.parseProject(projectName);
		
		configuration = configuration + "Project Name: "+projectName+" Classes Size: "+project.getClasses().size()+"\n";
		
		//configuration = configuration + " Amount of Bad Smells Before: " + detectBadSmells(project) + "\n";
		
		for (RuleType ruleType : this.rulesTypes) {
			
			String evaluation = new String(configuration);
			Project proj = new Project();
			
			proj = Parser.parseProject(projectName);
			
			evaluation = evaluation + " Total of Bad Smells Before Performing Strategy \n";
			evaluation = evaluation + detectBadSmells(proj); 
			
			evaluation =  evaluation + performRule(proj, ruleType);
		
			evaluation = evaluation + "\n Total of Bad Smells After Performing Strategy \n";
			evaluation = evaluation + detectBadSmells(proj);
			
			write(evaluation, "sweethome3D/"+ ruleType +".txt");
		
		}
		
		
	}
	
	
	
	private String detectBadSmells(Project project){
		
		String detectedBadSmells = new String();
		
		int count = 0;
		
		DownFragments downFragments = new DownFragments(project, 1);
		if (downFragments.check()) {
			count = count + downFragments.getDownFragmentsDescs().size();
			
			detectedBadSmells = detectedBadSmells + "\n Down Fragments: " + downFragments.getDownFragmentsDescs().size(); 
		}
		
		DownMethods downMethods = new DownMethods(project, 1);
		if (downMethods.check()) {
			count = count + downMethods.getMethodsToBeDown().size();
		
			detectedBadSmells = detectedBadSmells + "\n Down Methods: " + downMethods.getMethodsToBeDown().size();
		}
		
		PublicFields publicFields = new PublicFields(project);
		if (publicFields.check()) {
			count = count + publicFields.getPublicFields().size();
			
			detectedBadSmells = detectedBadSmells + "\n Public Fields: " + publicFields.getPublicFields().size();
		}
		
		FeatureEnvy featureEnvy = new FeatureEnvy(project, 1);
		if (featureEnvy.check()) {
			count = count + featureEnvy.getDescriptions().size();
			
			detectedBadSmells = detectedBadSmells + "\n Feature Envy: " + featureEnvy.getDescriptions().size();
		}
		
		UnusedMethods unusedMethods = new UnusedMethods(project, 0);
		if (unusedMethods.check()) {
			count = count + unusedMethods.getUnusedMethods().size();
			
			detectedBadSmells = detectedBadSmells + "\n Unused Methods: " + unusedMethods.getUnusedMethods().size();
		}
		
		FragmentsDataClumps fragmentsDataClumps = new FragmentsDataClumps(project, 2);
		if (fragmentsDataClumps.check()) {
			count = count + fragmentsDataClumps.getFragsDuplicated().size();
			
			detectedBadSmells = detectedBadSmells + "\n Fragments Data Clumps: " + fragmentsDataClumps.getFragsDuplicated().size();
		}
		
		ParametersDataClumps parametersDataClumps = new ParametersDataClumps(project, 3);
		if (parametersDataClumps.check()) {
			count = count + parametersDataClumps.getDuplicatedParametersList().size();
			
			detectedBadSmells = detectedBadSmells + "\n Parameters Data Clumps: " + parametersDataClumps.getDuplicatedParametersList().size();
		}
		
		detectedBadSmells = detectedBadSmells + "\n Total: " + count;
		
		return detectedBadSmells;
		
	}
	
	private String performRule(Project project, RuleType rule){
		String evaluation = new String();
		
		Evaluator evaluator = new Evaluator();
		
		QualityFactors before = null;
		QualityFactors after = null;
		Measures measures = null;
		
		evaluation = evaluation +"\n//================ Correction ================\n";
		
		System.out.println("Correction : " + rule);
				
		evaluation = evaluation + " Evaluation Before Applying Strategy\n";
		
		measures = evaluator.evaluateProjectQuality(project);
		
		evaluation = evaluation + measures;
		
		before = new QualityFactors(measures);
		
		evaluation = evaluation + before;
		
		evaluation = evaluation + "\n Total of Bad Smells Before Performing Rule \n" ;
		evaluation = evaluation + detectBadSmells(project);
		
		evaluation = evaluation + "\n Performing " + rule + "\n";
		
		buildRule(rule, project).execute();
		
		evaluation = evaluation + " Evaluation After Applying Strategy\n";
		
		measures = evaluator.evaluateProjectQuality(project);
		
		evaluation = evaluation + measures;
		
		after = new QualityFactors(measures);
		
		evaluation = evaluation + after;
		
		evaluation = evaluation + "\n Total of Bad Smells After Performing Rule \n";
		evaluation = evaluation + detectBadSmells(project);
		
		evaluation = evaluation +"\n\n Amount of Bad Smells " + rule + " Before Performing Rule: \n" + project.getDetectedBadSmells();
		
		evaluation = evaluation +"\n Amount of Bad Smells " + rule + " After Performing Rule: \n" + project.getAfterBadSmells();
		
		
		evaluation = evaluation + "\n Gains and Loses\n";
		
		evaluation = evaluation + evaluate(after, before);
		
		evaluation = evaluation + "Total: "+ evaluateTotal(after, before)+"\n";
		
		return evaluation;
	}
	
	private Rule buildRule(RuleType ruleType, Project project){
		Rule rule = null;
		
		switch (ruleType) {
		case RefusedBequestDownPushDownFrags:
			rule = new DownPushDownFrags(project, 1);
			break;	
		
		case RefusedBequestDownPushDownMethods:
			rule = new DownPushDownMethods(project, 1);
			break;	
			
		case DataClassPublicField:
			rule = new PublicEncapsulateFields(project);
			break;	
		
		case FeatureEnvyMoveMeth:
			rule = new FeatureEnvyMoveMeth(project, 1);
			break;	
			
		case DataClassUnusedGettersSettersMethods:
			rule = new UnusedRemoveMethods(project, 0);
			break;	
		case DataClumpsFragments:
			rule = new DataClumpsFragExtractFields(project);
			break;		
			
		case DataClumpsParameters:
			rule = new DataClumpsParametersExtractParameters(project);
			break;	
			
		case DivergentChange:
			rule = new DivergentChangeExtractClass(project);
			break;		
			
		case ShotgunSurvery:
			rule = new ShotgunSurgeryMoveMethodField(project);
			break;	
		
		case DuplicatedCodeMethods:
			rule = new DuplicatedMethodsPullUpMethods(project);
			break;	
		
			
		default:
			break;
		}
		return rule;
	}
	
	
	
	public String evaluate(QualityFactors after, QualityFactors before){
		
		String evaluation = new String();
		
		evaluation = evaluation + "Reusability: " + (after.evaluateReusability() - before.evaluateReusability())+"\n";
		evaluation = evaluation + "Extendibility: " + (after.evaluateExtendibility() - before.evaluateExtendibility())+"\n";
		evaluation = evaluation + "Flexibility: " + (after.evaluateFlexibility() - before.evaluateFlexibility())+"\n";
		evaluation = evaluation + "Effectiveness: " + (after.evaluateEffectiveness() - before.evaluateEffectiveness())+"\n";
		
		return evaluation;
	}
	
	public double evaluateTotal(QualityFactors after, QualityFactors before){
		
		double reusability = 0;
		double extendibility = 0; 
		double flexibility = 0; 
		double effectiveness = 0;
		
		reusability = after.evaluateReusability() - before.evaluateReusability();
		extendibility = after.evaluateExtendibility() - before.evaluateExtendibility();
		flexibility = after.evaluateFlexibility() - before.evaluateFlexibility();
		effectiveness = after.evaluateEffectiveness() - before.evaluateEffectiveness();
		
		return reusability + extendibility + flexibility + effectiveness;
	}

	
	private void write(String evaluation, String path){
		File file = new File(path);
		
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
		
		
		try {
			bw.write(evaluation);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

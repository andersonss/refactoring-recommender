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
import br.ic.ufal.refactoring.detections.duplication.subclasses.fields.down.DownFragments;
import br.ic.ufal.refactoring.detections.featureenvy.FeatureEnvy;
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
		
		//rulesTypes.add(RuleType.ClassDupExtMeth);
		rulesTypes.add(RuleType.RefusedBequestDownPushDownFrags);
		rulesTypes.add(RuleType.FeatureEnvyMoveMeth);
		rulesTypes.add(RuleType.DataClassPublicField);
		//rulesTypes.add(RuleType.UpPullUpFrags);
		//rulesTypes.add(RuleType.DownPushDownMethods);
		//rulesTypes.add(RuleType.UpPullUpMethods);
		// rulesTypes.add(RuleType.UnusedRemoveMethods);
		
	}
	
	public void planning(String projectName){
		
		System.out.println("Planning");
		
		File file = new File("RefusedBequestDownPushDownFrags-sweethome3D.txt");
		
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
		
		
		
		String evaluation = new String();
		
		evaluation = evaluation + new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()) + "\n";
		
		evaluation = evaluation + "Project Name:"+projectName+"\n";
		
		Project project = Parser.parseProject(projectName);
		
		String inf = "Project Name: "+projectName+" Classes Size: "+project.getClasses().size()+"\n";
		
		evaluation = evaluation + inf;
		
		System.out.println(inf);
		
		//int detectedBadSmells = detector(project);
		//evaluation = evaluation +"\n\n Amount of Detected Bad Smells: " + detectedBadSmells+"\n";
		
		/*RefusedBequestDownPushDownMethods,
		RefusedBequestDownPushDownFrags, 
		
		DataClassPublicField,
		DataClassPublicCollectionField,
		DataClassUnusedGettersSettersMethods,
		
		FeatureEnvyMoveMeth, 
		
		DataClumpsFragments,
		DataClumpsParameters,
		
		DuplicatedCodeMethods,
		
		ShotgunSurvery,
		
		DivergentChange*/
		
		//evaluation =  evaluation + performRule(projectName, RuleType.RefusedBequestDownPushDownFrags);
		
		evaluation =  evaluation + performRule(projectName, RuleType.RefusedBequestDownPushDownMethods);
		
		
		
		//evaluation = evaluation + applyStrategyOne(projectName);
		//evaluation = evaluation + applyStrategyTwo(projectName);
		//evaluation = evaluation + applyStrategyThree(projectName);
		
		try {
			bw.write(evaluation);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	private String performRule(String projectName, RuleType rule){
		String evaluation = new String();
		
		Evaluator evaluator = new Evaluator();
		
		QualityFactors before = null;
		QualityFactors after = null;
		Measures measures = null;
		
		evaluation = evaluation +"//================ Strategy ================\n";
		
		System.out.println("Strategy : " + rule);
		Project project = Parser.parseProject(projectName);
				
		evaluation = evaluation + " Evaluation Before Applying Strategy\n";
		
		measures = evaluator.evaluateProjectQuality(project);
		
		evaluation = evaluation + measures;
		
		before = new QualityFactors(measures);
		
		evaluation = evaluation + before;
		
		evaluation = evaluation + " Performing " + rule + "\n";
		
		buildRule(rule, project).execute();
		
		evaluation = evaluation + " Evaluation After Applying Strategy\n";
		
		measures = evaluator.evaluateProjectQuality(project);
		
		evaluation = evaluation + measures;
		
		after = new QualityFactors(measures);
		
		evaluation = evaluation + after;
		
		evaluation = evaluation + " Gains and Loses\n";
		
		evaluation = evaluation + evaluate(after, before);
		
		evaluation = evaluation + "Total: "+ evaluateTotal(after, before)+"\n";
		evaluation = evaluation +"\n\n Amount of Bad Smells Before: " + project.getDetectedBadSmells();
		evaluation = evaluation +"\n\n Amount of Bad Smells After: " + project.getAfterBadSmells();
		
		return evaluation;
	}
	
	private Rule buildRule(RuleType ruleType, Project project){
		Rule rule = null;
		
		switch (ruleType) {
		case RefusedBequestDownPushDownFrags:
			rule = new DownPushDownFrags(project, 1);
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
		
		
		case RefusedBequestDownPushDownMethods:
			rule = new DownPushDownMethods(project, 1);
			break;		
			
		case ShotgunSurvery:
			rule = new ShotgunSurgeryMoveMethodField(project);
			break;	
		
		case DivergentChange:
			rule = new DivergentChangeExtractClass(project);
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

}

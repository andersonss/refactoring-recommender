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
import br.ic.ufal.refactoring.detections.BadSmellType;
import br.ic.ufal.refactoring.detections.duplication.subclasses.fields.down.DownFragments;
import br.ic.ufal.refactoring.detections.featureenvy.FeatureEnvy;
import br.ic.ufal.refactoring.detections.visibility.fields.PublicFields;
import br.ic.ufal.refactoring.rules.ClassDupExtMeth;
import br.ic.ufal.refactoring.rules.DownPushDownFrags;
import br.ic.ufal.refactoring.rules.DownPushDownMethods;
import br.ic.ufal.refactoring.rules.FeatureEnvyMoveMeth;
import br.ic.ufal.refactoring.rules.PublicEncapsulateFields;
import br.ic.ufal.refactoring.rules.Rule;
import br.ic.ufal.refactoring.rules.RuleType;
import br.ic.ufal.refactoring.rules.UnusedRemoveMethods;
import br.ic.ufal.refactoring.rules.UpPullUpFrags;
import br.ic.ufal.refactoring.rules.UpPullUpMethods;

public class Engine {

	
	private List<RuleType> rulesTypes = new ArrayList<RuleType>();
	
	public Engine( ) {
		
		//rulesTypes.add(RuleType.ClassDupExtMeth);
		rulesTypes.add(RuleType.DownPushDownFrags);
		rulesTypes.add(RuleType.FeatureEnvyMoveMeth);
		rulesTypes.add(RuleType.PublicEncapsulateFields);
		//rulesTypes.add(RuleType.UpPullUpFrags);
		//rulesTypes.add(RuleType.DownPushDownMethods);
		//rulesTypes.add(RuleType.UpPullUpMethods);
		// rulesTypes.add(RuleType.UnusedRemoveMethods);
		
	}
	
	public void planning(String projectName){
		
		System.out.println("Planning");
		
		File file = new File("strategy3-xerces.txt");
		
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
		
		System.out.println("Project Name: "+projectName+" Classes Size: "+project.getClasses().size()+"\n");
		
		int detectedBadSmells = detector(project);
		evaluation = evaluation +"\n\n Amount of Detected Bad Smells: " + detectedBadSmells+"\n";
		
		//evaluation = evaluation + applyStrategyOne(projectName);
		//evaluation = evaluation + applyStrategyTwo(projectName);
		evaluation = evaluation + applyStrategyThree(projectName);
		
		try {
			bw.write(evaluation);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private int detector(Project project){
		int detectedBadSmells = 0;
		
		DownFragments downFragments = new DownFragments(project, 1);
		if (downFragments.check()) {
			detectedBadSmells = detectedBadSmells + downFragments.getDownFragmentsDescs().size();
		}
		
		PublicFields publicFields = new PublicFields(project);
		if (publicFields.check()) {
			detectedBadSmells = detectedBadSmells + publicFields.getPublicFields().size();
		}
		
		FeatureEnvy featureEnvy = new FeatureEnvy(project, 1);
		if (featureEnvy.check()) {
			detectedBadSmells = detectedBadSmells + featureEnvy.getDescriptions().size();
		}
		
		return detectedBadSmells;
	}
	
	private String applyStrategyThree(String projectName) {
		String evaluation = new String();
		
		List<List<RuleType>> strategies = new ArrayList<>();
		
		for (RuleType rule1 : this.rulesTypes) {
			for (RuleType rule2 : this.rulesTypes) {
				for (RuleType rule3 : this.rulesTypes) {
					if (!rule1.equals(rule2) && 
						!rule1.equals(rule3) && 
						!rule2.equals(rule3)) {
						if (!(rule1.equals(RuleType.DownPushDownFrags) && rule2.equals(RuleType.FeatureEnvyMoveMeth))
							&& !(rule2.equals(RuleType.DownPushDownFrags) && rule3.equals(RuleType.FeatureEnvyMoveMeth)) ) {
							List<RuleType> actions = new ArrayList<RuleType>();
							actions.add(rule1);
							actions.add(rule2);
							actions.add(rule3);
							strategies.add(actions);
						}
					}
				}
			}
		}
		
		Evaluator evaluator = new Evaluator();
		
		QualityFactors before = null;
		QualityFactors after = null;
		Measures measures = null;
		
		evaluation = evaluation +"//================ Strategy 3 ================\n";
		
		for (int i = 0; i < strategies.size(); i++) {
			List<RuleType> list = strategies.get(i);
			
			System.out.println("Strategy : " + i + " of " + strategies.size());
			Project project = Parser.parseProject(projectName);
				
				
				evaluation = evaluation + " Evaluation Before Applying Strategy\n";
				
				measures = evaluator.evaluateProjectQuality(project);
				
				evaluation = evaluation + measures;
				
				before = new QualityFactors(measures);
				
				evaluation = evaluation + before;
				
				RuleType ruleType = list.get(0);
				
				evaluation = evaluation + " Performing " + ruleType + "\n";
				
				performStep(ruleType, project);
				
				ruleType = list.get(1);
				
				evaluation = evaluation + " Performing " + ruleType + "\n";
				
				performStep(ruleType, project);
				
				ruleType = list.get(2);
				
				evaluation = evaluation + " Performing " + ruleType + "\n";
				
				performStep(ruleType, project);
				
				evaluation = evaluation + " Evaluation After Applying Strategy\n";
				
				measures = evaluator.evaluateProjectQuality(project);
				
				evaluation = evaluation + measures;
				
				after = new QualityFactors(measures);
				
				evaluation = evaluation + after;
				
				evaluation = evaluation + " Gains and Loses\n";
				
				evaluation = evaluation + evaluate(after, before);
				
				evaluation = evaluation + "Total: "+ evaluateTotal(after, before)+"\n";
				
				evaluation = evaluation +"\n\n Amount of Solved Bad Smells: " + project.getSolvedBadSmells();
			
		}
		
		return evaluation;
	}

	private String applyStrategyOne(String projectName){
		String evaluation = new String();
		
		List<List<RuleType>> strategies = new ArrayList<>();
		
		for (RuleType rule : this.rulesTypes) {
			List<RuleType> actions = new ArrayList<RuleType>();
			actions.add(rule);
			strategies.add(actions);
		}
		
		Evaluator evaluator = new Evaluator();
		
		QualityFactors before = null;
		QualityFactors after = null;
		Measures measures = null;
		
		evaluation = evaluation +"//================ Strategy 1 ================\n";
		
		for (int i = 0; i < strategies.size(); i++) {
			List<RuleType> list = strategies.get(i);
			
			System.out.println("Strategy : " + i + " of " + strategies.size());
			Project project = Parser.parseProject(projectName);
				
				
				evaluation = evaluation + " Evaluation Before Applying Strategy\n";
				
				measures = evaluator.evaluateProjectQuality(project);
				
				evaluation = evaluation + measures;
				
				before = new QualityFactors(measures);
				
				evaluation = evaluation + before;
				
				RuleType ruleType = list.get(0);
				
				evaluation = evaluation + " Performing " + ruleType + "\n";
				
				performStep(ruleType, project);
				
				evaluation = evaluation + " Evaluation After Applying Strategy\n";
				
				measures = evaluator.evaluateProjectQuality(project);
				
				evaluation = evaluation + measures;
				
				after = new QualityFactors(measures);
				
				evaluation = evaluation + after;
				
				evaluation = evaluation + " Gains and Loses\n";
				
				evaluation = evaluation + evaluate(after, before);
				
				evaluation = evaluation + "Total: "+ evaluateTotal(after, before)+"\n";
				evaluation = evaluation +"\n\n Amount of Solved Bad Smells: " + project.getSolvedBadSmells();
				
			
		}
		
		return evaluation;
	}
	
	private String applyStrategyTwo(String projectName){
		String evaluation = new String();
		
		List<List<RuleType>> strategies = new ArrayList<>();
		
		for (RuleType rule1 : this.rulesTypes) {
			for (RuleType rule2 : this.rulesTypes) {
				if (!rule1.equals(rule2)) {
					if ( !(rule1.equals(RuleType.DownPushDownFrags) && rule2.equals(RuleType.FeatureEnvyMoveMeth))) {
						
					List<RuleType> actions = new ArrayList<RuleType>();
					actions.add(rule1);
					actions.add(rule2);
					strategies.add(actions);
					}
					
				}
			}
		}
		
		Evaluator evaluator = new Evaluator();
		
		QualityFactors before = null;
		QualityFactors after = null;
		Measures measures = null;
		
		evaluation = evaluation +"//================ Strategy 2 ================\n";
		
		for (int i = 0; i < strategies.size(); i++) {
			List<RuleType> list = strategies.get(i);
			
			System.out.println("Strategy : " + i + " of " + strategies.size());
			Project project = Parser.parseProject(projectName);
				
				
				evaluation = evaluation + " Evaluation Before Applying Strategy\n";
				
				measures = evaluator.evaluateProjectQuality(project);
				
				evaluation = evaluation + measures;
				
				before = new QualityFactors(measures);
				
				evaluation = evaluation + before;
				
				RuleType ruleType = list.get(0);
				
				evaluation = evaluation + " Performing " + ruleType + "\n";
				
				performStep(ruleType, project);
				
				ruleType = list.get(1);
				
				evaluation = evaluation + " Performing " + ruleType + "\n";
				
				performStep(ruleType, project);
				
				evaluation = evaluation + " Evaluation After Applying Strategy\n";
				
				measures = evaluator.evaluateProjectQuality(project);
				
				evaluation = evaluation + measures;
				
				after = new QualityFactors(measures);
				
				evaluation = evaluation + after;
				
				evaluation = evaluation + " Gains and Loses\n";
				
				evaluation = evaluation + evaluate(after, before);
				
				evaluation = evaluation + "Total: "+ evaluateTotal(after, before)+"\n";
			
				evaluation = evaluation +"\n\n Amount of Solved Bad Smells: " + project.getSolvedBadSmells();
				
		}
		
		return evaluation;
	}
	
	private Rule buildRule(RuleType ruleType, Project project){
		Rule rule = null;
		
		switch (ruleType) {
		case ClassDupExtMeth:
			rule = new ClassDupExtMeth(project, 2);
			break;
		case FeatureEnvyMoveMeth:
			rule = new FeatureEnvyMoveMeth(project, 1);
			break;	
		case PublicEncapsulateFields:
			rule = new PublicEncapsulateFields(project);
			break;	
		case UpPullUpFrags:
			rule = new UpPullUpFrags(project, 5); 
			break;
		case DownPushDownFrags:
			rule = new DownPushDownFrags(project, 1);
			break;	
		case UnusedRemoveMethods:
			rule = new UnusedRemoveMethods(project, 0);
			break;	
		case DownPushDownMethods:
			rule = new DownPushDownMethods(project, 1);
			break;		
		case UpPullUpMethods:
			rule = new UpPullUpMethods(project);
			break;	
		default:
			break;
		}
		
		
		return rule;
	}
	
	
	private String performStep(RuleType ruleType, Project project){
		String evaluation = new String();
		/*String evaluation = new String();
		
		QualityFactors before = null;
		QualityFactors after = null;
		
		Evaluator evaluator = new Evaluator();
		
		evaluation = evaluation + " Before "+"\n";
		
		before = new QualityFactors(evaluator.evaluateProjectQuality(project));
		
		evaluation = evaluation + evaluate(before);
		
		evaluation = evaluation + ruleType +" Application"+"\n";
		*/
		buildRule(ruleType, project).execute();
		
		/*evaluation = evaluation + " After "+"\n";
		after = new QualityFactors(evaluator.evaluateProjectQuality(project));
		evaluation = evaluation + evaluate(after);*/
		
		return evaluation;
		
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

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
import br.ic.ufal.evaluator.QualityFactors;
import br.ic.ufal.parser.Parser;
import br.ic.ufal.parser.Project;
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
		
		rulesTypes.add(RuleType.ClassDupExtMeth);
		rulesTypes.add(RuleType.DownPushDownFrags);
		rulesTypes.add(RuleType.PublicEncapsulateFields);
		rulesTypes.add(RuleType.UpPullUpFrags);
		rulesTypes.add(RuleType.FeatureEnvyMoveMeth);
		rulesTypes.add(RuleType.DownPushDownMethods);
		rulesTypes.add(RuleType.UpPullUpMethods);
		rulesTypes.add(RuleType.UnusedRemoveMethods);
		
		
	}
	
	public void planning(){
		
		System.out.println("Planning");
		
		File file = new File("strategy1.txt");
		
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
		
		
		Project project = Parser.parseProject("SweetHome3D");
		
		String evaluation = new String();
		
		evaluation = evaluation + new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()) + "\n";
		
		evaluation = evaluation + "Project Name: " + project.getName()+"\n";
		evaluation = evaluation + "Amount of Classes: " + project.getClasses().size()+"\n";
		
		evaluation = evaluation +"//================ Strategy 1 ================\n";
		
		evaluation = evaluation + performStep(RuleType.PublicEncapsulateFields, project);
		
		evaluation = evaluation + performStep(RuleType.FeatureEnvyMoveMeth, project);
				
		evaluation = evaluation + performStep(RuleType.DownPushDownFrags, project);
		
		evaluation = evaluation + performStep(RuleType.UpPullUpFrags, project);
		
		evaluation = evaluation + performStep(RuleType.UnusedRemoveMethods, project);
		
		evaluation = evaluation + performStep(RuleType.UpPullUpMethods, project);
		
		/*evaluation = evaluation +"//================ Strategy 2 ================\n";
		
		evaluation = evaluation + performStep(RuleType.PublicEncapsulateFields, project);
		
		evaluation = evaluation + performStep(RuleType.FeatureEnvyMoveMeth, project);
				
		evaluation = evaluation + performStep(RuleType.DownPushDownFrags, project);
		
		evaluation = evaluation + performStep(RuleType.UpPullUpFrags, project);
		
		evaluation = evaluation + performStep(RuleType.UnusedRemoveMethods, project);
		
		evaluation = evaluation + performStep(RuleType.UpPullUpMethods, project);*/
		
		//evaluation = evaluation +"//================ Strategy 6 ================\n";
		
		/*evaluation = evaluation + performStep(RuleType.ClassDupExtMeth, project);
		
		evaluation = evaluation +"//================ Strategy 6 ================\n";
		
		evaluation = evaluation + performStep(RuleType.DownPushDownMethods, project);
		
		try {
			bw.write(evaluation);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		evaluation = evaluation +"//================ Strategy 7 ================\n";
		
		evaluation = evaluation + performStep(RuleType.UpPullUpMethods, project);
	
		evaluation = evaluation +"//================ Strategy 8 ================\n";
		
		evaluation = evaluation + performStep(RuleType.UnusedRemoveMethods, project);*/
		
		try {
			bw.write(evaluation);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
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
			rule = new DownPushDownFrags(project, 0);
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
	
	private String evaluate(QualityFactors factors){
		String evaluation = new String();
		
		evaluation = evaluation + "Reusability: " + factors.evaluateReusability()+"\n";
		evaluation = evaluation + "Extendibility: " + factors.evaluateExtendibility()+"\n";
		evaluation = evaluation + "Flexibility: " + factors.evaluateFlexibility()+"\n";
		evaluation = evaluation + "Effectiveness: " + factors.evaluateEffectiveness()+"\n";
		
		return evaluation;
	}
	
	private String performStep(RuleType ruleType, Project project){
		String evaluation = new String();
		
		QualityFactors factors = null;
		Evaluator evaluator = new Evaluator();
		
		evaluation = evaluation + " Before "+"\n";
		
		factors = new QualityFactors(evaluator.evaluateProjectQuality(project));
		
		evaluation = evaluation + evaluate(factors);
		
		evaluation = evaluation + ruleType +" Application"+"\n";
		
		buildRule(ruleType, project).execute();
		
		evaluation = evaluation + " After "+"\n";
		factors = new QualityFactors(evaluator.evaluateProjectQuality(project));
		evaluation = evaluation + evaluate(factors);
		
		return evaluation;
		
	}

}

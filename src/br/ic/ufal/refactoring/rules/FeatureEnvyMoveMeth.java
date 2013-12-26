package br.ic.ufal.refactoring.rules;

import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.corrections.Correction;
import br.ic.ufal.refactoring.corrections.movingfeaturesbetwenobjects.FeatureEnvyCorrection;
import br.ic.ufal.refactoring.detections.featureenvy.FeatureEnvy;

public class FeatureEnvyMoveMeth extends Rule {

	private int threshold = 1;
	
	public FeatureEnvyMoveMeth(Project project, int threshold) {
		super(project);
		this.threshold = threshold;
	}

	@Override
	public void execute() {
		System.out.println("Analysing Feature Envy");
		FeatureEnvy featureEnvy = new FeatureEnvy(getProject(), this.threshold);
		if (featureEnvy.check()) {     
			Correction featureEnvyCorrection = new FeatureEnvyCorrection(featureEnvy.getDescriptions(), getProject());
			featureEnvyCorrection.apply();
		}else{
			System.out.println("Not Exist Feature Envy");
		}
	}

}

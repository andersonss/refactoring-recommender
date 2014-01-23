package br.ic.ufal.refactoring.rules;

import java.util.HashMap;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;

import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.corrections.movingfeaturesbetwenobjects.MoveMethod;
import br.ic.ufal.refactoring.detections.BadSmellType;
import br.ic.ufal.refactoring.detections.featureenvy.FeatureEnvy;
import br.ic.ufal.refactoring.detections.featureenvy.FeatureEnvyDescription;
import br.ic.ufal.refactoring.detections.visibility.fields.PublicFields;

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
			
			int amountOfBadSmellsBefore = featureEnvy.getDescriptions().size();
			getProject().countDetectedBadSmells(BadSmellType.FeatureEnvy, amountOfBadSmellsBefore);
			
			for (int i = 0; i < featureEnvy.getDescriptions().size(); i++) {
				
				FeatureEnvyDescription desc = featureEnvy.getDescriptions().get(i);
				
				System.out.println("Correct Feature Envy: " + i + " of " + featureEnvy.getDescriptions().size());
				
				MoveMethod moveMethod = new MoveMethod(desc.getSourceClass(), desc.getTargetClass(), desc.getSourceMethod(), new HashMap<MethodInvocation, MethodDeclaration>(), false, desc.getSourceMethod().getName().getIdentifier(), getProject());
				moveMethod.apply();
				
			}
			featureEnvy = new FeatureEnvy(getProject(), this.threshold);
			
			if (featureEnvy.check()) {
				
				int amountOfBadSmellsAfter = featureEnvy.getDescriptions().size();
				
				getProject().countAfterBadSmells(BadSmellType.FeatureEnvy, amountOfBadSmellsAfter);
			}else{
				getProject().countAfterBadSmells(BadSmellType.FeatureEnvy, 0);
			}
			
			
		}else{
			System.out.println("Not Exist Feature Envy");
		}
	}

}

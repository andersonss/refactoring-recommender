package br.ic.ufal.refactoring.corrections.movingfeaturesbetwenobjects;

import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;

import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.corrections.Correction;
import br.ic.ufal.refactoring.detections.featureenvy.FeatureEnvyDescription;

public class FeatureEnvyCorrection extends Correction {

	private List<FeatureEnvyDescription> descriptions = null;
	
	public FeatureEnvyCorrection(List<FeatureEnvyDescription> descriptions, Project project) {
		super(project);
		this.descriptions = descriptions;
	}

	@Override
	public void apply() {
		
		System.out.println("Applying Move Methods");
		
		int count = 1;
		
		for (FeatureEnvyDescription desc : this.descriptions) {
			
			
			
			System.out.println("Correct Feature Envy: " + count + " of " + this.descriptions.size());
			MoveMethod moveMethod = new MoveMethod(desc.getSourceClass(), desc.getTargetClass(), desc.getSourceMethod(), new HashMap<MethodInvocation, MethodDeclaration>(), false, desc.getSourceMethod().getName().getIdentifier(), getProject());
			moveMethod.apply();
			
			count++;
			
		}
		
		System.out.println("Applied Move Methods");

	}

}

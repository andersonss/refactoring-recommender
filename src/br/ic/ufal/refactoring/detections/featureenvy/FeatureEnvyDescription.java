package br.ic.ufal.refactoring.detections.featureenvy;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import br.ic.ufal.parser.Clazz;

public class FeatureEnvyDescription {
	
	private Clazz sourceClass = null;
	private MethodDeclaration sourceMethod = null;
	private Clazz targetClass = null;

	public FeatureEnvyDescription() {
		
	}

	public Clazz getSourceClass() {
		return sourceClass;
	}

	public void setSourceClass(Clazz sourceClass) {
		this.sourceClass = sourceClass;
	}

	public MethodDeclaration getSourceMethod() {
		return sourceMethod;
	}

	public void setSourceMethod(MethodDeclaration sourceMethod) {
		this.sourceMethod = sourceMethod;
	}

	public Clazz getTargetClass() {
		return targetClass;
	}

	public void setTargetClass(Clazz targetClass) {
		this.targetClass = targetClass;
	}

	@Override
	public String toString() {
		return "FeatureEnvyDescription [sourceClass=" + sourceClass.getTypeDeclaration().getName()
				+ ", sourceMethod=" + sourceMethod.getName() + ", targetClass="
				+ targetClass.getTypeDeclaration().getName() + "]";
	}
	
	

}

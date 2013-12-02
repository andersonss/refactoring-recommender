package br.ic.ufal.refactoring.detections.featureenvy;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class FeatureEnvyDescription {
	
	private TypeDeclaration sourceClass = null;
	private MethodDeclaration sourceMethod = null;
	private TypeDeclaration targetClass = null;

	public FeatureEnvyDescription() {
		
	}

	public TypeDeclaration getSourceClass() {
		return sourceClass;
	}

	public void setSourceClass(TypeDeclaration sourceClass) {
		this.sourceClass = sourceClass;
	}

	public MethodDeclaration getSourceMethod() {
		return sourceMethod;
	}

	public void setSourceMethod(MethodDeclaration sourceMethod) {
		this.sourceMethod = sourceMethod;
	}

	public TypeDeclaration getTargetClass() {
		return targetClass;
	}

	public void setTargetClass(TypeDeclaration targetClass) {
		this.targetClass = targetClass;
	}

	@Override
	public String toString() {
		return "FeatureEnvyDescription [sourceClass=" + sourceClass.getName()
				+ ", sourceMethod=" + sourceMethod.getName() + ", targetClass="
				+ targetClass.getName() + "]";
	}
	
	

}

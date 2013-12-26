package br.ic.ufal.refactoring.corrections;

import br.ic.ufal.parser.Project;

public abstract class Correction {
	
	private Project project = null;

	public Correction(Project project) {
		this.project = project;
	}
	
	public abstract void apply();

	public Project getProject() {
		return project;
	}
}

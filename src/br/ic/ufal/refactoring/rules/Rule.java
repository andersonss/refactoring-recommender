package br.ic.ufal.refactoring.rules;

import br.ic.ufal.parser.Project;

public abstract class Rule {

	private Project project = null;
	
	public Rule(Project project) {
		this.project = project;
	}
	
	public abstract void execute();
	
	public Project getProject() {
		return project;
	}
}

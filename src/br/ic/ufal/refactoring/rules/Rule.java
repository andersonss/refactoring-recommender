package br.ic.ufal.refactoring.rules;

import br.ic.ufal.parser.Project;

public abstract class Rule {

	private Project project = null;

	/**
	 * 
	 * @param project
	 */
	public Rule(Project project) {
		this.project = project;
	}

	/**
	 * 
	 */
	public abstract void execute();

	/**
	 * 
	 * @return
	 */
	public Project getProject() {
		return project;
	}
}

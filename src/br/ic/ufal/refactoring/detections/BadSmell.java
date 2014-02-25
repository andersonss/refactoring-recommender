package br.ic.ufal.refactoring.detections;

import br.ic.ufal.parser.Project;

public abstract class BadSmell {

	private String description = new String();
	private Project project = null;

	/**
	 * 
	 * @param project
	 */
	public BadSmell(Project project) {
		this.project = project;
	}

	/**
	 * 
	 * @return
	 */
	public abstract boolean check();

	/**
	 * 
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 
	 * @return
	 */
	public Project getProject() {
		return this.project;
	}

	/**
	 * 
	 * @param project
	 */
	public void setProject(Project project) {
		this.project = project;
	}

	@Override
	public String toString() {
		return "BadSmell [description=" + description + ", project=" + project
				+ "]";
	}

}

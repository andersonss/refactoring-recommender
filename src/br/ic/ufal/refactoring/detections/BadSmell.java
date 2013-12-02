package br.ic.ufal.refactoring.detections;

import br.ic.ufal.parser.Project;

public abstract class BadSmell {

	private String description = new String();
	private Project project = null;
	
	public BadSmell(Project project) {
		this.project = project;
	}
	
	public abstract boolean check();
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public Project getProject() {
		return this.project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	@Override
	public String toString() {
		return "BadSmell [description=" + description + ", project=" + project+ "]";
	}
	
}

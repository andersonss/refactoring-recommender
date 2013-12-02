package br.ic.ufal.refactoring.detections.speculativegenerality.inheritance;

import java.util.ArrayList;
import java.util.List;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.detections.BadSmell;

public class AbstractSpeculativeGenerality extends BadSmell {
	
	private Project project = null;
	private int amountOfSubclasses = 10;
	private List<Clazz> speculativeAbsClasses = new ArrayList<Clazz>();
	
	public AbstractSpeculativeGenerality(Project project, int amountOfSubclasses) {
		super(project);
		this.project = project;
		this.amountOfSubclasses = amountOfSubclasses;
	}

	public boolean check(){
		
		for (Clazz clazz : this.project.getClasses()) {
			List<Clazz> subClasses = checkSubClasses(clazz);
			
			if (subClasses.size() >= 1  && subClasses.size() < amountOfSubclasses) {
				this.speculativeAbsClasses.add(clazz);
			}
		}
		
		return this.speculativeAbsClasses.size() > 0;
	}
	
	private List<Clazz> checkSubClasses(Clazz clazz){
		List<Clazz> subclasses = new ArrayList<Clazz>();
		
		for (Clazz c : super.getProject().getClasses()) {
			if (c.getTypeDeclaration().getSuperclassType() != null) {
				if (c.getTypeDeclaration().getSuperclassType().resolveBinding().isEqualTo(clazz.getTypeDeclaration().getSuperclassType().resolveBinding())) {
					subclasses.add(c);
				}
				
			}
		}
		
		return subclasses;
	}
	
	public List<Clazz> getSpeculativeAbsClasses() {
		return speculativeAbsClasses;
	}
}

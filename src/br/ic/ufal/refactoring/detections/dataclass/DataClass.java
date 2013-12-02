package br.ic.ufal.refactoring.detections.dataclass;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.detections.BadSmell;

public class DataClass extends BadSmell {

	private Project project = null;
	private List<Clazz> dataClasses = null;
	
	public DataClass(Project project) {
		super(project);
		this.project = project;
	}

	public boolean check(){
		
		this.dataClasses = new ArrayList<Clazz>();
		
		for (Clazz clazz : this.project.getClasses()) {
			TypeDeclaration typeDeclaration = clazz.getTypeDeclaration();
			
			if (typeDeclaration.getFields().length > 0) {
				
				boolean isDataClass = true;
				
				for (MethodDeclaration methodDeclaration : typeDeclaration.getMethods()) {
					IMethodBinding iMethodBinding = methodDeclaration.resolveBinding();
					if (!iMethodBinding.getName().contains("get") && 
						!iMethodBinding.getName().contains("set") && 
						!iMethodBinding.getName().contains("toString") && 
						!iMethodBinding.getName().contains("equals") &&
						!iMethodBinding.isConstructor() &&
						!iMethodBinding.isDefaultConstructor()){
						
						isDataClass = false;
					}
				}
				
				if (isDataClass) {
					this.dataClasses.add(clazz);
				}
			}
		}
		
		return this.dataClasses.size() > 0;
	}

	public List<Clazz> getDataClasses() {
		return dataClasses;
	}
	
	public void setDataClasses(List<Clazz> dataClasses) {
		this.dataClasses = dataClasses;
	}
	
}

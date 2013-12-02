package br.ic.ufal.parser;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;

public class Project {
	
	private String name = new String();
	private IJavaProject javaProject = null;
	private IPackageFragmentRoot[] packages = null;
	private List<Clazz> classes = new ArrayList<Clazz>();
	
	public Project() {
	
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public IJavaProject getJavaProject() {
		return javaProject;
	}

	public void setJavaProject(IJavaProject javaProject) {
		this.javaProject = javaProject;
	}

	public IPackageFragmentRoot[] getPackages() {
		return packages;
	}

	public void setPackages(IPackageFragmentRoot[] packages) {
		this.packages = packages;
	}

	public List<Clazz> getClasses() {
		return classes;
	}
	
	public void setClasses(List<Clazz> classes) {
		this.classes = classes;
	}
	
	public void addClazz(Clazz clazz){
		this.classes.add(clazz);
	}
	
}

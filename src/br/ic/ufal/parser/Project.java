package br.ic.ufal.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;

import br.ic.ufal.evaluator.QualityFactors;
import br.ic.ufal.refactoring.detections.BadSmellType;

public class Project implements Cloneable {
	
	private String name = new String();
	private IJavaProject javaProject = null;
	private IPackageFragmentRoot[] packages = null;
	private List<Clazz> classes = new ArrayList<Clazz>();
	
	private Map<BadSmellType, Integer> detectedBadSmells = new HashMap<BadSmellType, Integer>();
	
	private Map<BadSmellType, Integer> solvedBadSmells = new HashMap<BadSmellType, Integer>();
	
	public Project(String name, IJavaProject javaProject,IPackageFragmentRoot[] packages, List<Clazz> classes) {
		super();
		this.name = name;
		this.javaProject = javaProject;
		this.packages = packages;
		this.classes = classes;
		
		detectedBadSmells.put(BadSmellType.ClassDuplication, 0);
		detectedBadSmells.put(BadSmellType.PublicFields, 0);
		detectedBadSmells.put(BadSmellType.FeatureEnvy, 0);
		detectedBadSmells.put(BadSmellType.DownFragments, 0);
		detectedBadSmells.put(BadSmellType.UpFragments, 0);
		detectedBadSmells.put(BadSmellType.DownMethods, 0);
		detectedBadSmells.put(BadSmellType.UpMethods, 0);
		detectedBadSmells.put(BadSmellType.UnsuedMethods, 0);
		
		solvedBadSmells.put(BadSmellType.ClassDuplication, 0);
		solvedBadSmells.put(BadSmellType.PublicFields, 0);
		solvedBadSmells.put(BadSmellType.FeatureEnvy, 0);
		solvedBadSmells.put(BadSmellType.DownFragments, 0);
		solvedBadSmells.put(BadSmellType.UpFragments, 0);
		solvedBadSmells.put(BadSmellType.DownMethods, 0);
		solvedBadSmells.put(BadSmellType.UpMethods, 0);
		solvedBadSmells.put(BadSmellType.UnsuedMethods, 0);
		
	}

	public Project() {
		detectedBadSmells.put(BadSmellType.ClassDuplication, 0);
		detectedBadSmells.put(BadSmellType.PublicFields, 0);
		detectedBadSmells.put(BadSmellType.FeatureEnvy, 0);
		detectedBadSmells.put(BadSmellType.DownFragments, 0);
		detectedBadSmells.put(BadSmellType.UpFragments, 0);
		detectedBadSmells.put(BadSmellType.DownMethods, 0);
		detectedBadSmells.put(BadSmellType.UpMethods, 0);
		detectedBadSmells.put(BadSmellType.UnsuedMethods, 0);
		
		solvedBadSmells.put(BadSmellType.ClassDuplication, 0);
		solvedBadSmells.put(BadSmellType.PublicFields, 0);
		solvedBadSmells.put(BadSmellType.FeatureEnvy, 0);
		solvedBadSmells.put(BadSmellType.DownFragments, 0);
		solvedBadSmells.put(BadSmellType.UpFragments, 0);
		solvedBadSmells.put(BadSmellType.DownMethods, 0);
		solvedBadSmells.put(BadSmellType.UpMethods, 0);
		solvedBadSmells.put(BadSmellType.UnsuedMethods, 0);
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
	
	public void countDetectedBadSmells(BadSmellType badSmellType, int value){
		
		Integer oldvalue = this.detectedBadSmells.get(badSmellType);
		
		this.detectedBadSmells.put(badSmellType, oldvalue + value);	
	}
	
	public void countSolvedBadSmells(BadSmellType badSmellType, int value){
		
		Integer oldvalue = this.solvedBadSmells.get(badSmellType);
		
		this.solvedBadSmells.put(badSmellType, oldvalue + value);	
	}
	
	public Map<BadSmellType, Integer> getDetectedBadSmells() {
		return detectedBadSmells;
	}
	
	public Map<BadSmellType, Integer> getSolvedBadSmells() {
		return solvedBadSmells;
	}
	
	public Project getClone(){
		try {
			return (Project) super.clone();
		} catch (CloneNotSupportedException e) {
			System.out.println("Cloning not allowed.");
			return this;
		}
		
	}
	
	

	
}

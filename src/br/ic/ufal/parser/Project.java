package br.ic.ufal.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;

import br.ic.ufal.refactoring.detections.BadSmellType;

public class Project implements Cloneable {

	private String name = new String();
	private IProject iProject = null;
	private IJavaProject javaProject = null;
	private IPackageFragmentRoot[] packagesFragmentsRoot = null;
	private IPackageFragment[] ipackagesFragments = null;
	private List<Clazz> classes = new ArrayList<Clazz>();

	private final Map<BadSmellType, Integer> detectedBadSmells = new HashMap<BadSmellType, Integer>();

	private final Map<BadSmellType, Integer> afterBadSmells = new HashMap<BadSmellType, Integer>();

	/**
	 * @param name
	 * @param javaProject
	 * @param packages
	 * @param classes
	 */
	public Project(String name, IJavaProject javaProject,
			IPackageFragmentRoot[] packages, List<Clazz> classes) {
		super();
		this.name = name;
		this.javaProject = javaProject;
		this.packagesFragmentsRoot = packages;
		this.classes = classes;

		detectedBadSmells.put(BadSmellType.ClassDuplication, 0);
		detectedBadSmells.put(BadSmellType.PublicFields, 0);
		detectedBadSmells.put(BadSmellType.FeatureEnvy, 0);
		detectedBadSmells.put(BadSmellType.DownFragments, 0);
		detectedBadSmells.put(BadSmellType.UpFragments, 0);
		detectedBadSmells.put(BadSmellType.DownMethods, 0);
		detectedBadSmells.put(BadSmellType.UpMethods, 0);
		detectedBadSmells.put(BadSmellType.UnsuedMethods, 0);
		detectedBadSmells.put(BadSmellType.FragmentsDataClumps, 0);
		detectedBadSmells.put(BadSmellType.ParametersDataClumps, 0);

		afterBadSmells.put(BadSmellType.ClassDuplication, 0);
		afterBadSmells.put(BadSmellType.PublicFields, 0);
		afterBadSmells.put(BadSmellType.FeatureEnvy, 0);
		afterBadSmells.put(BadSmellType.DownFragments, 0);
		afterBadSmells.put(BadSmellType.UpFragments, 0);
		afterBadSmells.put(BadSmellType.DownMethods, 0);
		afterBadSmells.put(BadSmellType.UpMethods, 0);
		afterBadSmells.put(BadSmellType.UnsuedMethods, 0);
		afterBadSmells.put(BadSmellType.FragmentsDataClumps, 0);
		afterBadSmells.put(BadSmellType.ParametersDataClumps, 0);

	}

	/**
	 * 
	 */
	public Project() {
		detectedBadSmells.put(BadSmellType.ClassDuplication, 0);
		detectedBadSmells.put(BadSmellType.PublicFields, 0);
		detectedBadSmells.put(BadSmellType.FeatureEnvy, 0);
		detectedBadSmells.put(BadSmellType.DownFragments, 0);
		detectedBadSmells.put(BadSmellType.UpFragments, 0);
		detectedBadSmells.put(BadSmellType.DownMethods, 0);
		detectedBadSmells.put(BadSmellType.UpMethods, 0);
		detectedBadSmells.put(BadSmellType.UnsuedMethods, 0);
		detectedBadSmells.put(BadSmellType.FragmentsDataClumps, 0);
		detectedBadSmells.put(BadSmellType.ParametersDataClumps, 0);

		afterBadSmells.put(BadSmellType.ClassDuplication, 0);
		afterBadSmells.put(BadSmellType.PublicFields, 0);
		afterBadSmells.put(BadSmellType.FeatureEnvy, 0);
		afterBadSmells.put(BadSmellType.DownFragments, 0);
		afterBadSmells.put(BadSmellType.UpFragments, 0);
		afterBadSmells.put(BadSmellType.DownMethods, 0);
		afterBadSmells.put(BadSmellType.UpMethods, 0);
		afterBadSmells.put(BadSmellType.UnsuedMethods, 0);
		afterBadSmells.put(BadSmellType.FragmentsDataClumps, 0);
		afterBadSmells.put(BadSmellType.ParametersDataClumps, 0);
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return
	 */
	public IJavaProject getJavaProject() {
		return javaProject;
	}

	/**
	 * @param javaProject
	 */
	public void setJavaProject(IJavaProject javaProject) {
		this.javaProject = javaProject;
	}

	/**
	 * @return
	 */
	public IPackageFragmentRoot[] getPackages() {
		return packagesFragmentsRoot;
	}

	/**
	 * @param packages
	 */
	public void setPackages(IPackageFragmentRoot[] packages) {
		this.packagesFragmentsRoot = packages;
	}

	/**
	 * @return
	 */
	public IPackageFragment[] getPackagesFragments() {
		return ipackagesFragments;
	}

	/**
	 * @param packagesFragments
	 */
	public void setPackagesFragments(IPackageFragment[] packagesFragments) {
		this.ipackagesFragments = packagesFragments;
	}

	/**
	 * @return
	 */
	public List<Clazz> getClasses() {
		return classes;
	}

	/**
	 * @param classes
	 */
	public void setClasses(List<Clazz> classes) {
		this.classes = classes;
	}

	/**
	 * @param clazz
	 */
	public void addClazz(Clazz clazz) {
		this.classes.add(clazz);
	}

	/**
	 * 
	 * @return
	 */
	public IProject getIProject() {
		return iProject;
	}

	/**
	 * 
	 * @param iProject
	 */
	public void setiProject(IProject iProject) {
		this.iProject = iProject;
	}

	/**
	 * 
	 * @param badSmellType
	 * @param value
	 */
	public void countDetectedBadSmells(BadSmellType badSmellType, int value) {

		// Integer oldvalue = this.detectedBadSmells.get(badSmellType);

		this.detectedBadSmells.put(badSmellType, value);
	}

	/**
	 * 
	 * @param badSmellType
	 * @param value
	 */
	public void countAfterBadSmells(BadSmellType badSmellType, int value) {

		// Integer oldvalue = this.afterBadSmells.get(badSmellType);

		this.afterBadSmells.put(badSmellType, value);
	}

	/**
	 * 
	 * @return
	 */
	public Map<BadSmellType, Integer> getDetectedBadSmells() {
		return detectedBadSmells;
	}

	/**
	 * 
	 * @return
	 */
	public Map<BadSmellType, Integer> getAfterBadSmells() {
		return afterBadSmells;
	}

	/**
	 * 
	 * @return
	 */
	public static int generateUniqueId() {
		UUID idOne = UUID.randomUUID();
		String str = "" + idOne;
		int uid = str.hashCode();
		String filterStr = "" + uid;
		str = filterStr.replaceAll("-", "");
		return Integer.parseInt(str);
	}

	/**
	 * 
	 * @return
	 */
	public Project getClone() {
		try {
			return (Project) super.clone();
		} catch (CloneNotSupportedException e) {
			System.out.println("Cloning not allowed.");
			return this;
		}

	}

}

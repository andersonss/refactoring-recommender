package br.ic.ufal.parser;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jface.text.Document;

import br.ic.ufal.util.ParseUtil;

public class Parser {
	
	
	public static Project parseProject(String path){
		
		Project proj = new Project();
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject iProject = root.getProject(path);
		
		try {
			if (iProject.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
				IJavaProject javaProject = JavaCore.create(iProject);
				
				proj.setName(javaProject.getElementName());
				
				proj.setJavaProject(javaProject);
				proj.setiProject(iProject);
				proj.setPackagesFragments(javaProject.getPackageFragments());
				IPackageFragmentRoot[] iPackageFragmentRoots = javaProject.getPackageFragmentRoots();
				
				proj.setPackages(iPackageFragmentRoots);
				
				for(IPackageFragmentRoot iPackageFragmentRoot : iPackageFragmentRoots) {
			
					IJavaElement[] children = iPackageFragmentRoot.getChildren();
					for(IJavaElement child : children) {
						if(child.getElementType() == IJavaElement.PACKAGE_FRAGMENT) {
							IPackageFragment iPackageFragment = (IPackageFragment)child;
							ICompilationUnit[] iCompilationUnits = iPackageFragment.getCompilationUnits();
							for (ICompilationUnit originalunit : iCompilationUnits) {
								
								ICompilationUnit unit = originalunit.getWorkingCopy(null);
								
								Clazz clazz = new Clazz();
								clazz.setICompilationUnit(unit);
								CompilationUnit compilationUnit = ParseUtil.toCompilationUnit(unit);
								clazz.setCompilationUnit(compilationUnit);
								
								TypeDeclaration typeDeclaration = ParseUtil.getTypeDeclaration(compilationUnit);
								if (typeDeclaration != null) {
									clazz.setTypeDeclaration(typeDeclaration);
								}
								
								Document document = null;
								
								try {
									document = new Document(unit.getBuffer().getContents());
									clazz.setDocument(document);
								} catch (JavaModelException e) {
									e.printStackTrace();
								}
								
								if (typeDeclaration != null) {
									proj.addClazz(clazz);
								}
								
								
							}
						}
					}
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		return proj;
		
	}

	public static List<Project> parseAllProjects( ) throws JavaModelException {
		
		List<Project> projs = new ArrayList<Project>();
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject[] projects = root.getProjects();
			
		for (IProject project : projects) {
			
			Project proj = new Project();
			
			try {
				if (project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
					IJavaProject javaProject = JavaCore.create(project);
					
					proj.setName(javaProject.getElementName());
					
					proj.setJavaProject(javaProject);
					
					IPackageFragmentRoot[] iPackageFragmentRoots = javaProject.getPackageFragmentRoots();
					
					proj.setPackages(iPackageFragmentRoots);
					
					
					for(IPackageFragmentRoot iPackageFragmentRoot : iPackageFragmentRoots) {
						IJavaElement[] children = iPackageFragmentRoot.getChildren();
						for(IJavaElement child : children) {
							if(child.getElementType() == IJavaElement.PACKAGE_FRAGMENT) {
								IPackageFragment iPackageFragment = (IPackageFragment)child;
								ICompilationUnit[] iCompilationUnits = iPackageFragment.getCompilationUnits();
								for (ICompilationUnit unit : iCompilationUnits) {
									Clazz clazz = new Clazz();
									clazz.setICompilationUnit(unit);
									
									CompilationUnit compilationUnit = ParseUtil.toCompilationUnit(unit);
									
									clazz.setCompilationUnit(compilationUnit);
									
									TypeDeclaration typeDeclaration = ParseUtil.getTypeDeclaration(compilationUnit);
									if (typeDeclaration != null) {
										clazz.setTypeDeclaration(typeDeclaration);
									}
									
									Document document = null;
									
									try {
										document = new Document(unit.getBuffer().getContents());
										clazz.setDocument(document);
									} catch (JavaModelException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
									if (typeDeclaration != null) {
										proj.addClazz(clazz);
									}
									
									
								}
							}
						}
					}
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
			
			projs.add(proj);
		}
		
		return projs;
	}
	
}

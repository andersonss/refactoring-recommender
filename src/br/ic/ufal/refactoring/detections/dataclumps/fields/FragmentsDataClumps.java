package br.ic.ufal.refactoring.detections.dataclumps.fields;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.detections.BadSmell;
import br.ic.ufal.util.OperationsUtil;

public class FragmentsDataClumps extends BadSmell {

	private List<DuplicatedFragments> fragsDuplicated = new ArrayList<DuplicatedFragments>();
	private OperationsUtil operationsUtil = new OperationsUtil();
	
	private int threshold = 0;
	
	public FragmentsDataClumps(Project project, int threshold) {
		super(project);
		this.threshold = threshold;
	}

	@Override
	public boolean check() {
		
		System.out.println("Check Fragments Data Clumps");
		
		this.fragsDuplicated = operationsUtil.retrieveDuplicatedFragments(getProject(), threshold);
		
		//this.fragsDuplicated = review(fragsDuplicated);
	
		this.fragsDuplicated = operationsUtil.identifyNotRelatedFragmentsDuplication(fragsDuplicated);		
		
		return this.fragsDuplicated.size() > 0;
	}
	
	public List<DuplicatedFragments> review(List<DuplicatedFragments> duplicatedFragments){
		
		duplicatedFragments = unifier(duplicatedFragments);
		duplicatedFragments = removeDuplications(duplicatedFragments);
		duplicatedFragments = removeSubSet(duplicatedFragments);
		
		return duplicatedFragments;
	} 
	
	private List<DuplicatedFragments> unifier(List<DuplicatedFragments> duplicatedFragments){
		
		for (int i = 0; i < duplicatedFragments.size(); i++) {
			List<Clazz> iClasses = duplicatedFragments.get(i).getClasses();
			List<VariableDeclaration> iFragments = duplicatedFragments.get(i).getFragments();
			
			for (int j = i+1; j < duplicatedFragments.size(); j++) {
				List<Clazz> jClasses = duplicatedFragments.get(j).getClasses();
				List<VariableDeclaration> jFragments = duplicatedFragments.get(j).getFragments();
				
				if (operationsUtil.similar(iFragments, jFragments)) {
					
					List<Clazz> notContainClasses = notContainClasses(jClasses, iClasses);
					
					for (Clazz clazz : notContainClasses) {
						duplicatedFragments.get(i).addClazz(clazz);
					}
					
					
				}
			}
			
		}
		
		return duplicatedFragments;
	}
	
	private List<MethodDeclaration> notContainMethods(List<MethodDeclaration> ms, List<MethodDeclaration> ms2){
		
		List<MethodDeclaration> methods = new ArrayList<MethodDeclaration>();
		
		for (MethodDeclaration method : ms) {
			
			boolean contain = false;
			
			for (MethodDeclaration method2 : ms2) {
				/*if (method.getName().equalsIgnoreCase(method2.getName())) {
					contain = true;
				}*/
				if (method.getName().getFullyQualifiedName().equalsIgnoreCase(method2.getName().getFullyQualifiedName())) {
					contain = true;
				}
			}
			
			if (!contain) {
				methods.add(method);
			}
		}
		
		return methods;
	}
	
	private List<Clazz> notContainClasses(List<Clazz> cs, List<Clazz> cs2){
		
		List<Clazz> clazzs = new ArrayList<Clazz>();
		
		for (Clazz clazz : cs) {
			
			boolean contain = false;
			
			for (Clazz classe2 : cs2) {
				if (clazz.getTypeDeclaration().getName().getFullyQualifiedName().equalsIgnoreCase(classe2.getTypeDeclaration().getName().getFullyQualifiedName())) {
					contain = true;
				}
			}
			
			if (!contain) {
				clazzs.add(clazz);
			}
		}
		
		return clazzs;
	}
	
	private List<DuplicatedFragments> removeDuplications(List<DuplicatedFragments> duplicatedParameters){
		List<DuplicatedFragments> reviewed = copy(duplicatedParameters);
		List<DuplicatedFragments> removed = new ArrayList<DuplicatedFragments>();
		
		for (int i = 0; i < duplicatedParameters.size(); i++) {
			for (int j = i+1; j < duplicatedParameters.size(); j++) {
				
				if (duplicatedParameters.get(i).equals(duplicatedParameters.get(j))) {
					reviewed.remove(duplicatedParameters.get(j));
					if (!removed.contains(duplicatedParameters.get(j))) {
						removed.add(duplicatedParameters.get(j));
					}
				}
			}
		}
		
		for (DuplicatedFragments dp : removed) {
			reviewed.add(dp);
		}
		
		return reviewed;
	}
	
	private List<DuplicatedFragments> removeSubSet(List<DuplicatedFragments> duplicatedFragments){
		
		List<DuplicatedFragments> reviewed = copy(duplicatedFragments);
		
		for (int i = 0; i < duplicatedFragments.size(); i++) {
			for (int j = i+1; j < duplicatedFragments.size(); j++) {
				if (isSub(duplicatedFragments.get(i), duplicatedFragments.get(j))) {
					reviewed.remove(duplicatedFragments.get(j));
				}else{
					if (isSub(duplicatedFragments.get(j), duplicatedFragments.get(i))) {
						reviewed.remove(duplicatedFragments.get(i));
					}
				}
			}
		}
		
		return reviewed;
	}
	
	private List<DuplicatedFragments> copy(List<DuplicatedFragments> duplciatedFragments){
		List<DuplicatedFragments> copy = new ArrayList<DuplicatedFragments>();
		
		for (DuplicatedFragments dp : duplciatedFragments) {
			copy.add(dp);
		}
		
		return copy;
	}
	
	private boolean isSub(DuplicatedFragments dcs1, DuplicatedFragments dcs2){
		
		/*
		
		if (!dcs1.getBlock().equals(dcs2.getBlock())) {
			return false;
		}*/
		
		for (Clazz clazz : dcs2.getClasses()) {
			boolean exist = false;
			for (Clazz c : dcs1.getClasses()) {
				if (clazz.getTypeDeclaration().getName().getFullyQualifiedName().equalsIgnoreCase(clazz.getTypeDeclaration().getName().getFullyQualifiedName())) {
					exist = true;
				}
			}
			
			if (!exist) {
				return false;
			}
		}
		
		return true;
	}

	
	public List<DuplicatedFragments> getFragsDuplicated() {
		return fragsDuplicated;
	}
}

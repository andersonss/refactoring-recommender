package br.ic.ufal.refactoring.detections.dataclumps.parameters;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.detections.BadSmell;

public class ParametersDataClumps extends BadSmell{
	
	List<DuplicatedParameters> duplicatedParametersList = new ArrayList<DuplicatedParameters>();
	int threshold = 0 ;

	public ParametersDataClumps(Project project, int threshold) {
		super(project);
		this.threshold = threshold;
	}

	@Override
	public boolean check() {
		System.out.println("Retrieve Duplicated Paramters");
		this.duplicatedParametersList = retrieveDuplicatedParameters(getProject(), 3);
		System.out.println("Review Duplicated Paramters");
		this.duplicatedParametersList = review(duplicatedParametersList);
		
		return this.duplicatedParametersList.size() > 0;
	}

	public List<DuplicatedParameters> retrieveDuplicatedParameters(Project project, int threshold){
		
		
		DuplicatedParameters duplicatedParameters = new DuplicatedParameters();
		
		for (int i = 0; i < project.getClasses().size(); i++) {
			
			Clazz c1 = project.getClasses().get(i);
			
			MethodDeclaration ms1[] = c1.getTypeDeclaration().getMethods();
			
			
			for (int j = i+1; j < project.getClasses().size(); j++) {
				
				Clazz c2 = project.getClasses().get(j);
				
				MethodDeclaration ms2[] = c2.getTypeDeclaration().getMethods();
				
				for (MethodDeclaration m1 : ms1) {
					
					List<SingleVariableDeclaration> m1Parameters = m1.parameters();
					
					for (MethodDeclaration m2 : ms2) {
						
						List<SingleVariableDeclaration> m2Parameters = m2.parameters();
						
						ParametersBlock block = new ParametersBlock();
						
						int k = 0;
						
						for (SingleVariableDeclaration stmt1 : m1Parameters) {
							
							k++;
							boolean stmtDuplicated = false;
						
							for (SingleVariableDeclaration stmt2 : m2Parameters) {
								
								if (stmt1.getType().toString().equalsIgnoreCase(stmt2.getType().toString()) &&
									stmt1.getName().getFullyQualifiedName().equalsIgnoreCase(stmt2.getName().getFullyQualifiedName())) {
									stmtDuplicated = true;
								
								}
								
				  			}
							
							if (stmtDuplicated) {
								block.addParameter(stmt1);
							}else{
								if (block.getParamters().size() >0 ) {
									
									duplicatedParameters.setBlock(block);
									duplicatedParameters.addDuplicatedMethod(m1);
									duplicatedParameters.addDuplicatedMethod(m2);
									
									
									duplicatedParameters.addDuplicatedClass(c1);
									duplicatedParameters.addDuplicatedClass(c2);
									if (duplicatedParameters.getBlock().getParamters().size() > threshold) {
										
										duplicatedParametersList.add(duplicatedParameters);
									
									}
									
									duplicatedParameters = new DuplicatedParameters();
									
									block = new ParametersBlock();
								}
							}	
							
							if (block.getParamters().size() >0 && 
								k == m1Parameters.size()) {
								
								duplicatedParameters.setBlock(block);
								duplicatedParameters.addDuplicatedMethod(m1);
								duplicatedParameters.addDuplicatedMethod(m2);
								
								
								duplicatedParameters.addDuplicatedClass(c1);
								duplicatedParameters.addDuplicatedClass(c2);
								
								if (duplicatedParameters.getBlock().getParamters().size() > threshold) {
								
									duplicatedParametersList.add(duplicatedParameters);
								
								}
								
								duplicatedParameters = new DuplicatedParameters();
								
							
							}
							
							
						}
						
						
					}
					
					
					
				}
				
			}
		}
		
		return duplicatedParametersList;
	}

	public List<DuplicatedParameters> review(List<DuplicatedParameters> duplicatedParameters){
		
		List<DuplicatedParameters> dplist = new ArrayList<DuplicatedParameters>();
		
		for (DuplicatedParameters dps : duplicatedParameters) {
			if (dps.getBlock().getParamters().size() > this.threshold ) {
				dplist.add(dps);
			}
		}
		
		duplicatedParameters = dplist;
		
		
		System.out.println("Unify Parameters");
		duplicatedParameters = unifier(duplicatedParameters);
		System.out.println("Remove Duplicated Parameters");
		duplicatedParameters = removeDuplicatinos(duplicatedParameters);
		System.out.println("Remove Subset Parameters");
		duplicatedParameters = removeSubSet(duplicatedParameters);
		
		
		
		return duplicatedParameters;
	} 
	
	
	
	private List<DuplicatedParameters> unifier(List<DuplicatedParameters> duplicatedParameters){
		
		for (int i = 0; i < duplicatedParameters.size(); i++) {
			System.out.println("Unify Paramters: " + i + " of " + duplicatedParameters.size());
			List<Clazz> iClasses = duplicatedParameters.get(i).getDuplicatedClasses();
			List<MethodDeclaration> iDuplicatedMethods = duplicatedParameters.get(i).getDuplicatedMethods();
			ParametersBlock iParametersBlock = duplicatedParameters.get(i).getBlock();
 			
			for (int j = i+1; j < duplicatedParameters.size(); j++) {
				List<Clazz> jClasses = duplicatedParameters.get(j).getDuplicatedClasses();
				List<MethodDeclaration> jDuplicatedMethods = duplicatedParameters.get(j).getDuplicatedMethods();
				ParametersBlock jParametersBlock = duplicatedParameters.get(j).getBlock();
				
				if (iParametersBlock.equals(jParametersBlock)) {
					
					
					List<MethodDeclaration> notContainMethods = notContainMethods(jDuplicatedMethods, iDuplicatedMethods);
					
					
					for (MethodDeclaration method : notContainMethods) {
						duplicatedParameters.get(i).addDuplicatedMethod(method);
					}
					
					List<Clazz> notContainClasses = notContainClasses(jClasses, iClasses);
					
					for (Clazz clazz : notContainClasses) {
						duplicatedParameters.get(i).addDuplicatedClass(clazz);
					}
					
					
				}
			}
			
		}
		
		return duplicatedParameters;
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
	
	private List<DuplicatedParameters> removeDuplicatinos(List<DuplicatedParameters> duplicatedParameters){
		List<DuplicatedParameters> reviewed = copy(duplicatedParameters);
		List<DuplicatedParameters> removed = new ArrayList<DuplicatedParameters>();
		
		for (int i = 0; i < duplicatedParameters.size(); i++) {
			System.out.println("Remove Duplication: " + i + " of " + duplicatedParameters.size());
			for (int j = i+1; j < duplicatedParameters.size(); j++) {
				
				if (duplicatedParameters.get(i).equals(duplicatedParameters.get(j))) {
					reviewed.remove(duplicatedParameters.get(j));
					if (!removed.contains(duplicatedParameters.get(j))) {
						removed.add(duplicatedParameters.get(j));
					}
				}
			}
		}
		
		for (DuplicatedParameters dp : removed) {
			reviewed.add(dp);
		}
		
		return reviewed;
	}
	
	private List<DuplicatedParameters> removeSubSet(List<DuplicatedParameters> duplicatedParameters){
		
		List<DuplicatedParameters> reviewed = copy(duplicatedParameters);
		
		for (int i = 0; i < duplicatedParameters.size(); i++) {
			for (int j = i+1; j < duplicatedParameters.size(); j++) {
				if (isSub(duplicatedParameters.get(i), duplicatedParameters.get(j))) {
					reviewed.remove(duplicatedParameters.get(j));
				}else{
					if (isSub(duplicatedParameters.get(j), duplicatedParameters.get(i))) {
						reviewed.remove(duplicatedParameters.get(i));
					}
				}
			}
		}
		
		return reviewed;
	}
	
	private List<DuplicatedParameters> copy(List<DuplicatedParameters> duplicatedParameters){
		List<DuplicatedParameters> copy = new ArrayList<DuplicatedParameters>();
		
		for (DuplicatedParameters dp : duplicatedParameters) {
			copy.add(dp);
		}
		
		return copy;
	}
	
	private boolean isSub(DuplicatedParameters dcs1, DuplicatedParameters dcs2){
		
		if (!dcs1.getBlock().equals(dcs2.getBlock())) {
			return false;
		}
		
		for (MethodDeclaration method : dcs2.getDuplicatedMethods()) {
			boolean exist = false;
			for (MethodDeclaration m : dcs1.getDuplicatedMethods()) {
				if (method.getName().getFullyQualifiedName().equalsIgnoreCase(m.getName().getFullyQualifiedName())) {
					exist = true;
				}
			}
			
			if (!exist) {
				return false;
			}
		}
		
		for (Clazz clazz : dcs2.getDuplicatedClasses()) {
			boolean exist = false;
			for (Clazz c : dcs1.getDuplicatedClasses()) {
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
	
	public List<DuplicatedParameters> getDuplicatedParametersList() {
		return duplicatedParametersList;
	}
}

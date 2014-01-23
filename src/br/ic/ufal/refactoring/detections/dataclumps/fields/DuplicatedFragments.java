package br.ic.ufal.refactoring.detections.dataclumps.fields;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.VariableDeclaration;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.util.OperationsUtil;

public class DuplicatedFragments {
	
	private OperationsUtil operationsUtil = new OperationsUtil();
	private List<Clazz> classes = new ArrayList<Clazz>();
	private List<VariableDeclaration> fragments = new ArrayList<VariableDeclaration>();
	
	public List<Clazz> getClasses() {
		return classes;
	}
	
	public void addClazz(Clazz clazz){
		 if (!contain(clazz)) {
             this.classes.add(clazz);
		 }
	}
	
	 private boolean contain(Clazz clazz){
         for (Clazz c : this.classes) {
                 if (c.getTypeDeclaration().getName().getFullyQualifiedName().equalsIgnoreCase(clazz.getTypeDeclaration().getName().getFullyQualifiedName())) {
                         return true;
                 }
         }
         return false;
	 }
	
	public List<VariableDeclaration> getFragments() {
		return fragments;
	}
	
	public void setFragments(List<VariableDeclaration> fragments) {
		this.fragments = fragments;
	}
	
	public void addFragment(VariableDeclaration variableDeclaration){
		this.fragments.add(variableDeclaration);
	}
	
	 @Override
     public boolean equals(Object obj) {
             DuplicatedFragments description = (DuplicatedFragments)obj;
             
             boolean existDescription = true;
             
             for (Clazz clazz : description.getClasses()) {
                     if (!existClasse(clazz, this.classes)) {
                             existDescription = false;
                     }
             }
             
             for (Clazz clazz : this.classes) {
                 if (!existClasse(clazz, description.getClasses())) {
                         existDescription = false;
                 }
             }
             
             if (this.classes.size()!= description.getClasses().size()) {
                     return false;
             }
             
             
             if (!operationsUtil.similar(fragments, description.getFragments())) {
                     return false;
             }
             
             return existDescription;
     }

     private boolean existClasse(Clazz clazz, List<Clazz> classes){
             for (Clazz c : classes) {
                     if (c.getTypeDeclaration().resolveBinding().isEqualTo(c.getTypeDeclaration().resolveBinding())) {
                             return true;
                     }
             }
             
             return false;
     }
     
     

	@Override
	public String toString() {
		
		String classes = new String();
		for (Clazz clazz : this.classes) {
			classes = classes + clazz.getTypeDeclaration().getName()+ ", ";
		}
		
		return "\n\n\n DuplicatedFragments [classes=" + classes + ", fragments="+ fragments + "]";
	}

	
	
}

package br.ic.ufal.refactoring.detections.dataclumps.fields;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.util.ParseUtil;

public class DuplicatedFragments {

	private List<Clazz> classes = new ArrayList<>();
	private List<VariableDeclaration> fragments = new ArrayList<VariableDeclaration>();
	
	public DuplicatedFragments() {
	}
	
	public List<Clazz> getClasses() {
		return classes;
	}
	
	public void setClasses(List<Clazz> classes) {
		this.classes = classes;
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
	
	public void addFragment(VariableDeclaration fragment){
		this.fragments.add(fragment);
	}
	
	@Override
	public boolean equals(Object obj) {
		DuplicatedFragments description = (DuplicatedFragments)obj;
		
		boolean existDescription = true;
		
		for (Clazz clazz : description.getClasses()) {
			if (!existClasse(clazz)) {
				existDescription = false;
			}
		}
		
		if (this.classes.size()!= description.getClasses().size()) {
			return false;
		}
		
		
		if (!similar(fragments, description.getFragments())) {
			return false;
		}
		
		return existDescription;
	}

	private boolean existClasse(Clazz clazz){
		for (Clazz c : this.classes) {
			if (c.getTypeDeclaration().resolveBinding().isEqualTo(c.getTypeDeclaration().resolveBinding())) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean similar(List<VariableDeclaration> fragments, List<VariableDeclaration> fragments1){
		for (VariableDeclaration frag : fragments) {
			if (!existFragment(frag, fragments1)) {
				return false;
			}
		}
		for (VariableDeclaration frag : fragments1) {
			if (!existFragment(frag, fragments)) {
				return false;
			}
		}
		
		if (fragments.size() != fragments1.size()) {
			return false;
		}
		return true;
	}
	
	private boolean existFragment(VariableDeclaration fragment, List<VariableDeclaration> fragments){
		for (VariableDeclaration frag : fragments) {
			if (frag.getInitializer() != null && fragment.getInitializer() != null) {
				if (frag.getName().getFullyQualifiedName().equals(fragment.getName().getFullyQualifiedName()) &&
						frag.getInitializer().toString().equalsIgnoreCase((fragment.getInitializer().toString()))) {
						return true;
				}
			}
			if (frag.getInitializer() == null && fragment.getInitializer() == null) {
				if (frag.getName().getFullyQualifiedName().equals(fragment.getName().getFullyQualifiedName())) {
					TypeDeclaration typeDeclaration = ParseUtil.getTypeDeclaration((CompilationUnit)frag.getRoot());
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		
		String dcs = new String();
		for (Clazz clazz : this.classes) {
			dcs = dcs + clazz.getTypeDeclaration().getName().getFullyQualifiedName() + ", ";
		}
		
		return "DuplicatedFragments [classes= " + dcs+ ", fragments=" + fragments + "]";
	}

	

}

package br.ic.ufal.refactoring.detections.duplication.subclasses.fields.down;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.detections.BadSmell;
import br.ic.ufal.util.OperationsUtil;

public class DownFragments extends BadSmell {

	private OperationsUtil operationsUtil = new OperationsUtil();
	private List<DownFragmentsDesc> downFragmentsDescs = new ArrayList<DownFragmentsDesc>();
	
	private int threshold = 0;
	
	public DownFragments(Project project, int threshold) {
		super(project);
		this.threshold = threshold;
	}

	@Override
	public boolean check() {
		
		System.out.println("Check Down Fragments");
	
		for (Clazz superclass : super.getProject().getClasses()) {
			
			List<Clazz> subClasses = operationsUtil.getSubclasses(superclass, super.getProject().getClasses());
			
			if (subClasses.size() > 0) {
				for (FieldDeclaration fieldDeclaration : superclass.getTypeDeclaration().getFields()) {
					List<VariableDeclaration> fragments = fieldDeclaration.fragments();
					
					for (VariableDeclaration fragment : fragments) {
						int count = operationsUtil.countFragmentsInClasses(fragment, subClasses);
					
						if (count == 1 ) {
							
							DownFragmentsDesc desc = retrieveDownFragmentDesc(superclass);
							
							if (desc != null) {
								desc.getFragmentsToBeDown().add(fragment);
							}else{
								DownFragmentsDesc downFragmentsDesc = new DownFragmentsDesc();
								
								downFragmentsDesc.setSuperclass(superclass);
								
								for (Clazz subclass : subClasses) {
									if (operationsUtil.useFragment(fragment, subclass) > this.threshold) {
										downFragmentsDesc.addSubclass(subclass);
									}
								}
								
								if (downFragmentsDesc.getSubclasses().size() > 0) {
									downFragmentsDesc.addFragment(fragment);
									
									this.downFragmentsDescs.add(downFragmentsDesc);
								}
							}
						}
					}
				}
			}
		}
		return this.downFragmentsDescs.size() > 0;
	}
	
	public List<DownFragmentsDesc> getDownFragmentsDescs() {
		return downFragmentsDescs;
	}
	
	private DownFragmentsDesc retrieveDownFragmentDesc(Clazz superclass){
		
		for (DownFragmentsDesc desc : this.downFragmentsDescs) {
			if (superclass.getTypeDeclaration().resolveBinding().isEqualTo(desc.getSuperclass().getTypeDeclaration().resolveBinding())) {
				return desc;
			}
		}
		
		return null;
	}
}

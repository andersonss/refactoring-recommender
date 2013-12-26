package br.ic.ufal.refactoring.detections.featureenvy;

import gr.uom.java.ast.util.ExpressionExtractor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.detections.BadSmell;

public class FeatureEnvy extends BadSmell {

	private List<FeatureEnvyDescription> descriptions = new ArrayList<FeatureEnvyDescription>();
	private int envyLevel = 1;
	
	public FeatureEnvy(Project project, int envyLevel) {
		super(project);
		this.envyLevel = envyLevel;
	}

	@Override
	public boolean check() {
		
		System.out.println("Feature Envy Check");
		
		ExpressionExtractor expressionExtractor = new ExpressionExtractor();
		
		for (Clazz clazz : super.getProject().getClasses()) {
			
			
			
			for (MethodDeclaration method : clazz.getTypeDeclaration().getMethods()) {
				
				Map<String, Set<String>> invocations = new HashMap<String, Set<String>>();
				
				for (Clazz c : super.getProject().getClasses()) {
					invocations.put(c.getTypeDeclaration().getName().getFullyQualifiedName(), new HashSet<String>());
				}
				
				List<Expression> methodInvocations = expressionExtractor.getMethodInvocations(method.getBody());
				
				for (Expression expression : methodInvocations) {
					
					if (expression instanceof MethodInvocation) {
						MethodInvocation methodInvocation = (MethodInvocation) expression;
						
						if (methodInvocation.getName() != null) {
							IMethodBinding methodBinding = methodInvocation.resolveMethodBinding();
							
							Set<String> values = invocations.get(methodBinding.getDeclaringClass().getName());
							if (values != null) {
								values.add(methodInvocation.getName().toString());
							}
							
							invocations.put(methodBinding.getDeclaringClass().getQualifiedName(), values);
						}
					}
				}
				
				FeatureEnvyDescription desc = identifyFeatureEnvy(invocations, method, clazz);
		        
		        if ( desc != null) {
					this.descriptions.add(desc);
				}
			}
		}
		
		return this.descriptions.size() > 0;
	}
	
	private FeatureEnvyDescription identifyFeatureEnvy(Map<String, Set<String>> invocations, MethodDeclaration method, Clazz sourceClass){
	
		Set s=invocations.entrySet();

        Iterator it=s.iterator();
        
        String highestKey = new String();
        int amount = -1;
        
        while(it.hasNext())
        {
            Map.Entry m =(Map.Entry)it.next();

            String key= (String)m.getKey();

            Set<String> values=(Set<String>)m.getValue();

            if (values != null) {
            	if (values.size() > amount) {
    				highestKey = key;
    				amount = values.size();
    			}
			}
        }
        	//System.out.println("Envy Level: " + this.envyLevel);
        	//System.out.println("Invocations: "+invocations.get(sourceClass.getTypeDeclaration().getName().getFullyQualifiedName()).size());
        	if (amount > this.envyLevel*invocations.get(sourceClass.getTypeDeclaration().getName().getFullyQualifiedName()).size()) {
        		Clazz targetClazz = getClass(highestKey);
    			
        		if (targetClazz != null) {
        			FeatureEnvyDescription desc = new FeatureEnvyDescription();
        			desc.setSourceClass(sourceClass);
        			desc.setSourceMethod(method);
        			
        			if (sourceClass.getTypeDeclaration().getSuperclassType() != null) {
        				if (sourceClass.getTypeDeclaration().getSuperclassType().resolveBinding() != null) {
							if (targetClazz.getTypeDeclaration().resolveBinding() != null) {
								if (sourceClass.getTypeDeclaration().getSuperclassType().resolveBinding().isEqualTo(targetClazz.getTypeDeclaration().resolveBinding())) {
		        					return null;
		        				}
							}
						}
        				
        			}
        			
        			desc.setTargetClass(targetClazz);
        			
        			return desc;
				}
        		
    			
    		}
		
		
		return null;
	}
	
	
	private Clazz getClass(String name){
		for (Clazz clazz : super.getProject().getClasses()) {
			if (name.equalsIgnoreCase(clazz.getTypeDeclaration().getName().getFullyQualifiedName())) {
				
				return clazz;
			}
		}
		
		return null;
	}
	
	public List<FeatureEnvyDescription> getDescriptions() {
		return descriptions;
	}

}

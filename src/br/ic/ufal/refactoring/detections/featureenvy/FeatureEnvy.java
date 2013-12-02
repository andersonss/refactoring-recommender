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
	
	public FeatureEnvy(Project project) {
		super(project);
		
	}

	@Override
	public boolean check() {
		
		System.out.println("Feature Envy Check");
		
		ExpressionExtractor expressionExtractor = new ExpressionExtractor();
		
		for (Clazz clazz : super.getProject().getClasses()) {
			
			ICompilationUnit iCompilationUnit = clazz.getICompilationUnit();
			
			CompilationUnit compilationUnit = clazz.getCompilationUnit();
			
			TypeDeclaration typeDeclaration = (TypeDeclaration)compilationUnit.types().get(0);
			
			//System.out.println("Analysed Class: " + typeDeclaration.getName().getFullyQualifiedName());

			for (MethodDeclaration method : typeDeclaration.getMethods()) {
				
				//System.out.println("\n\n Analysed Method: " + method.getName().getFullyQualifiedName());
				
				Map<String, Set<String>> invocations = new HashMap<String, Set<String>>();
				
				for (Clazz c : super.getProject().getClasses()) {
					invocations.put(c.getTypeDeclaration().getName().getFullyQualifiedName(), new HashSet<String>());
				}
				
				List<Expression> methodInvocations = expressionExtractor.getMethodInvocations(method.getBody());
				
				for (Expression expression : methodInvocations) {
					
					if (expression instanceof MethodInvocation) {
						MethodInvocation methodInvocation = (MethodInvocation) expression;
						
						IMethodBinding methodBinding = methodInvocation.resolveMethodBinding();
					
						//System.out.println("Type: "+ methodBinding.getDeclaringClass().getName() +" Method Invocation: " + methodInvocation.getName());
						
						Set<String> values = invocations.get(methodBinding.getDeclaringClass().getName());
						values.add(methodInvocation.getName().toString());
						
						invocations.put(methodBinding.getDeclaringClass().getName(), values);
						
					}
					
				}
				
				
				
				/*Set s=invocations.entrySet();

		        Iterator it=s.iterator();

		        while(it.hasNext())
		        {
		            Map.Entry m =(Map.Entry)it.next();

		            String key= (String)m.getKey();

		            Set<String> values=(Set<String>)m.getValue();

		            System.out.println("Key :"+key+"  Values :"+values);
		        }*/
				
		        FeatureEnvyDescription desc = identifyFeatureEnvy(invocations, method, typeDeclaration);
		        
		        if ( desc != null) {
					this.descriptions.add(desc);
				}
			}
		}
		
		
		
		/*List<Expression> newVariableInstructions = expressionExtractor.getVariableInstructions(method.getBody());
		
		for (Expression expression : newVariableInstructions) {
			
			SimpleName sn = (SimpleName)expression;
			
			if (sn.getParent() instanceof QualifiedName) {
				QualifiedName qualifiedName = (QualifiedName)sn.getParent();
				System.out.println("QN Type: "+ qualifiedName.getQualifier().resolveTypeBinding().getQualifiedName() +" Simple Name: " + qualifiedName.getName());
			}
		}
		
		List<Expression> fieldAccesses = expressionExtractor.getFieldAccesses(method.getBody());
		
		for (Expression expression : fieldAccesses) {
			FieldAccess fa = (FieldAccess)expression;
			System.out.println("Field Access: " + fa.getName());
		}*/
		
		/*List<Expression> instanceCreations = expressionExtractor.getClassInstanceCreations(method.getBody());
		
		for (Expression expression : instanceCreations) {
			
				ClassInstanceCreation classInstanceCreation = (ClassInstanceCreation) expression;
				System.out.println("Class Instance Creation: " + classInstanceCreation.getType());
		}*/
		
		return this.descriptions.size() > 0;
	}
	
	private FeatureEnvyDescription identifyFeatureEnvy(Map<String, Set<String>> invocations, MethodDeclaration method, TypeDeclaration sourceClass){
	
		Set s=invocations.entrySet();

        Iterator it=s.iterator();
        
        String highestKey = new String();
        int amount = -1;
        
        while(it.hasNext())
        {
            Map.Entry m =(Map.Entry)it.next();

            String key= (String)m.getKey();

            Set<String> values=(Set<String>)m.getValue();

            if (values.size() > amount) {
				highestKey = key;
				amount = values.size();
			}
        }
        
        if (amount > invocations.get(sourceClass.getName().getFullyQualifiedName()).size()) {
			FeatureEnvyDescription desc = new FeatureEnvyDescription();
			desc.setSourceClass(sourceClass);
			desc.setSourceMethod(method);
			desc.setTargetClass(getClass(highestKey));
			return desc;
		}
		
		return null;
	}
	
	
	private TypeDeclaration getClass(String name){
		for (Clazz c : super.getProject().getClasses()) {
			if (name.equalsIgnoreCase(c.getTypeDeclaration().getName().getFullyQualifiedName())) {
				
				ICompilationUnit iCompilationUnit = c.getICompilationUnit();
				
				CompilationUnit compilationUnit = c.getCompilationUnit();
				
				TypeDeclaration typeDeclaration = (TypeDeclaration)compilationUnit.types().get(0);
				
				return typeDeclaration;
			}
		}
		
		return null;
	}
	
	public List<FeatureEnvyDescription> getDescriptions() {
		return descriptions;
	}

}

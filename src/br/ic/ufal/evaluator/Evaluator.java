package br.ic.ufal.evaluator;

import gr.uom.java.ast.util.ExpressionExtractor;

import java.util.List;

import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.parser.Project;
import br.ic.ufal.util.ParseUtil;

public class Evaluator {

	private Project project = new Project();
	private int treedeep = 0;
	
	public Evaluator( ) {
	}

	public Measures evaluateProjectQuality(Project project){
		
		this.project = project;
		this.treedeep = 0;
		
		Measures measures = new Measures();
		System.out.println("Measures");
		measures.setDesignsize(evaluateDesignZise());
		measures.setAbstraction(evaluateAbstraction());
		measures.setEncapsulation(evaluateEncapsulation());
		measures.setCoupling(evaluateCoupling());
		measures.setInheritance(evaluateInheritance());
		measures.setCohesion(evaluateCohesion());
		measures.setComposition(evaluateComposition());
		measures.setPolymorphism(evaluatePolymorphism());
		measures.setMessaging(evaluateMessaging());
		
		return measures;
	}

	private double evaluateMessaging() {
		System.out.println("Evaluate Messaging");
		double messaging = 0;
		
		for (Clazz clazz : this.project.getClasses()) {
			double cm = 0;
			for (MethodDeclaration method : clazz.getTypeDeclaration().getMethods()) {
				for (Object obj : method.modifiers()) {
					if (obj instanceof Modifier) {
						Modifier modifier = (Modifier) obj;
						if (modifier.getKeyword().equals(ModifierKeyword.PUBLIC_KEYWORD)) {
							cm++;
						}
						
					}
				}
			}
			messaging = messaging + cm;
		}
		
		return messaging;
	}

	private double evaluatePolymorphism() {
		System.out.println("Evaluate Polymorphism");
		double polymorphism = 0;
		
		for (Clazz clazz : this.project.getClasses()) {
			if (clazz.getTypeDeclaration().getSuperclassType() != null) {
				polymorphism++;
			}
		}
		
		return polymorphism;
	}

	private double evaluateComposition() {
		System.out.println("Evaluate Composition");
		double composition = 0;
		
		for (Clazz clazz : this.project.getClasses()) {
			for (FieldDeclaration field : clazz.getTypeDeclaration().getFields()) {
				if (existFieldClass(field)) {
					composition++;
				}
			}
		}
		
		return composition;
	}
	
	private boolean existFieldClass(FieldDeclaration fieldDeclaration){
		
		for (Clazz clazz : this.project.getClasses()) {
			if (fieldDeclaration.getType() != null && clazz.getTypeDeclaration() != null) {
				if (fieldDeclaration.getType().resolveBinding() != null && clazz.getTypeDeclaration().resolveBinding() != null ) {
					if (fieldDeclaration.getType().resolveBinding().isEqualTo(clazz.getTypeDeclaration().resolveBinding())) {
						return true;
					}
				}
			}
		}
		
		return false;
	}

	private double evaluateCohesion() {
		System.out.println("Evaluate Cohesion");
		double cohesion = 0;
		
		ExpressionExtractor expressionExtractor = new ExpressionExtractor();
		
		for (Clazz clazz : this.project.getClasses()) {
			
			
			for (MethodDeclaration methodDeclaration : clazz.getTypeDeclaration().getMethods()) {
				
				List<Expression> variables = expressionExtractor.getVariableInstructions(methodDeclaration.getBody());
				
				for (Expression expression : variables) {
					
					SimpleName variable = (SimpleName)expression;
					IBinding variableBinding = variable.resolveBinding();
					if (variableBinding != null) {
						if(variableBinding.getKind() == IBinding.VARIABLE) {
							IVariableBinding accessedVariableBinding = (IVariableBinding)variableBinding;
							if(accessedVariableBinding.isField()) {
								for (FieldDeclaration fieldDeclaration : clazz.getTypeDeclaration().getFields()) {
									List<VariableDeclarationFragment> fragments = fieldDeclaration.fragments();
									for (VariableDeclarationFragment fragment : fragments) {
										
										if (accessedVariableBinding != null && fragment.resolveBinding() != null) {
											
										if (accessedVariableBinding.isEqualTo(fragment.resolveBinding().getVariableDeclaration())) {
											cohesion++;
										}
										
										}
										
										
									}
								}
							}
							
						}
					}
					
				}
			}
		}
		
		
		
		
		
		/*List<TypeDeclaration> typDeclarations = Util.getTypeDeclarations(project.getiCompilationUnits());
		
		for (TypeDeclaration clazz : typDeclarations) {
			double access = 0;
			double noaccess = 0;
			for (FieldDeclaration field : clazz.getFields()) {
				double count = 0;
				double ncount = 0;
				List<VariableDeclarationFragment> fragments = field.fragments();
				for (VariableDeclarationFragment fragment : fragments) {
					for (MethodDeclaration method : clazz.getMethods()) {
						if (methodContainVar(method, fragment.getName().toString())) {
							count++;
						}else{
							ncount++;
						}
					}
				}
				access+= count/2;
				noaccess+=ncount/2;
			}
			cohesion+= noaccess-access;
			if (cohesion <0 ) {
				return 0;
			}
		}*/
		
		return cohesion;
	}

	private double evaluateInheritance() {
		System.out.println("Evaluate Inheritance");
		double inheritance = 0;
		
		for (Clazz clazz : this.project.getClasses()) {
			
			if (clazz.getTypeDeclaration() != null) {
				Type superclass = clazz.getTypeDeclaration().getSuperclassType();
				double inheritanceClasse = 0;
				if (superclass != null) {
					for (MethodDeclaration method : clazz.getTypeDeclaration().getMethods()) {
						IMethodBinding iMethodBinding = method.resolveBinding();
						for (IMethodBinding meth : superclass.resolveBinding().getDeclaredMethods()) {
							if (iMethodBinding != null) {
								if (iMethodBinding.isSubsignature(meth) &&
										!meth.toString().contains("init")) {
										inheritanceClasse++;
								}
							}
						}
					}
				}
				
				inheritance = inheritance + inheritanceClasse;
			}
			
			
		}
		
		return inheritance;
	}

	private double evaluateCoupling() {
		System.out.println("Evaluate Coupling");
		double coupling = 0;
		
		for (Clazz clazz : this.project.getClasses()) {
			double individualCoupling = 0;
			for (Clazz cclazz : this.project.getClasses()) {
				if (clazz.getTypeDeclaration() != null && 
					cclazz.getTypeDeclaration() != null &&	
					!clazz.getTypeDeclaration().equals(cclazz.getTypeDeclaration())) {
					for (FieldDeclaration field : cclazz.getTypeDeclaration().getFields()) {
						
						if (field.getType() != null &&  clazz.getTypeDeclaration() != null ) {
							if (field.getType().resolveBinding() != null && clazz.getTypeDeclaration().resolveBinding() != null) {
							if (field.getType(). resolveBinding().isEqualTo((clazz.getTypeDeclaration().resolveBinding()))) {
								individualCoupling++;
							}
							}
						}
					}
					
					for (MethodDeclaration method : cclazz.getTypeDeclaration().getMethods()) {
						List<SingleVariableDeclaration> parameters = method.parameters();
						for (SingleVariableDeclaration parameter : parameters) {
							if (parameter.getType() != null && parameter.getType().resolveBinding() != null &&
								clazz.getTypeDeclaration() != null && clazz.getTypeDeclaration().resolveBinding() != null) {
								
							if (parameter.getType().resolveBinding().isEqualTo(clazz.getTypeDeclaration().resolveBinding())) {
								individualCoupling++;
							}
							
							}
							
						}
					}
				}
			}
			
			//TODO Avaliar o acomplamento total.
			coupling = coupling + individualCoupling;
		}
		
		return coupling;
	}

	private double evaluateEncapsulation() {
		System.out.println("Evaluate Encapsulation");
		double encapsulation = 0;
		
		for (Clazz clazz : this.project.getClasses()) {
			if (clazz.getTypeDeclaration() != null) {
				if (clazz.getTypeDeclaration().getFields().length > 0) {
					double amountPrivateAttr = countPrivateAttr(clazz.getTypeDeclaration().getFields());
					double ratio = amountPrivateAttr/numberOfFragments(clazz.getTypeDeclaration().getFields());
					encapsulation = encapsulation + ratio;
				}
			}
			
		}
		
		return encapsulation;
		
	}
	
	private int numberOfFragments(FieldDeclaration[] fields){
		int numberOfFrag = 0;
		
		for (FieldDeclaration fieldDeclaration : fields) {
			numberOfFrag = numberOfFrag + fieldDeclaration.fragments().size();
		}
		
		return numberOfFrag;
	}
	
	private int countPrivateAttr(FieldDeclaration[] fields){
		int count = 0;
		
		for (FieldDeclaration field : fields) {
			for (Object obj : field.modifiers()) {
				if (obj instanceof Modifier) {
					Modifier modifier = (Modifier) obj;
					if (modifier.getKeyword().equals(ModifierKeyword.PRIVATE_KEYWORD)) {
						count = count + field.fragments().size();
					}
					
				}
			}
		}
		
		return count;
	}

	private double evaluateAbstraction() {
		System.out.println("Evaluate Abstraction");
		double abstraction = 0;
		
		for (Clazz clazz : this.project.getClasses()) {
			this.treedeep = 0;
			searchInheritanceTree(clazz.getTypeDeclaration());
			abstraction = abstraction + treedeep;
		}
		
		return abstraction;
	}
	
	private void searchInheritanceTree(TypeDeclaration typeDeclaration){
	
		if (typeDeclaration != null) {
			if (typeDeclaration.getSuperclassType()!= null) {
				this.treedeep++;
				
				TypeDeclaration superclass = ParseUtil.getTypeDeclaration(typeDeclaration.getSuperclassType().resolveBinding(), this.project.getClasses());
				
				searchInheritanceTree(superclass);
			}
		}
	}

	private double evaluateDesignZise() {
		
		System.out.println("Evaluate Design");
		
		return this.project.getClasses().size();
		
	}
	
	
	
}

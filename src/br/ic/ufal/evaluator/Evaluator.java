package br.ic.ufal.evaluator;

import gr.uom.java.ast.util.ExpressionExtractor;

import java.util.ArrayList;
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
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.parser.Project;
import br.ic.ufal.util.ParseUtil;

public class Evaluator {

	private Project project = new Project();

	/**
	 * 
	 */
	public Evaluator() {
	}

	/**
	 * @param project
	 * @return
	 */
	public Measures evaluateProjectQuality(Project project) {

		this.project = project;

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

	/**
	 * @return
	 */
	private double evaluateMessaging() {
		System.out.println("Evaluate Messaging");
		double messaging = 0;

		for (Clazz clazz : this.project.getClasses()) {
			double cm = 0;
			for (MethodDeclaration method : clazz.getTypeDeclaration()
					.getMethods()) {
				for (Object obj : method.modifiers()) {
					if (obj instanceof Modifier) {
						Modifier modifier = (Modifier) obj;
						if (modifier.getKeyword().equals(
								ModifierKeyword.PUBLIC_KEYWORD)) {
							cm++;
						}
					}
				}
			}
			messaging = messaging + cm;
		}

		return messaging;
	}

	/**
	 * @return
	 */
	private double evaluatePolymorphism() {
		System.out.println("Evaluate Polymorphism");
		double polymorphism = 0;

		for (Clazz clazz : this.project.getClasses()) {
			List<Clazz> subclasses = getSubclasses(clazz,
					this.project.getClasses());
			if (clazz.getTypeDeclaration().getSuperclassType() == null
					&& subclasses.size() > 0) {
				polymorphism = polymorphism + subclasses.size();
			}
		}

		return polymorphism;
	}

	/**
	 * @param clazz
	 * @param classes
	 * @return
	 */
	private List<Clazz> getSubclasses(Clazz clazz, List<Clazz> classes) {
		List<Clazz> subclasses = new ArrayList<Clazz>();

		for (Clazz sub : classes) {
			if (isSubClass(clazz, sub)) {
				subclasses.add(sub);
			}
		}

		return subclasses;
	}

	/**
	 * @param clazz
	 * @param sub
	 * @return
	 */
	private boolean isSubClass(Clazz clazz, Clazz sub) {
		if (sub.getTypeDeclaration().getSuperclassType() != null) {

			Clazz superclass = ParseUtil.getClazz(sub.getTypeDeclaration()
					.getSuperclassType().resolveBinding(),
					this.project.getClasses());

			if (superclass != null) {
				if (clazz
						.getTypeDeclaration()
						.resolveBinding()
						.isEqualTo(
								superclass.getTypeDeclaration()
										.resolveBinding())) {
					return true;
				} else {
					return isSubClass(clazz, superclass);
				}
			}
		}

		return false;

	}

	/**
	 * @return
	 */
	private double evaluateComposition() {
		System.out.println("Evaluate Composition");
		double composition = 0;

		for (Clazz clazz : this.project.getClasses()) {
			int classComposition = 0;
			for (FieldDeclaration field : clazz.getTypeDeclaration()
					.getFields()) {
				if (existFieldClass(field)) {
					classComposition++;
				}
			}
			composition = composition + classComposition;
		}

		return composition;
	}

	/**
	 * @param fieldDeclaration
	 * @return
	 */
	private boolean existFieldClass(FieldDeclaration fieldDeclaration) {

		for (Clazz clazz : this.project.getClasses()) {
			if (fieldDeclaration.getType() != null
					&& clazz.getTypeDeclaration() != null) {
				if (fieldDeclaration.getType().resolveBinding() != null
						&& clazz.getTypeDeclaration().resolveBinding() != null) {
					if (fieldDeclaration
							.getType()
							.resolveBinding()
							.isEqualTo(
									clazz.getTypeDeclaration().resolveBinding())) {
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * @return
	 */
	private double evaluateCohesion() {
		System.out.println("Evaluate Cohesion");
		double cohesion = 0;

		for (Clazz clazz : this.project.getClasses()) {
			double classCohesion = 0;
			FieldDeclaration[] fields = clazz.getTypeDeclaration().getFields();
			for (int i = 0; i < clazz.getTypeDeclaration().getMethods().length; i++) {
				List<IVariableBinding> ivariables = getVariables(clazz
						.getTypeDeclaration().getMethods()[i], fields);

				for (int j = i + 1; j < clazz.getTypeDeclaration().getMethods().length; j++) {
					List<IVariableBinding> jvariables = getVariables(clazz
							.getTypeDeclaration().getMethods()[j], fields);
					classCohesion = classCohesion
							+ countSimilarVariables(ivariables, jvariables);

				}
			}
			cohesion = cohesion + classCohesion;
		}

		return cohesion;
	}

	/**
	 * @param methodDeclaration
	 * @param fields
	 * @return
	 */
	private List<IVariableBinding> getVariables(
			MethodDeclaration methodDeclaration, FieldDeclaration[] fields) {
		List<IVariableBinding> ivariables = new ArrayList<IVariableBinding>();

		ExpressionExtractor expressionExtractor = new ExpressionExtractor();

		List<Expression> variables = expressionExtractor
				.getVariableInstructions(methodDeclaration.getBody());

		for (Expression expression : variables) {

			SimpleName variable = (SimpleName) expression;
			IBinding variableBinding = variable.resolveBinding();

			if (variableBinding != null) {

				if (variableBinding.getKind() == IBinding.VARIABLE) {
					IVariableBinding accessedVariableBinding = (IVariableBinding) variableBinding;

					if (accessedVariableBinding.isField()) {

						for (FieldDeclaration fieldDeclaration : fields) {
							List<VariableDeclarationFragment> fragments = fieldDeclaration
									.fragments();

							for (VariableDeclarationFragment fragment : fragments) {

								if (accessedVariableBinding != null
										&& fragment.resolveBinding() != null) {

									if (accessedVariableBinding
											.isEqualTo(fragment
													.resolveBinding()
													.getVariableDeclaration())) {
										ivariables.add(accessedVariableBinding);
									}

								}

							}
						}
					}

				}
			}

		}

		return ivariables;

	}

	/**
	 * @param ivariables
	 * @param jvariables
	 * @return
	 */
	private int countSimilarVariables(List<IVariableBinding> ivariables,
			List<IVariableBinding> jvariables) {
		int count = 0;

		for (IVariableBinding iVariableBinding : ivariables) {
			for (IVariableBinding jVariableBinding : jvariables) {
				if (iVariableBinding.isEqualTo(jVariableBinding)) {
					count++;
				}
			}
		}

		return count;
	}

	/**
	 * @return
	 */
	private double evaluateInheritance() {
		System.out.println("Evaluate Inheritance");
		double inheritance = 0;

		for (Clazz clazz : this.project.getClasses()) {

			if (clazz.getTypeDeclaration() != null) {
				Type superclass = clazz.getTypeDeclaration()
						.getSuperclassType();
				double inheritanceClasse = 0;
				double notinheritanceClasse = 0;
				double ratio = 0;
				if (superclass != null) {
					for (MethodDeclaration method : clazz.getTypeDeclaration()
							.getMethods()) {
						if (!method.isConstructor()) {

							IMethodBinding iMethodBinding = method
									.resolveBinding();
							boolean isSub = false;
							if (superclass.resolveBinding() != null) {

								for (IMethodBinding meth : superclass
										.resolveBinding().getDeclaredMethods()) {
									if (iMethodBinding != null) {
										if (iMethodBinding.isSubsignature(meth)
												&& !meth.toString().contains(
														"init")) {
											inheritanceClasse++;
											isSub = true;
										}
									}
								}
							}

							if (!isSub) {
								notinheritanceClasse++;
							}
						}
					}
				}
				if (notinheritanceClasse > 0) {
					ratio = inheritanceClasse / notinheritanceClasse;
				} else {
					if (notinheritanceClasse == 0 && inheritanceClasse > 0) {
						ratio = 1;
					}

				}
				inheritance = inheritance + ratio;
			}

		}

		return inheritance;
	}

	/**
	 * @return
	 */
	private double evaluateCoupling() {
		System.out.println("Evaluate Coupling");
		double coupling = 0;

		for (Clazz clazz : this.project.getClasses()) {
			double individualCoupling = 0;
			for (Clazz cclazz : this.project.getClasses()) {
				if (clazz.getTypeDeclaration() != null
						&& cclazz.getTypeDeclaration() != null
						&& !clazz.getTypeDeclaration().equals(
								cclazz.getTypeDeclaration())) {
					for (FieldDeclaration field : cclazz.getTypeDeclaration()
							.getFields()) {

						if (field.getType() != null
								&& clazz.getTypeDeclaration() != null) {
							if (field.getType().resolveBinding() != null
									&& clazz.getTypeDeclaration()
											.resolveBinding() != null) {
								if (field
										.getType()
										.resolveBinding()
										.isEqualTo(
												(clazz.getTypeDeclaration()
														.resolveBinding()))) {
									individualCoupling++;
								}
							}
						}
					}

					/*
					 * for (MethodDeclaration method :
					 * cclazz.getTypeDeclaration().getMethods()) {
					 * List<SingleVariableDeclaration> parameters =
					 * method.parameters(); for (SingleVariableDeclaration
					 * parameter : parameters) { if (parameter.getType() != null
					 * && parameter.getType().resolveBinding() != null &&
					 * clazz.getTypeDeclaration() != null &&
					 * clazz.getTypeDeclaration().resolveBinding() != null) {
					 * 
					 * if (parameter.getType().resolveBinding().isEqualTo(clazz.
					 * getTypeDeclaration().resolveBinding())) {
					 * individualCoupling++; }
					 * 
					 * }
					 * 
					 * } }
					 */
				}
			}

			// TODO Avaliar o acomplamento total.
			coupling = coupling + individualCoupling;
		}

		return coupling;
	}

	/**
	 * @return
	 */
	private double evaluateEncapsulation() {
		System.out.println("Evaluate Encapsulation");
		double encapsulation = 0;

		for (Clazz clazz : this.project.getClasses()) {
			double classEncapsulation = 0;
			if (clazz.getTypeDeclaration() != null) {
				if (clazz.getTypeDeclaration().getFields().length > 0) {
					double amountPrivateAttr = countPrivateAttr(clazz
							.getTypeDeclaration().getFields());
					double ratio = amountPrivateAttr
							/ numberOfFragments(clazz.getTypeDeclaration()
									.getFields());
					classEncapsulation = classEncapsulation + ratio;
				}
			}
			encapsulation = encapsulation + classEncapsulation;
		}

		return encapsulation;

	}

	/**
	 * @param fields
	 * @return
	 */
	private int numberOfFragments(FieldDeclaration[] fields) {
		int numberOfFrag = 0;

		for (FieldDeclaration fieldDeclaration : fields) {
			numberOfFrag = numberOfFrag + fieldDeclaration.fragments().size();
		}

		return numberOfFrag;
	}

	/**
	 * @param fields
	 * @return
	 */
	private int countPrivateAttr(FieldDeclaration[] fields) {
		int count = 0;

		for (FieldDeclaration field : fields) {
			for (Object obj : field.modifiers()) {
				if (obj instanceof Modifier) {
					Modifier modifier = (Modifier) obj;
					if (modifier.getKeyword().equals(
							ModifierKeyword.PRIVATE_KEYWORD)) {
						count = count + field.fragments().size();
					}

				}
			}
		}

		return count;
	}

	/**
	 * @return
	 */
	private double evaluateAbstraction() {
		System.out.println("Evaluate Abstraction");
		double count = 0, classabstraction = 0;

		for (Clazz clazz : this.project.getClasses()) {
			List<Clazz> subclasses = getSubclasses(clazz,
					this.project.getClasses());

			if (clazz.getTypeDeclaration().getSuperclassType() == null
					&& subclasses.size() > 0) {
				classabstraction = classabstraction + subclasses.size();
				count++;
			}

		}
		if (count > 0) {
			return classabstraction / count;
		}
		return 0;
	}

	/*
	 * private void searchInheritanceTree(TypeDeclaration typeDeclaration){
	 * 
	 * if (typeDeclaration != null) { if (typeDeclaration.getSuperclassType()!=
	 * null) { this.treedeep++;
	 * 
	 * TypeDeclaration superclass =
	 * ParseUtil.getTypeDeclaration(typeDeclaration.
	 * getSuperclassType().resolveBinding(), this.project.getClasses());
	 * 
	 * searchInheritanceTree(superclass); } } }
	 */

	/**
	 * @return
	 */
	private double evaluateDesignZise() {

		System.out.println("Evaluate Design");

		return this.project.getClasses().size();

	}

}

package br.ic.ufal.util;

import gr.uom.java.ast.util.ExpressionExtractor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.detections.dataclumps.fields.DuplicatedFragments;

public class OperationsUtil {

	public OperationsUtil() {
	}
	
	public boolean classContainFragment(VariableDeclaration fragment, Clazz clazz){
		//TODO Melhorar condicional
		for (FieldDeclaration fieldDeclaration : clazz.getTypeDeclaration().getFields()) {
			List<VariableDeclaration> fragments = fieldDeclaration.fragments();
			for (VariableDeclaration frag : fragments) {
				if (frag.getName().getFullyQualifiedName().equals(fragment.getName().getFullyQualifiedName()) ) {
					return true;
				}
			}
		}
		
		
		return false;
	}
	
	public List<DuplicatedFragments> identifyRelatedFragmentsDuplication(List<DuplicatedFragments> duplicatedFragments){
		
		List<DuplicatedFragments> relatedFragments = new ArrayList<DuplicatedFragments>();
		
		for (DuplicatedFragments df : duplicatedFragments) {
			
			boolean samesuperclass = true;
			
			Type superclass = df.getClasses().get(0).getTypeDeclaration().getSuperclassType();
			
			if (superclass == null) {
				samesuperclass = false;
			}
			
			for (int i = 1; i < df.getClasses().size(); i++) {
				if (superclass != null && df.getClasses().get(i).getTypeDeclaration().getSuperclassType() != null) {
					if (!df.getClasses().get(i).getTypeDeclaration().getSuperclassType().resolveBinding().isEqualTo(superclass.resolveBinding())) {
						samesuperclass = false;
					}
				}
				
				
			}
			
			if (samesuperclass) {
				relatedFragments.add(df);
			}
		}
		
		return relatedFragments;
		
	}
	
	public int countMethodsInClasses(MethodDeclaration methodDeclaration, List<Clazz> classes){
		int count = 0;
		
		for (Clazz clazz : classes) {
			
			if (classContatinMethod(methodDeclaration, clazz)) {
				count=count +1;
			}
		}
		
		return count;
	}
	
	public int countFragmentsInClasses(VariableDeclaration fragment, List<Clazz> classes){
		int count = 0;
		
		for (Clazz clazz : classes) {
			
			if (useFragment(fragment, clazz) > 0) {
				count=count +1;
			}
		}
		
		return count;
	}
	
	//TODO Melhorar a comparacao entre metodos
	public boolean classContatinMethod(MethodDeclaration methodDeclaration, Clazz clazz){
		for (MethodDeclaration method : clazz.getTypeDeclaration().getMethods()) {
			if (method.getName().toString().equals(methodDeclaration.getName().toString())) {
				return true;
			}
			/*if (!method.modifiers().equals(methodDeclaration.modifiers())) {
				return false;
			}
			if (!method.getReturnType2().resolveBinding().isEqualTo(methodDeclaration.getReturnType2().resolveBinding())) {
				return false;
			}
			if (!method.parameters().equals(methodDeclaration.parameters())) {
				return false;
			}*/
			
			
		}
		
		return false;
	}
	
	
	
	public List<Clazz> getSubclasses(Clazz clazz, List<Clazz> classes){
		
		List<Clazz> subclasses = new ArrayList<Clazz>();
		
		TypeDeclaration classTypeDeclaration = clazz.getTypeDeclaration();
		
		for (Clazz subclass : classes) {
			
			TypeDeclaration subclassTypeDeclaration = subclass.getTypeDeclaration();
			Type superclass = subclassTypeDeclaration.getSuperclassType();
			if (superclass != null) {
				if (superclass.resolveBinding().isEqualTo(classTypeDeclaration.resolveBinding())) {
					subclasses.add(subclass);
				}
			}
			
		}
		
		return subclasses;
	}

	public int useMethod(MethodDeclaration verifiedMethod, Project project) {

		int usemethod = 0;

		ExpressionExtractor expressionExtractor = new ExpressionExtractor();
		for (Clazz clazz : project.getClasses()) {

			for (MethodDeclaration method : clazz.getTypeDeclaration()
					.getMethods()) {
				List<Expression> methodInvocations = expressionExtractor
						.getMethodInvocations(method.getBody());

				for (Expression expression : methodInvocations) {

					if (expression instanceof MethodInvocation) {
						MethodInvocation methodInvocation = (MethodInvocation) expression;

						IMethodBinding methodBinding = methodInvocation
								.resolveMethodBinding();
						if (methodBinding != null && verifiedMethod != null) {
							if (methodBinding.isEqualTo(verifiedMethod
									.resolveBinding())) {
								usemethod++;
							}
						}
					}

				}
			}
		}

		return usemethod;
	}

	public int useFragment(VariableDeclaration verifiedFragment, Clazz clazz) {

		int useFragment = 0;

		ExpressionExtractor expressionExtractor = new ExpressionExtractor();
		
			for (MethodDeclaration methodDeclaration : clazz
					.getTypeDeclaration().getMethods()) {
				Block methodBody = methodDeclaration.getBody();
				if (methodBody != null) {
					List<Statement> statements = methodBody.statements();
					for (Statement statement : statements) {

						List<Expression> accessedVariables = expressionExtractor
								.getVariableInstructions(statement);
						List<Expression> arrayAccesses = expressionExtractor
								.getArrayAccesses(statement);

						for (Expression expression : accessedVariables) {
							SimpleName accessedVariable = (SimpleName) expression;
							IBinding binding = accessedVariable
									.resolveBinding();
							if (binding.getKind() == IBinding.VARIABLE) {
								IVariableBinding accessedVariableBinding = (IVariableBinding) binding;
								if (accessedVariableBinding.isField()
										&& verifiedFragment
												.resolveBinding()
												.isEqualTo(
														accessedVariableBinding)) {
									useFragment++;
								}
							}
						}
						for (Expression expression : arrayAccesses) {
							ArrayAccess arrayAccess = (ArrayAccess) expression;
							Expression arrayExpression = arrayAccess.getArray();
							SimpleName arrayVariable = null;
							if (arrayExpression instanceof SimpleName) {
								arrayVariable = (SimpleName) arrayExpression;
							} else if (arrayExpression instanceof FieldAccess) {
								FieldAccess fieldAccess = (FieldAccess) arrayExpression;
								arrayVariable = fieldAccess.getName();
							}
							if (arrayVariable != null) {
								IBinding arrayBinding = arrayVariable
										.resolveBinding();
								if (arrayBinding.getKind() == IBinding.VARIABLE) {
									IVariableBinding arrayVariableBinding = (IVariableBinding) arrayBinding;
									if (arrayVariableBinding.isField()
											&& verifiedFragment
													.resolveBinding()
													.isEqualTo(
															arrayVariableBinding)) {
										useFragment++;
									}
								}
							}
						}
					}

				}

			}
		

		return useFragment;
	}
	
	public int useFragment(VariableDeclaration verifiedFragment, Project project) {

		int useFragment = 0;

		ExpressionExtractor expressionExtractor = new ExpressionExtractor();
		for (Clazz clazz : project.getClasses()) {

			for (MethodDeclaration methodDeclaration : clazz
					.getTypeDeclaration().getMethods()) {
				Block methodBody = methodDeclaration.getBody();
				if (methodBody != null) {
					List<Statement> statements = methodBody.statements();
					for (Statement statement : statements) {

						List<Expression> accessedVariables = expressionExtractor
								.getVariableInstructions(statement);
						List<Expression> arrayAccesses = expressionExtractor
								.getArrayAccesses(statement);

						for (Expression expression : accessedVariables) {
							SimpleName accessedVariable = (SimpleName) expression;
							IBinding binding = accessedVariable
									.resolveBinding();
							if (binding.getKind() == IBinding.VARIABLE) {
								IVariableBinding accessedVariableBinding = (IVariableBinding) binding;
								if (accessedVariableBinding.isField()
										&& verifiedFragment
												.resolveBinding()
												.isEqualTo(
														accessedVariableBinding)) {
									useFragment++;
								}
							}
						}
						for (Expression expression : arrayAccesses) {
							ArrayAccess arrayAccess = (ArrayAccess) expression;
							Expression arrayExpression = arrayAccess.getArray();
							SimpleName arrayVariable = null;
							if (arrayExpression instanceof SimpleName) {
								arrayVariable = (SimpleName) arrayExpression;
							} else if (arrayExpression instanceof FieldAccess) {
								FieldAccess fieldAccess = (FieldAccess) arrayExpression;
								arrayVariable = fieldAccess.getName();
							}
							if (arrayVariable != null) {
								IBinding arrayBinding = arrayVariable
										.resolveBinding();
								if (arrayBinding.getKind() == IBinding.VARIABLE) {
									IVariableBinding arrayVariableBinding = (IVariableBinding) arrayBinding;
									if (arrayVariableBinding.isField()
											&& verifiedFragment
													.resolveBinding()
													.isEqualTo(
															arrayVariableBinding)) {
										useFragment++;
									}
								}
							}
						}
					}

				}

			}
		}

		return useFragment;
	}

	public int useParameter(VariableDeclaration verifiedParameter, MethodDeclaration methodDeclaration, Project project) {

		int useParameter = 0;

		ExpressionExtractor expressionExtractor = new ExpressionExtractor();
		Block methodBody = methodDeclaration.getBody();
		if (methodBody != null) {
			List<Statement> statements = methodBody.statements();
			for (Statement statement : statements) {

				List<Expression> accessedVariables = expressionExtractor.getVariableInstructions(statement);
				List<Expression> arrayAccesses = expressionExtractor.getArrayAccesses(statement);

				for (Expression expression : accessedVariables) {
					SimpleName accessedVariable = (SimpleName) expression;
					IBinding binding = accessedVariable.resolveBinding();
					if (binding.getKind() == IBinding.VARIABLE) {
						IVariableBinding accessedVariableBinding = (IVariableBinding) binding;
						if (accessedVariableBinding.isField() && 
							verifiedParameter.resolveBinding().isEqualTo(accessedVariableBinding)) {
							useParameter++;
						}
					}
				}
				for (Expression expression : arrayAccesses) {
					ArrayAccess arrayAccess = (ArrayAccess) expression;
					Expression arrayExpression = arrayAccess.getArray();
					SimpleName arrayVariable = null;
					if (arrayExpression instanceof SimpleName) {
						arrayVariable = (SimpleName) arrayExpression;
					} else if (arrayExpression instanceof FieldAccess) {
						FieldAccess fieldAccess = (FieldAccess) arrayExpression;
						arrayVariable = fieldAccess.getName();
					}
					if (arrayVariable != null) {
						IBinding arrayBinding = arrayVariable.resolveBinding();
						if (arrayBinding.getKind() == IBinding.VARIABLE) {
							IVariableBinding arrayVariableBinding = (IVariableBinding) arrayBinding;
							if (arrayVariableBinding.isField() && 
								verifiedParameter.resolveBinding().isEqualTo(arrayVariableBinding)) {
								useParameter++;
							}
						}
					}
				}
			}

		}

		return useParameter;
	}

	public List<DuplicatedFragments> retrieveDuplicatedFragments(Project project) {
		List<Clazz> clazzs = project.getClasses();

		List<DuplicatedFragments> duplicatedFragments = new ArrayList<DuplicatedFragments>();

		for (int i = 0; i < clazzs.size(); i++) {

			Clazz c1 = clazzs.get(i);
			TypeDeclaration td1 = c1.getTypeDeclaration();

			for (int j = i + 1; j < clazzs.size(); j++) {

				DuplicatedFragments description = new DuplicatedFragments();

				Clazz c2 = clazzs.get(j);
				TypeDeclaration td2 = c2.getTypeDeclaration();

				for (int f = 0; f < td1.getFields().length; f++) {

					FieldDeclaration field = td1.getFields()[f];

					for (int d = 0; d < td2.getFields().length; d++) {

						FieldDeclaration field1 = td2.getFields()[d];

						if (field.getType().resolveBinding()
								.isEqualTo(field1.getType().resolveBinding())) {

							List<VariableDeclarationFragment> fieldFragments = field
									.fragments();
							List<VariableDeclarationFragment> field1Fragments = field1
									.fragments();

							for (VariableDeclarationFragment frag : fieldFragments) {
								for (VariableDeclarationFragment fragment : field1Fragments) {
									if (frag.getInitializer() != null
											&& fragment.getInitializer() != null) {
										if (frag.getName()
												.getFullyQualifiedName()
												.equals(fragment
														.getName()
														.getFullyQualifiedName())
												&& frag.getInitializer()
														.toString()
														.equalsIgnoreCase(
																(fragment
																		.getInitializer()
																		.toString()))) {

											TypeDeclaration typeDeclaration = ParseUtil
													.getTypeDeclaration((CompilationUnit) frag
															.getRoot());
											Clazz clazz = ParseUtil.getClazz(
													typeDeclaration,
													project.getClasses());

											description
													.addFragment((VariableDeclarationFragment) frag);
											description.addClazz(clazz);

											typeDeclaration = ParseUtil
													.getTypeDeclaration((CompilationUnit) fragment
															.getRoot());
											clazz = ParseUtil.getClazz(
													typeDeclaration,
													project.getClasses());

											// description.addFragment((VariableDeclarationFragment)fragment);
											description.addClazz(clazz);
										}
									}
									if (frag.getInitializer() == null
											&& fragment.getInitializer() == null) {
										if (frag.getName()
												.getFullyQualifiedName()
												.equals(fragment
														.getName()
														.getFullyQualifiedName())) {
											TypeDeclaration typeDeclaration = ParseUtil
													.getTypeDeclaration((CompilationUnit) frag
															.getRoot());
											Clazz clazz = ParseUtil.getClazz(
													typeDeclaration,
													project.getClasses());

											description
													.addFragment((VariableDeclarationFragment) frag);
											description.addClazz(clazz);

											typeDeclaration = ParseUtil
													.getTypeDeclaration((CompilationUnit) fragment
															.getRoot());
											clazz = ParseUtil.getClazz(
													typeDeclaration,
													project.getClasses());

											// description.addFragment((VariableDeclarationFragment)fragment);
											description.addClazz(clazz);
										}
									}

								}
							}
						}
					}
				}
				if (description.getFragments().size() > 0) {

					duplicatedFragments.add(description);
				}
			}

		}

		return review(duplicatedFragments);
	}

	public List<DuplicatedFragments> review(
			List<DuplicatedFragments> duplicatedFragments) {

		duplicatedFragments = unifier(duplicatedFragments);
		duplicatedFragments = removeDuplicatinos(duplicatedFragments);
		duplicatedFragments = removeSubSet(duplicatedFragments);

		return duplicatedFragments;
	}

	private List<DuplicatedFragments> unifier(
			List<DuplicatedFragments> duplicatedFragments) {

		for (int i = 0; i < duplicatedFragments.size(); i++) {
			List<Clazz> iClasses = duplicatedFragments.get(i).getClasses();
			List<VariableDeclarationFragment> iFrags = duplicatedFragments.get(
					i).getFragments();

			for (int j = i + 1; j < duplicatedFragments.size(); j++) {
				List<Clazz> jClasses = duplicatedFragments.get(j).getClasses();
				List<VariableDeclarationFragment> jFrags = duplicatedFragments
						.get(j).getFragments();

				if (similar(iFrags, jFrags)) {

					/*
					 * List<MethodDeclaration> notContainMethods =
					 * notContainMethods(jDuplicatedMethods,
					 * iDuplicatedMethods);
					 * 
					 * 
					 * for (MethodDeclaration method : notContainMethods) {
					 * duplicatedFragments.get(i).addDuplicatedMethod(method); }
					 */

					List<Clazz> notContainClasses = notContainClasses(jClasses,
							iClasses);

					for (Clazz clazz : notContainClasses) {
						duplicatedFragments.get(i).addClazz(clazz);
					}

				}
			}

		}

		return duplicatedFragments;
	}

	private boolean similar(List<VariableDeclarationFragment> fragments,
			List<VariableDeclarationFragment> fragments1) {
		for (VariableDeclarationFragment frag : fragments) {
			if (!existFragment(frag, fragments1)) {
				return false;
			}
		}
		for (VariableDeclarationFragment frag : fragments1) {
			if (!existFragment(frag, fragments)) {
				return false;
			}
		}

		if (fragments.size() != fragments1.size()) {
			return false;
		}
		return true;
	}

	private List<Clazz> notContainClasses(List<Clazz> cs, List<Clazz> cs2) {

		List<Clazz> clazzs = new ArrayList<Clazz>();

		for (Clazz clazz : cs) {

			boolean contain = false;

			for (Clazz classe2 : cs2) {
				if (clazz
						.getTypeDeclaration()
						.getName()
						.getFullyQualifiedName()
						.equalsIgnoreCase(
								classe2.getTypeDeclaration().getName()
										.getFullyQualifiedName())) {
					contain = true;
				}
			}

			if (!contain) {
				clazzs.add(clazz);
			}
		}

		return clazzs;
	}

	private List<DuplicatedFragments> removeDuplicatinos(
			List<DuplicatedFragments> duplicatedFragments) {
		List<DuplicatedFragments> reviewed = copy(duplicatedFragments);
		List<DuplicatedFragments> removed = new ArrayList<DuplicatedFragments>();

		for (int i = 0; i < duplicatedFragments.size(); i++) {
			for (int j = i + 1; j < duplicatedFragments.size(); j++) {

				if (duplicatedFragments.get(i).equals(
						duplicatedFragments.get(j))) {
					reviewed.remove(duplicatedFragments.get(j));
					if (!removed.contains(duplicatedFragments.get(j))) {
						removed.add(duplicatedFragments.get(j));
					}
				}
			}
		}

		for (DuplicatedFragments dp : removed) {
			reviewed.add(dp);
		}

		return reviewed;
	}

	private List<DuplicatedFragments> removeSubSet(
			List<DuplicatedFragments> duplicatedParameters) {

		List<DuplicatedFragments> reviewed = copy(duplicatedParameters);

		for (int i = 0; i < duplicatedParameters.size(); i++) {
			for (int j = i + 1; j < duplicatedParameters.size(); j++) {
				if (isSub(duplicatedParameters.get(i),
						duplicatedParameters.get(j))) {
					reviewed.remove(duplicatedParameters.get(j));
				} else {
					if (isSub(duplicatedParameters.get(j),
							duplicatedParameters.get(i))) {
						reviewed.remove(duplicatedParameters.get(i));
					}
				}
			}
		}

		return reviewed;
	}

	private List<DuplicatedFragments> copy(
			List<DuplicatedFragments> duplicatedParameters) {
		List<DuplicatedFragments> copy = new ArrayList<DuplicatedFragments>();

		for (DuplicatedFragments dp : duplicatedParameters) {
			copy.add(dp);
		}

		return copy;
	}

	private boolean isSub(DuplicatedFragments dcs1, DuplicatedFragments dcs2) {

		for (VariableDeclarationFragment dfrag : dcs1.getFragments()) {
			if (!existFragment(dfrag, dcs2.getFragments())) {
				return false;
			}
		}

		for (VariableDeclarationFragment dfrag : dcs2.getFragments()) {
			if (!existFragment(dfrag, dcs1.getFragments())) {
				return false;
			}
		}

		for (Clazz clazz : dcs2.getClasses()) {
			boolean exist = false;
			for (Clazz c : dcs1.getClasses()) {
				if (clazz
						.getTypeDeclaration()
						.getName()
						.getFullyQualifiedName()
						.equalsIgnoreCase(
								clazz.getTypeDeclaration().getName()
										.getFullyQualifiedName())) {
					exist = true;
				}
			}

			if (!exist) {
				return false;
			}
		}

		return true;
	}

	private boolean existFragment(VariableDeclarationFragment fragment,
			List<VariableDeclarationFragment> fragments) {
		for (VariableDeclarationFragment frag : fragments) {
			if (frag.getInitializer() != null
					&& fragment.getInitializer() != null) {
				if (frag.getName().getFullyQualifiedName()
						.equals(fragment.getName().getFullyQualifiedName())
						&& frag.getInitializer()
								.toString()
								.equalsIgnoreCase(
										(fragment.getInitializer().toString()))) {
					return true;
				}
			}
			if (frag.getInitializer() == null
					&& fragment.getInitializer() == null) {
				if (frag.getName().getFullyQualifiedName()
						.equals(fragment.getName().getFullyQualifiedName())) {
					TypeDeclaration typeDeclaration = ParseUtil
							.getTypeDeclaration((CompilationUnit) frag
									.getRoot());
					return true;
				}
			}
		}
		return false;
	}

	public List<DuplicatedStatements> retrieveDuplicatedDifferentClassStatements(
			Project project) {

		List<DuplicatedStatements> blockList = new ArrayList<DuplicatedStatements>();

		DuplicatedStatements duplicatedStatements = new DuplicatedStatements();

		for (int i = 0; i < project.getClasses().size(); i++) {

			Clazz c1 = project.getClasses().get(i);

			MethodDeclaration[] ms1 = c1.getTypeDeclaration().getMethods();

			for (int j = i + 1; j < project.getClasses().size(); j++) {

				Clazz c2 = project.getClasses().get(j);

				MethodDeclaration[] ms2 = c2.getTypeDeclaration().getMethods();

				for (MethodDeclaration m1 : ms1) {

					for (MethodDeclaration m2 : ms2) {

						StatementsBlock block = new StatementsBlock();

						int k = 0;
						List<Statement> stmts1 = m1.getBody().statements();
						for (Statement stmt1 : stmts1) {
							k++;
							boolean stmtDuplicated = false;
							List<Statement> stmts2 = m2.getBody().statements();
							for (Statement stmt2 : stmts2) {

								if (stmt1.equals(stmt2)
										&& !block.getStatements().contains(
												stmt1)) {

									stmtDuplicated = true;

								}

							}

							if (stmtDuplicated) {
								block.addStatement(stmt1);
							} else {
								if (block.getStatements().size() > 0) {
									/*
									 * if
									 * (!duplicatedStatements.getBlocks().contains
									 * (block)) {
									 * 
									 * duplicatedStatements.addBlock(block); }
									 */
									duplicatedStatements.setBlocks(block);

									duplicatedStatements
											.addDuplicatedMethod(m1);
									duplicatedStatements
											.addDuplicatedMethod(m2);

									duplicatedStatements.addDuplicatedClass(c1);
									duplicatedStatements.addDuplicatedClass(c2);

									blockList.add(duplicatedStatements);

									duplicatedStatements = new DuplicatedStatements();

									block = new StatementsBlock();
								}
							}

							if (block.getStatements().size() > 0
									&& k == m1.getBody().statements().size()) {
								/*
								 * if
								 * (!duplicatedStatements.getBlocks().contains
								 * (block)) {
								 * duplicatedStatements.addBlock(block); }
								 */
								duplicatedStatements.setBlocks(block);

								duplicatedStatements.addDuplicatedMethod(m1);
								duplicatedStatements.addDuplicatedMethod(m2);

								duplicatedStatements.addDuplicatedClass(c1);
								duplicatedStatements.addDuplicatedClass(c2);

								blockList.add(duplicatedStatements);

								duplicatedStatements = new DuplicatedStatements();

							}

						}

					}

				}

			}
		}

		return blockList;
	}
}

package br.ic.ufal.refactoring.detections.duplication.clazz;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.parser.Project;
import br.ic.ufal.refactoring.detections.BadSmell;

public class ClazzDuplication extends BadSmell {

	private List<DuplicatedStatements> duplicatedStatements = new ArrayList<DuplicatedStatements>();

	public ClazzDuplication(Project project) {
		super(project);
	}

	@Override
	public boolean check() {

		this.duplicatedStatements = retrieveDuplicatedParameters(getProject());
		this.duplicatedStatements = review(duplicatedStatements);

		return this.duplicatedStatements.size() > 0;
	}

	public List<DuplicatedStatements> retrieveDuplicatedParameters(Project project) {

		List<DuplicatedStatements> duplicatedStatementsList = new ArrayList<DuplicatedStatements>();

		DuplicatedStatements duplicatedStatements = new DuplicatedStatements();

		for (Clazz c1 : project.getClasses()) {

			MethodDeclaration ms1[] = c1.getTypeDeclaration().getMethods();

			MethodDeclaration ms2[] = c1.getTypeDeclaration().getMethods();
			
			

			for (MethodDeclaration m1 : ms1) {

				List<Statement> m1Statements = m1.getBody().statements();
				
				System.out.println("Method 1: " + m1.getName());

				for (MethodDeclaration m2 : ms2) {
					
					List<Statement> m2Statements = m2.getBody().statements();

					if (!m1.getName().getFullyQualifiedName().equalsIgnoreCase(m2.getName().getFullyQualifiedName())) {
						
						StatementsBlock block = new StatementsBlock();
						
						int aux = -1;
						
						for (int i = 0; i < m1Statements.size(); i++) {
							
							
							int stmt = existStatement(m1Statements.get(i), m2Statements);
							if (stmt != -1) {
								if (aux == -1 || aux == stmt -1) {
									block.addStatement(m1Statements.get(i));
									aux = stmt;
								}
							}
							
							if ( (block.getStatementsBlock().size() > 0 && stmt == -1) ||
								 (block.getStatementsBlock().size() > 0 && i == m1Statements.size() -1) ) {

								duplicatedStatements.setBlock(block);
								duplicatedStatements.addDuplicatedMethod(m1);
								duplicatedStatements.addDuplicatedMethod(m2);
								
								duplicatedStatementsList.add(duplicatedStatements);
								
								duplicatedStatements.addDuplicatedClass(c1);

								duplicatedStatements = new DuplicatedStatements();

								block = new StatementsBlock();
								aux = -1;
							}
						}
					}
				}
			}
		}

		return duplicatedStatementsList;
	}
	
	private int existStatement(Statement statement, List<Statement> statements){
		
		int position = -1;
		
		for (int j = 0; j < statements.size(); j++) {
			
			if (statement.getNodeType() == statements.get(j).getNodeType() && 
				statement.toString().equalsIgnoreCase(statements.get(j).toString())) {
			
				return j;
			}
			
		}
		
		return position;
	}

	public List<DuplicatedStatements> review(List<DuplicatedStatements> duplicatedStatements) {

		duplicatedStatements = unifier(duplicatedStatements);
		duplicatedStatements = removeDuplicatinos(duplicatedStatements);
		duplicatedStatements = removeSubSet(duplicatedStatements);

		return duplicatedStatements;
	}

	private List<DuplicatedStatements> unifier(List<DuplicatedStatements> duplicatedStatements) {

		for (int i = 0; i < duplicatedStatements.size(); i++) {
			List<MethodDeclaration> iDuplicatedMethods = duplicatedStatements.get(i).getDuplicatedMethods();
			StatementsBlock iStatementsBlock = duplicatedStatements.get(i).getBlock();

			for (int j = i + 1; j < duplicatedStatements.size(); j++) {
				List<MethodDeclaration> jDuplicatedMethods = duplicatedStatements.get(j).getDuplicatedMethods();
				StatementsBlock jStatementsBlock = duplicatedStatements.get(j).getBlock();

				if (iStatementsBlock.equals(jStatementsBlock)) {

					List<MethodDeclaration> notContainMethods = notContainMethods(jDuplicatedMethods, iDuplicatedMethods);

					for (MethodDeclaration method : notContainMethods) {
						duplicatedStatements.get(i).addDuplicatedMethod(method);
					}

				}
			}

		}

		return duplicatedStatements;
	}

	private List<MethodDeclaration> notContainMethods(
			List<MethodDeclaration> ms, List<MethodDeclaration> ms2) {

		List<MethodDeclaration> methods = new ArrayList<MethodDeclaration>();

		for (MethodDeclaration method : ms) {

			boolean contain = false;

			for (MethodDeclaration method2 : ms2) {
				/*
				 * if (method.getName().equalsIgnoreCase(method2.getName())) {
				 * contain = true; }
				 */
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

	private List<DuplicatedStatements> removeDuplicatinos(List<DuplicatedStatements> duplicatedStatements) {
		List<DuplicatedStatements> reviewed = copy(duplicatedStatements);
		List<DuplicatedStatements> removed = new ArrayList<DuplicatedStatements>();

		for (int i = 0; i < duplicatedStatements.size(); i++) {
			for (int j = i + 1; j < duplicatedStatements.size(); j++) {

				if (duplicatedStatements.get(i).equals(duplicatedStatements.get(j))) {
					reviewed.remove(duplicatedStatements.get(j));
					if (!removed.contains(duplicatedStatements.get(j))) {
						removed.add(duplicatedStatements.get(j));
					}
				}
			}
		}

		for (DuplicatedStatements dp : removed) {
			reviewed.add(dp);
		}

		return reviewed;
	}

	private List<DuplicatedStatements> removeSubSet(List<DuplicatedStatements> duplicatedParameters) {

		List<DuplicatedStatements> reviewed = copy(duplicatedParameters);

		for (int i = 0; i < duplicatedParameters.size(); i++) {
			for (int j = i + 1; j < duplicatedParameters.size(); j++) {
				if (isSub(duplicatedParameters.get(i),duplicatedParameters.get(j))) {
					reviewed.remove(duplicatedParameters.get(j));
				} else {
					if (isSub(duplicatedParameters.get(j),duplicatedParameters.get(i))) {
						reviewed.remove(duplicatedParameters.get(i));
					}
				}
			}
		}

		return reviewed;
	}

	private List<DuplicatedStatements> copy(List<DuplicatedStatements> duplicatedParameters) {
		List<DuplicatedStatements> copy = new ArrayList<DuplicatedStatements>();

		for (DuplicatedStatements dp : duplicatedParameters) {
			copy.add(dp);
		}

		return copy;
	}

	private boolean isSub(DuplicatedStatements dcs1, DuplicatedStatements dcs2) {

		if (!dcs1.getBlock().equals(dcs2.getBlock())) {
			return false;
		}

		for (MethodDeclaration method : dcs2.getDuplicatedMethods()) {
			boolean exist = false;
			for (MethodDeclaration m : dcs1.getDuplicatedMethods()) {
				if (method.getName().getFullyQualifiedName()
						.equalsIgnoreCase(m.getName().getFullyQualifiedName())) {
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

	public List<DuplicatedStatements> getDuplicatedStatements() {
		return duplicatedStatements;
	}
}

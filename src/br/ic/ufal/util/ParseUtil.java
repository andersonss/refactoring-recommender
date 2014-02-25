package br.ic.ufal.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jface.text.Document;

import br.ic.ufal.parser.Clazz;
import br.ic.ufal.parser.Project;

public class ParseUtil {

	/**
	 * 
	 */
	public ParseUtil() {

	}

	/**
	 * 
	 * @param unit
	 * @param proj
	 */
	public static void addClazz(ICompilationUnit unit, Project proj) {
		Clazz clazz = new Clazz();
		clazz.setICompilationUnit(unit);
		CompilationUnit compilationUnit = ParseUtil.toCompilationUnit(unit);
		clazz.setCompilationUnit(compilationUnit);

		TypeDeclaration typeDeclaration = ParseUtil
				.getTypeDeclaration(compilationUnit);
		if (typeDeclaration != null) {
			clazz.setTypeDeclaration(typeDeclaration);
		}

		Document document = null;

		try {
			document = new Document(unit.getBuffer().getContents());
			clazz.setDocument(document);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}

		if (typeDeclaration != null) {
			proj.addClazz(clazz);
		}
	}

	/**
	 * 
	 * @param typeDeclaration
	 * @param fragments
	 * @return
	 */
	public static Set<VariableDeclaration> getFragments(
			TypeDeclaration typeDeclaration, Set<VariableDeclaration> fragments) {
		Set<VariableDeclaration> newfrags = new HashSet<VariableDeclaration>();

		for (FieldDeclaration variableDeclaration : typeDeclaration.getFields()) {
			List<VariableDeclaration> frags = variableDeclaration.fragments();
			for (VariableDeclaration frag : frags) {
				for (VariableDeclaration fg : fragments) {
					if (frag.getInitializer() != null
							&& fg.getInitializer() != null) {
						if (frag.getName().toString()
								.equalsIgnoreCase(fg.getName().toString())
								&& frag.getInitializer()
										.toString()
										.equalsIgnoreCase(
												fg.getInitializer().toString())) {
							newfrags.add(frag);
						}
					} else {
						if (frag.getName().toString()
								.equalsIgnoreCase(fg.getName().toString())) {
							newfrags.add(frag);
						}
					}

				}
			}
		}

		return newfrags;
	}

	/**
	 * 
	 * @param iCompilationUnit
	 * @param proj
	 * @return
	 */
	public static IPackageFragment getPackageFrament(
			ICompilationUnit iCompilationUnit, Project proj) {

		for (IPackageFragment iPackageFragment : proj.getPackagesFragments()) {
			ICompilationUnit[] iCompilationUnits = null;
			try {
				iCompilationUnits = iPackageFragment.getCompilationUnits();
				for (ICompilationUnit ic : iCompilationUnits) {
					if (ic.getElementName().equals(
							iCompilationUnit.getElementName())
							&& ic.getPath().equals(iCompilationUnit.getPath())) {
						return iPackageFragment;
					}
				}
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return null;
	}

	/**
	 * 
	 * @param document
	 * @param clazz
	 * @param proj
	 * @throws JavaModelException
	 */
	public static void updateClazz(Document document, Clazz clazz, Project proj)
			throws JavaModelException {
		for (Clazz c : proj.getClasses()) {
			if (c.getTypeDeclaration()
					.getName()
					.getIdentifier()
					.equalsIgnoreCase(
							clazz.getTypeDeclaration().getName()
									.getIdentifier())) {
				c.setDocument(document);
				c.getICompilationUnit().getBuffer()
						.setContents(c.getDocument().get());
				c.setCompilationUnit(toCompilationUnit(c.getICompilationUnit()));
				c.setTypeDeclaration(getTypeDeclaration(c.getCompilationUnit()));
			}
		}
	}

	/**
	 * 
	 * @param unit
	 * @return
	 */
	public static CompilationUnit toCompilationUnit(ICompilationUnit unit) {

		System.out.println(unit.getElementName());
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);

		return (CompilationUnit) parser.createAST(null);
	}

	/**
	 * @param iTypeBinding
	 * @param classes
	 * @return
	 */
	public static Clazz getClazz(ITypeBinding iTypeBinding, List<Clazz> classes) {

		for (Clazz clazz : classes) {
			if (clazz.getTypeDeclaration() != null) {
				if (clazz.getTypeDeclaration().resolveBinding()
						.isEqualTo(iTypeBinding)) {
					return clazz;
				}
			}

		}

		return null;
	}

	/**
	 * @param iTypeBinding
	 * @param classes
	 * @return
	 */
	public static TypeDeclaration getTypeDeclaration(ITypeBinding iTypeBinding,
			List<Clazz> classes) {

		for (Clazz clazz : classes) {
			if (clazz.getTypeDeclaration() != null) {
				if (clazz.getTypeDeclaration().resolveBinding()
						.isEqualTo(iTypeBinding)) {
					return clazz.getTypeDeclaration();
				}
			}

		}

		return null;
	}

	/**
	 * @param c
	 * @param classes
	 * @return
	 */
	public static Clazz getClazz(Class c, List<Clazz> classes) {

		for (Clazz clazz : classes) {
			if (clazz.getTypeDeclaration() != null) {
				if (clazz.getTypeDeclaration().getClass().equals(c)) {
					return clazz;
				}
			}

		}

		return null;
	}

	/**
	 * @param compilationUnit
	 * @param classes
	 * @return
	 */
	public static Clazz getClazz(CompilationUnit compilationUnit,
			List<Clazz> classes) {

		TypeDeclaration typeDeclaration = getTypeDeclaration(compilationUnit);

		for (Clazz clazz : classes) {
			if (typeDeclaration.resolveBinding().isEqualTo(
					clazz.getTypeDeclaration().resolveBinding())) {
				return clazz;
			}
		}
		return null;
	}

	/**
	 * @param typeDeclaration
	 * @param classes
	 * @return
	 */
	public static Clazz getClazz(TypeDeclaration typeDeclaration,
			List<Clazz> classes) {

		for (Clazz clazz : classes) {
			if (typeDeclaration.resolveBinding().isEqualTo(
					clazz.getTypeDeclaration().resolveBinding())) {
				return clazz;
			}
		}
		return null;
	}

	/**
	 * @param type
	 * @param classes
	 * @return
	 */
	public static Clazz getClazz(Type type, List<Clazz> classes) {

		for (Clazz clazz : classes) {
			if (type.resolveBinding().isEqualTo(
					clazz.getTypeDeclaration().resolveBinding())) {
				return clazz;
			}
		}
		return null;
	}

	/**
	 * @param compilationUnit
	 * @return
	 */
	public static TypeDeclaration getTypeDeclaration(
			CompilationUnit compilationUnit) {

		List<AbstractTypeDeclaration> types = compilationUnit.types();

		for (AbstractTypeDeclaration abstracttype : types) {

			if (abstracttype instanceof TypeDeclaration) {
				TypeDeclaration typedecl = (TypeDeclaration) abstracttype;

				return typedecl;
			}
		}

		return null;

	}

	/**
	 * @param iCompilationUnit
	 * @return
	 */
	public static Document getDocument(ICompilationUnit iCompilationUnit) {
		Document sourceDocument = null;

		try {
			sourceDocument = new Document(iCompilationUnit.getBuffer()
					.getContents());
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sourceDocument;
	}
}

package br.ic.ufal.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.Statement;

public class StatementsBlock {

	
	private List<Statement> statements = new ArrayList<Statement>();
	
	public StatementsBlock() {
		// TODO Auto-generated constructor stub
	}
	
	public List<Statement> getStatements() {
		return statements;
	}
	
	public void addStatement(Statement statement){
		this.statements.add(statement);
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		
		StatementsBlock block = (StatementsBlock) obj;
		
		for (Statement stmt : block.getStatements()) {
			if (!existStatement(stmt)) {
				return false;
			}
		}
		
		if (this.statements.size() != block.getStatements().size()) {
			return false;
		}
		
		return true;
	}
	
	private boolean existStatement(Statement stmt){
		for (Statement st : this.statements) {
			if (st.toString().equalsIgnoreCase(stmt.toString())) {
				return true;
			}
			
		}
		return false;
	}

	@Override
	public String toString() {
		return "StatementsBlock [statements=" + statements + "]";
	}
}

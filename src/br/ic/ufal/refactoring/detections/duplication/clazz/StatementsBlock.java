package br.ic.ufal.refactoring.detections.duplication.clazz;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;

public class StatementsBlock {
	
	private List<Statement> statementsBlock = new ArrayList<Statement>();
	
	public StatementsBlock() {
		// TODO Auto-generated constructor stub
	}

	public List<Statement> getStatementsBlock() {
		return statementsBlock;
	}

	public void setStatementsBlock(List<Statement> statementsBlock) {
		this.statementsBlock = statementsBlock;
	}

	public void addStatement(Statement statement){
		this.statementsBlock.add(statement);
	}

	@Override
	public boolean equals(Object obj) {
		
		StatementsBlock block = (StatementsBlock) obj;
		
		for (Statement statement : block.getStatementsBlock()) {
			if (!existStatement(statement)) {
				return false;
			}
		}
		
		if (this.statementsBlock.size() != block.getStatementsBlock().size()) {
			return false;
		}
		
		return true;
	}
	
	private boolean existStatement(Statement statement){
		for (Statement s : this.statementsBlock) {
			if (statement.getNodeType() == s.getNodeType() &&
				statement.toString().equalsIgnoreCase(s.toString()))  {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "StatementsBlock [statements=" + statementsBlock + "]";
	}
}

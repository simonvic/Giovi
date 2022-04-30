package it.simonvic.giovi.lang;

import it.simonvic.giovi.Variable;
import it.simonvic.giovi.exceptions.VariableAlreadyDefinedException;
import it.simonvic.giovi.exceptions.VariableNotDefinedException;

public class Block {


	private final Variables variables = new Variables();
	private final Block parentBlock;

	public Block(Block parentBlock) {
		this.parentBlock = parentBlock;
	}

	public boolean isVariableDefined(String identifier) {
		return isVariableDefined(this, identifier);
	}

	public Object getVariableValue(String identifier) {
		return getVariableValue(this, identifier);
	}

	public void updateVariableValue(String identifier, Object value) {
		updateVariableValue(this, identifier, value);
	}

	public void declareVariable(String identifier, Object value) {
		if (isVariableDefined(this.parentBlock, identifier)) {
			throw new VariableAlreadyDefinedException(identifier);
		}
		variables.declare(new Variable<>(identifier, value));
	}

	private static boolean isVariableDefined(Block block, String identifier) {
		if (block == null) return false;
		return block.variables.isDefined(identifier) || isVariableDefined(block.parentBlock, identifier);
	}

	private static Object getVariableValue(Block block, String identifier) {
		if (block == null) {
			throw new VariableNotDefinedException(identifier);
		}
		try {
			return block.variables.getValue(identifier);
		} catch (VariableNotDefinedException ex) {
			return getVariableValue(block.parentBlock, identifier);
		}
	}

	private static void updateVariableValue(Block block, String identifier, Object value) {
		if (block == null) {
			throw new VariableNotDefinedException(identifier);
		}

		try {
			block.variables.update(identifier, value);
		} catch (VariableNotDefinedException ex) {
			updateVariableValue(block.parentBlock, identifier, value);
		}
	}

}

package it.simonvic.giovi.lang;

import it.simonvic.giovi.exceptions.VariableAlreadyDefinedException;
import it.simonvic.giovi.exceptions.VariableNotDefinedException;
import it.simonvic.giovi.lang.values.*;

import java.util.HashMap;

public class Block {


	private final HashMap<String, GioviValue<?>> variables = new HashMap<>();

	private final Block parentBlock;

	public Block(Block parentBlock) {
		this.parentBlock = parentBlock;
	}

	public Block getParentBlock() {
		return parentBlock;
	}

	public void delete(String identifier) {
		variables.remove(identifier);
	}

	public void declare(String identifier, GioviValue<?> value) {
		if (isDefinedVariable(identifier)) throw new VariableAlreadyDefinedException(identifier);
		variables.put(identifier, value);
	}

	public void setVariableValue(String identifier, GioviValue<?> value) {
		Block.setVariableValue(this, identifier, value);
	}

	private static void setVariableValue(Block block, String identifier, GioviValue<?> value) {
		if (block == null) throw new VariableNotDefinedException(identifier);
		if (block.isDefinedLocalVariable(identifier)) {
			block.variables.get(identifier).set(value);
		} else {
			Block.setVariableValue(block.parentBlock, identifier, value);
		}
	}

	public GioviValue<?> getVariableValue(String identifier) {
		return Block.getVariableValue(this, identifier);
	}

	public boolean isDefinedVariable(String identifier) {
		return Block.isDefinedVariable(this, identifier);
	}


	private GioviValue<?> getLocalVariableValue(String identifier) {
		return variables.get(identifier);
	}

	private boolean isDefinedLocalVariable(String identifier) {
		return variables.containsKey(identifier);
	}

	private static boolean isDefinedVariable(Block block, String identifier) {
		if (block == null) return false;
		return block.isDefinedLocalVariable(identifier) || Block.isDefinedVariable(block.parentBlock, identifier);
	}

	private static GioviValue<?> getVariableValue(Block block, String identifier) {
		if (block == null) throw new VariableNotDefinedException(identifier);
		return block.isDefinedLocalVariable(identifier)
			? block.getLocalVariableValue(identifier)
			: Block.getVariableValue(block.parentBlock, identifier);

	}

}

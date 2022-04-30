package it.simonvic.giovi.lang;

import it.simonvic.giovi.Variable;
import it.simonvic.giovi.exceptions.VariableAlreadyDefinedException;
import it.simonvic.giovi.exceptions.VariableNotDefinedException;

import java.util.HashMap;

public class Variables {

	private final HashMap<String, Variable<?>> variables = new HashMap<>();


	public boolean isDefined(String identifier) {
		return variables.containsKey(identifier);
	}

	public void declare(Variable<?> variable) {
		String identifier = variable.getIdentifier();
		if (isDefined(identifier)) {
			throw new VariableAlreadyDefinedException(identifier);
		}
		variables.put(identifier, variable);
	}

	public Object getValue(String identifier) {
		if (isDefined(identifier)) {
			return variables.get(identifier).getValue();
		}
		throw new VariableNotDefinedException(identifier);
	}

	public Variable<?> getVariable(String identifier) {
		if (isDefined(identifier)) {
			return variables.get(identifier);
		}
		throw new VariableNotDefinedException(identifier);
	}

	public void update(Variable<?> variable) {
		String identifier = variable.getIdentifier();
		if (!isDefined(identifier)) {
			throw new VariableNotDefinedException(identifier);
		}
		variables.put(identifier, variable);
	}
}

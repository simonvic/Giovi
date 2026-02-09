package it.simonvic.giovi.util;

import giovi.GFunction;
import giovi.lang.GioviFunction;
import giovi.lang.GioviType;
import giovi.lang.GioviTypeFunction;
import it.simonvic.giovi.lang.FormalParameter;
import it.simonvic.giovi.lang.FormalParameters;
import it.simonvic.giovi.lang.FunctionSignature;

public class GioviFunctions {

	public static GioviFunction of(
		GioviType<?> returnType,
		String identifier,
		FormalParameter... formalParameters
	) {
		return new GioviFunction(
			new GFunction(
				new FunctionSignature(
					identifier,
					new FormalParameters(formalParameters),
					returnType
				)
			)
		);
	}

	public static GioviFunction of(
		String identifier,
		FormalParameter... formalParameters
	) {
		return new GioviFunction(
			new GFunction(
				new FunctionSignature(
					identifier,
					new FormalParameters(formalParameters)
				)
			)
		);
	}

	public static GioviTypeFunction typeOf(
		GioviType<?> returnType,
		String identifier,
		FormalParameter... formalParameters
	) {
		return new GioviTypeFunction(
			new FunctionSignature(
				identifier,
				new FormalParameters(formalParameters),
				returnType
			)
		);
	}

	public static GioviTypeFunction typeOf(
		String identifier,
		FormalParameter... formalParameters
	) {
		return new GioviTypeFunction(
			new FunctionSignature(
				identifier,
				new FormalParameters(formalParameters)
			)
		);
	}

}

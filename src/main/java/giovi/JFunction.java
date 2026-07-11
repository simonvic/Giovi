package giovi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;

import giovi.lang.GioviValue;
import giovi.lang.GioviVoid;
import it.simonvic.giovi.VarD;
import it.simonvic.giovi.lang.FunctionSignature;
import it.simonvic.giovi.util.Natives;

public record JFunction(
	// TODO: what if we dynamically fetch the method based on arguments? In case of defaults
	Method method,
	FunctionSignature signature
) implements Function {

	public JFunction(Method body) {
		this(body, Natives.functionSignatureFrom(body));
	}

	public JFunction copy() {
		// TODO: is signature copy necessary?
		return new JFunction(method, signature.copy());
	}

	@Override
	public String toString() {
		// return method.toString() + " {<native>}";
		return signature.toString() + " {" + method.toString() + "}";
	}

	public GioviParserVisitResult invoke(
		LinkedHashMap<String, VarD<?>> actualParams
	) throws IllegalAccessException, InvocationTargetException {
		var args = new GioviValue<?>[actualParams.size()];
		int i = 0;
		for (var actualParam : actualParams.entrySet()) {
			args[i++] = actualParam.getValue().unwrap();
		}
		Object returnValue = method.invoke(null, (Object[]) args);
		if (method.getReturnType().equals(Void.TYPE)) {
			return new GioviVoid();
		}
		return (GioviParserVisitResult) returnValue;
	}

}

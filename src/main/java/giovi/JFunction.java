package giovi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;

import giovi.lang.GioviValue;
import it.simonvic.giovi.VarD;
import it.simonvic.giovi.lang.FunctionSignature;
import it.simonvic.giovi.util.Natives;

public record JFunction(
	Method method,
	FunctionSignature signature
) implements Function {

	public JFunction(Method body) {
		this(body, Natives.functionSignatureFrom(body));
	}

	@Override
	public JFunction copy() {
		return this;
	}

	@Override
	public String toString() {
		// return method.toString() + " {<native>}";
		return signature.toString() + " {" + method.toString() + "}";
	}

	/**
	 * Invoke the native method described by the signature, using positional parameters
	 * @param actualParams
	 * @return return value of the function
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public GioviValue<?> invoke(
		List<VarD<?>> actualParams
	) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		// Method method = Natives.lookupMethod(clazz, signature, actualParams);
		return Natives.invoke(method, actualParams);
	}

	/**
	 * Invoke the native method described by the signature, using named parameters.
	 * @param actualParams
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public GioviValue<?> invoke(
		LinkedHashMap<String, VarD<?>> actualParams
	) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		// Method method = Natives.lookupMethod(clazz, signature, actualParams);
		return Natives.invoke(method, signature, actualParams);
	}

}

package giovi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;

import giovi.lang.*;
import it.simonvic.giovi.lang.FunctionSignature;
import it.simonvic.giovi.lang.FunctionSignatureParam;
import it.simonvic.giovi.lang.annotation.Native;
import it.simonvic.giovi.lang.annotation.Native.Type;

public record JFunction(
	// TODO: remove instance. All GioviFunction are "static".
	// "self" functions should probably be desugared by the visitor
	GioviValue<?> instance,
	Method method,
	FunctionSignature signature)
	implements Function {

	public JFunction(GioviValue<?> instance, Method body) {
		this(instance, body, signatureFrom(body));
	}

	// TODO: not robust enough
	private static FunctionSignature signatureFrom(Method method) {
		String identifier = method.getName();
		GioviType<?> returnType = gioviTypeFrom(method);
		var parameters = new LinkedHashMap<String, FunctionSignatureParam>();
		for (Parameter param : method.getParameters()) {
			var annotation = param.getAnnotation(Native.Func.Param.class);
			if (annotation == null) continue;
			String paramName = annotation.value();
			GioviType<?> paramType = gioviTypeFrom(param);
			parameters.put(paramName, new FunctionSignatureParam(paramName, paramType));
		}
		return new FunctionSignature(identifier, parameters, returnType);
	}

	private static GioviType<?> gioviTypeFrom(Type annotation) {
		Class<? extends GioviType<?>> clazz = annotation.value();
		if (GioviTypePrimitive.class.isAssignableFrom(clazz)) {
			try {
				return clazz.getConstructor().newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			}
		}
		return new GioviTypeVoid();
	}

	private static GioviType<?> gioviTypeFrom(Method method) {
		var typeAnnotation = method.getAnnotation(Native.Type.class);
		if (typeAnnotation == null) {
			return new GioviTypeVoid();
		}
		return gioviTypeFrom(typeAnnotation);
	}

	private static GioviType<?> gioviTypeFrom(Parameter param) {
		var typeAnnotation = param.getAnnotation(Native.Type.class);
		if (typeAnnotation == null) {
			return new GioviTypeVoid();
		}
		return gioviTypeFrom(typeAnnotation);
	}

	public JFunction copy() {
		// TODO: is signature copy necessary?
		return new JFunction(instance, method, signature.copy());
	}

	@Override
	public String toString() {
		// return method.toString() + " {<native>}";
		return signature.toString() + " {" + method.toString() + "}";
	}

	public GioviParserVisitResult invoke(GioviValue<?>... args)
		throws IllegalAccessException, InvocationTargetException {
		Object returnValue = method.invoke(instance, (Object[]) args);
		if (method.getReturnType().equals(Void.TYPE)) {
			return new GioviVoid();
		}
		return (GioviParserVisitResult) returnValue;
	}

}

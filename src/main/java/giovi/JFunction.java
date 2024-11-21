package giovi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;

import giovi.lang.*;
import it.simonvic.giovi.lang.FunctionSignature;
import it.simonvic.giovi.lang.FunctionSignatureParam;
import it.simonvic.giovi.lang.annotation.Native;

public record JFunction(
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
		GioviType<?> returnType = method.getReturnType().isAnnotationPresent(Native.Type.class)
			? gioviTypeFrom(method.getReturnType().getAnnotation(Native.Type.class).value())
			: new GioviTypeVoid();
		var parameters = new LinkedHashMap<String, FunctionSignatureParam>();
		for (Parameter param : method.getParameters()) {
			String paramName;
			GioviType<?> paramType;
			if (param.isAnnotationPresent(Native.Func.Param.class)) {
				var annotation = param.getAnnotation(Native.Func.Param.class);
				paramName = annotation.name();
				paramType = gioviTypeFrom(annotation.type().value());
			} else if (param.isAnnotationPresent(Native.Func.GenericParam.class)) {
				var annotation = param.getAnnotation(Native.Func.GenericParam.class);
				paramName = annotation.name();
				paramType = new GioviTypeVoid();
			} else {
				continue;
			}
			parameters.put(paramName, new FunctionSignatureParam(paramName, paramType));
		}
		return new FunctionSignature(identifier, parameters, returnType);
	}

	private static GioviType<?> gioviTypeFrom(Class<? extends GioviType<?>> clazz) {
		if (GioviTypePrimitive.class.isAssignableFrom(clazz)) {
			try {
				return clazz.getConstructor().newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			}
		}
		return new GioviTypeVoid();
	}

	public JFunction copy() {
		return new JFunction(instance, method, signature.copy());
	}

	@Override
	public String toString() {
		// return method.toString() + " {<native>}";
		return signature.toString() + " {" + method.toString() + "}";
	}

	public GioviParserVisitResult invoke(GioviParserVisitResult... args)
		throws IllegalAccessException, InvocationTargetException {
		Object returnValue = method.invoke(instance, (Object[]) args);
		if (method.getReturnType().equals(Void.TYPE)) {
			return new GioviVoid();
		}
		return (GioviParserVisitResult) returnValue;
	}

}

package it.simonvic.giovi.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;

import giovi.Struct;
import giovi.lang.GioviType;
import giovi.lang.GioviTypePrimitive;
import giovi.lang.GioviTypeVoid;
import it.simonvic.giovi.lang.FunctionSignature;
import it.simonvic.giovi.lang.FunctionSignatureParam;
import it.simonvic.giovi.lang.annotation.Native;
import it.simonvic.giovi.lang.annotation.Native.Type;

public class Natives {

	public static Struct structSignatureFrom(Class<?> clazz) {
		return new Struct("");
	}

	// TODO: not robust enough
	public static FunctionSignature functionSignatureFrom(Method method) {
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


}

package it.simonvic.giovi.util;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.LinkedHashMap;

import giovi.JFunction;
import giovi.Struct;
import giovi.lang.*;
import it.simonvic.giovi.VarD;
import it.simonvic.giovi.lang.FunctionSignature;
import it.simonvic.giovi.lang.FunctionSignatureParam;
import it.simonvic.giovi.lang.annotation.Native;

public class Natives {

	public static Struct structSignatureOf(Class<?> clazz) {
		// Find field marked as signature...
		for (Field field : clazz.getFields()) {
			var annotation = field.getAnnotation(Native.Struct.Signature.class);
			if (annotation != null) {
				try {
					return (Struct) field.get(null);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					continue;
				}
			}
		}
		// ... or build it if not defined
		return Natives.structSignatureFrom(clazz);
	}

	public static Struct structSignatureFrom(Class<?> clazz) {
		String typename = clazz.getAnnotation(Native.Struct.class).typename();
		if (typename.isEmpty()) {
			typename = clazz.getSimpleName();
		}

		// TODO: check if classField/method are static or final?
		var fields = new LinkedHashMap<String, VarD<?>>();
		var constants = new HashMap<String, GioviValue<?>>();
		for (Field classField : clazz.getFields()) {
			if (!Modifier.isStatic(classField.getModifiers())) {
				System.err.println(
					"Non static fields are not supported. Ignoring " + classField);
				continue;
			}
			try {
				var gioviValue = (GioviValue<?>) classField.get(null);
				if (classField.isAnnotationPresent(Native.Struct.Field.class)) {
					fields.put(classField.getName(), new VarD<>(gioviValue, gioviValue.copy()));
				} else if (classField.isAnnotationPresent(Native.Const.class)) {
					constants.put(classField.getName(), gioviValue);
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}

		var functions = new HashMap<String, GioviFunction>();
		for (Method method : clazz.getMethods()) {
			if (method.isAnnotationPresent(Native.Func.class)) {
				functions.put(method.getName(), new GioviFunction(new JFunction(null, method)));
			}
		}

		return new Struct(typename, fields, constants, functions);
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

	private static GioviType<?> gioviTypeFrom(Native.Type annotation) {
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
		if (typeAnnotation != null) {
			return gioviTypeFrom(typeAnnotation);
		}

		var nativeTypeAnnotation = param.getAnnotation(Native.TypeNative.class);
		if (nativeTypeAnnotation != null) {
			return new GioviTypeStruct(structSignatureOf(nativeTypeAnnotation.value()));
		}
		return new GioviTypeVoid();
	}


}

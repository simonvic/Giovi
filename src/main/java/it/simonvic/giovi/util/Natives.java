package it.simonvic.giovi.util;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.LinkedHashMap;

import giovi.JFunction;
import giovi.Module;
import giovi.RecordFieldSignature;
import giovi.StructSignature;
import giovi.lang.*;
import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.lang.FormalParameter;
import it.simonvic.giovi.lang.FormalParameters;
import it.simonvic.giovi.lang.FunctionSignature;
import it.simonvic.giovi.lang.annotation.Native;
import it.simonvic.giovi.lang.annotation.Native.TypeList;
import it.simonvic.giovi.lang.annotation.Native.TypeMap;

public class Natives {

	public static void scanModule(Module module, Class<?> clazz) {
		for (Field classField : clazz.getFields()) {
			if (!Modifier.isStatic(classField.getModifiers())) {
				System.err.println(
					"Non static fields are not supported. Ignoring " + classField);
				continue;
			}
			try {
				// TODO: allow to define a field directly with a Signature
				if (!(classField.get(null) instanceof GioviValue gioviValue)) {
					continue;
				}
				if (classField.isAnnotationPresent(Native.Const.class)) {
					module.scope().declareConstant(classField.getName(), gioviValue);
				}
				// TODO: scan @Variable?
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}

		for (Method method : clazz.getMethods()) {
			if (method.isAnnotationPresent(Native.Func.class)) {
				module.scope().declare(new JFunction(method));
			}
		}

		for (var nestedClass : clazz.getClasses()) {
			if (nestedClass.isAnnotationPresent(Native.Struct.class)) {
				module.scope().define(structTypeOf(nestedClass));
			}
		}
	}

	public static StructSignature structSignatureOf(Class<?> clazz) {
		// Find field marked as signature...
		for (Field field : clazz.getFields()) {
			var annotation = field.getAnnotation(Native.Struct.Signature.class);
			if (annotation != null) {
				try {
					return (StructSignature) field.get(null);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					continue;
				}
			}
		}
		// ... or build it if not defined
		return Natives.structSignatureFrom(clazz);
	}

	public static StructSignature structSignatureFrom(Class<?> clazz) {
		String typename = clazz.getAnnotation(Native.Struct.class).typename();
		if (typename.isEmpty()) {
			typename = clazz.getSimpleName();
		}

		// TODO: check if classField/method are static or final?
		var fields = new LinkedHashMap<String, RecordFieldSignature>();
		var constants = new HashMap<String, GioviValue<?>>();
		for (Field classField : clazz.getFields()) {
			if (!Modifier.isStatic(classField.getModifiers())) {
				System.err.println(
					"Non static fields are not supported. Ignoring " + classField);
				continue;
			}
			try {
				// TODO: allow to define a field directly with a Signature
				if (!(classField.get(null) instanceof GioviValue gioviValue)) {
					continue;
				}
				if (classField.isAnnotationPresent(Native.Struct.Field.class)) {
					fields.put(classField.getName(), new RecordFieldSignature(
						classField.getName(),
						gioviValue.getGioviType(),
						gioviValue.copy()));
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
				functions.put(method.getName(), new GioviFunction(new JFunction(method)));
			}
		}

		return new StructSignature(
			typename,
			fields,
			constants,
			functions);
	}

	// TODO: not robust enough
	public static FunctionSignature functionSignatureFrom(Method method) {
		String identifier = method.getName();
		GioviType<?> returnType = gioviReturnTypeFrom(method);
		var parameters = new LinkedHashMap<String, FormalParameter>();
		for (Parameter param : method.getParameters()) {
			var annotation = param.getAnnotation(Native.Func.Param.class);
			String paramName = (annotation != null && !annotation.value().isBlank())
				? annotation.value()
				: param.getName();

			var labelAnnotation = param.getAnnotation(Native.Func.Param.Label.class);
			String label = (labelAnnotation != null) ? labelAnnotation.value() : paramName;

			GioviType<?> paramType = gioviTypeFrom(param);

			FormalParameter formalParam;
			var optionalAnnotation = param.getAnnotation(Native.Func.Param.Optional.class);
			if (optionalAnnotation == null) {
				formalParam = new FormalParameter(label, paramName, paramType);
			} else {
				GioviValue<?> defaultValue;
				String defaultMethodSource = optionalAnnotation.value();
				if (defaultMethodSource.isEmpty()) {
					defaultValue = paramType.spawn(); // Type's default
				} else {
					try {
						defaultValue = (GioviValue<?>) method
							.getDeclaringClass()
							.getMethod(defaultMethodSource)
							.invoke(null);
						defaultValue = paramType.widen(defaultValue);
					} catch (
						IllegalAccessException
						| InvocationTargetException
						| NoSuchMethodException
						| SecurityException
						| NonWideneable e) {
						throw new RuntimeException(
							"Can't invoke parameter default value method source", e);
					}
				}
				formalParam = new FormalParameter(paramName, paramType, defaultValue);
			}

			parameters.put(paramName, formalParam);
		}
		return new FunctionSignature(identifier, new FormalParameters(parameters), returnType);
	}

	private static GioviType<?> gioviTypeFrom(Native.Type annotation) {
		Class<? extends GioviTypePrimitive<?>> clazz = annotation.value();
		if (clazz != GioviTypeVoid.class) {
			try {
				return clazz.getConstructor().newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				throw new RuntimeException(e);
			}
		}
		return new GioviTypeStruct(structSignatureOf(annotation.nativ()));
	}

	private static GioviType<?> gioviTypeFrom(TypeList typeListAnnotation) {
		return new GioviTypeList(gioviTypeFrom(typeListAnnotation.value()));
	}

	private static GioviType<?> gioviTypeFrom(TypeMap typeMapAnnotation) {
		return new GioviTypeMap(
			gioviTypeFrom(typeMapAnnotation.key()),
			gioviTypeFrom(typeMapAnnotation.val()));
	}

	private static GioviType<?> gioviReturnTypeFrom(Method method) {
		if (method.getReturnType().equals(Void.TYPE)) {
			return new GioviTypeVoid();
		}

		AnnotatedType annotatedReturnType = method.getAnnotatedReturnType();

		var typeListAnnotation = annotatedReturnType.getAnnotation(TypeList.class);
		if (typeListAnnotation != null) {
			return gioviTypeFrom(typeListAnnotation);
		}

		var typeMapAnnotation = annotatedReturnType.getAnnotation(TypeMap.class);
		if (typeMapAnnotation != null) {
			return gioviTypeFrom(typeMapAnnotation);
		}

		// NOTE: getAnnotation(Type) will return the nested Type. E.g. TypeList(Type). So we do this last
		var typeAnnotation = annotatedReturnType.getAnnotation(Native.Type.class);
		if (typeAnnotation != null) {
			return gioviTypeFrom(typeAnnotation);
		}

		throw new GioviException("Can't find type for return type " + method);
	}

	private static GioviType<?> gioviTypeFrom(Parameter param) {
		var typeListAnnotation = param.getAnnotation(Native.TypeList.class);
		if (typeListAnnotation != null) {
			return gioviTypeFrom(typeListAnnotation);
		}

		var typeMapAnnotation = param.getAnnotation(Native.TypeMap.class);
		if (typeMapAnnotation != null) {
			return gioviTypeFrom(typeMapAnnotation);
		}

		// NOTE: getAnnotation(Type) will return the nested Type. E.g. TypeList(Type). So we do this last
		var typeAnnotation = param.getAnnotation(Native.Type.class);
		if (typeAnnotation != null) {
			return gioviTypeFrom(typeAnnotation);
		}

		return new GioviTypeVoid();
	}

	public static GioviTypeStruct structTypeOf(Class<?> clazz) {
		return new GioviTypeStruct(Natives.structSignatureOf(clazz));
	}


}

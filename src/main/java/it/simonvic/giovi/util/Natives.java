package it.simonvic.giovi.util;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

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
import it.simonvic.giovi.lang.annotation.Native.DescribedBy;
import it.simonvic.giovi.lang.annotation.Native.Self;
import it.simonvic.giovi.lang.annotation.Native.TypeList;
import it.simonvic.giovi.lang.annotation.Native.TypeMap;

public class Natives {

	// TODO: what if we compile native modules to classes instead of reading it with reflection?

	public static void scanModule(Module module, Class<?> clazz) {
		for (Field classField : clazz.getFields()) {
			if (!Modifier.isStatic(classField.getModifiers())) {
				System.err.println(
					"Non static fields are not supported. Ignoring " + classField
				);
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

	/**
	 * Find signature field in a class or build it
	 * @param clazz
	 * @return
	 */
	public static StructSignature structSignatureOf(Class<?> clazz) {
		// Find field marked as signature...
		for (Field field : List.of(clazz.getFields()).reversed()) { // NOTE: signature is usually the last field
			var annotation = field.getAnnotation(Native.Struct.Signature.class);
			if (annotation != null) {
				try {
					return (StructSignature) field.get(null);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new RuntimeException("Error in retrieving struct signature", e);
				}
			}
		}
		// ... or build it if not defined
		return Natives.structSignatureFrom(clazz);
	}

	/**
	 * Build function signature from a class
	 * @param clazz
	 * @return
	 */
	public static StructSignature structSignatureFrom(Class<?> clazz) {
		var structAnnotation = clazz.getAnnotation(Native.Struct.class);
		String typename = structAnnotation.typename();
		if (typename.isEmpty()) {
			typename = clazz.getSimpleName();
		}

		// TODO: check if classField/method are static or final?
		var fields = new LinkedHashMap<String, RecordFieldSignature>();
		if (!structAnnotation.inherit().equals(Object.class)) {
			var inheritedSignature = structSignatureOf(structAnnotation.inherit());
			fields.putAll(inheritedSignature.fields());
		}
		var constants = new HashMap<String, GioviValue<?>>();
		// TODO: allow java Records
		for (Field classField : clazz.getFields()) {
			if (!Modifier.isStatic(classField.getModifiers())) {
				System.err.println(
					"Non static fields are not supported. Ignoring " + classField
				);
				continue;
			}
			try {
				// TODO: allow to define a field directly with a Signature
				if (!(classField.get(null) instanceof GioviValue gioviValue)) {
					continue;
				}
				if (classField.isAnnotationPresent(Native.Struct.Field.class)) {
					fields.put(
						classField.getName(),
						new RecordFieldSignature(
							classField.getName(),
							gioviValue.getGioviType(),
							gioviValue.copy()
						)
					);
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
			functions
		);
	}

	// TODO: not robust enough
	public static FunctionSignature functionSignatureFrom(Method method) {
		String identifier = method.getName();
		GioviType<?> returnType = gioviReturnTypeFrom(method);
		var parameters = new LinkedHashMap<String, FormalParameter>();
		for (Parameter param : method.getParameters()) {
			if (param.isAnnotationPresent(Native.Self.class)) {
				parameters.put("self", new FormalParameter("self", GioviType.SELF));
				continue;
			}
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
						| NonWideneable e
					) {
						throw new RuntimeException(
							"Can't invoke parameter default value method source", e
						);
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
			} catch (
				InstantiationException
				| IllegalAccessException
				| IllegalArgumentException
				| InvocationTargetException
				| NoSuchMethodException
				| SecurityException e
			) {
				throw new RuntimeException(e);
			}
		}
		return new GioviTypeStruct(structSignatureOf(annotation.nativ()));
	}

	private static GioviType<?> gioviTypeFrom(Class<? extends GioviType<?>> clazz) {
		if (clazz != GioviTypeVoid.class) {
			try {
				return clazz.getConstructor().newInstance();
			} catch (
				InstantiationException
				| IllegalAccessException
				| IllegalArgumentException
				| InvocationTargetException
				| NoSuchMethodException
				| SecurityException e
			) {
				throw new RuntimeException(e);
			}
		}
		return new GioviTypeStruct(structSignatureOf(clazz));
	}

	private static GioviType<?> gioviTypeFrom(TypeList typeListAnnotation) {
		return new GioviTypeList(gioviTypeFrom(typeListAnnotation.value()));
	}

	private static GioviType<?> gioviTypeFrom(TypeMap typeMapAnnotation) {
		return new GioviTypeMap(
			gioviTypeFrom(typeMapAnnotation.key()),
			gioviTypeFrom(typeMapAnnotation.val())
		);
	}

	private static GioviType<?> gioviReturnTypeFrom(Method method) {
		if (method.getReturnType().equals(Void.TYPE)) {
			return GioviType.VOID;
		}

		AnnotatedType annotatedReturnType = method.getAnnotatedReturnType();

		if (annotatedReturnType.isAnnotationPresent(Self.class)) {
			return GioviType.SELF;
		}

		var typeListAnnotation = annotatedReturnType.getAnnotation(TypeList.class);
		if (typeListAnnotation != null) {
			return gioviTypeFrom(typeListAnnotation);
		}

		if (annotatedReturnType.getType() == GioviMap.class) {
			// NOTE: we can't use @TypeMap on return types (can't repeat annotations)
			var typeMapAnnotation = annotatedReturnType.getAnnotationsByType(Native.Type.class);
			return new GioviTypeMap(
				gioviTypeFrom(typeMapAnnotation[0]),
				gioviTypeFrom(typeMapAnnotation[1])
			);
		}

		// NOTE: getAnnotation(Type) will return the nested Type. E.g. TypeList(Type). So we do this last
		var typeAnnotation = annotatedReturnType.getAnnotation(Native.Type.class);
		if (typeAnnotation != null) {
			return gioviTypeFrom(typeAnnotation);
		}

		var descriptor = method.getReturnType().getAnnotation(DescribedBy.class);
		if (descriptor != null) {
			return gioviTypeFrom(descriptor.value());
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

		var descriptor = param.getType().getAnnotation(DescribedBy.class);
		if (descriptor != null) {
			return gioviTypeFrom(descriptor.value());
		}

		throw new GioviException(
			"Can't find type for parameter: " + param
				+ " in " + param.getDeclaringExecutable()
		);
	}

	public static GioviTypeStruct structTypeOf(Class<?> clazz) {
		return new GioviTypeStruct(Natives.structSignatureOf(clazz));
	}


}

package it.simonvic.giovi.util;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import giovi.*;
import giovi.Module;
import giovi.lang.*;
import it.simonvic.giovi.Var;
import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.lang.FormalParameter;
import it.simonvic.giovi.lang.FormalParameters;
import it.simonvic.giovi.lang.FunctionSignature;
import it.simonvic.giovi.lang.annotation.Native;
import it.simonvic.giovi.lang.annotation.Native.*;
import it.simonvic.giovi.lang.annotation.Native.Func.Param.Label;
import it.simonvic.giovi.lang.annotation.Native.Func.Param.Optional;

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
		System.err.println("Struct signature not found! Building it on the fly");
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
		try {
			Field signatureField = method.getDeclaringClass().getField(identifier);
			if (signatureField.isAnnotationPresent(Native.Func.Signature.class)) {
				return (FunctionSignature) signatureField.get(null);
			}
		} catch (NoSuchFieldException e) {
			// no signature field... we build it
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new GioviException(e);
		}
		GioviType<?> returnType = gioviReturnTypeFrom(method);
		var parameters = new LinkedHashMap<String, FormalParameter>();

		Map<String, GioviValue<?>> defaultValues;
		try {
			defaultValues = Natives.getDefaultValues(method);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}

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

			var optionalAnnotation = param.getAnnotation(Native.Func.Param.Optional.class);
			FormalParameter formalParam;
			if (optionalAnnotation != null) {
				GioviValue<?> defaultValue = defaultValues.get(paramName);
				if (defaultValue == null) {
					defaultValue = paramType.spawn();
				}
				formalParam = new FormalParameter(label, paramName, paramType, defaultValue, true);
			} else {
				formalParam = new FormalParameter(label, paramName, paramType);
			}

			parameters.put(paramName, formalParam);
		}
		return new FunctionSignature(identifier, new FormalParameters(parameters), returnType);
	}

	@SuppressWarnings("unchecked")
	private static Map<String, GioviValue<?>> getDefaultValues(Method method)
		throws IllegalArgumentException, IllegalAccessException {
		try {
			Field defaultValuesField = method.getDeclaringClass()
				.getField(method.getName() + "_defaults");
			if (
				defaultValuesField != null
					&& defaultValuesField.isAnnotationPresent(Native.Func.Defaults.class)
			) {
				return (Map<String, GioviValue<?>>) defaultValuesField.get(null);
			}
		} catch (NoSuchFieldException _) {
			// we ignore
		}
		return Map.of();
	}

	private static GioviType<?> gioviTypeFrom(Native.Type annotation) {
		if (annotation.nativ() != Object.class) {
			return new GioviTypeStruct(structSignatureOf(annotation.nativ()));
		}
		return gioviTypeFrom(annotation.value());
	}

	private static GioviType<?> gioviTypeFrom(Class<? extends GioviType<?>> clazz) {
		if (clazz == GioviTypeAny.class) {
			return GioviType.ANY;
		}
		if (GioviTypePrimitive.class.isAssignableFrom(clazz)) {
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

	private static GioviType<?> gioviTypeFrom(TypeType typeTypeAnnotation) {
		return new GioviTypeType<>(gioviTypeFrom(typeTypeAnnotation.value()));
	}

	private static GioviTypeTuple gioviTypeFrom(TypeTuple typeTupleAnnotation) {
		return new GioviTypeTuple(
			new TupleSignature(
				Stream.of(typeTupleAnnotation.value())
					.map(Natives::gioviTypeFrom)
					.map(TupleFieldSignature::new)
					.toList()
			)
		);
	}

	private static GioviTypeRecord gioviTypeFrom(TypeRecord typeRecordAnnotation) {
		var fields = new LinkedHashMap<String, RecordFieldSignature>();
		Stream.of(typeRecordAnnotation.value())
			.map(Natives::recordFieldSignatureFrom)
			.forEach(f -> fields.put(f.identifier(), f));
		return new GioviTypeRecord(new RecordSignature(fields));
	}

	private static RecordFieldSignature recordFieldSignatureFrom(
		TypeRecord.Field memberAnnotation
	) {
		return new RecordFieldSignature(
			memberAnnotation.name(),
			gioviTypeFrom(memberAnnotation.type())
		);
	}


	private static GioviType<?> gioviReturnTypeFrom(Method method) {
		if (method.getReturnType().equals(Void.TYPE)) {
			return GioviType.VOID;
		}

		if (method.isAnnotationPresent(Self.class)) {
			return GioviType.SELF;
		}

		// NOTE: we can't use these annotations on return types (can't repeat @Type)

		if (method.getReturnType().equals(GioviMap.class)) {
			return gioviTypeFrom(method.getAnnotation(Native.TypeMap.class));
		}

		if (method.getReturnType().equals(GioviTuple.class)) {
			return gioviTypeFrom(method.getAnnotation(Native.TypeTuple.class));
		}

		if (method.getReturnType().equals(GioviRecord.class)) {
			return gioviTypeFrom(method.getAnnotation(Native.TypeRecord.class));
		}

		AnnotatedType annotatedReturnType = method.getAnnotatedReturnType();

		if (annotatedReturnType.isAnnotationPresent(Self.class)) {
			return GioviType.SELF;
		}

		var typeListAnnotation = annotatedReturnType.getAnnotation(TypeList.class);
		if (typeListAnnotation != null) {
			return gioviTypeFrom(typeListAnnotation);
		}

		var typeTypeAnnotation = annotatedReturnType.getAnnotation(Native.TypeType.class);
		if (typeTypeAnnotation != null) {
			return gioviTypeFrom(typeTypeAnnotation);
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

		var typeTypeAnnotation = param.getAnnotation(Native.TypeType.class);
		if (typeTypeAnnotation != null) {
			return gioviTypeFrom(typeTypeAnnotation);
		}

		var typeTupleAnnotation = param.getAnnotation(Native.TypeTuple.class);
		if (typeTupleAnnotation != null) {
			return gioviTypeFrom(typeTupleAnnotation);
		}

		var typeRecordAnnotation = param.getAnnotation(Native.TypeRecord.class);
		if (typeRecordAnnotation != null) {
			return gioviTypeFrom(typeRecordAnnotation);
		}

		// NOTE: getAnnotation(Type) will return the nested Type. E.g. TypeList(Type). So we do this last
		var typeAnnotation = param.getAnnotation(Native.Type.class);
		if (typeAnnotation != null) {
			return gioviTypeFrom(typeAnnotation);
		}

		var self = param.getAnnotation(Native.Self.class);
		if (self != null) {
			return GioviType.SELF;
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

	/**
	 * Lookup native method based on signature
	 * @param clazz
	 * @param signature
	 * @return
	 * @throws NoSuchMethodException
	 */
	public static Method lookupMethod(
		Class<?> clazz,
		FunctionSignature signature
	) throws NoSuchMethodException {
		return lookupMethod(clazz, signature, List.of());
	}

	/**
	 * Lookup native method based on signature and actual (positional) parameters
	 * @param clazz
	 * @param signature
	 * @param actualParams
	 * @return
	 * @throws NoSuchMethodException
	 */
	public static Method lookupMethod(
		Class<?> clazz,
		FunctionSignature signature,
		List<Var<?>> actualParams
	) throws NoSuchMethodException {
		String identifier = signature.identifier();
		method_loop: for (var method : clazz.getMethods()) {
			if (!method.isAnnotationPresent(Func.class)) {
				continue;
			}
			if (!method.getName().equals(identifier)) {
				continue;
			}
			if (!Natives.gioviReturnTypeFrom(method).equals(signature.returnType())) {
				continue;
			}

			var nativeParams = method.getParameters();
			int nativeParamsCount = nativeParams.length;
			int actualParamsCount = actualParams.size();
			if (actualParamsCount > nativeParamsCount) {
				continue;
			}
			for (int i = 0; i < actualParamsCount; i++) {
				var nativeParam = nativeParams[i];
				if (nativeParam.isAnnotationPresent(Self.class)) {
					// We skip the self parameter
					continue;
				}
				var actualParam = actualParams.get(i);
				if (!Natives.gioviTypeFrom(nativeParam).equals(actualParam.getGioviType())) {
					continue method_loop;
				}
			}

			// expecting remaining formal parameters to be optional
			var formalParams = signature.getParametersList();
			for (int i = actualParamsCount; i < nativeParamsCount; i++) {
				var nativeParam = nativeParams[i];
				if (nativeParam.isAnnotationPresent(Self.class)) {
					// We ignore the self parameter
					continue;
				}
				if (!formalParams.get(i).isOptional()) {
					// found a mandatory formal parameter, so method doesn't match
					continue method_loop;
				}
			}

			return method;
		}
		throw new NoSuchMethodException();
	}

	/**
	 * Lookup native method based on signature and actual (named) parameters
	 * @param clazz
	 * @param signature
	 * @param actualParams
	 * @return
	 * @throws NoSuchMethodException
	 */
	public static Method lookupMethod(
		Class<?> clazz,
		FunctionSignature signature,
		LinkedHashMap<String, Var<?>> actualParams
	) throws NoSuchMethodException {
		String identifier = signature.identifier();
		method_loop: for (var method : clazz.getMethods()) {
			if (!method.isAnnotationPresent(Func.class)) {
				continue;
			}
			if (!method.getName().equals(identifier)) {
				continue;
			}
			if (!Natives.gioviReturnTypeFrom(method).equals(signature.returnType())) {
				continue;
			}

			var nativeParams = method.getParameters();
			int nativeParamsCount = nativeParams.length;
			int actualParamsCount = actualParams.size();
			if (actualParamsCount > nativeParamsCount) {
				continue;
			}

			var nativeParamsByLabel = new HashMap<String, Parameter>();
			for (var parameter : nativeParams) {
				var labelAnnotation = parameter.getAnnotation(Label.class);
				nativeParamsByLabel.put(
					(labelAnnotation != null) ? labelAnnotation.value() : parameter.getName(),
					parameter
				);
			}

			for (var actualParamPair : actualParams.entrySet()) {
				var actualParamName = actualParamPair.getKey();
				var nativeParam = nativeParamsByLabel.remove(actualParamName); // pop the parameter
				if (nativeParam == null) {
					// actual parameter not present in native method
					continue method_loop;
				}
				if (actualParamName.equals("self") && nativeParam.isAnnotationPresent(Self.class)) {
					// We skip the self parameter
					continue;
				}
				var actualParamType = actualParamPair.getValue().getGioviType();
				if (!Natives.gioviTypeFrom(nativeParam).equals(actualParamType)) {
					// type mismatch
					continue method_loop;
				}
			}

			// expecting remaining native params to be optional
			for (var nativeParam : nativeParamsByLabel.values()) {
				if (!nativeParam.isAnnotationPresent(Optional.class)) {
					continue method_loop;
				}
			}

			return method;
		}
		throw new NoSuchMethodException();
	}

	/**
	 * Setup actual parameters and invoke the native method.
	 * @param method; has to be looked up correctly
	 * @param signature
	 * @param actualParams
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static GioviValue<?> invoke(
		Method method,
		FunctionSignature signature,
		LinkedHashMap<String, Var<?>> actualParams
	) throws IllegalAccessException, InvocationTargetException {
		Parameter[] nativeParams = method.getParameters();
		var args = new Object[nativeParams.length];
		for (int i = 0; i < nativeParams.length; i++) {
			var nativeParam = nativeParams[i];
			String nativeParamName = nativeParam.getName();
			var actualParam = actualParams.get(nativeParamName);
			args[i] = (actualParam != null)
				? actualParam.unwrap()
				: signature.getParameter(nativeParamName).defaultValue().copy();
		}
		var returnValue = method.invoke(null, args);
		if (method.getReturnType().equals(Void.TYPE)) {
			return new GioviVoid();
		}
		return (GioviValue<?>) returnValue;
	}

	/**
	 * Setup actual parameters and invoke the native method.
	 * @param method; has to be looked up correctly
	 * @param actualParams
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static GioviValue<?> invoke(
		Method method,
		List<Var<?>> actualParams
	) throws IllegalAccessException, InvocationTargetException {
		Parameter[] nativeParams = method.getParameters();
		var args = new Object[nativeParams.length];
		for (int i = 0; i < nativeParams.length; i++) {
			args[i] = actualParams.get(i).unwrap();
		}
		var returnValue = method.invoke(null, args);
		if (method.getReturnType().equals(Void.TYPE)) {
			return new GioviVoid();
		}
		return (GioviValue<?>) returnValue;
	}


}

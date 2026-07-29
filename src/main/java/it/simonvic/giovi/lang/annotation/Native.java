package it.simonvic.giovi.lang.annotation;

import java.lang.annotation.*;

import giovi.StructSignature;
import giovi.lang.GioviType;
import giovi.lang.GioviTypeAny;
import giovi.lang.GioviValue;
import it.simonvic.giovi.lang.FunctionSignature;
import it.simonvic.giovi.util.Natives;

/// For a full implementation this depends on:
/// 1. ~Struct constants~
/// 2. ~Struct functions~
/// 3. ~Function overloading?~
/// 4. ~Better way to get Types from Values~
///    1. ~Eventually performance improvements to primitive types~
/// 5. Expose Range ~and Types~ as GioviValues (or reimplement them as Native)
/// 6. ~Find a way to handle Generic-like types (List,Maps,Range)~
/// 7. ~Import statements~
/// 8. ~Some sort of generic typing~
/// 9. Functions as parameters (how to invoke?)
public interface Native {

	// TODO: add @Descriptor or @Type for method/fields to provide instance

	/// To be used on Value classes to indicate the Type class by which is modeled
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	@Documented
	public @interface DescribedBy {

		Class<? extends GioviType<?>> value();

	}

	/// To be used on Type classes to indicate the Value class that it models
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	@Documented
	public @interface Describes {

		Class<? extends GioviValue<?>> value();

	}


	/// Indicates that the class needs is exposed in Giovi as a Module
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	@Documented
	public @interface Module {

		// boolean autoimport() default true;
		// TODO: add testing scope

	}

	/// Indicates that the field is exposed in Giovi as a Constant.
	///
	/// The field must be a [GioviValue]
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD })
	@Documented
	public @interface Const {
	}

	/// Indicates that the class is exposed in Giovi as a native Struct
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	@Documented
	public @interface Struct {

		/// Specify the typename to expose.
		///
		/// If left unspecified, the class name will be used.
		String typename() default "";

		/// Specify that the struct should inherit from another struct.
		///
		/// The extended class must be annotated with [Struct]
		Class<?> inherit() default Object.class;

		/// Used on a field to provide a manually created signature of the struct.
		///
		/// The field must be of type [StructSignature]
		/// 
		/// @see Natives#structSignatureFrom(Class)
		@Retention(RetentionPolicy.RUNTIME)
		@Target(ElementType.FIELD)
		@Documented
		public @interface Signature {
		}

		/// Used on a field to specify a field of the struct.
		///
		/// Must be a [GioviValue] and its value will be used as a default value for the
		/// struct field.
		///
		/// Used on a field to specify a field of the struct.
		///
		/// Must be a [GioviValue] and its value will be used as a default value for the
		/// struct field.
		@Retention(RetentionPolicy.RUNTIME)
		@Target(ElementType.FIELD)
		@Documented
		public @interface Field {
		}

	}

	/// Used on a method to be exposed as a function in Giovi.
	///
	/// If the method is declared within a class annotated with [Module], the
	/// function will be declared within the module. If it's declared within a
	/// [Struct], it will be declared within the struct, hence associated to the
	/// type.
	///
	/// @see Self
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD })
	@Documented
	public @interface Func {

		/// Used to provide a manually created function signature.
		///
		/// Must be of type [FunctionSignature]
		@Retention(RetentionPolicy.RUNTIME)
		@Target({ ElementType.FIELD })
		@Documented
		public @interface Signature {
		}


		//@formatter:off
		/// Used to provide a default value for an [Param.Optional] [Param] of a [Func].
		///
		/// The field must be named with the same name of the function, with a
		/// `_defaults` suffix.
		///
		/// Must be of type `Map<String, GioviValue<?>>` and should map the param name to
		/// its default value.
		///
		/// ```java
		/// @Func.Defaults
		/// public static final Map<String, GioviValue<?>> makePizza_defaults = Map.of(
		/// 	"type", new GioviText("margherita"),
		/// );
		///
		/// @Func
		/// public static void makePizza(
		/// 	GioviInt tableNumber,
		/// 	@Optional GioviText type,
		/// 	@Optional GioviBool hasPineapple
		/// ) { }
		/// ```
		///
		/// @see Param.Optional
		//@formatter:on
		@Retention(RetentionPolicy.RUNTIME)
		@Target({ ElementType.FIELD })
		@Documented
		public @interface Defaults {
		}


		/// By default, all parameters of a [Func] annotated method, will be considered
		/// as a parameter in the function signature, whose name is the same as the
		/// method parameter.
		///
		/// If some reserved keywords has to be used, the parameter can be annotated with
		/// this annotation by providing a String for the name of the parameter.
		@Retention(RetentionPolicy.RUNTIME)
		@Target(ElementType.PARAMETER)
		@Documented
		public @interface Param {

			String value();

			/// Used on a parameter of a [Func] annotated method to declare the invocation
			/// label of the parameter
			@Retention(RetentionPolicy.RUNTIME)
			@Target(ElementType.PARAMETER)
			@Documented
			public @interface Label {

				String value();

			}

			/// Used on a parameter of a [Func] annotated method to declare the paramater as
			/// optional.
			///
			/// If a [Defaults] annotated field is not declared, the parameter default value
			/// will be equal to its type's zero value
			@Retention(RetentionPolicy.RUNTIME)
			@Target(ElementType.PARAMETER)
			@Documented
			public @interface Optional {

			}

		}


	}

	/// By default, where possible, the Giovi type will be inferred from the native
	/// class.
	///
	/// Where needed, a GioviType class can be specified with this annotation. If a
	/// class is not provided, GioviTypeAny.class is used; in that case, the element
	/// type must be of the abstract type `GioviValue<?>` and the type conversions
	/// must be handled manually
	///
	/// If the `nativ`(e) attribute is specified, the provided class will be looked
	/// up for native types; in this case, the `value` will be ignored
	///
	/// @see GioviTypeAny
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.TYPE_USE, ElementType.PARAMETER })
	@Repeatable(Type.Repeat.class)
	@Documented
	public @interface Type {

		Class<? extends GioviType<?>> value() default GioviTypeAny.class;

		Class<?> nativ() default Object.class;

		@Retention(RetentionPolicy.RUNTIME)
		@Target({ ElementType.TYPE_USE })
		@Documented
		public @interface Repeat {

			Type[] value();

		}

	}


	/// Used to specify a list type
	/// 
	/// @see Type
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.TYPE_USE, ElementType.PARAMETER })
	@Documented
	public @interface TypeList {

		/// The entry type of the list
		Type value();

	}

	/// Used to specify a map type
	/// 
	/// If you have to annotate a method return type to be of type map, you'll have
	/// to move the annotation from the return type to the method instead; this is
	/// because it's not possible to reapeat [Type].
	///
	/// @see Type
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.TYPE_USE, ElementType.PARAMETER, ElementType.METHOD })
	@Documented
	public @interface TypeMap {

		/// The map key type
		Type key();

		/// The map value type
		Type val();

	}

	/// Used to specify a GioviTypeType
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.TYPE_USE, ElementType.PARAMETER })
	@Repeatable(TypeType.Repeat.class)
	@Documented
	public @interface TypeType {

		/// The type that this type describes :)
		Type value();

		@Retention(RetentionPolicy.RUNTIME)
		@Target({ ElementType.TYPE_USE })
		@Documented
		public @interface Repeat {

			TypeType[] value();

		}

	}

	/// Used to specify a GioviTuple type
	///
	/// If you have to annotate a method return type, you'll have to move the
	/// annotation from the return type to the method instead; this is because it's
	/// not possible to reapeat [Type].
	///
	/// @see Type
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.TYPE_USE, ElementType.PARAMETER, ElementType.METHOD })
	@Repeatable(TypeTuple.Repeat.class)
	@Documented
	public @interface TypeTuple {

		Type[] value();


		@Retention(RetentionPolicy.RUNTIME)
		@Target({ ElementType.TYPE_USE })
		@Documented
		public @interface Repeat {

			TypeTuple[] value();

		}

	}

	/// Used to specify a GioviRecord type
	///
	/// If you have to annotate a method return type, you'll have to move the
	/// annotation from the return type to the method instead; this is because it's
	/// not possible to reapeat [Type].
	///
	/// @see Type
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.TYPE_USE, ElementType.PARAMETER, ElementType.METHOD })
	@Repeatable(TypeRecord.Repeat.class)
	@Documented
	public @interface TypeRecord {

		Field[] value();


		@Retention(RetentionPolicy.RUNTIME)
		@Target({ ElementType.PARAMETER })
		@Documented
		public @interface Field {

			String name();

			Type type();

		}

		@Retention(RetentionPolicy.RUNTIME)
		@Target({ ElementType.TYPE_USE })
		@Documented
		public @interface Repeat {

			TypeRecord[] value();

		}

	}

	//@formatter:off
	/// Similarily to the `self` keyword, the annotation can be used on the return type of a [Func]
	/// annotated method to indicate that the function returns the `Self` type; can be used on a
	/// "self" named parameter to mark the function as `self`.
	///
	/// If the return type is a complex type, the annotation must be put on the method instead
	///
	/// ```java
	/// @Func
	/// public static GioviText getName(@Self Pizza self) {
	/// 	return new GioviText(self.name());
	/// }
	///
	/// @Func
	/// public static @Self Pizza makePizza() {
	/// 	return new Pizza();
	/// }
	///
	/// @Func
	/// public static @Self Pizza cookPizza(@Self Pizza self) {
	/// 	self.cook();
	/// 	return self;
	/// }
	///
	/// @Func
	/// @Self
	/// public static GioviMap getIngredients(@Self Pizza self) {
	/// 	return self.getMapOfIngredients();
	/// }
	/// ```
	//@formatter:on
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.TYPE_USE, ElementType.PARAMETER, ElementType.METHOD })
	@Documented
	public @interface Self {
	}

}

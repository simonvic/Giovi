package it.simonvic.giovi.lang.annotation;

import java.lang.annotation.*;

import giovi.lang.GioviType;
import giovi.lang.GioviTypeAny;
import giovi.lang.GioviValue;

/// For a full implementation this depends on:
/// 1. ~Struct constants~
/// 2. ~Struct functions~
/// 3. Function overloading?
/// 4. Better way to get Types from Values
///    1. Eventually performance improvements to primitive types
/// 5. Expose Range and Types as GioviValues (or reimplement them as Native)
/// 6. Find a way to handle Generic-like types (List,Maps,Range)
/// 7. ~Import statements~
/// 8. Some sort of generic typing
public interface Native {

	// TODO: add @Descriptor or @Type for method/fields to provide instance

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	@Documented
	public @interface DescribedBy {

		Class<? extends GioviType<?>> value();

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	@Documented
	public @interface Describes {

		Class<? extends GioviValue<?>> value();

	}


	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	@Documented
	public @interface Module {

		boolean autoimport() default true;

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE })
	@Documented
	public @interface Const {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	@Documented
	public @interface Struct {

		String typename() default "";

		Class<?> inherit() default Object.class;

		@Retention(RetentionPolicy.RUNTIME)
		@Target(ElementType.FIELD)
		@Documented
		public @interface Signature {
		}

		@Retention(RetentionPolicy.RUNTIME)
		@Target(ElementType.FIELD)
		@Documented
		public @interface Field {
		}

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.PARAMETER })
	@Documented
	public @interface Func {

		// TODO: eventually add @OverloadOf(@Func) and enhance method lookup

		@Retention(RetentionPolicy.RUNTIME)
		@Target(ElementType.PARAMETER)
		@Documented
		public @interface Param {

			String value() default "";

			@Retention(RetentionPolicy.RUNTIME)
			@Target(ElementType.PARAMETER)
			@Documented
			public @interface Label {

				String value();

			}


			@Retention(RetentionPolicy.RUNTIME)
			@Target(ElementType.PARAMETER)
			@Documented
			public @interface Optional {

				/**
				 * Method source for the default value; leave empty to use the type's default
				 */
				String value() default "";

			}

		}


	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.TYPE_USE, ElementType.PARAMETER })
	@Repeatable(Types.class)
	@Documented
	public @interface Type {

		Class<? extends GioviType<?>> value() default GioviTypeAny.class;

		Class<?> nativ() default Object.class;

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.TYPE_USE })
	@Documented
	public @interface Types {

		Type[] value();

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.TYPE_USE, ElementType.PARAMETER })
	@Documented
	public @interface TypeList {

		Type value();

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.TYPE_USE, ElementType.PARAMETER })
	@Documented
	public @interface TypeMap {

		Type key();

		Type val();

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.TYPE_USE, ElementType.PARAMETER })
	@Documented
	public @interface Self {

	}


}

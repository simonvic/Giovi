package it.simonvic.giovi.lang.annotation;

import java.lang.annotation.*;

import giovi.lang.GioviType;

/*
 * For a full implementation this depends on:
 * 1. Struct constants
 * 2. Struct functions
 * 3. Function overloading?
 * 4. Better way to get Types from Values
 * 4.1. Eventually performance improvements to primitive types
 * 5. Expose Range and Types as GioviValues (or reimplement them as Native)
 * 6. Find a way to handle Generic-like types (List,Maps,Range)
 * 7. Import/Include statements
 * 8. Some sort of generic typing
 *
 */

public interface Native {

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
		// TODO: optional parameters

		@Retention(RetentionPolicy.RUNTIME)
		@Target(ElementType.PARAMETER)
		@Documented
		public @interface Param {

			String label() default "";

			String value(); // eventually enable parameter name discovery compiler option

		}

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.TYPE_USE, ElementType.PARAMETER })
	@Documented
	public @interface Type {

		Class<? extends GioviType<?>> value();

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.TYPE_USE, ElementType.PARAMETER })
	@Documented
	public @interface TypeNative {

		Class<?> value();

	}

}

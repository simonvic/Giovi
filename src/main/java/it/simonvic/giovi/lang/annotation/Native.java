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

		@Retention(RetentionPolicy.RUNTIME)
		@Target(ElementType.FIELD)
		@Documented
		public @interface Field {
		}


	}

}

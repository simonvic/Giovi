package it.simonvic.giovi;

import static it.simonvic.giovi.GioviVisitorUtils.visit;

import org.junit.jupiter.api.Test;

/**
 * TestSandbox
 */
public class TestSandbox {

	@Test
	void sandbox() {
		visit("""
			Color <r Int, g Int, b Int>
			palette [Color] <- [
				Color.<r:69 g:35 b:54>
			]
			for c in palette do c -> \\out
			""");
		// visit("""
		// 	Bar <
		// 		baz Text default "bazdefault"
		// 		foobar [Int] default [69 420]
		// 	>
		// 	Foo <
		// 		foo Text default "foodefault"
		// 		bar Bar
		// 	>
		// 	x Foo default Foo.<
		// 		foo: "foo"
		// 		bar: Bar.<
		// 			baz: "baz"
		// 			foobar: [1 2 3]
		// 		>
		// 	>
		// 	"{
		//
		// 	-----
		// 	x
		// 	}" -> \\out
		// 	x -> \\out
		// 	"{
		//
		// 	----
		// 	x.foo
		// 	}" -> \\out
		// 	x.foo -> \\out
		// 	"{
		//
		// 	----
		// 	x.bar
		// 	}" -> \\out
		// 	x.bar -> \\out
		// 	"{
		//
		// 	----
		// 	x.bar.baz
		// 	}" -> \\out
		// 	x.bar.baz -> \\out
		// 	"{
		//
		// 	----
		// 	x.bar.foobar
		// 	}" -> \\out
		// 	x.bar.foobar -> \\out
		// 	""");
	}

}

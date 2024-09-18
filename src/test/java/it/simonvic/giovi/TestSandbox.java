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
			420 -> x Int default 69
			l [Int] default [1 2 x]
			l -> \\out
			l@0 -> \\out
			for i in l do {
				i -> \\out
			}
			""");
	}

}

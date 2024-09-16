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
			x [Int:Bool] default [1:true 2:false]
			x -> \\out
			#[69:true 420:true] -> x
			#default -> x
			""");
	}

}

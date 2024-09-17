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
			x [[Int]] <- [
				[1 0 0]
				[1 1 1]
				[0 1 0]
				[0 0 1]
			]
			x -> \\out
			x -<- [1 1 1]
			x -> \\out
			""");
	}

}

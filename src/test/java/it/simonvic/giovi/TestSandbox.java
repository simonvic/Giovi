package it.simonvic.giovi;

import static it.simonvic.giovi.GioviVisitorUtils.visit;

import org.junit.jupiter.api.Test;

/**
 * TestSandbox
 */
public class TestSandbox {

	@Test
	void sandbox() {
		visit(
			"""
			Color <
				r Int default 255
				g Int default 255
				b Int default 255
			>
			<2 2 2> -> c Color 
			c -> \\out
			c.default -> \\out
			if c {
				is k Color do {
					k -> \\out
					k.default -> \\out
				}
			}
			""");
	}
}

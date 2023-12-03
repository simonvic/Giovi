package it.simonvic.giovi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import it.simonvic.giovi.util.GioviLists;

public class TestUtils {

	@Test
	void test_GioviListsOf() {
		assertEquals(GioviLists.of(1, 2, 3), GioviLists.of(1, 2, 3));
		assertEquals(GioviLists.of("Simone", "Francesco", "Mario"),
			GioviLists.of("Simone", "Francesco", "Mario"));
	}

	
}

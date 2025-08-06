package it.simonvic.giovi;

import static it.simonvic.giovi.GioviVisitorUtils.visit;

import org.junit.jupiter.api.Test;

public class TestNative {

	@Test
	void foo() {
		visit("""
			_out <- giovi.util.Math.PI
			_out <- giovi.util.Math.floor(2.5)
			_out <- giovi.util.Math.floor(2.5)
			_out <- giovi.util.Random.integer()
			_out <- giovi.util.Color.WHITE
			_out <- giovi.util.Color.foo()
			_out <- giovi.util.Color.rgb(1, 2, 3)
			_out <- giovi.util.Color.rgba(1, 2, 3, 255)
			_out <- giovi.util.Color.Rgb.<>
			_out <- giovi.util.Test.TEST_CONST
			_out <- giovi.util.Test.foo
			_out <- giovi.util.Test.foo(
				x: 420,
				y: giovi.util.Color.Rgb.<>,
				z: [1 2 3],
				w : ["a":1 "b":2 "c":3]
			)
			_out <- giovi.util.Test.getList
			_out <- giovi.util.Test.getList()
			#_out <- giovi.util.Test.getMap
			#_out <- giovi.util.Test.getMap()
			_out <- giovi.util.Test.getRgb
			_out <- giovi.util.Test.getRgb()
			_out <- giovi.util.Test.getListOfRgb
			_out <- giovi.util.Test.getListOfRgb()
			""");
	}

}

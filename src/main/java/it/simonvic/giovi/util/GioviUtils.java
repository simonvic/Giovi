package it.simonvic.giovi.util;

public class GioviUtils {

	/**
	 * Convert a Giovi index [-n, n] to a native index [0, n]
	 * @param index
	 * @param max
	 * @return index
	 */
	public static int nativeIndexOf(int index, int max) {
		return index >= 0 ? index : max + index;
	}

}

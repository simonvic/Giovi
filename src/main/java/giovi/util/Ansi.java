package giovi.util;

import giovi.lang.GioviInt;
import giovi.lang.GioviStruct;
import giovi.lang.GioviText;
import giovi.util.Color.Rgb;
import it.simonvic.giovi.lang.annotation.Native.Const;
import it.simonvic.giovi.lang.annotation.Native.Func;
import it.simonvic.giovi.lang.annotation.Native.Module;
import it.simonvic.giovi.lang.annotation.Native.Type;

@Module
public class Ansi {

	@Const
	public static final GioviText ESC = new GioviText("\u001B");

	@Func
	public static GioviText moveTo(GioviInt line, GioviInt column) {
		return new GioviText(String.format("%s[%s;%sH", ESC, line, column));
	}

	@Func
	public static GioviText moveToColumn(GioviInt x) {
		return new GioviText(String.format("%s[%sD", ESC, x));
	}

	@Func
	public static GioviText moveUp(GioviInt x) {
		return new GioviText(String.format("%s[%sA", ESC, x));
	}

	@Func
	public static GioviText moveDown(GioviInt x) {
		return new GioviText(String.format("%s[%sB", ESC, x));
	}

	@Func
	public static GioviText moveRight(GioviInt x) {
		return new GioviText(String.format("%s[%sC", ESC, x));
	}

	@Func
	public static GioviText moveLeft(GioviInt x) {
		return new GioviText(String.format("%s[%sD", ESC, x));
	}

	@Func
	public static GioviText erase(GioviInt mode) {
		return new GioviText(String.format("%s[%sJ", ESC, mode));
	}

	@Func
	public static GioviText sized(GioviInt numerator, GioviInt denominator, GioviText text) {
		if (denominator.value() == 0) {
			return new GioviText(
				String.format(
					"%s]66;n=%s;%s%s%s",
					ESC,
					numerator,
					text,
					"\u0007",
					"\n".repeat(numerator.value())
				)
			);
		}
		return new GioviText(
			String.format(
				"%s]66;n=%s:d=%s;%s%s%s",
				ESC,
				numerator,
				denominator,
				text,
				"\u0007",
				"\n".repeat(numerator.value())
			)
		);
	}

	@Func
	public static GioviText fg(
		@Type(nativ = Rgb.class) GioviStruct color
	) {
		return new GioviText(
			String.format(
				"%s[38;2;%s;%s;%sm",
				ESC,
				color.value().getField("r").orElseThrow().unwrapAsGioviValue(),
				color.value().getField("g").orElseThrow().unwrapAsGioviValue(),
				color.value().getField("b").orElseThrow().unwrapAsGioviValue()
			)
		);
	}

	@Func
	public static GioviText bg(
		@Type(nativ = Rgb.class) GioviStruct color
	) {
		return new GioviText(
			String.format(
				"%s[48;2;%s;%s;%sm",
				ESC,
				color.value().getField("r").orElseThrow().unwrapAsGioviValue(),
				color.value().getField("g").orElseThrow().unwrapAsGioviValue(),
				color.value().getField("b").orElseThrow().unwrapAsGioviValue()
			)
		);
	}

	@Func
	public static GioviText styleReset() {
		return new GioviText(ESC + "[0m");
	}

	@Func
	public static GioviText bold() {
		return new GioviText(ESC + "[1m");
	}

	@Func
	public static GioviText dim() {
		return new GioviText(ESC + "[2m");
	}

	@Func
	public static GioviText italic() {
		return new GioviText(ESC + "[3m");
	}

	@Func
	public static GioviText underline() {
		return new GioviText(ESC + "[4m");
	}

	@Func
	public static GioviText blink() {
		return new GioviText(ESC + "[5m");
	}

	@Func
	public static GioviText inverse() {
		return new GioviText(ESC + "[7m");
	}

	@Func
	public static GioviText hidden() {
		return new GioviText(ESC + "[8m");
	}

	@Func
	public static GioviText strike() {
		return new GioviText(ESC + "[9m");
	}

}

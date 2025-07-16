package giovi.util;

import giovi.lang.*;
import giovi.util.Color.Rgb;
import it.simonvic.giovi.lang.annotation.Native.Const;
import it.simonvic.giovi.lang.annotation.Native.Func;
import it.simonvic.giovi.lang.annotation.Native.Func.Param;
import it.simonvic.giovi.lang.annotation.Native.Module;
import it.simonvic.giovi.lang.annotation.Native.Type;
import it.simonvic.giovi.lang.annotation.Native.TypeNative;

@Module
public class Ansi {

	@Const
	public static final @Type(GioviTypeText.class) GioviText ESC = new GioviText("\u001B");

	@Func
	public static @Type(GioviTypeText.class) GioviText moveTo(
		@Param("line") @Type(GioviTypeInt.class) GioviInt line,
		@Param("column") @Type(GioviTypeInt.class) GioviInt column
	) {
		return new GioviText(String.format("%s[%s;%sH", ESC, line, column));
	}

	@Func
	public static @Type(GioviTypeText.class) GioviText moveToColumn(
		@Param("x") @Type(GioviTypeInt.class) GioviInt x
	) {
		return new GioviText(String.format("%s[%sD", ESC, x));
	}

	@Func
	public static @Type(GioviTypeText.class) GioviText moveUp(
		@Param("x") @Type(GioviTypeInt.class) GioviInt x
	) {
		return new GioviText(String.format("%s[%sA", ESC, x));
	}

	@Func
	public static @Type(GioviTypeText.class) GioviText moveDown(
		@Param("x") @Type(GioviTypeInt.class) GioviInt x
	) {
		return new GioviText(String.format("%s[%sB", ESC, x));
	}

	@Func
	public static @Type(GioviTypeText.class) GioviText moveRight(
		@Param("x") @Type(GioviTypeInt.class) GioviInt x
	) {
		return new GioviText(String.format("%s[%sC", ESC, x));
	}

	@Func
	public static @Type(GioviTypeText.class) GioviText moveLeft(
		@Param("x") @Type(GioviTypeInt.class) GioviInt x
	) {
		return new GioviText(String.format("%s[%sD", ESC, x));
	}

	@Func
	public static @Type(GioviTypeText.class) GioviText erase(
		@Param("mode") @Type(GioviTypeInt.class) GioviInt mode
	) {
		return new GioviText(String.format("%s[%sJ", ESC, mode));
	}

	@Func
	public static @Type(GioviTypeText.class) GioviText sized(
		@Param("numerator") @Type(GioviTypeInt.class) GioviInt numerator,
		@Param("denominator") @Type(GioviTypeInt.class) GioviInt denominator,
		@Param("text") @Type(GioviTypeText.class) GioviText text
	) {
		if (denominator.value() == 0) {
			return new GioviText(String.format(
				"%s]66;n=%s;%s%s%s",
				ESC,
				numerator,
				text,
				"\u0007",
				"\n".repeat(numerator.value())));
		}
		return new GioviText(String.format(
			"%s]66;n=%s:d=%s;%s%s%s",
			ESC,
			numerator,
			denominator,
			text,
			"\u0007",
			"\n".repeat(numerator.value())));
	}

	@Func
	public static @Type(GioviTypeText.class) GioviText fg(
		@Param("color") @TypeNative(Rgb.class) GioviStruct color
	) {
		return new GioviText(String.format(
			"%s[38;2;%s;%s;%sm",
			ESC,
			color.value().getField("r").orElseThrow().unwrapAsGioviValue(),
			color.value().getField("g").orElseThrow().unwrapAsGioviValue(),
			color.value().getField("b").orElseThrow().unwrapAsGioviValue()));
	}

	@Func
	public static @Type(GioviTypeText.class) GioviText bg(
		@Param("color") @TypeNative(Rgb.class) GioviStruct color
	) {
		return new GioviText(String.format(
			"%s[48;2;%s;%s;%sm",
			ESC,
			color.value().getField("r").orElseThrow().unwrapAsGioviValue(),
			color.value().getField("g").orElseThrow().unwrapAsGioviValue(),
			color.value().getField("b").orElseThrow().unwrapAsGioviValue()));
	}

	@Func
	public static @Type(GioviTypeText.class) GioviText styleReset() {
		return new GioviText(ESC + "[0m");
	}

	@Func
	public static @Type(GioviTypeText.class) GioviText bold() {
		return new GioviText(ESC + "[1m");
	}

	@Func
	public static @Type(GioviTypeText.class) GioviText dim() {
		return new GioviText(ESC + "[2m");
	}

	@Func
	public static @Type(GioviTypeText.class) GioviText italic() {
		return new GioviText(ESC + "[3m");
	}

	@Func
	public static @Type(GioviTypeText.class) GioviText underline() {
		return new GioviText(ESC + "[4m");
	}

	@Func
	public static @Type(GioviTypeText.class) GioviText blink() {
		return new GioviText(ESC + "[5m");
	}

	@Func
	public static @Type(GioviTypeText.class) GioviText inverse() {
		return new GioviText(ESC + "[7m");
	}

	@Func
	public static @Type(GioviTypeText.class) GioviText hidden() {
		return new GioviText(ESC + "[8m");
	}

	@Func
	public static @Type(GioviTypeText.class) GioviText strike() {
		return new GioviText(ESC + "[9m");
	}

}

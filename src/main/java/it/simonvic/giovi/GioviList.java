package it.simonvic.giovi;

import it.simonvic.giovi.lang.values.BoolValue;
import it.simonvic.giovi.lang.values.IntValue;
import it.simonvic.giovi.lang.values.RealValue;
import it.simonvic.giovi.lang.values.TextValue;

import java.util.ArrayList;
import java.util.List;

public class GioviList {

//	public static List<NaturalValue> of(int... values) {
//		List<NaturalValue> list = new ArrayList<>();
//		for(int v : values) {
//			list.add(new NaturalValue(new Natural(v)));
//		}
//		return list;
//	}

	public static List<IntValue> of(int... values) {
		List<IntValue> list = new ArrayList<>();
		for(int v : values) {
			list.add(new IntValue(v));
		}
		return list;
	}

	public static List<RealValue> of(double... values) {
		List<RealValue> list = new ArrayList<>();
		for(double v : values) {
			list.add(new RealValue(v));
		}
		return list;
	}

	public static List<BoolValue> of(boolean... values) {
		List<BoolValue> list = new ArrayList<>();
		for(boolean v : values) {
			list.add(new BoolValue(v));
		}
		return list;
	}

	public static List<TextValue> of(String... values) {
		List<TextValue> list = new ArrayList<>();
		for(String v : values) {
			list.add(new TextValue(v));
		}
		return list;
	}

}

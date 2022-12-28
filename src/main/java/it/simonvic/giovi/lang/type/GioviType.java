package it.simonvic.giovi.lang.type;

import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.lang.GioviObject;
import it.simonvic.giovi.lang.values.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Objects;

public class GioviType extends GioviObject {

	private Class<? extends GioviValue<?>> type;

	public GioviType(Class<? extends GioviValue<?>> type) {
		this.type = type;
	}

	public Class<? extends GioviValue<?>> unbox() {
		return type;
	}

	public void box(Class<? extends GioviValue<?>> type) {
		this.type = type;
	}

	public GioviValue<?> spawn() {
		try {
			return type.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException |
				 NoSuchMethodException e) {
			throw new GioviException(e);
		}
	}

//	public GioviValue<?> spawn(GioviValue<?> value) {
//		try {
//			GioviValue<?> gioviValue = type.getDeclaredConstructor().newInstance();
//			gioviValue.box(value);
//			return gioviValue;
//		} catch (InstantiationException | IllegalAccessException | InvocationTargetException |
//				 NoSuchMethodException e) {
//			throw new GioviException(e);
//		}
//	}
//
//	public GioviValue<?> spawn(GioviValue<?> value, GioviValue<?> defaultValue) {
//		try {
//			GioviValue<?> gioviValue = type.getDeclaredConstructor().newInstance();
//			gioviValue.box(value);
//			gioviValue.setDefaultValue(value);
//			return gioviValue;
//		} catch (InstantiationException | IllegalAccessException | InvocationTargetException |
//				 NoSuchMethodException e) {
//			throw new GioviException(e);
//		}
//	}
//
//	public GioviValue<?> spawn(String identifier) {
//		try {
//			return type.getDeclaredConstructor(String.class).newInstance(identifier);
//		} catch (InstantiationException | IllegalAccessException | InvocationTargetException |
//				 NoSuchMethodException e) {
//			throw new GioviException(e);
//		}
//	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final GioviType other = (GioviType) obj;
		return Objects.equals(this.type, other.type);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type);
	}

	@Override
	public String toString() {
		return type == null ? "null" : type.getSimpleName();
	}


	public static boolean canUpcast(Class<?> from, Class<?> to) {
		if (from == null) {
			return true;
		}

		if (to == from) {
			return true;
		}

		if (from == GioviNatural.class) {
			return to == GioviInt.class || to == GioviReal.class;
		}
		if (from == GioviInt.class) {
			return to == GioviReal.class;
		}
		return false;
	}

	public static GioviType inferGioviType(Collection<GioviValue<?>> entries) {
		Class<?> inferredClass = null;
		for (GioviValue<?> entry : entries) {
			if (GioviType.canUpcast(inferredClass, entry.getClass())) {
				inferredClass = entry.getClass();
			}
		}
		if (inferredClass == null) throw new GioviException("Impossible to infer list type");
		return new GioviType((Class<? extends GioviValue<?>>) inferredClass);
	}

}

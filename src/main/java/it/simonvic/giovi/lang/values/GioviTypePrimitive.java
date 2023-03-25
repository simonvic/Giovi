package it.simonvic.giovi.lang.values;

import java.lang.reflect.InvocationTargetException;

import it.simonvic.giovi.exceptions.GioviException;

public sealed class GioviTypePrimitive
	extends GioviType<Class<? extends GioviValue<?>>>
	permits GioviTypeList, GioviTypeMap {

	public GioviTypePrimitive(
		Class<? extends GioviValue<?>> value,
		Class<? extends GioviValue<?>> defaultValue) {
		super(value, defaultValue);
	}

	public GioviTypePrimitive(Class<? extends GioviValue<?>> value) {
		super(value, value);
	}

	@Override
	public GioviType<?> getGioviType() {
		return new GioviTypePrimitive(GioviTypePrimitive.class);
	}

	@Override
	public boolean canUpcast(GioviType<?> to) {
		return switch (to) {
			case GioviTypeStruct t -> false;
			case GioviTypePrimitive t -> {
				var targetClass = t.unbox();
				if (this.value == targetClass) {
					yield true;
				}
				if (this.value == GioviNatural.class) {
					yield targetClass == GioviInt.class || targetClass == GioviReal.class;
				}
				if (this.value == GioviInt.class) {
					yield targetClass == GioviReal.class;
				}
				yield false;
			}
		};
	}

	public GioviValue<?> spawn() {
		try {
			return this.value.getDeclaredConstructor().newInstance();
		} catch (InstantiationException
			| IllegalAccessException
			| InvocationTargetException
			| NoSuchMethodException e) {
			throw new GioviException(e);
		}
	}


}


package it.simonvic.giovi.lang.values;

import java.lang.reflect.InvocationTargetException;

import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.lang.values.natives.Type;

public final class GioviTypePrimitive extends GioviType<Type.Primitive<?>> {

	public GioviTypePrimitive(Type.Primitive<?> value, Type.Primitive<?> defaultValue) {
		super(value, defaultValue);
	}

	public GioviTypePrimitive(Type.Primitive<?> value) {
		super(value, value.copy());
	}

	@Override
	public GioviTypePrimitive copy() {
		return new GioviTypePrimitive(this.value.copy(), this.defaultValue.copy());
	}


	// TODO: add GioviType for all primitives
	@Override
	public boolean canUpcast(GioviType<?> to) {
		return switch (to) {
			case GioviTypePrimitive t -> {
				var targetClass = t.unbox().getClazz();
				if (this.value.getClazz() == targetClass) {
					yield true;
				}
				if (this.value.getClazz() == GioviNatural.class) {
					yield targetClass == GioviInt.class || targetClass == GioviReal.class;
				}
				if (this.value.getClazz() == GioviInt.class) {
					yield targetClass == GioviReal.class;
				}
				yield false;
			}
			default -> false;
		};
	}

	@Override
	public GioviValue<?> spawn() {
		try {
			return this.value.getClazz().getDeclaredConstructor().newInstance();
		} catch (InstantiationException
			| IllegalAccessException
			| InvocationTargetException
			| NoSuchMethodException e) {
			throw new GioviException(e);
		}
	}

}


package giovi.lang;

import java.util.ArrayList;

import it.simonvic.giovi.Entry;
import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.lang.annotation.Native.*;
import it.simonvic.giovi.lang.annotation.Native.Func.Param.Optional;

@Describes(GioviMap.class)
public record GioviTypeMap(GioviType<?> keyType, GioviType<?> elementType)
	implements GioviTypeComposite<GioviMap> {

	public GioviType<?> getKeyType() {
		return keyType;
	}

	public GioviType<?> getElementType() {
		return elementType;
	}

	@Override
	public Class<? extends GioviMap> value() {
		return GioviMap.class;
	}

	@Override
	public String toString() {
		return "[" + keyType + " : " + elementType + "]";
	}

	@Override
	public boolean isWidenableTo(GioviType<?> type) {
		return switch (type) {
			case GioviTypeMap(var keyType, var elementType)
				-> this.keyType.isWidenableTo(keyType)
					&& this.elementType.isWidenableTo(elementType);
			default -> GioviTypeComposite.super.isWidenableTo(type);
		};
	}

	@Override
	public GioviMap widen(GioviValue<?> value) throws NonWideneable {
		return switch (value) {
			case GioviMap(_, var v) -> {
				try {
					yield new GioviMap(this, v);
				} catch (IllegalArgumentException ex) {
					throw new NonWideneable(ex);
				}
			}
			default -> GioviTypeComposite.super.widen(value);
		};
	}

	@Override
	public GioviMap spawn() {
		return new GioviMap(this);
	}

	@Override
	public GioviTypeMap copy() {
		return new GioviTypeMap(this.keyType.copy(), this.elementType.copy());
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// Native


	@Func
	public static GioviInt count(@Self GioviMap self) {
		return new GioviInt(self.value().size());
	}

	@Func
	public static @TypeList(@Type) GioviList keys(@Self GioviMap self) {
		return new GioviList(
			new GioviTypeList(self.getGioviType().keyType()),
			new ArrayList<>(self.value().keySet())
		);
	}

	@Func
	public static @TypeList(@Type) GioviList values(@Self GioviMap self) {
		return new GioviList(
			new GioviTypeList(self.getGioviType().elementType()),
			new ArrayList<>(self.value().values())
		);
	}

	@Func
	public static GioviBool isEmpty(@Self GioviMap self) {
		return GioviBool.of(self.value().isEmpty());
	}

	@Func
	@Self
	public static GioviMap clear(@Self GioviMap self) {
		self.value().clear();
		return self;
	}

	@Func
	@Self
	public static GioviMap put(
		@Self GioviMap self,
		GioviAny key,
		@Optional GioviAny value
	) throws NonWideneable {
		self.value().put(
			new Entry<>(self.getGioviType().keyType.widen(key.value())),
			new Entry<>(self.getGioviType().elementType.widen(value.value()))
		);
		return self;
	}

}

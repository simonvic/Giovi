package it.simonvic.giovi.lang.values.natives;

import it.simonvic.giovi.lang.values.*;

public abstract sealed class Type<T extends GioviValue<?>> {

	protected final Class<T> clazz;

	public Type(Class<T> clazz) {
		this.clazz = clazz;
	}

	public Class<T> getClazz() {
		return clazz;
	}

	public String getFullyQualifiedName() {
		return clazz.getSimpleName().replace("Giovi", "giovi.");
	}

	public String getSimpleName() {
		return clazz.getSimpleName().replace("Giovi", "");
	}

	public abstract Type<T> copy();


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Type<?> other = (Type<?>) obj;
		if (clazz == null) {
			if (other.clazz != null) return false;
		} else if (!clazz.equals(other.clazz)) return false;
		return true;
	}

	@Override
	public String toString() {
		return getFullyQualifiedName();
	}

	public static final class Primitive<T extends GioviValue<?>> extends Type<T> {

		public Primitive(Class<T> clazz) {
			super(clazz);
		}

		@Override
		public Primitive<T> copy() {
			return new Primitive<T>(this.clazz);
		}

	}

	public static final class List extends Type<GioviList> {

		private final Type<?> entryType;

		public List(Type<?> entryType) {
			super(GioviList.class);
			this.entryType = entryType;
		}

		public Type<?> getEntryType() {
			return entryType;
		}

		@Override
		public List copy() {
			return new List(this.entryType.copy());
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((entryType == null) ? 0 : entryType.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (!super.equals(obj)) return false;
			if (getClass() != obj.getClass()) return false;
			List other = (List) obj;
			if (entryType == null) {
				if (other.entryType != null) return false;
			} else if (!entryType.equals(other.entryType)) return false;
			return true;
		}

		@Override
		public String toString() {
			return super.toString() + String.format("[%s]", entryType);
		}

	}

	public static final class Map extends Type<GioviMap> {

		private final Type<?> keyType;
		private final Type<?> elementType;

		public Map(Type<?> keyType, Type<?> elementType) {
			super(GioviMap.class);
			this.keyType = keyType;
			this.elementType = elementType;
		}

		public Type<?> getKeyType() {
			return keyType;
		}

		public Type<?> getElementType() {
			return elementType;
		}

		@Override
		public Map copy() {
			return new Map(this.keyType.copy(), this.elementType.copy());
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((keyType == null) ? 0 : keyType.hashCode());
			result = prime * result + ((elementType == null) ? 0 : elementType.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (!super.equals(obj)) return false;
			if (getClass() != obj.getClass()) return false;
			Map other = (Map) obj;
			if (keyType == null) {
				if (other.keyType != null) return false;
			} else if (!keyType.equals(other.keyType)) return false;
			if (elementType == null) {
				if (other.elementType != null) return false;
			} else if (!elementType.equals(other.elementType)) return false;
			return true;
		}

		@Override
		public String toString() {
			return super.toString() + String.format("[%s:%s]", keyType, elementType);
		}

	}

	public static abstract sealed class Struct<T extends GioviStruct> extends Type<T> {

		public Struct(Class<T> clazz) {
			super(clazz);
		}

		public static final class Tuple extends Struct<GioviStructTuple> {

			public Tuple() {
				super(GioviStructTuple.class);
			}

			@Override
			public Tuple copy() {
				return new Tuple();
			}

		}

		public static final class Typed extends Struct<GioviStructTyped> {

			public Typed() {
				super(GioviStructTyped.class);
			}

			@Override
			public Typed copy() {
				return new Typed();
			}

		}
	}


}

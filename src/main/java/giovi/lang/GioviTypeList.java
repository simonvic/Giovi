package giovi.lang;

import static it.simonvic.giovi.util.GioviLists.copyOf;
import static it.simonvic.giovi.util.GioviUtils.nativeIndexOf;

import java.util.Collections;
import java.util.Map;

import giovi.RecordFieldSignature;
import giovi.RecordSignature;
import it.simonvic.giovi.VarD;
import it.simonvic.giovi.VarEntry;
import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.lang.annotation.Native.*;
import it.simonvic.giovi.lang.annotation.Native.Func.Param.Label;
import it.simonvic.giovi.lang.annotation.Native.Func.Param.Optional;
import it.simonvic.giovi.util.GRecords;

@Describes(GioviList.class)
public record GioviTypeList(GioviType<?> entryType, Integer initSize)
	implements GioviTypeComposite<GioviList> {

	public GioviTypeList(GioviType<?> entryType) {
		this(entryType, 0);
	}

	@Override
	public Class<? extends GioviList> value() {
		return GioviList.class;
	}

	@Override
	public String toString() {
		return "[" + entryType + "]";
	}

	@Override
	public GioviTypeList copy() {
		return new GioviTypeList(this.entryType.copy(), this.initSize);
	}

	@Override
	public boolean isWidenableTo(GioviType<?> type) {
		return switch (type) {
			case GioviTypeList t -> this.entryType.isWidenableTo(t.entryType);
			default -> GioviTypeComposite.super.isWidenableTo(type);
		};
	}

	@Override
	public GioviList widen(GioviValue<?> value) throws NonWideneable {
		return switch (value) {
			case GioviList v when v.type().entryType().equals(this.entryType) -> v;
			case GioviList v -> {
				var widened = new GioviList(this);
				for (var entry : v.value()) {
					widened.value().add(new VarEntry<>(this.entryType.widen(entry.unwrap())));
				}
				yield widened;
			}
			default -> GioviTypeComposite.super.widen(value);
		};
	}

	@Override
	public GioviList spawn() {
		var list = new GioviList(this);
		// TODO: remove initSize from the type signature
		// HACK: it doesn't belong to the type and will lead to unexpected behaviour,
		// it should be handled by the visitor appropriately
		var rawList = list.value();
		for (int i = 0; i < this.initSize; i++) {
			rawList.add(new VarEntry<>(this.entryType.spawn()));
		}
		return list;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// Native

	// TODO: range() once range is implemented as a type

	@Func
	public static GioviInt count(@Self GioviList self) {
		return new GioviInt(self.value().size());
	}

	@Func
	public static GioviBool isEmpty(@Self GioviList self) {
		return GioviBool.of(self.value().isEmpty());
	}

	@Func
	public static @Self GioviList clear(@Self GioviList self) {
		self.value().clear();
		return self;
	}

	@Func.Defaults
	public static final Map<String, GioviValue<?>> add_defaults = Map.of(
		"index", new GioviInt(-1)
	);

	@Func
	public static @Self GioviList add(
		@Self GioviList self,
		GioviAny element,
		@Label("to") @Optional GioviInt index
	) throws NonWideneable {
		int idx = index.value();
		self.value().add(
			idx >= 0 ? idx : nativeIndexOf(idx, self.value().size()) + 1,
			new VarEntry<>(self.getGioviType().entryType.widen(element.value()))
		);
		return self;
	}

	@Func
	public static @Self GioviList replace(
		@Self GioviList self,
		GioviAny element,
		GioviInt index
	) throws NonWideneable {
		self.value().set(
			nativeIndexOf(index.value(), self.value().size()),
			new VarEntry<>(self.getGioviType().entryType.widen(element))
		);
		return self;
	}

	@Func
	public static @Self GioviList remove(
		@Self GioviList self,
		GioviInt index
	) throws NonWideneable {
		self.value().remove(nativeIndexOf(index.value(), self.value().size()));
		return self;
	}

	// @Func
	// public static @Self GioviList sort(
	// 	@Self GioviList self,
	// 	@Optional GioviBool descending
	// ) throws NonWideneable {
	// 	self.value().sort((v1, v2) -> 0);
	// 	return self;
	// }

	@Func
	public static @Self GioviList reverse(@Self GioviList self) {
		Collections.reverse(self.value());
		return self;
	}

	@Func
	public static @Self GioviList reversed(@Self GioviList self) {
		return new GioviList(self.getGioviType(), copyOf(self.value()).reversed());
	}

	private static final GioviTypeRecord find_returnType = new GioviTypeRecord(
		new RecordSignature(
			new RecordFieldSignature("found", GioviType.BOOL),
			new RecordFieldSignature("index", GioviType.INT)
		)
	);

	@Func
	@TypeRecord({
		@TypeRecord.Member(name = "found", type = @Type(GioviTypeBool.class)),
		@TypeRecord.Member(name = "index", type = @Type(GioviTypeInt.class))
	})
	public static GioviRecord find(
		@Self GioviList self,
		GioviAny element
	) {
		for (int i = 0; i < self.value().size(); i++) {
			if (self.value().get(i).equal(element.value()).asNativeBool()) {
				return new GioviRecord(
					find_returnType,
					GRecords.of(
						"found", new VarD<>(GioviBool.of(true)),
						"index", new VarD<>(new GioviInt(i))
					)
				);
			}
		}
		return find_returnType.spawn();
	}

}

package giovi.util;

import giovi.lang.GioviInt;
import giovi.lang.GioviList;
import giovi.lang.GioviType;
import giovi.lang.GioviTypeList;
import it.simonvic.giovi.Entry;
import it.simonvic.giovi.lang.annotation.Native.Func;
import it.simonvic.giovi.lang.annotation.Native.Module;
import it.simonvic.giovi.lang.annotation.Native.Type;
import it.simonvic.giovi.lang.annotation.Native.TypeList;
import it.simonvic.giovi.lang.annotation.Native.TypeType;

@Module
public class ListUtils {

	@Func
	public static @TypeList(@Type) GioviList of(
		@TypeType(@Type) GioviType<?> t,
		GioviInt initSize
	) {
		// TODO: use this instead of ** operator?
		var list = new GioviList(new GioviTypeList(t));
		var rawList = list.value();
		for (int i = 0; i < initSize.value(); i++) {
			rawList.add(new Entry<>(t.spawn()));
		}
		return list;
	}


}

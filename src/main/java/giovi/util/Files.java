package giovi.util;

import java.io.IOException;
import java.nio.file.Path;

import giovi.lang.GioviList;
import giovi.lang.GioviText;
import giovi.lang.GioviType;
import giovi.lang.GioviTypeText;
import it.simonvic.giovi.VarEntry;
import it.simonvic.giovi.lang.annotation.Native.Func;
import it.simonvic.giovi.lang.annotation.Native.Module;
import it.simonvic.giovi.lang.annotation.Native.Type;
import it.simonvic.giovi.lang.annotation.Native.TypeList;

@Module
public class Files {


	@Func
	public static @TypeList(@Type(GioviTypeText.class)) GioviList lines(
		GioviText path
	) throws IOException {
		return new GioviList(
			GioviType.LIST_OF_TEXT, java.nio.file.Files
				.lines(Path.of(path.value()))
				.map(GioviText::new)
				.<VarEntry<?>>map(VarEntry::new)
				.toList()
		);
	}

}

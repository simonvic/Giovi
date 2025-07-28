package giovi.util;

import giovi.lang.GioviInt;
import giovi.lang.GioviTypeInt;
import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.lang.annotation.Native.Func;
import it.simonvic.giovi.lang.annotation.Native.Func.Param;
import it.simonvic.giovi.lang.annotation.Native.Func.Param.Label;
import it.simonvic.giovi.lang.annotation.Native.Module;
import it.simonvic.giovi.lang.annotation.Native.Type;

@Module
public class Thread {

	@Func
	public static void sleep(@Label("forMS") @Param("ms") @Type(GioviTypeInt.class) GioviInt ms) {
		try {
			java.lang.Thread.sleep(ms.value());
		} catch (InterruptedException e) {
			throw new GioviException(e);
		}
	}


}

package giovi;

import java.util.HashMap;
import java.util.Map;

public enum AccessKey implements GioviParserVisitResult {

	COUNT("%"),
	KEYS("@"),
	VALUES("*");

	private static final Map<String, AccessKey> cache = new HashMap<>();

	static {
		for (var token : values()) {
			cache.put(token.label, token);
		}
	}

	public static AccessKey of(String label) {
		return cache.get(label);
	}

	public final String label;

	private AccessKey(String label) {
		this.label = label;
	}

}

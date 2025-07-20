package giovi;

import java.net.URI;

import it.simonvic.giovi.antlr.GioviParser.BlockContext;
import it.simonvic.giovi.lang.ModuleScope;

public sealed interface Module extends GioviParserVisitResult {

	Module parent();

	ModuleScope scope();

	@Override
	default GioviParserVisitResult dot(GioviParserVisitResult right) {
		if (right instanceof MemberKey(var identifier)) {
			var symbolOpt = scope().getSymbol(identifier);
			if (symbolOpt.isPresent()) {
				return symbolOpt.orElseThrow();
			}
		}
		return GioviParserVisitResult.super.dot(right);
	}

	public static record NativeModule(
		String identifier,
		Module parent,
		ModuleScope scope
	) implements Module {

		public NativeModule(String identifier, Module parent) {
			this(identifier, parent, new ModuleScope(parent.scope()));
		}

		public NativeModule(String identifier) {
			this(identifier, null, new ModuleScope());
		}

		@Override
		public String toString() {
			return (parent() != null ? parent().toString() : "") + "%" + identifier;
		}

	}

	public static record FileModule(
		URI uri,
		Module parent,
		ModuleScope scope
	) implements Module {

		public FileModule(URI uri, Module parent) {
			this(uri, parent, new ModuleScope(parent.scope()));
		}

		public FileModule(URI uri) {
			this(uri, null, new ModuleScope());
		}

		@Override
		public String toString() {
			return (parent() != null ? parent().toString() : "") + "/" + uri;
		}

	}


	public static record InnerModule(
		Module parent,
		ModuleScope scope,
		String identifier,
		BlockContext bodyCtx
	) implements Module {

		public InnerModule(Module parent, String identifier, BlockContext bodyCtx) {
			this(parent, new ModuleScope(parent.scope()), identifier, bodyCtx);
		}

		@Override
		public String toString() {
			return (parent() != null ? parent().toString() : "") + "$" + identifier;
		}


	}

}

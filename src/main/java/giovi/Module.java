package giovi;

import java.net.URI;
import java.nio.file.Path;

import it.simonvic.giovi.antlr.GioviParser.BlockContext;
import it.simonvic.giovi.lang.ModuleScope;

public sealed interface Module extends GioviParserVisitResult {

	String identifier();

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
			this(
				identifier,
				parent,
				(parent != null)
					? new ModuleScope(parent.scope())
					: new ModuleScope());
		}

		public NativeModule(String identifier) {
			this(identifier, null);
		}

		@Override
		public String toString() {
			return (parent() != null ? parent().toString() : "") + "%" + identifier;
		}

	}

	public static record FileModule(
		String identifier,
		Module parent,
		ModuleScope scope,
		URI uri
	) implements Module {

		public FileModule(String identifier, Module parent, URI uri) {
			this(identifier, parent, new ModuleScope(parent.scope()), uri);
		}

		@Override
		public String toString() {
			return (parent() != null ? parent().toString() : "") + "/" + identifier;
		}

	}


	public static record InnerModule(
		String identifier,
		Module parent,
		ModuleScope scope,
		BlockContext bodyCtx
	) implements Module {

		public InnerModule(String identifier, Module parent, BlockContext bodyCtx) {
			this(identifier, parent, new ModuleScope(parent.scope()), bodyCtx);
		}

		@Override
		public String toString() {
			return (parent() != null ? parent().toString() : "") + "$" + identifier;
		}


	}

}

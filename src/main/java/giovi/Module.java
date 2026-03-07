package giovi;

import java.net.URI;

import giovi.lang.GioviFunction;
import it.simonvic.giovi.GioviVisitor;
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

	@Override
	default GioviParserVisitResult dotRef(GioviParserVisitResult right) {
		if (right instanceof MemberKey(var identifier)) {
			var symbolOpt = scope().getSymbol(identifier);
			if (symbolOpt.isPresent()
				&& symbolOpt.orElseThrow() instanceof GioviFunction function) {
				return function;
			}
		}
		return GioviParserVisitResult.super.dotRef(right);
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
		ModuleScope scope
	) implements Module {

		public FileModule(URI uri) {
			this(uri, new ModuleScope(GioviVisitor.ROOT_MODULE.scope()));
		}

		@Override
		public Module parent() {
			return GioviVisitor.ROOT_MODULE;
		}

		@Override
		public String toString() {
			return (parent() != null ? parent().toString() : "") + "/" + getPath();
		}

		public String getPath() {
			// can we assume the scheme is always like this?
			return uri.toString().replaceFirst("file://", "");
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

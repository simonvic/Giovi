package giovi;

import java.net.URI;

import giovi.lang.GioviFunction;
import it.simonvic.giovi.GioviVisitor;
import it.simonvic.giovi.antlr.GioviParser.BlockContext;
import it.simonvic.giovi.lang.Scope;

public abstract sealed class Module
	extends Scope
	implements GioviParserVisitResult {

	public Module() {
		super();
	}

	public Module(Scope parent) {
		super(parent);
	}

	@Override
	public GioviParserVisitResult dot(GioviParserVisitResult right) {
		if (right instanceof MemberKey(var identifier)) {
			var symbolOpt = this.getSymbol(identifier);
			if (symbolOpt.isPresent()) {
				return symbolOpt.orElseThrow();
			}
		}
		return GioviParserVisitResult.super.dot(right);
	}

	@Override
	public GioviParserVisitResult dotRef(GioviParserVisitResult right) {
		if (right instanceof MemberKey(var identifier)) {
			var symbolOpt = this.getSymbol(identifier);
			if (
				symbolOpt.isPresent()
					&& symbolOpt.orElseThrow() instanceof GioviFunction function
			) {
				return function;
			}
		}
		return GioviParserVisitResult.super.dotRef(right);
	}


	public static final class NativeModule extends Module {

		private final String identifier;

		public NativeModule(String identifier, Scope parent) {
			super(parent);
			this.identifier = identifier;
		}

		public NativeModule(String identifier) {
			super();
			this.identifier = identifier;
		}

		public String identifier() {
			return identifier;
		}

		@Override
		public String toString() {
			return (getParent() != null ? getParent().toString() : "") + "%" + identifier;
		}

	}

	public static final class FileModule extends Module {

		private final URI uri;

		public FileModule(URI uri) {
			super(GioviVisitor.ROOT_MODULE);
			this.uri = uri;
		}

		public URI uri() {
			return uri;
		}

		public String getPath() {
			// can we assume the scheme is always like this?
			return uri.toString().replaceFirst("file://", "");
		}

		@Override
		public String toString() {
			return (getParent() != null ? getParent().toString() : "") + "/" + getPath();
		}

	}


	public static final class InnerModule extends Module {

		private final String identifier;
		private final BlockContext bodyCtx;

		public InnerModule(Module parent, String identifier, BlockContext bodyCtx) {
			super(parent);
			this.identifier = identifier;
			this.bodyCtx = bodyCtx;
		}

		public String identifier() {
			return identifier;
		}

		public BlockContext bodyCtx() {
			return bodyCtx;
		}

		@Override
		public String toString() {
			return (getParent() != null ? getParent().toString() : "") + "$" + identifier;
		}


	}

}

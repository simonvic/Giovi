package it.simonvic.giovi.lsp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.DocumentSymbol;

import giovi.*;
import giovi.Module;
import giovi.lang.*;
import it.simonvic.giovi.Entry;
import it.simonvic.giovi.GField;
import it.simonvic.giovi.ValueWrapper;
import it.simonvic.giovi.Var;
import it.simonvic.giovi.antlr.GioviParser.*;
import it.simonvic.giovi.antlr.GioviParserBaseVisitor;
import it.simonvic.giovi.exceptions.NonWideneable;
import it.simonvic.giovi.lang.*;
import it.simonvic.giovi.util.GioviTypes;

public class LSVisitor extends GioviParserBaseVisitor<GioviParserVisitResult> {

	private final Module module;
	private final List<Diagnostic> diagnostics = new ArrayList<>();
	private final List<CodeAction> codeActions = new ArrayList<>();
	private final List<DocumentSymbol> symbols = new ArrayList<>();

	private Scope currentScope;

	public LSVisitor(Module module) {
		this.module = module;
		this.currentScope = module;
	}

	public List<Diagnostic> getDiagnostics() {
		return diagnostics;
	}

	public List<CodeAction> getCodeActions() {
		return codeActions;
	}

	public List<DocumentSymbol> getSymbols() {
		return symbols;
	}

	public Scope getCurrentScope() {
		return currentScope;
	}

	private void setCurrentScope(Scope scope) {
		currentScope = scope;
	}

	private void enterScope() {
		setCurrentScope(new Scope(getCurrentScope()));
	}

	private void enterFunctionScope() {
		setCurrentScope(new FunctionScope(getCurrentScope()));
	}

	private void enterLoopScope() {
		setCurrentScope(new LoopScope(getCurrentScope()));
	}

	private void exitScope() {
		setCurrentScope(getCurrentScope().getParent());
	}

	@Override
	public GioviParserVisitResult visitConstantDeclaration(ConstantDeclarationContext ctx) {
		GioviValue<?> constant = null;
		try {
			constant = doVisitConstant(ctx);
		} catch (NonWideneable e) {
			diagnostics.add(new Diagnostic(LSPUtils.range(ctx), e.getMessage()));
		}
		return constant;
	}

	private GioviValue<?> doVisitConstant(ConstantDeclarationContext ctx) throws NonWideneable {
		var constant = visit(ctx.expression()).unwrapAsGioviValue();
		if (ctx.type() != null) {
			var type = visitType(ctx.type());
			constant = constant.widenTo(type);
		}
		return constant;
	}


	@Override
	public GioviParserVisitResult visitStructDefinition(StructDefinitionContext ctx) {
		var fields = new LinkedHashMap<String, RecordFieldSignature>();
		String typename = ctx.typename.getText();

		if (ctx.preAggregates != null) {
			aggregate(fields, ctx.preAggregates.typeQualified());
		}

		for (var fieldCtx : ctx.structBodyDefinition().fieldDeclaration()) {
			var field = (RecordFieldSignature) visit(fieldCtx);
			if (fields.containsKey(field.identifier())) {
				var inheritedField = fields.get(field.identifier());
				if (!inheritedField.type().equals(field.type())) {
					diagnostics.add(
						new Diagnostic(
							LSPUtils.range(fieldCtx),
							"Can't override field " + field.identifier() + " with type "
								+ field.type()
								+ " because inherited with type " + inheritedField.type()
						)
					);
				}
			}
			fields.put(field.identifier(), field);
		}

		if (ctx.postAggregates != null) {
			aggregate(fields, ctx.postAggregates.typeQualified());
		}

		var constants = new HashMap<String, GioviValue<?>>();
		var functions = new HashMap<String, GioviFunction>();
		var structSignature = new StructSignature(
			typename,
			fields,
			constants,
			functions
		);
		var structType = new GioviTypeStruct(structSignature);
		getCurrentScope().define(structType);

		for (var constCtx : ctx.structBodyDefinition().constantDeclaration()) {
			GioviValue<?> constant;
			try {
				constant = doVisitConstant(constCtx);
				constants.put(constCtx.identifier().getText(), constant);
			} catch (NonWideneable e) {
				diagnostics.add(new Diagnostic(LSPUtils.range(constCtx), e.getMessage()));
			}
		}

		for (var funcCtx : ctx.structBodyDefinition().functionDeclaration()) {
			var signature = (FunctionSignature) visit(funcCtx.functionSignature());
			functions.put(
				signature.identifier(),
				new GioviFunction(new GFunction(signature, funcCtx.body))
			);
		}

		for (var funcCtx : ctx.structBodyDefinition().selfFunctionDeclaration()) {
			var signatureCtx = funcCtx.selfFunctionSignature();

			String identifier = signatureCtx.name.getText();

			var parameters = (FormalParameters) visit(signatureCtx.formalParameters());
			if (funcCtx.selfFunctionSignature().self != null) {
				parameters.parameters()
					.putFirst("self", new FormalParameter("self", GioviType.SELF));
			}

			GioviType<?> returnType;
			if (signatureCtx.returnType != null) {
				returnType = visit(signatureCtx.returnType).asGioviType();
			} else if (signatureCtx.returnSelf != null) {
				returnType = GioviType.SELF;
			} else {
				returnType = GioviType.VOID;
			}

			var signature = new FunctionSignature(identifier, parameters, returnType);
			functions.put(identifier, new GioviFunction(new GFunction(signature, funcCtx.body)));
		}

		return null;
	}

	/**
	 * @param fields
	 * @param aggregatesTypesCtx
	 */
	private void aggregate(
		LinkedHashMap<String, RecordFieldSignature> fields,
		List<TypeQualifiedContext> aggregatesTypesCtx
	) {
		for (var aggregateTypeCtx : aggregatesTypesCtx) {
			var aggregateType = visit(aggregateTypeCtx);
			if (aggregateType instanceof GioviTypeStruct aggregateTypeStruct) {
				for (var inheritedField : aggregateTypeStruct.signature().fields().values()) {
					fields.put(inheritedField.identifier(), inheritedField);
				}
			} else {
				diagnostics.add(new Diagnostic(LSPUtils.range(aggregateTypeCtx), "Unknown type"));
			}
		}
	}

	@Override
	public GioviParserVisitResult visitFieldDeclaration(FieldDeclarationContext ctx) {
		String identifier = ctx.identifier().getText();
		if (ctx.SELF() != null) {
			return new RecordFieldSignature(identifier, GioviType.SELF);
		}
		var type = visit(ctx.type()).asGioviType();
		GioviValue<?> defaultValue;
		try {
			defaultValue = ctx.defaultDefinition() == null
				? type.spawn()
				: type.widen(visit(ctx.defaultDefinition()).asGioviValue());
		} catch (NonWideneable e) {
			diagnostics.add(new Diagnostic(LSPUtils.range(ctx), e.getMessage()));
			defaultValue = type.spawn();
		}
		return new RecordFieldSignature(identifier, type, defaultValue);
	}

	@Override
	public FunctionSignature visitFunctionDeclaration(FunctionDeclarationContext ctx) {
		var signature = visitFunctionSignature(ctx.functionSignature());
		var function = new GFunction(signature, ctx.body);
		getCurrentScope().declare(function);
		return signature;
	}

	@Override
	public FunctionSignature visitFunctionSignature(FunctionSignatureContext ctx) {
		String identifier = ctx.name.getText();
		var signature = visitAnonFunctionSignature(ctx.anonFunctionSignature());
		return new FunctionSignature(identifier, signature.parameters(), signature.returnType());
	}

	@Override
	public FunctionSignature visitAnonFunctionSignature(AnonFunctionSignatureContext ctx) {
		var parameters = (FormalParameters) visit(ctx.formalParameters());

		GioviType<?> returnType = ctx.returnType != null
			? visit(ctx.returnType).asGioviType()
			: GioviType.VOID;

		return new FunctionSignature("", parameters, returnType);
	}

	@Override
	public FormalParameters visitFormalParameters(FormalParametersContext ctx) {
		int size = ctx.formalParameter().size();
		var parameters = new FormalParameter[size];
		for (int i = 0; i < size; i++) {
			parameters[i] = (FormalParameter) visit(ctx.formalParameter(i));
		}
		return new FormalParameters(parameters);
	}

	@Override
	public FormalParameter visitFormalParameter(FormalParameterContext ctx) {
		String identifier = ctx.name.getText();
		String label = ctx.label != null
			? ctx.label.getText()
			: identifier;
		var type = visitType(ctx.type());
		Var<?> param;
		try {
			param = spawnVar(type, ctx.defaultDefinition());
		} catch (NonWideneable e) {
			diagnostics.add(new Diagnostic(LSPUtils.range(ctx), e.getMessage()));
			param = new Var<>(type.spawn());
		}
		boolean isOptional = ctx.defaultDefinition() != null;
		return new FormalParameter(label, identifier, type, param.unwrap(), isOptional);
	}

	@Override
	public GioviParserVisitResult visitInvocation(InvocationContext ctx) {
		return new GioviVoid();
	}

	@Override
	public GioviParserVisitResult visitExpressionInvocation(ExpressionInvocationContext ctx) {
		return new GioviVoid();
	}

	private Var<?> spawnVar(
		TypeContext typeCtx,
		DefaultDefinitionContext defaultDefinitionCtx
	) throws NonWideneable {
		return spawnVar(visit(typeCtx).asGioviType(), defaultDefinitionCtx);
	}

	private Var<?> spawnVar(
		GioviType<?> type,
		DefaultDefinitionContext defaultDefinitionCtx
	) throws NonWideneable {
		if (defaultDefinitionCtx == null) {
			return new Var<>(type.spawn(), type.spawn());
		}
		var defaultValue = visit(defaultDefinitionCtx).unwrapAsGioviValue();
		defaultValue = type.widen(defaultValue);
		return new Var<>(defaultValue.copy(), defaultValue);
	}

	@Override
	public GioviType<?> visitType(TypeContext ctx) {
		return super.visitType(ctx).asGioviType();
	}

	@Override
	public GioviType<?> visitTypeQualified(TypeQualifiedContext ctx) {
		var scope = getCurrentScope();
		for (int i = 0; i < ctx.identifier().size() - 1; i++) { // NOTE: last identifier is the typename
			var moduleIdentifierCtx = ctx.identifier(i);
			String moduleIdentifier = moduleIdentifierCtx.getText();
			var optModule = scope.getModule(moduleIdentifier);
			if (optModule.isEmpty()) {
				diagnostics
					.add(new Diagnostic(LSPUtils.range(moduleIdentifierCtx), "Can't find module"));
			} else {
				scope = optModule.orElseThrow();
			}
		}
		String typename = ctx.typename.getText();
		var optScope = scope.getType(typename);
		if (optScope.isEmpty()) {
			diagnostics
				.add(new Diagnostic(LSPUtils.range(ctx.typename), "Type not found " + typename));
			return null;
		}
		return optScope.orElseThrow();
	}

	@Override
	public GioviTypeList visitTypeList(TypeListContext ctx) {
		return new GioviTypeList(visitType(ctx.type()));
	}

	@Override
	public GioviTypeMap visitTypeMap(TypeMapContext ctx) {
		return new GioviTypeMap(visitType(ctx.mapKeyType), visitType(ctx.mapValueType));
	}

	@Override
	public Var<?> visitVarDeclaration(VarDeclarationContext ctx) {
		try {
			var type = visitType(ctx.type());
			var value = type.spawn();
			GioviValue<?> defaultValue;
			defaultValue = (ctx.defaultDefinition() != null)
				? type.widen(visit(ctx.defaultDefinition()).asGioviValue())
				: type.spawn();
			var variable = new Var<>(value, defaultValue);
			getCurrentScope().declare(ctx.identifier().getText(), variable);
			return variable;
		} catch (Exception e) {
			diagnostics.add(new Diagnostic(LSPUtils.range(ctx), e.getMessage()));
		}
		return null;
	}

	@Override
	public GioviParserVisitResult visitVarDeclarationInfer(VarDeclarationInferContext ctx) {
		try {
			var value = visit(ctx.value).unwrapAsGioviValue();
			var var = (ctx.type() != null)
				? spawnVar(ctx.type(), ctx.defaultDefinition())
				: spawnVar(value.getGioviType(), ctx.defaultDefinition());
			var.setValueWithRedirection(value);
			getCurrentScope().declare(ctx.identifier().getText(), var);
			return var;
		} catch (Exception e) {
			diagnostics.add(new Diagnostic(LSPUtils.range(ctx), e.getMessage()));
		}
		return null;
	}

	@Override
	public GioviParserVisitResult visitRedirectionToReservedIdentifier(
		RedirectionToReservedIdentifierContext ctx
	) {
		checkRedirectionToStdInput(ctx.dest);
		return super.visitRedirectionToReservedIdentifier(ctx);
	}

	@Override
	public GioviParserVisitResult visitRedirectionToReservedIdentifierRTL(
		RedirectionToReservedIdentifierRTLContext ctx
	) {
		checkRedirectionToStdInput(ctx.dest);
		return super.visitRedirectionToReservedIdentifierRTL(ctx);
	}

	private void checkRedirectionToStdInput(ReservedIdentifierContext dest) {
		if (dest.getText().equals("_in")) {
			var d = new Diagnostic();
			d.setRange(LSPUtils.range(dest));
			d.setSeverity(DiagnosticSeverity.Error);
			d.setMessage("Can't redirect to standard input");
			diagnostics.add(d);
		}
	}

	@Override
	public GioviParserVisitResult visitReservedIdentifier(ReservedIdentifierContext ctx) {
		switch (ctx.getText()) {
			case "_in", "_out" -> {}
			case String s -> {
				var d = new Diagnostic();
				d.setRange(LSPUtils.range(ctx));
				d.setMessage("Unknown reserved identifier: " + s);
				d.setSeverity(DiagnosticSeverity.Error);
				diagnostics.add(d);
			}
		}
		return null;
	}

	@Override
	public GioviParserVisitResult visitTupleCreationBody(TupleCreationBodyContext ctx) {
		int size = ctx.expression().size();
		var fields = new ArrayList<GField<?>>(size);
		var fieldsSignature = new ArrayList<TupleFieldSignature>(size);
		for (var expressionCtx : ctx.expression()) {
			var fieldValue = visit(expressionCtx).unwrapAsGioviValue();
			var fieldType = fieldValue.getGioviType();
			fields.add(new GField<>(fieldValue, fieldType.spawn()));
			fieldsSignature.add(new TupleFieldSignature(fieldType));
		}
		return new GioviTuple(
			new GioviTypeTuple(new TupleSignature(fieldsSignature)),
			new GTuple(fields)
		);

	}

	@Override
	public GioviParserVisitResult visitRecordCreationBody(RecordCreationBodyContext ctx) {
		int size = ctx.recordEntry().size();
		var fields = new LinkedHashMap<String, GField<?>>(size);
		var fieldsSignature = new LinkedHashMap<String, RecordFieldSignature>(size);
		for (var entryCtx : ctx.recordEntry()) {
			var fieldValue = visit(entryCtx.expression()).unwrapAsGioviValue();
			var fieldType = fieldValue.getGioviType();
			String identifier = entryCtx.identifier().getText();
			fields.put(identifier, new GField<>(fieldValue, fieldType.spawn()));
			fieldsSignature.put(identifier, new RecordFieldSignature(identifier, fieldType));
		}
		return new GioviRecord(
			new GioviTypeRecord(new RecordSignature(fieldsSignature)),
			new GRecord(fields)
		);
	}

	@Override
	public GioviStruct visitStructCreation(StructCreationContext ctx) {
		var type = visit(ctx.typeQualified());
		if (!(type instanceof GioviTypeStruct typeStruct)) {
			diagnostics.add(
				new Diagnostic(
					LSPUtils.range(ctx.typeQualified()), "Can't create struct: unknown type"
				)
			);
			return null;
		}
		return typeStruct.spawn();
	}

	@Override
	public Range<?> visitRange(RangeContext ctx) {
		try {
			return Range.of(
				visit(ctx.start).unwrapAsGioviValue(),
				visit(ctx.end).unwrapAsGioviValue(),
				ctx.step == null ? null : visit(ctx.step)
			);
		} catch (Exception e) {
			diagnostics.add(new Diagnostic(LSPUtils.range(ctx), "Can't create range"));
			return null;
		}
	}

	@Override
	public GioviList visitListCreation(ListCreationContext ctx) {
		var entries = new ArrayList<Entry<?>>(ctx.listEntries().listEntry().size());
		for (var entryCtx : ctx.listEntries().listEntry()) {
			var entry = visit(entryCtx);
			switch (entry) {
				case Range<?> range -> entries.addAll(range.expandAsVarEntries());
				case GioviValue<?> v -> entries.add(new Entry<>(v));
				case ValueWrapper<?> v -> entries.add(new Entry<>(v.unwrap().copy()));
				case null -> {}
				default -> diagnostics.add(
					new Diagnostic(
						LSPUtils.range(entryCtx),
						"Incompatible type for list creation: " + entry.getClass()
					)
				);
			}
		}
		var entryType = GioviTypes.inferGioviType(entries);
		return new GioviList(new GioviTypeList(entryType));
	}

	@Override
	public GioviMap visitMapCreation(MapCreationContext ctx) {
		var map = new LinkedHashMap<Entry<?>, Entry<?>>();
		if (ctx.mapEntries() == null) {
			return new GioviMap(new GioviTypeMap(GioviType.VOID, GioviType.VOID), map);
		}
		for (var entryCtx : ctx.mapEntries().mapEntry()) {
			var key = visit(entryCtx.mapKey).unwrapAsGioviValue();
			var element = visit(entryCtx.mapValue).unwrapAsGioviValue();
			map.put(new Entry<>(key), new Entry<>(element));
		}
		var keyType = GioviTypes.inferGioviType(map.keySet());
		var elementType = GioviTypes.inferGioviType(map.values());
		return new GioviMap(new GioviTypeMap(keyType, elementType));
	}

	@Override
	public GioviParserVisitResult visitIdentifier(IdentifierContext ctx) {
		String identifier = ctx.getText();
		return getCurrentScope()
			.getSymbol(identifier)
			.orElseGet(() -> {
				diagnostics
					.add(
						new Diagnostic(
							LSPUtils.range(ctx),
							"No variable, constant, function, type or module found with name "
								+ identifier
						)
					);
				return null;
			});
	}

	@Override
	public GioviInt visitLiteralInteger(LiteralIntegerContext ctx) {
		return new GioviInt();
	}

	@Override
	public GioviNatural visitLiteralNatural(LiteralNaturalContext ctx) {
		return new GioviNatural();
	}

	@Override
	public GioviReal visitLiteralReal(LiteralRealContext ctx) {
		return new GioviReal();
	}

	@Override
	public GioviBool visitLiteralBool(LiteralBoolContext ctx) {
		return new GioviBool();
	}

	@Override
	public GioviText visitLiteralText(LiteralTextContext ctx) {
		return new GioviText();
	}

	@Override
	public GioviText visitLiteralTextBlock(LiteralTextBlockContext ctx) {
		return new GioviText();
	}

	@Override
	public GioviText visitLiteralExactText(LiteralExactTextContext ctx) {
		return new GioviText();
	}

	@Override
	public GioviText visitLiteralExactTextBlock(LiteralExactTextBlockContext ctx) {
		return new GioviText();
	}

	@Override
	public GioviParserVisitResult visitLiteralDefault(LiteralDefaultContext ctx) {
		return new GioviDefault();
	}

	@Override
	public GioviParserVisitResult visitLiteralPlaceholder(LiteralPlaceholderContext ctx) {
		return new GioviVoid();
	}

}

package it.simonvic.giovi.visitors;

import it.simonvic.giovi.GioviVisitor;
import it.simonvic.giovi.antlr.GioviParser.*;
import it.simonvic.giovi.lang.GioviObject;
import it.simonvic.giovi.lang.type.GioviType;
import it.simonvic.giovi.lang.values.*;

public class StructHelperVisitor extends GioviHelperVisitor {

	public StructHelperVisitor(GioviVisitor gioviVisitor) {
		super(gioviVisitor);
	}

	public static GioviStruct evaluate(GioviVisitor visitor, StructDefinitionContext ctx) {
		return new StructHelperVisitor(visitor).evaluate(ctx);

	}

	public GioviStruct evaluate(StructDefinitionContext ctx) {
		var s = new Struct.Named();
		for (var attributeCtx : ctx.structBodyDefinition().structAttributeDefinition()) {
			s.put(attributeCtx.identifier().getText(), visit(attributeCtx).asGioviValue());
		}
//		for (var structDefinitionCtx : ctx.structBodyDefinition().structDefinition()) {
//			s.put(structDefinitionCtx.structName.getText(), visit(structDefinitionCtx).asGioviStruct());
//		}
//		for (var functionCtx : ctx.structBodyDefinition().functionDeclaration()) {
//			s.put(functionCtx.identifier().getText(), visit(functionCtx).asGioviFunction());
//		}

		return new GioviNamedStruct(s);
	}

	@Override
	public GioviObject visitStructAttributeDefinition(StructAttributeDefinitionContext ctx) {
		GioviType type = gioviVisitor.visit(ctx.type()).asGioviType();
		GioviValue<?> value;
		if (type.unbox() == GioviList.class) {
			GioviType entryType = gioviVisitor.visit(ctx.type().collectionType().listType().type()).asGioviType();
			value = new GioviList(entryType);
		} else if (type.unbox() == GioviMap.class) {
			GioviType keyType = gioviVisitor.visit(ctx.type().collectionType().mapType().mapKeyType).asGioviType();
			GioviType elementType = gioviVisitor.visit(ctx.type().collectionType().mapType().mapValueType).asGioviType();
			value = new GioviMap(keyType, elementType);
		} else if (gioviVisitor.getCurrentScope().isDefinedStruct(ctx.type().getText())) {
			value = gioviVisitor.getCurrentScope().getDefinitionStruct(ctx.type().getText());
		} else {
			value = type.spawn();
		}

		if (ctx.defaultDefinition() != null) {
			var defaultValue = gioviVisitor.visit(ctx.defaultDefinition()).asGioviValue();
			value.setDefaultValue(defaultValue);
			value.box(defaultValue);
		}

		return value;
	}
}

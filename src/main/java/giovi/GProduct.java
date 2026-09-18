package giovi;

public sealed interface GProduct permits GTuple {

	int getFieldsCount();

	GProduct copy();

	GTuple as(TupleSignature signature);

	GRecord as(RecordSignature signature);

	GStruct as(StructSignature signature);

}


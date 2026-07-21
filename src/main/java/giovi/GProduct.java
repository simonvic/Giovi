package giovi;

public sealed interface GProduct permits GTuple {

	GProduct copy();

	GTuple as(TupleSignature signature);

}


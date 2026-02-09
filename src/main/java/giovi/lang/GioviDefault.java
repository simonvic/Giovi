package giovi.lang;

import giovi.GioviParserVisitResult;

public class GioviDefault implements GioviParserVisitResult {

	/*
	 TODO: in case we want to allow `default * foo` along `foo * default`
	 Naah that's too much

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	//////////////////////////////////////////////////////////////////////////////////////////////// cast

	@Override
	public GioviParserVisitResult as(GioviParserVisitResult right) {
		return switch (right) {
			case GioviValue<?> v -> v.getGioviType().spawn().as(v);
			default -> GioviParserVisitResult.super.as(right);
		};
	}

	//////////////////////////////////////////////////////////////////////////////////////////////// redirection

	@Override
	public GioviParserVisitResult redirection(GioviParserVisitResult content) {
		return switch (content) {
			case GioviValue<?> v -> v.getGioviType().spawn().redirection(content);
			default -> GioviParserVisitResult.super.redirection(content);
		};
	}

	@Override
	public GioviParserVisitResult redirectionPlus(GioviParserVisitResult content) {
		return switch (content) {
			case GioviValue<?> v -> v.getGioviType().spawn().redirectionPlus(content);
			default -> GioviParserVisitResult.super.redirectionPlus(content);
		};
	}

	@Override
	public GioviParserVisitResult redirectionMinus(GioviParserVisitResult content) {
		return switch (content) {
			case GioviValue<?> v -> v.getGioviType().spawn().redirectionMinus(content);
			default -> GioviParserVisitResult.super.redirectionMinus(content);
		};
	}

	@Override
	public GioviParserVisitResult redirectionMultiply(GioviParserVisitResult content) {
		return switch (content) {
			case GioviValue<?> v -> v.getGioviType().spawn().redirectionMultiply(content);
			default -> GioviParserVisitResult.super.redirectionMultiply(content);
		};
	}

	@Override
	public GioviParserVisitResult redirectionDivide(GioviParserVisitResult content) {
		return switch (content) {
			case GioviValue<?> v -> v.getGioviType().spawn().redirectionDivide(content);
			default -> GioviParserVisitResult.super.redirectionDivide(content);
		};
	}


	//////////////////////////////////////////////////////////////////////////////////////////////// arithmetic

	@Override
	public GioviParserVisitResult plus(GioviParserVisitResult right) {
		return switch (right) {
			case GioviValue<?> v -> v.getGioviType().spawn().plus(right);
			default -> GioviParserVisitResult.super.plus(right);
		};
	}

	@Override
	public GioviParserVisitResult minus(GioviParserVisitResult right) {
		return switch (right) {
			case GioviValue<?> v -> v.getGioviType().spawn().minus(right);
			default -> GioviParserVisitResult.super.minus(right);
		};
	}

	@Override
	public GioviParserVisitResult multiply(GioviParserVisitResult right) {
		return switch (right) {
			case GioviValue<?> v -> v.getGioviType().spawn().multiply(right);
			default -> GioviParserVisitResult.super.multiply(right);
		};
	}

	@Override
	public GioviParserVisitResult divide(GioviParserVisitResult right) {
		return switch (right) {
			case GioviValue<?> v -> v.getGioviType().spawn().divide(right);
			default -> GioviParserVisitResult.super.divide(right);
		};
	}

	@Override
	public GioviParserVisitResult modulo(GioviParserVisitResult right) {
		return switch (right) {
			case GioviValue<?> v -> v.getGioviType().spawn().modulo(right);
			default -> GioviParserVisitResult.super.modulo(right);
		};
	}

	@Override
	public GioviParserVisitResult power(GioviParserVisitResult right) {
		return switch (right) {
			case GioviValue<?> v -> v.getGioviType().spawn().power(right);
			default -> GioviParserVisitResult.super.power(right);
		};
	}



	//////////////////////////////////////////////////////////////////////////////////////////////// boolean

	@Override
	public GioviParserVisitResult and(GioviParserVisitResult right) {
		return switch (right) {
			case GioviValue<?> v -> v.getGioviType().spawn().and(right);
			default -> GioviParserVisitResult.super.and(right);
		};
	}

	@Override
	public GioviParserVisitResult or(GioviParserVisitResult right) {
		return switch (right) {
			case GioviValue<?> v -> v.getGioviType().spawn().or(right);
			default -> GioviParserVisitResult.super.or(right);
		};
	}


	//////////////////////////////////////////////////////////////////////////////////////////////// relational

	@Override
	public GioviParserVisitResult equal(GioviParserVisitResult right) {
		return switch (right) {
			case GioviValue<?> v -> v.getGioviType().spawn().equal(right);
			default -> GioviParserVisitResult.super.equal(right);
		};
	}

	@Override
	public GioviParserVisitResult notEqual(GioviParserVisitResult right) {
		return switch (right) {
			case GioviValue<?> v -> v.getGioviType().spawn().notEqual(right);
			default -> GioviParserVisitResult.super.notEqual(right);
		};
	}

	@Override
	public GioviParserVisitResult less(GioviParserVisitResult right) {
		return switch (right) {
			case GioviValue<?> v -> v.getGioviType().spawn().less(right);
			default -> GioviParserVisitResult.super.less(right);
		};
	}

	@Override
	public GioviParserVisitResult lessEqual(GioviParserVisitResult right) {
		return switch (right) {
			case GioviValue<?> v -> v.getGioviType().spawn().lessEqual(right);
			default -> GioviParserVisitResult.super.lessEqual(right);
		};
	}

	@Override
	public GioviParserVisitResult greater(GioviParserVisitResult right) {
		return switch (right) {
			case GioviValue<?> v -> v.getGioviType().spawn().greater(right);
			default -> GioviParserVisitResult.super.greater(right);
		};
	}

	@Override
	public GioviParserVisitResult greaterEqual(GioviParserVisitResult right) {
		return switch (right) {
			case GioviValue<?> v -> v.getGioviType().spawn().greaterEqual(right);
			default -> GioviParserVisitResult.super.greaterEqual(right);
		};
	}

	@Override
	public GioviParserVisitResult contains(GioviParserVisitResult right) {
		return switch (right) {
			case GioviValue<?> v -> v.getGioviType().spawn().contains(right);
			default -> GioviParserVisitResult.super.contains(right);
		};
	}

	@Override
	public GioviParserVisitResult isContained(GioviParserVisitResult right) {
		return switch (right) {
			case GioviValue<?> v -> v.getGioviType().spawn().isContained(right);
			default -> GioviParserVisitResult.super.isContained(right);
		};
	}


	//////////////////////////////////////////////////////////////////////////////////////////////// access

	*/
}

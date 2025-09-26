package it.simonvic.giovi.lsp;

public class GioviDocument {

	private final String uri;
	private Integer version;
	private String content;
	// private boolean dirtyAST;
	// private ParseTree ast;

	public GioviDocument(String uri) {
		this.uri = uri;
	}

	public GioviDocument(String uri, Integer version, String content) {
		this.uri = uri;
		this.version = version;
		this.content = content;
	}

	public String getUri() {
		return uri;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
		// if (!this.content.equals(content)) {
		// 	this.dirtyAST = true;
		// }
	}

	// public boolean isDirtyAST() {
	// 	return dirtyAST;
	// }
	//
	// public ParseTree getAST() {
	// 	if (dirtyAST) {
	// 		updateAST();
	// 	}
	// 	return ast;
	// }
	//
	// public void updateAST() {
	// 	ast = LSPUtils.extendedParserFrom(content).begin();
	// }
	//

}


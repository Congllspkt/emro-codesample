package smartsuite.app.shared;

import com.docusign.esign.model.Document;

public class DocumentMgt implements Comparable<DocumentMgt>{
	private int index;	//document 순서를 지정하기 위한 변수
	private Document document;
	
	public DocumentMgt(){
		
	}
	public DocumentMgt(int index,Document document){
		this.index = index;
		this.document = document;
	}
	
	@Override
	public int compareTo(DocumentMgt documentMgt) {
		int targetIndex = documentMgt.index;
		if(index == targetIndex) return 0;
		else if(index > targetIndex) return 1;
		else return -1;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public Document getDocument() {
		return document;
	}
	public void setDocument(Document document) {
		this.document = document;
	}
	
}



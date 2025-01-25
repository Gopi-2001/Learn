package CircularLinkedListII;

public class CLLNode {
	protected CLLNode next;
	protected int data;
	
	public CLLNode(int data) {
		this.data = data;
	}
	
	public CLLNode() {}
	
	public CLLNode getNext() {
		return this.next;
	}
	
	public void setNext(CLLNode next) {
		this.next = next;
	}
	
	public void setData(int data) {
		this.data = data;
	}
	
	public int getData() {
		return this.data;
	}
}

package Easy;

public class Node {
	public int data;
	public Node next;
	
	public Node () {}
	
	public Node (int data) {
		this.data = data;
		this.next = null;
	}
	
	public Node (int data,Node next) {
		this.data = data;
		this.next = next;
	}
	
	public void setNext(Node next) {
		this.next = next;
	}
	
	public Node getNext() {
		return this.next;
	}
	
	public void setData(int data) {
		this.data = data;
	}
	
	
	public int getData() {
		return this.data;
	}
}

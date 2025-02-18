package DoublyLinkedListI;

public class DoublyLinkedList {
	DoublyNode head;
	DoublyNode tail;
	int size;
	
	public DoublyLinkedList() {}
	
	public DoublyLinkedList(int data) {
		DoublyNode node  = new DoublyNode();
		node.data = data;
		node.prev = null;
		node.next = null;
		
		head = node;
		tail = node;
		size = 1;
	}
	
	
	public void insert(int data,int location) {
				
		if(location<=0) location  = 0;
		else if(location>= size) location = size;
		
		DoublyNode node  = new DoublyNode();
		node.data = data;
		
		if(head==null) {
			head = tail = node;
			size = 1;
		} else {
			
			if(location==0) {
				node.next = head;
				head.prev = node;
				head = node;
			} else if(location==size) {
				node.prev = tail;			
				tail.next = node;
				tail = node;
			} else {
				int index = 0;
				DoublyNode curr = head;
				while(index<location-1) {
					curr = curr.next;
					index++;
				}
				node.prev = curr;
				node.next = curr.next;
				curr.next.prev = node;
				curr.next = node;
			}
			size++;			
		}
		
		
	}
	
	public void delete(int location) {
		if(location<=0) location  = 0;
		else if(location>= size) location = size;
		
		if(head!=null) {
			
			if(location==0) {
				if(size==1) { head = tail = null; }
				else {
					head = head.next;
					head.prev = null;
				}
			} else if(location==size) {
				if(size==1) { head = tail = null; }
				else {
					tail = tail.prev;
					tail.next = null;
				}
			} else {
				int index = 0;
				DoublyNode curr = head;
				while(index<location-1) {
					index++;
					curr = curr.next;
				}
				curr.next = curr.next.next;
				curr.next.prev = curr;
			}
			
		size--;
		}
	}
	
	public void traverse() {
		if(head==null) {
			System.out.println("Doubly Linked List doesn't exist");
			return; 
		}
		DoublyNode temp  = head;
		
		while(temp!=null) {
			System.out.print(temp.data);
			if(temp.next!=null) System.out.print("->");	
			temp = temp.next;
		}
		System.out.println();	
		
	}
	
	public void reverseTraverse() {
		
		if(head==null) {
			System.out.println("Doubly Linked List doesn't exist");
			return; 
		}
		DoublyNode temp  = tail;
		
		while(temp!=null) {
			System.out.print(temp.data);
			if(temp.prev!=null) System.out.print("<-");	
			temp = temp.prev;
		}
		System.out.println();	
	}
	
	public boolean search(int data) {
		DoublyNode temp  = head;
		int index = 0;
		while(temp!=null) {
			if(temp.data==data) {
				System.out.println("Value " + data + " is present at " + index);
				return true;
			}
			index++;
		}
		
		return false;
	}
	
	public void deleteDoublyLL() {
		DoublyNode temp = head;
		
		while(temp!=null) {
			temp.prev = null;
			temp = temp.next;
		}
		
		head = null;
		tail = null;
		
		System.out.println("Deleted the Doubly Linked List");
	}
}

package DoublyLinkedListII;

public class DoublyLinkedList {
	private DLLNode head;
	private DLLNode tail;
	private int length;
	
	public DoublyLinkedList() {
		this.length = 0;
	}
	
	public int length() {
		return length;
	}
	
	// Find the position of the head value that is equal to given value
	public int getPosition(int data) {
		int pos = 0;
		
		DLLNode temp = head;
		
		while(temp!=null) {
			if(temp.data == data) {
				return pos;
			}
			temp = temp.next;
			pos++;
		}
		
		return Integer.MIN_VALUE;
	}
	
	public void insertFirst(int data) {
		if(head==null) {
			head = new DLLNode(data);
			tail = head;
		} else  {
		DLLNode newNode = new DLLNode(data,null,head);
		head.prev = newNode;
		head = newNode;
		}
		length++;	
	}
	
	public void insertLast(int data) {
		if(head==null) {
			insertFirst(data);
		} else  {
		DLLNode newNode = new DLLNode(data,tail,null);
		tail.next = newNode;
		tail = newNode;
		length++;	
		}
	}
	
	public void insert(int data,int position) {
		if(position<0) position = 0;
		else if(position>length) position = length;
		DLLNode newNode = new DLLNode(data);
		
		if(head == null) {
			insertFirst(data);
		} else {
			if(position==0) {
				insertFirst(data);
			} else if(position==length) {
				insertLast(data);
			} else {
				DLLNode temp = head;
				int index = 0;
				while(index<position-1) {
					index++;
					temp = temp.next;
				}
				
				newNode.next = temp.next;
				newNode.prev = temp;
				temp.next.prev = newNode;
				temp.next = newNode;
				length++;
			}
		}
		
	}
	
	// Remove the head from the doubly linked list
	public int removeFirst() {
		if(head==null) return Integer.MIN_VALUE;
		DLLNode save = head;
		if(length==1) head = tail = null;
		else {
			head = head.next;
			head.prev = null;
		}
		
		length--;
		
		return save.data; 
	}
	
	// Remove the last node from the doubly linked list
	public int removeLast() {
		if(tail==null) return Integer.MIN_VALUE;
		DLLNode save = tail;
		
		if(length==1) head = tail = null;
		else {
			tail = tail.prev;
			tail.next = null;
		}
		length--;
		
		return save.data;
	}
	
	// Remove the node at given position from the doubly linked list
	public int remove(int pos) {
		if(pos<0) pos = 0;  
		else if(pos>length) pos = length;
		
		if(head==null) return Integer.MIN_VALUE;
		DLLNode save = head;
		
		if(pos==0) {
			return removeFirst();
		} else if(pos==length) {
			return removeLast();
		} else {
			DLLNode temp = head;
			int index = 0;
			
//			while(index<pos) {
//				index++;
//				temp = temp.next;
//			}
//			save = temp;
//			temp.prev.next = temp.next;
//			temp.next.prev = temp.prev;
			
// Note the commented is also correct, 
// below solution use the node before the node to be deleted
// above solution use the node to be deleted
			
			while(index<pos-1) {
				index++;
				temp = temp.next;
			}
			save = temp;
			temp.next.prev = temp;
			temp.next = temp.next.next;
			length--;
		}
		
		return save.data;
	}
	
	// Remove a specific node from the doubly linked list
	public void remove(DLLNode node) {
		if(head==null) return;
		
		if(node.equals(head)) removeFirst();
		
		DLLNode temp = head;
		while(temp!=null) {
			if(temp.equals(node)) {
				temp.prev.next = temp.next;
				temp.next.prev = temp.prev;
				length--;
				return;
			}
			temp = temp.next;
		}
	}
	
	public String toString() {
		StringBuilder s = new StringBuilder();
		
		s.append('[');
		DLLNode temp = head;
		
		while(temp!=null) {
			s.append(temp.data);
			
			if(temp.next!=null) s.append(",");
			temp = temp.next;
		}
		s.append(']');
		
		return s.toString();
	}
	
	// Delete the entire doubly linked list
	public void clearList() {
		DLLNode temp = head;
		while(temp!=null) {
			temp.prev = null;
			temp = temp.next;
		}
		head = null;
		tail = null;
		
		length = 0;
	}
	
	
	
	
}

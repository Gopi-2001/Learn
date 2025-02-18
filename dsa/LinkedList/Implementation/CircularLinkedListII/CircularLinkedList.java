package CircularLinkedListII;

public class CircularLinkedList {
	//Why not start from head node ?
	//Tail node makes it more easier to add in the beginning
	//protected CLLNode head;
	
	protected CLLNode tail;
	protected int length;
	
	// Construct a new Circular Linked List
	public CircularLinkedList() {
		tail = null;
		length = 0;
	}
	
	// Returns true if no elements in list
	public boolean isEmpty() {
		return tail == null;
	}
	
	// Remove everything from the CLL
	public void clear() {
		length = 0;
		tail = null;
	}
	
	// Remove and returns element equal to data, or null
	public int remove(int data) {
		if(tail == null) return Integer.MIN_VALUE;
		CLLNode finger = tail.next;
		CLLNode previous = tail;
		int compares;
		
		for(compares=0;compares<length && (finger.data!=data);compares++) {
			previous = finger;
			finger = finger.next;
		}
		if(finger.data == data) {
			
			// an example of the pigeon-hole principle
			if(tail == tail.next) {
				tail = null;
			} else {
				// If finger is tail then take second last node 
				// and delete the last node
				
				if(finger==tail) tail = previous;
				previous.next = previous.next.next;
			}
			
			// finger data free
			finger.next = null; // to keep things disconnected fewer elements
			length--;
			
			return finger.data;
		} 
		else return Integer.MIN_VALUE;
		
		
	}
	
	// Return the length of the CLL
	public int length() {
		return this.length;
	}
	
	public boolean contains(int data) {
	//		if(tail!=null) {
	//			CLLNode temp = tail;
	//			for(int i=0;i<length;i++) {
	//				if(temp.data == data) return true;
	//				temp = temp.next;
	//			}	
	//		}
	//		return false;
		
	// book approach
		
		if(tail==null) return false;
		
		CLLNode finger = tail.next;
		
		while((finger!=tail) && (finger.data != data)) {
			finger = finger.next;
		}
		
		return finger.data == data;
	}
	
	// Adds data to beginning of list
	public void add(int data) {
		addToHead(data);
	}
	
	// Adds elements to head of list
	public void addToHead(int data) {
		CLLNode newNode = new CLLNode(data);
		
		if(tail==null) {
			newNode.next = newNode;
			tail = newNode;
		} else {
			newNode.next = tail.next;
			tail.next = newNode;
		}
		
		length++;
	}
	
	// Adds elements to tail of list 
	public void addToTail(int data) {
		addToHead(data);
		tail = tail.next;
	}
	
	// Return data at head of list
	public int peek() {
		return tail.next.data;
	}
	
	// Return data at tail of list
	public int tailPeek() {
		return tail.data;
	}
	
	public int removeFromHead() {
		CLLNode temp = tail.next;
		
		if(temp == temp.next) {
			tail = null;
		} else {
			tail.next = temp.next;
			temp.next = null; // Helps clean things up, temp is free
		}
		length--;
		return temp.data;
	}
	
	public int removeFromTail() {
				
		if(isEmpty()) return Integer.MIN_VALUE;
		
		CLLNode finger = tail;
		
		while(finger.next!=tail) {
			finger = finger.next;
		}
		
		// Now finger points to second last node
		
		CLLNode temp = tail;
		
		if(finger==tail) {
			tail = null;
		} else {
			finger.next = tail.next;
			tail = finger;
		}
		length--;
		return temp.data;
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		
		result.append('[');
		CLLNode temp = tail.next;
		result.append(temp.data);
		
		while(temp != tail) {
			if(temp != tail) result.append(',');
			
			temp = temp.next;	
			result.append(temp.data);					
		}
		
		result.append(']');
		
		return result.toString();
	}	
}

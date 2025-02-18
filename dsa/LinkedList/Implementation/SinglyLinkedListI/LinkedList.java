package SinglyLinkedListI;

public class LinkedList {
	private int length = 0;
	private ListNode head;
	
	public LinkedList() {
		this.length = 0;
	}
	
	public int length() {
		return length;
	}
	
	public void clearList() {
		head = null;
		length = 0;
	}
	
	public ListNode getHead() {
		return head;
	}
	
	public void insertAtBegin(int data) {
		ListNode node = new ListNode(data);
		node.next = head;
		head = node;
		length++;
	}
	
	public void insertAtEnd(int data) {
		ListNode node = new ListNode(data);
		
		if(head==null) head = node;
		else {
			// ListNode p,q;
			// p initialize to head
			// q set to next of p and when q is null stop 
			// assign p = q if end it not reached
			// for(p=head; (q=p.next)!=null;p=q);
			
			ListNode temp;
			for(temp=head;temp.next!=null;temp=temp.next);
			
			temp.next = node;
		}
		length++;
	}
	
	public void insert(int data,int pos) {
		if(pos<0) pos = 0;
		if(pos>=length) pos = length-1;
		
		ListNode node = new ListNode(data);
		
		if(pos==0) {
			node.next = head;
			head = node;
		}
		else {
			ListNode temp = head;
			int i = 0;
			while(i<pos-1) {
				temp = temp.next;
				i++;
			}
			
			node.next = temp.next;
			temp.next = node;
		}
		
		length++;	
	}
	
	// Remove and return the node at the head of the list
	public ListNode removeFromBegin() {
		ListNode node = head;
		
		if(node!=null) {
			head =  node.next;
			node.next = null;
		}
		length--;	
		return node;
	}
	
	// Remove and return the node at the end of the list
	public ListNode removeFromEnd() {
		ListNode node = head;
		if(node!=null) {
			if(node.next==null) head = null;
			else {
				ListNode temp = null;
				while(node.next!=null) {
					temp = node;
					node = node.next;
				}
				temp.next = null;
			}
		}
		length--;
		return node;
	}
	
	public void remove(int position) {
		if(position<0) position = 0;
		if(position>=length) position = length-1;
		
		if(head==null) return;
		
		if(position==0) {
			head = head.next;
		} 
		else {
			ListNode temp = head;
			for(int i=0;i<position-1;i++){
				temp = temp.next;
			}
			temp.next = temp.next.next;			
		}
		
		length--;	
	}
	
	// Remove the first node matching the value
	public void removeMatch(int value) {
		if(head==null) return;
		ListNode temp  = head;
		ListNode prev = null;
		
		while(temp.data!=value) {
			prev = temp;
			temp = temp.next;
		}
		if(prev==null) head = head.next;
		else prev.next = temp.next;
		
		length--;
	}
	
	public int getPosition(int value) {
		ListNode node = head;
		int pos = 0;
		
		while(node!=null) {
			if(node.data == value) return pos;
			pos++;
			node = node.next;
			
		}
		
		return Integer.MIN_VALUE;
	}
	
	public String toString() {
		StringBuilder res = new StringBuilder("[");
		
		if(head==null) res.append("]");
		else {
			ListNode node = head;
			while(node!=null) {
				res.append(node.data);
				if(node.next!=null) res.append(",");
				node = node.next;
			}
			res.append("]");
		}
		
		return res.toString();
	}

	
}

package CircularLinkedListI;

public class CircularLinkedList {
	private ListNode head;
	private ListNode tail;
	private int size;
	
	public CircularLinkedList() {
		this.size = 0;
	}
	
	public CircularLinkedList(int data) {
		createCLL(data);	
	}
	
	public int getSize() {
		return this.size;
	}
	
	private void createCLL(int data) {
		ListNode newNode = new ListNode(data);
		newNode.next = newNode;
		head = newNode;
		tail = newNode;
		
		size = 1;
	}
	
	public void insert(int data,int location) {
				
		if(head==null) {
			createCLL(data);
		} else {
			
			ListNode newNode  = new ListNode(data);
			if(location<=0) {
				newNode.next = head;
				tail.next = newNode;
				head = newNode;
			} else if(location>=size) {
				newNode.next = head;
				tail.next = newNode;
				tail = newNode;
			} else {
				
				ListNode temp = head;
				
				for(int i=0;i<location-1;i++) {
					temp = temp.next;
				}
				
				newNode.next = temp.next;
				temp.next = newNode;
				
			}
			
			size++;
			
		}
	}
	
	public void traverse() {
		ListNode temp = head;
		
		for(int i=0;i<size;i++) {
			System.out.print(temp.data);
			
			if(i!=size-1) System.out.print("->");
			
			temp = temp.next;
		}
		System.out.print("\n");
	}
	
	public void delete(int location) {
		if(head!=null) {
			//If size is 1
			if(size==1) { 
				head.next = null; 
				tail.next = null;
				head = tail = null;
			} 
			else {
				// If size > 1 and loc <= 0
				if(location<=0) {
					head = head.next;
					tail.next = head;
				}
				// If size > 1 and loc >= size
				else if(location>=size) {
					ListNode temp = head;
					for(int i=0;i<size-1;i++) {
					temp = temp.next;	
					}
					tail = temp;
					tail.next = head;	
				} 
				// If 0 < location < size
				else {
					ListNode temp = head;
					for(int i=0;i<location-1;i++) {
					temp = temp.next;	
					}
					temp.next = temp.next.next;
				}
			}
			size--;
		}
	}
	
	public void deleteCLL() {
		head.next = null;
		tail.next = null;
		head = tail = null;
	}
	
}

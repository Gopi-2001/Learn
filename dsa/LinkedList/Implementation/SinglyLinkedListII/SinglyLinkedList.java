package SinglyLinkedListII;

public class SinglyLinkedList {
	
	ListNode head;
	ListNode tail;
	int size = 0;
	
	// Initially Head and Tail will be same
	// Thus linking head and tail with the node
	public ListNode createLinkedList(int nodeValue) {
		ListNode node = new ListNode();
		node.data = nodeValue;
		node.next = null;
		
		head = node;
		tail =  node;
		
		size++;
		
		return node;
	}
	
	
	//Insertion in SinglyLinkedList
	// We can insert at Beginning , middle or end
	
	// create a node
	// if(head == null) createSinglyLinkedList
	// else if(position<=0) { node.next = head; head = node; size++;}
	// else if(position>=size) {node.next = null; tail.next = node; tail = node; size++;}
	// else { FindLocation then nextNode = current.next, current.next = node, node.next = nextNode }
	
	public void insert(int data,int position) {
		ListNode node = new ListNode(data);
		
		if(head == null) {
			createLinkedList(data);
		} 
		else { 
			if(position<=0) {			
			node.next = head;
			head = node;
			}
			else if(position>=size) {
			tail.next = node;
			tail = node;
			}
			else {
				ListNode temp = head;
				for(int i=0;i<position-1;i++) temp = temp.next;
				
				ListNode nextNode = temp.next;
				temp.next = node;
				node.next = nextNode;	
			}
			size++;
	  }
	}
	
	// Traversal Method
	// if(head==null) print no data message
	// else { loop all nodes and print value }
	
	public void printLinkedList(){
		if(head==null) {
			System.out.println("LinkedList does not Exist");
		}
		else {			
			for(ListNode temp = head;temp!=null;temp = temp.next) {
				System.out.print(temp.data);
				if(temp!=tail)	System.out.print("->");	
			}
			System.out.println();
		}
	}
	
	public boolean contains(int nodeValue) {
		for(ListNode temp = head;temp!=null;temp=temp.next) {
			if(temp.data==nodeValue) return true;
		}
		return false;
	}
	
	public int indexOf(int nodeValue) {
		if(head!=null) {
			ListNode temp = head;
			for(int i=0;i<size;i++) {
				if(temp.data==nodeValue) return i;
				temp = temp.next;
			}
		}	
		return -1;
	}
	
	
	public void deleteLocation(int location) {
		if(size==0) return;
		
		if(location==0) {
			head = head.next;		
		}
		else if(location>=size) {				
			ListNode temp = head;					
			for(int i=0;i<size-1;i++) temp = temp.next;
			temp.next = null;
			tail =  temp;
		}
		else {
			ListNode temp = head;					
			for(int i=0;i<location-1;i++) temp = temp.next;
			temp.next = temp.next.next;
		}
		size--;
		
		if(size==0) head = tail = null;
		
	}
	
	public void deleteLinkedList() {
		head = tail = null;
		System.out.println("Linked List Deleted Successfully");
	}
	
	
	
	
	
	
}

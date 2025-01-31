package Easy;

public class reverseList {
	public static void main(String[] args) {
		Node head = new Node(1);
		Node node1 = new Node(2);
		Node node2 = new Node(3);
		Node node3 = new Node(10);
		Node node4 = new Node(11);
		
		head.next = node1;
		node1.next = node2;
		node2.next = node3;
		node3.next = node4;
		
		print(head);
		
		Node ans = reverseListIterative(head);
		
		print(ans);
		
		Node rev = node4;
		// after reversing node4 became head
		print(node4);
		
		// here head is the last node
		print(head);
		
		reverseListRecursive(rev,rev);
		
		// again after reversing head became first node
		print(head);
		
	}
	
	public static void print(Node head) {
		
		Node curr = head;
		
		while(curr!=null) {
			System.out.print(curr.data);
			
			if(curr.next!=null) System.out.print("->");
			curr = curr.next;
		}
		System.out.println();
		
	}
	
	public static void reverseListRecursive(Node current,Node head) {
		
		if(current==null) return;
		
		Node next = current.next;
		
		if(next==null) {
			head = current;
			return;
		}
		
		reverseListRecursive(next,head);
		
		// Make next node point to current node
		next.next = current;
		
		// Remove existing Link
		current.next = null;
	}
	public static Node reverseListIterative(Node head) {
		
		Node curr = head;
		
		Node prev = null;
		
		while(curr!=null) {
			// Store the next node
			Node next = curr.next;
			
			//update current next to prev
			curr.next = prev;
			
			prev = curr;
			
			curr = next;
		}
		
		return prev;
	}
}

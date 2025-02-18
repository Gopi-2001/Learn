package Easy;

public class isLinkedListEvenOrOdd {
	public static void main(String[] args) {
		Node head = new Node(1);
		Node node1 = new Node(2);
		Node node2 = new Node(3);
		Node node3 = new Node(10);
		Node node4 = new Node(11);
		
		head.next = node1;
		node1.next = node2;
		node2.next = node3;
		//node3.next = node4;
		
		evenOrOdd(head);
		
	}
	
	public static void evenOrOdd(Node head) {
		Node curr = head;
		
		while(curr!=null && curr.next!=null) {
			curr = curr.next.next;
		}
		
		if(curr==null) System.out.println("EVEN");
		else System.out.println("ODD");
	}
	
	
}

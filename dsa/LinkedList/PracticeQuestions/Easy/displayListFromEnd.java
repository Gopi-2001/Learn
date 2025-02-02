package Easy;

public class displayListFromEnd {
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
		
		printRev(head);
	}
	
	public static void printRev(Node head) {
		if(head==null) return;
		
		printRev(head.next);
		System.out.println(head.data);
	}
}

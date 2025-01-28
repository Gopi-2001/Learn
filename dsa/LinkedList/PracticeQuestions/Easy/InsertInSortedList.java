package Easy;

public class InsertInSortedList {
	public  static void main(String[] args) {
		Node head = new Node(1);
		Node node1 = new Node(2);
		Node node2 = new Node(3);
		Node node3 = new Node(10);
		Node node4 = new Node(11);
		Node node5 = new Node(13);
		Node node6 = new Node(15);
		
		head.next = node1;
		node1.next = node2;
		node2.next = node3;
		node3.next = node4;
		node4.next = node5;
		node5.next = node6;
		
		//Node newNode = new Node(5);
		Node newNode = new Node(0);
		
		Node curr  = insert(head,newNode);
		
		while(curr!=null){
			System.out.print(curr.data);
			if(curr.next!=null) System.out.print("->");
			curr = curr.next;
		}
		
	}
	
	public static Node insert(Node head,Node newNode) {
		Node curr = head;
	
		if(curr==null) return newNode;
		
		Node prev = null;
		
		while(curr!=null && curr.data<newNode.data) {
			prev = curr;
			curr = curr.next;
		}
		
		newNode.next = curr;
		
		if(prev!=null) prev.next = newNode;
		else head = newNode;
		
		return head;
	}
}

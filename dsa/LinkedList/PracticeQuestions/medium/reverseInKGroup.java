package medium;

import Easy.Node;

public class reverseInKGroup {
	
	// Recursive 
	public static Node reverseKGroupRecursive(Node head, int k) {

        Node curr = head;
        Node next = null;
        Node prev = null;
        int checkLen = k;
        int count = k;

        while(curr!=null && checkLen>0){
            curr =curr.next;
            checkLen--;
        }

        if(checkLen>0) return head;

        curr = head;
        while(curr!=null && count>0){
            next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
            count--;
        }

        if(next!=null) {
            head.next = reverseKGroupRecursive(next,k);
        }

        return prev;

    }
	
	 public static Node reverseKGroupIterative(Node head, int k) {

		 Node curr = head;

		 Node prevGroupTail = null;

		 Node currGroupHead = head;

	        int total = 0;

	        while(curr != null){
	            total++;
	            curr = curr.next;
	        }

	        curr = head;

	        while(curr != null){

	            int count = k;

	            Node prev = null;
	            
	            if(total-k<0) {
	                prevGroupTail.next = curr;
	                break;
	            }

	            while(curr!=null && count>0){
	               Node next = curr.next;
	               curr.next = prev;
	               prev = curr;
	               curr = next; 
	               count--;
	            }

	            total = total - k;
	            
	            if(prevGroupTail!=null) {
	            	prevGroupTail.next = prev;
	            } else {
	            	head = prev;
	            }
	            	   
	          
	            prevGroupTail = currGroupHead;
	            currGroupHead = curr;
	        }

	        return head;
	    }
	
	public static void main(String[] args) {
		Node head = new Node(1);
		Node node1 = new Node(2);
		Node node2 = new Node(3);
		Node node3 = new Node(10);
		Node node4 = new Node(11);
		Node node5 = new Node(13);
		Node node6 = new Node(16);
		
		head.next = node1;
		node1.next = node2;
		node2.next = node3;
		node3.next = node4;
		node4.next = node5;
		node5.next = node6;
		
		
		
		print(head);
		
		Node ans  = reverseKGroupIterative(head,2);
		System.out.print("Iterative: ");
		print(ans);
		
		
		Node ans1 = reverseKGroupRecursive(ans,2);
		System.out.print("Recursive: ");
		print(ans1);
		
		
		
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
}

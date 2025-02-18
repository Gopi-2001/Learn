package medium;

import Easy.Node;

// https://leetcode.com/problems/swap-nodes-in-pairs/

public class swapPair {
	
	public static Node swapPairRecursive(Node head){
		
		if(head==null || head.next==null) return head;
		
		Node ans = head.next;
		head.next = swapPairRecursive(head.next.next);
		ans.next = head; 
		
		return ans;
	}
	
	public static Node swapPairIterative(Node head){
		 Node ans = null;
	     Node temp = null;

	    while(head!=null && head.next!=null){

	        if(temp!=null){
	            temp.next.next = head.next;
	        }

	        temp = head.next;
	        head.next = head.next.next;
	        temp.next = head;
	        
	        head = head.next;

	        if(ans==null){
	            ans = temp;
	        }

	    }
	    
	    if(ans==null) ans=head;
	    
	    return ans;
		
	}
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
		
		//Node res = swapPairRecursive(head);
		
		Node res = swapPairIterative(head);
		
		print(res);
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

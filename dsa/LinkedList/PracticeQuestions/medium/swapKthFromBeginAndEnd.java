package medium;

import java.util.ArrayList;

import Easy.Node;

public class swapKthFromBeginAndEnd {
	
	public static Node swapNodes(Node head, int k) {
		Node first = head, second = head;

        // to get the kth Node make k-1 traversal
       for(int i=1;i<k;i++){
        first = first.next;
       }
	       
       // get the (size-k)th node
       Node temp = first;
       while(temp.next!=null){
        temp = temp.next;
        second = second.next; 
       }

       int t = first.data;
       first.data = second.data;
       second.data = t;

	   return head;
	}
	
	public static Node swapNodes2(Node head, int k) {
	    
		ArrayList<Integer> al = new ArrayList<>();	
	    Node curr = head;
	    
	    while(curr!=null){
	        al.add(curr.data);
	        curr = curr.next;
	    }
	
	    int temp = al.get(k-1);
	    al.set(k-1,al.get(al.size()-k));
	    al.set(al.size()-k,temp);
	    
	    Node ans = new Node();
	    Node anshead = ans;
	
	    for(int i : al){
	        ans.next = new Node(i);
	        ans = ans.next;
	    }
	
	    return anshead.next;
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
		
		//Node ans  = swapNodes2(head,2);
		
		Node ans = swapNodes2(head,2);
		
		print(ans);
		
		
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

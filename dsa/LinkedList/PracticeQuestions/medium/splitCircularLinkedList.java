package medium;

import Easy.Node;
import java.util.*;

class Pair<F,S> {
	public final F first;
	public final S second;
	
	public Pair(F first,S second) {
		this.first = first;
		this.second = second;
	}
}

public class splitCircularLinkedList {
	
	public static Pair<Node,Node> splitList(Node head){
        Node fast = head;
        Node slow = head;
        Node head1 = null;
        Node head2 = null;
        
        while(fast.next!=head && fast.next.next!=head){
            fast = fast.next.next;
            slow = slow.next;
        }
        
        if(fast.next.next==head)
            fast = fast.next; 
        
        head1 = head;
        
        //if(head.next!=head)
        head2 = slow.next;
        
        slow.next = head1;
        fast.next = head2;
        
        Pair<Node,Node> pair = new Pair<>(head1,head2);
        
        return pair;
	}
	
	public static Pair<Node,Node> splitUsingHashing(Node head){
		   HashSet<Node> set = new HashSet<>(); 
		 
	       Node head1 = null;
	       Node head2 = null;
	       
	       Node curr = head;
	       int cnt = 0;
	       
	       while(!set.contains(curr)) {
	    	   set.add(curr);
	    	   curr = curr.next;
	    	   cnt++;
	       }
	       
	       head1 = head;
	       
	       int cnt1 = (cnt+1)/2;
	       curr = head;
	       
	       while(cnt1>1) {
	    	curr = curr.next;
	    	cnt1--;
	       }
	       
	       head2 = curr.next;
	       
	       curr.next = head1;
	        
	       curr = head2;
	       
	       while(curr.next!=head) {
	    	   curr = curr.next;
	       }
	       
	       curr.next = head2;
	       
	       Pair<Node,Node> pair = new Pair<>(head1,head2);
	        
	       return pair;
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
		node6.next = head;
		
		
		print(head);
		
		//Pair<Node,Node> pair = splitList(head);
		
		Pair<Node,Node> pair = splitUsingHashing(head);
		
		print(pair.first);
		print(pair.second);
		
	}
	
	public static void print(Node head) {
		
		Node curr = head;
		
		while(curr.next!=head) {
			System.out.print(curr.data);
			
			if(curr.next.next!=head) System.out.print("->");
			curr = curr.next;
		}
		System.out.println();	
	}
}

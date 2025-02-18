package medium;

import java.util.Stack;

import Easy.Node;

// https://leetcode.com/problems/palindrome-linked-list/

public class isPalindrome {
	
	public static boolean isPalindrome(Node head) {
		if(head==null || head.next==null) return true;
		
		Node fast = head;
        Node slow = head; 
        
        while(fast!=null && fast.next!=null){
            fast = fast.next.next;
            slow = slow.next;  
        }
        
        if(fast!=null) slow = slow.next;
               
        slow = reverse(slow);

        fast = head;
        
        Node pSlow = slow;
        Node prev = null;
        
        while(slow!=null) {
        	if(fast.data!=slow.data) return false;
        	prev = fast;
        	fast = fast.next;
        	slow = slow.next;
        }
        
        pSlow = reverse(pSlow);
        prev.next = pSlow;

		return true;
	}
	
	private static Node reverse(Node head) {
		if(head==null) return head;
		Node prev = null;
		Node curr = head;
		
		while(curr!=null) {
			Node next = curr.next;
			curr.next = prev;
			prev = curr;
			curr = next;
		}
		
		return prev;
	}

	public static boolean isPalindromeUsingStack(Node head) {
        if(head==null || head.next==null) return true;

        Stack<Node> st = new Stack<>();

        Node fast = head;
        Node slow = head; 
        
        while(fast!=null && fast.next!=null){
            st.push(slow);
            fast = fast.next.next;
            slow = slow.next;  
        }

        if(fast!=null) {
            slow = slow.next;
        }

        while(slow!=null){
            if(st.isEmpty() || (st.peek().data != slow.data)) 
                return false;

            slow = slow.next;
            st.pop();
        }

        return true;
    }
	
	public static void main(String[] args) {
		Node head = new Node(1);
		Node node1 = new Node(2);
		Node node2 = new Node(3);
		Node node3 = new Node(3);
		Node node4 = new Node(2);
		Node node5 = new Node(1);
	
		
		head.next = node1;
		node1.next = node2;
		node2.next = node3;
		node3.next = node4;
		node4.next = node5;

		
		
		print(head);
		
		//boolean ans = isPalindromeUsingStack(head);
		
		boolean ans = isPalindrome(head);
		
		System.out.println(ans);
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
}

package Easy;

import java.util.HashMap;
import java.util.Map;
import Easy.Node;
//Remove nth node from the end - https://leetcode.com/problems/remove-nth-node-from-end-of-list/



public class NthNodeFromEnd {
	public static void main(String[] args) {
	Node node4 = new Node(6,null);
	Node node3 = new Node(4,node4);
	Node node2 = new Node(17,node3);
	Node node1 = new Node(1,node2);
	Node head = new Node(5,node1);
	
	Node node5 = new Node(8,null);
	node4.next = node5;
	
	// 5 -> 1 -> 17 -> 4 -> 6 -> 8
	
	System.out.println("Brute force Ans - " + solution1.FindNthNodeFromEnd(head, 3).data);
	System.out.println("HashTable Ans - " + solution2.FindNthNodeFromEnd(head, 3).data);
	System.out.println("Double traversal Ans - " + solution3.FindNthNodeFromEnd(head, 3).data);
	System.out.println("Two pointer Ans - " + solution4.FindNthNodeFromEnd(head, 3).data);
	System.out.println("Recursion Ans - " + solution5.FindNthNodeFromEnd(head, 3).data);
}
}

class solution1 {
	// Brute Force - O(N^2)
	
	
	// For each node - count number of node after it.
	// if(count == n-1) return the node
	// if(count < n-1) return null and print "fewer number of nodes in the list"
	// if(count > n-1) continue with the next node
	
	public static Node FindNthNodeFromEnd(Node head,int n) {
		Node curr = head;
		//System.out.println("a");
		while(curr != null) {
			Node temp = curr.next;
			int count = 0;
			
			while(temp!=null) {
				temp = temp.next;
				count++;
			}
			
			if(count == n-1) return curr;
			if(count < n-1) {
				System.out.println("Fewer number of nodes in the list");
				break;
			}
			
			curr = curr.next;
		}
		
		return null;
	}
	
	
}



class solution2 {
	// using HashMap
	// T.C : O(N)
	// S.C : O(N)
	
	public static Node FindNthNodeFromEnd(Node head,int n) {
		Map<Integer,Node> map = new HashMap<>();
		//System.out.println("b");
		Node curr = head;
		int length = 0;
		
		while(curr!=null) {
			map.put(length+1, curr);
			curr = curr.next;
			length++;
		}
		
		// (length-n+1)th node from start == nth node from end
		if(map.containsKey(length-n+1)) return map.get(length-n+1);
		
		return null;
	}
}

class solution3 {
	public static Node FindNthNodeFromEnd(Node head,int n) {
		//System.out.println("c");
		Node curr = head;
		int length = 0;
		Node result  = null;
		// Find the length of the LinkedList
		while(curr!=null) {
			curr = curr.next;
			length++;
		}
		
		curr = head;
		int k = length-n+1;
		while(k>0) {
			result = curr;
			curr = curr.next;
			k--;
		}
		
		return result;
	}
}

class solution4 {
	// using 2 pointer - first and second
	// Initially both point to head node
	// let first node make n moves 
	// now first node can make only 'length - n + 1' move
	// 'length - n + 1' from stat == nth from end
	// Now move second node along with first node
	
	public static Node FindNthNodeFromEnd(Node head,int n) { 
		//System.out.println("d");
		Node first = head;
		Node second = null;
		
		for(int i=0;i<n && first!=null;i++) {
			first = first.next;
		}
		
		while(first!=null) {
			if(second==null) second = head;
			
			second = second.next;
			first = first.next;
		}
		
		
		return second;
	}
}

class solution5 {
	
	// Using a global variable to track the post recursive call 
	// And when it is same as Nth return the node
	
	static int counter = 0;
	
	public static Node FindNthNodeFromEnd(Node head,int n) { 
// correct recursion solution
		
			if(head!=null) {
		
			Node result = FindNthNodeFromEnd(head.next,n);
			//System.out.println("abc2" + result);
			if(result!=null) return result;
			counter++;
			if(n==counter) return head;
			}
			
		    return null;

// Book solution is wrong
		
//	   if(head!=null) {
//		   	Node result = FindNthNodeFromEnd(head.next,n);
//
//			System.out.println("abc2" + result);
//			counter++;
//			if(n==counter) return head;
//	   }
//	   return null;
	}
}
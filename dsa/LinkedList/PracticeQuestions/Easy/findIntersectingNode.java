package Easy;

import java.util.*;


public class findIntersectingNode {
	public static void main(String[] args) {
		Node head = new Node(1);
		Node node1 = new Node(2);
		Node node2 = new Node(3);
		Node node3 = new Node(10);
		Node node4 = new Node(11);
		Node node5 = new Node(11);
		Node node6 = new Node(11);
		
		head.next = node1;
		node1.next = node2;
		node2.next = node3;
		node3.next = node4;
		
		node6.next = node5;
		node5.next = node2;
		
		Node res = findNodeUsingBruteForce(head,node6);
		
		res = findNodeUsingHashing(head,node6);
		
		res = findNodeUsingStack(head,node6);
		
		res = findIntersectingNode(head,node6);
		
		if(res!=null) System.out.println(res.data);
		
		
	}
	
	
	// O(m*n)
	// Compare every node of the first list with every node of the second list
	// when second list has a matching node then it is the intersecting node
	public static Node findNodeUsingBruteForce(Node head1,Node head2) {
		
		Node curr = head1;
		
		while(curr!=null) {
			Node curr2 = head2;
			
			while(curr2!=null) {
				if(curr2==curr) return curr;
				curr2 = curr2.next;
			}
			
			curr = curr.next;
		}
		
		return null;
	}
	
	// Time Complexity : O(n + m)
	// Space Complexity : O(n) or O(m)
	public static Node findNodeUsingHashing(Node head1,Node head2) {
		HashSet<Node> set = new HashSet<>();
		
		Node curr = head1;
		while(curr!=null) {
			set.add(curr);
			curr = curr.next;
		}
		
		curr = head2;
		
		while(curr!=null) {
			if(set.contains(curr)) return curr;
			curr = curr.next;
		}
		
		return null;
	}
	
	// Space complexity - O(n+m)
	// Time : O(n + m)
	// Start comparing from the end and when there is a mismatch return the prev
	public static Node findNodeUsingStack(Node head1,Node head2) {
		Stack<Node> st1 = new Stack<Node>();
		Stack<Node> st2 = new Stack<Node>();
		
		Node curr = head1;
		
		while(curr!=null) {
			st1.push(curr);
			curr = curr.next;
		}
		
		curr = head2;
		while(curr!=null) {
			st2.push(curr);
			curr = curr.next;
		}
		
		Node prev = null;
		while(!st1.isEmpty() && !st2.isEmpty()) {
			if(st1.peek()==st2.peek()) {
				prev = st1.pop();
				st2.pop();
			} else return prev;
		}
		
		
		return null;
	}
	
	// Using find the first repeating number approach
	
	public static Node findNodeFirstRepeating(Node head1,Node head2) {
		
		ArrayList<Node> al = new ArrayList<>();
		
		Node curr = head1;
		
		while(curr!=null) {
			al.add(curr);
			curr = curr.next;
		}
		
		curr = head2;
		while(curr!=null) {
			al.add(curr);
			curr = curr.next;
		}
		
		for(int i=0;i<al.size();i++) {
			for(int j=i+1;j<al.size();j++) {
				if(al.get(i)==al.get(j)) return al.get(i);
			}
		}
		
		return null;
	}
	
	
	// better approach
	// Find Lengths (L1 and L2) of both lists -- O(n) + O(m) = O(max(m,n))
	// Take the difference d of the lengths -- O(1)
	// Make d steps in longer list -- O(d)
	// Step in both lists in parallel until links to next node match -- O(min(m,n))
	// Time Complexity = O(max(m,n))
	// Space Complexity = O(1)
	
	public static Node findIntersectingNode(Node head1,Node head2) {
		
		int L1 = 0, L2 =0, diff = 0;
		
		Node curr = head1;
		
		while(curr!=null) {
			L1++;
			curr = curr.next;
		}
		
		curr = head2;
		
		while(curr!=null) {
			L2++;
			curr = curr.next;
		}
		
		diff = Math.abs(L2-L1);
		
		Node list1 = head1;
		Node list2 = head2;
		
		if(L2>L1) {
			list1 = head2;
			list2 = head1;
		}
		
		while(diff>0) {
			list1 = list1.next;
			diff--;
		}
		
		while(list2!=null) {
			if(list1==list2) return list1;
			
			list1 = list1.next;
			list2 = list2.next;
		}
		
		return null;
	}
	
}

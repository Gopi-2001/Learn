package Easy;

import java.util.*;


// https://leetcode.com/problems/merge-two-sorted-lists/description/

public class mergeTwoSorted {
	
	public static void main(String[] args) {
		
		Node node0 = new Node(1);
		Node node1 = new Node(2);
		Node node2 = new Node(3);
		Node node3 = new Node(4);
		Node node4 = new Node(5);
		Node node5 = new Node(6);
		Node node6 = new Node(7);
		
		node0.next = node1;
		node1.next = node2;
		node2.next = node3;
		
		node4.next = node5;
		node5.next = node6;
		
		
		//Node res = mergeTwoSortedUsingRecursion(node0,node4);
		
		//Node res = mergeTwoSortedUsingStack(node0,node4);
		
		Node res = mergeTwoSortedUsingIteration(node0,node4);
		
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
	
	public static Node mergeTwoSortedUsingIteration(Node list1,Node list2) {
		Node head = new Node(0);
		Node curr = head;
		
		while(list1!=null && list2!=null) {
			if(list1.data<=list2.data) {
				curr.next = list1;
				list1 = list1.next;
			} else {
				curr.next = list2;
				list2 = list2.next;
			}
			curr = curr.next;
		}
		
		if(list1==null) curr.next = list2;
		else if(list2==null) curr.next = list1;
		
		return head.next;
	}
	
	public static Node mergeTwoSortedUsingRecursion(Node list1, Node list2) {
		if(list1==null)
				return list2;
		if(list2==null)
			return list1;
			
		Node ans = new Node();
		
		if(list1.data <= list2.data) {
			ans = list1;
			ans.next = mergeTwoSortedUsingRecursion(list1.next,list2);
		} else {
			ans = list2;
			ans.next = mergeTwoSortedUsingRecursion(list1.next,list2.next);
		}
			
		return ans;
	}
	
	
	public static Node mergeTwoSortedUsingStack(Node list1, Node list2) {
        Stack<Node> s1 = new Stack<>();
        Stack<Node> s2 = new Stack<>();

        Node curr = list1;

        while(curr!=null){
            s1.push(curr);
            curr = curr.next;
        }

        curr = list2;

        while(curr!=null){
            s2.push(curr);
            curr = curr.next;
        }

        Node res = null;

        while(!s1.isEmpty() && !s2.isEmpty()){
                if(s1.peek().data >= s2.peek().data){
                    Node node = s1.pop();
                    if(res==null) res = node;
                    else { 
                    	node.next = res;
                    	res = node;
                    }
                } else {
                    Node node = s2.pop();
                    if(res==null) res = node;
                    else { 
                    	node.next = res;
                    	res = node;
                    }
                }
        }

        while(!s1.isEmpty()){
            Node node = s1.pop();
            if(res==null) res = node;
            else { 
            	node.next = res;
            	res = node;
            }
        }

        while(!s2.isEmpty()){
            Node node = s2.pop();
            if(res==null) res = node;
            else { 
            	node.next = res;
            	res = node;
            }
        }

        return res;
	}
	
}

package Easy;

// https://cp-algorithms.com/others/tortoise_and_hare.html

import java.util.HashSet;

public class LoopInLinkedList {
	public  static void main(String[] args) {
		Node head = new Node(1);
		Node node1 = new Node(2);
		Node node2 = new Node(3);
		Node node3 = new Node(4);
		Node node4 = new Node(5);
		Node node5 = new Node(6);
		Node node6 = new Node(7);
		
		head.next = node1;
		node1.next = node2;
		node2.next = node3;
		node3.next = node4;
		node4.next = node5;
		node5.next = node6;
		node6.next = node2;
		
		System.out.println("Does loop exists: " + findIfLoopExists(head));
		System.out.println("Does loop exists: " + findIfLoopExists2(head));
		System.out.println("Head of loop: " + findBeginOfLoop(head).data);
		System.out.println("Length of the loop: " + lengthOfLoop(head));
		
	}
	
	// Floyd Algorithm
	public static boolean findIfLoopExists(Node head) {
		Node fastPtr = head;
		Node slowPtr = head;
		
		while(fastPtr!=null && fastPtr.next!=null) {
			fastPtr = fastPtr.next.next;
			slowPtr = slowPtr.next;
			
			if(slowPtr == fastPtr) {
				return true;
			}
		}
		
		return false;
	}
	
	// Using hashTable
	public static boolean findIfLoopExists2(Node head) {
		HashSet<Node> set =  new HashSet<>();
		Node curr = head;
		
		while(curr!=null) {
			if(set.contains(curr)) return true;
			
			set.add(curr);
			curr = curr.next;
		}
		
		return false;
	}
	
	
	public static Node findBeginOfLoop(Node head) {
		Node fastPtr = head;
		Node slowPtr = head;
		
		boolean loopExists = false;
		
		while(fastPtr!=null && fastPtr.next!=null) {
			fastPtr = fastPtr.next.next;
			slowPtr = slowPtr.next;
			
			if(slowPtr == fastPtr) {
				slowPtr = head;
				
				while(slowPtr!=fastPtr) {
					slowPtr = slowPtr.next;
					fastPtr = fastPtr.next;
				}
				return fastPtr;
			}
		}
		
		return null;
	}
	
	public static int lengthOfLoop(Node head) {
		Node fastPtr = head;
		Node slowPtr = head;
		
		int length = 1;
		
		while(fastPtr!=null && fastPtr.next!=null) {
			fastPtr = fastPtr.next.next;
			slowPtr = slowPtr.next;
			
			if(slowPtr == fastPtr) {
				slowPtr = slowPtr.next;
				while(slowPtr!=fastPtr) {
					length++;
					slowPtr = slowPtr.next;
				}
				break;
			}
		}
		
		return length;
	}
}

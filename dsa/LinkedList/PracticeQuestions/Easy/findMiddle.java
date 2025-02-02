package Easy;

import java.util.*;

public class findMiddle {
	
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
		
		System.out.println(middleNodeUsingHashing(head).data);
	}
	
	public static Node middleNodeUsingHashing(Node head) {
        Node curr = head;
        HashMap<Integer,Node> map = new HashMap<>();
        int i = 0;
        while(curr!=null){
            map.put(i,curr);
            i++;
            curr = curr.next;
        }
         
        return map.get(i/2); 
    }
	
	public static Node middleNodeUsingLength(Node head) {
		int len = 0;
		Node curr = head;
		
		while(curr!=null) {
			len++;
			curr = curr.next;
		}
		
		len = len/2;
		
		curr = head;
		while(len>0) {
			curr = curr.next;
			len--;
		}
		
		return curr;
	}
	public static Node middleNodeUsingTwoPointer(Node head) {
		Node fast = head;
		Node slow = head;
		
		while(fast!=null && fast.next!=null) {
			fast = fast.next.next;
			slow = slow.next;
		}
		
		return slow;
	}
}

package SinglyLinkedListI;

public class main {
	public static void main(String[] args) {
		LinkedList ll = new LinkedList();
		
		ll.insertAtBegin(1);
		ll.insertAtBegin(3);
		ll.insertAtEnd(4);
		ll.insert(5, 1);
		
		System.out.println(ll.toString());
		
		ll.remove(1);
		ll.insertAtBegin(5);
		
		System.out.println(ll.toString());
		
		ll.removeFromBegin();
		ll.removeFromEnd();
		
		System.out.println(ll.toString());
		
		ll.insertAtBegin(5);
		ll.insertAtEnd(6);
		ll.insert(7, 2);
		
		System.out.println(ll.toString());
		System.out.println(ll.getHead().data);
		
		System.out.println(ll.length());
		System.out.println("Position of 3: " + ll.getPosition(3));
		
		ll.remove(1);
		ll.removeMatch(1);
		System.out.println(ll.toString());
		
		ll.clearList();
		
		System.out.println(ll.toString());
		
		
		
		
		
		
		
		
	}
}

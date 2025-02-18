package DoublyLinkedListII;

public class main {

	public static void main(String[] args) {
		
		DoublyLinkedList dll = new DoublyLinkedList();
		
		dll.insert(1, 0);
		dll.insertFirst(2);
		dll.insertLast(3);
		
		System.out.println(dll.toString());
		
		dll.insert(4, dll.length());
		
		System.out.println(dll.toString());
		
		dll.removeFirst();
		dll.removeLast();
		
		System.out.println(dll.toString());
		
		dll.insert(4, 0);
		dll.insert(5, 0);
		dll.insert(6, 6);
		
		System.out.println(dll.toString());
		
		dll.remove(2);
		
		System.out.println(dll.toString());
		
		dll.clearList();
		
		System.out.println(dll.toString());
		
	}

}

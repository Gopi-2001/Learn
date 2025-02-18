package DoublyLinkedListI;

public class main {
	
	public static void main(String[] args) {
		DoublyLinkedList dll = new DoublyLinkedList();
		dll.insert(2,0);
		
		dll.insert(1,1);
		dll.insert(3, 1);
		
		dll.traverse();
		dll.reverseTraverse();
		
		dll.insert(4, 2);
		dll.insert(5, 1);
		dll.delete(0);
		
		dll.traverse();
		
		dll.insert(6, 4);
		
		dll.traverse();
		
		dll.delete(3);
		
		dll.traverse();
		
		dll.deleteDoublyLL();
		
		dll.traverse();
		
	}		
}

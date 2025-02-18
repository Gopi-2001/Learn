package SinglyLinkedListII;

public class main {
	
	public static void main(String[] args) {
		
		SinglyLinkedList ssl = new SinglyLinkedList();
		
		ssl.createLinkedList(5);
		ssl.insert(6, 0);
		ssl.insert(7, 8);
		
		ssl.printLinkedList();
		
		System.out.println(ssl.contains(9) +" "+ ssl.indexOf(5));
		
		ssl.deleteLocation(1);
		
		ssl.printLinkedList();
	}

}

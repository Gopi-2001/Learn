package CircularLinkedListII;

public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		CircularLinkedList cll = new CircularLinkedList();
		
		cll.add(5);
		System.out.println(cll.toString());
		
		cll.addToHead(6);
		cll.addToTail(7);
		System.out.println(cll.toString());
		
		cll.removeFromHead();
		cll.removeFromTail();
		System.out.println(cll.toString());
		
		cll.add(8);
		cll.add(10);
		System.out.println(cll.toString());
		System.out.println(cll.peek());
		System.out.println(cll.tailPeek());
		System.out.println(cll.contains(10));
		System.out.println(cll.isEmpty());
		System.out.println(cll.length());
		
		System.out.println(cll.remove(8));
		
		
	}

}

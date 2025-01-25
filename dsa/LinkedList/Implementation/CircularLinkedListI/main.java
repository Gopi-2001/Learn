package CircularLinkedListI;

public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CircularLinkedList cll =  new CircularLinkedList();
		cll.insert(1, 0);
		cll.insert(2, 0);
		
		cll.traverse();
		
		CircularLinkedList cll1 = new CircularLinkedList(1);
		cll1.insert(2, 0);
		
		cll1.insert(3, cll1.getSize());
		
		cll1.delete(1);
		
		cll1.traverse();
		
		cll1.insert(4, cll1.getSize());
		cll1.insert(5, cll1.getSize());
		cll1.insert(6, cll1.getSize());
		
		cll1.delete(0);
		cll1.delete(cll1.getSize());
		cll1.traverse();
		
	}

}

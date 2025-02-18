package Easy;

class EmptyStackExpection extends Exception {
	public EmptyStackExpection (String message) {
		super(message);
	}
}

class ListNode {
	protected int data;
	protected ListNode next;
	
	public ListNode() {
		this.data = 0;
		this.next = null;
	}
	
	public ListNode(int data) {
		this.data = data;
		this.next = null;
	}
}


public class LinkedStack {
	private int length ;
	ListNode top;
	
	// Constructor Create a Empty Stack
	public LinkedStack() {
		this.length = 0;
		this.top = null;
	}
	
	// check is Empty
	public boolean isEmpty() {
		return (length == 0);
	}
	
	// return length of the stack
	public int size() {
		return length;
	}
	
	// Push element at the top of stack
	public void push(int data) {
		ListNode temp = new ListNode(data);
		temp.next = top;
		top = temp;
		length++;
	}
	
	// Remove element from top of stack
	public int pop() throws EmptyStackExpection {
		if(isEmpty()) throw new EmptyStackExpection("Stack is Empty, no element to pop.");
		
		ListNode result = top;
		top = top.next;
		length--;
		
		return result.data;
	}
	
	
	// Peek the top element of the stack 
	public int peek() throws EmptyStackExpection {
		if(isEmpty()) throw new EmptyStackExpection("Stack is Empty, no element to peek.");
		
		return top.data;
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		
		ListNode current = top;
		
		while(current != null) {
			result.append(current.data + "\n");
			current = current.next;
		}
		
		return result.toString();
		
	}
	
	
}

class LinkedStackMain {
	public static void main(String[] args) throws EmptyStackExpection {
		LinkedStack stack = new LinkedStack();
		
		System.out.println("Is stack empty? " + stack.isEmpty());
		
		stack.push(5);
		stack.push(4);
		stack.push(3);
		stack.push(2);
		
		System.out.println("Print the stack - " + stack.toString());
		System.out.println("Length of the stack - " + stack.size());
		System.out.println("Pop the top element - " + stack.pop());
		System.out.println("Peek the top element - " + stack.peek());
		
	}
}

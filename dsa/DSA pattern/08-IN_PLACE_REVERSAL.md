# Pattern 8: In-place Reversal of a LinkedList

## 🎯 Core Concept
Reverse linked lists (or portions of them) by rearranging pointers without using extra space.

## ❓ When to Use
- ✅ Reverse entire linked list
- ✅ Reverse between two positions
- ✅ Reverse every K group
- ✅ Palindrome linked list
- ✅ Reorder linked list

## 📊 Time & Space Complexity
- **Time**: O(n) - visit each node once
- **Space**: O(1) - in-place, only pointers

## 🔍 How It Works

### Basic Reversal Logic
```
Original:     1 → 2 → 3 → 4 → null
Reversed:     null ← 1 ← 2 ← 3 ← 4

Process: at each node, reverse the link to point backwards
```

### Step by Step
```
Initial:  1 → 2 → 3 → 4 → null
prev = null, curr = 1

Step 1: Save next (2), point 1 to null, move prev and curr
        null ← 1   2 → 3 → 4 → null

Step 2: Save next (3), point 2 to 1, move prev and curr
        null ← 1 ← 2   3 → 4 → null

Step 3: Save next (4), point 3 to 2, move prev and curr
        null ← 1 ← 2 ← 3   4 → null

Step 4: Save next (null), point 4 to 3, move prev and curr
        null ← 1 ← 2 ← 3 ← 4   curr = null (stop)

Result: 4 → 3 → 2 → 1 → null
```

## 💻 Implementation Template

### Template 1: Reverse Entire List
```java
class ListNode {
    int val;
    ListNode next;
    ListNode(int val) { this.val = val; }
}

public class ReverseLinkedList {
    public static ListNode reverse(ListNode head) {
        ListNode prev = null;
        ListNode curr = head;
        
        while (curr != null) {
            // Save next node
            ListNode next = curr.next;
            
            // Reverse the link
            curr.next = prev;
            
            // Move prev and curr forward
            prev = curr;
            curr = next;
        }
        
        return prev;  // New head
    }
    
    public static void printList(ListNode head) {
        while (head != null) {
            System.out.print(head.val + " → ");
            head = head.next;
        }
        System.out.println("null");
    }
    
    public static void main(String[] args) {
        // Create: 1 → 2 → 3 → 4 → null
        ListNode head = new ListNode(1);
        head.next = new ListNode(2);
        head.next.next = new ListNode(3);
        head.next.next.next = new ListNode(4);
        
        System.out.print("Original: ");
        printList(head);
        
        head = reverse(head);
        System.out.print("Reversed: ");
        printList(head);
        // Output: Reversed: 4 → 3 → 2 → 1 → null
    }
}
```

### Template 2: Reverse Between Two Positions
```java
public class ReverseBetween {
    public static ListNode reverseBetween(ListNode head, int left, int right) {
        if (left == right) {
            return head;
        }
        
        // Create dummy to handle edge case where reversal starts at head
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        
        // Find node before left position
        ListNode beforeLeft = dummy;
        for (int i = 0; i < left - 1; i++) {
            beforeLeft = beforeLeft.next;
        }
        
        // Reverse from left to right
        ListNode prev = beforeLeft;
        ListNode curr = beforeLeft.next;
        
        for (int i = 0; i < right - left + 1; i++) {
            ListNode next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }
        
        // Connect reversed portion
        beforeLeft.next.next = curr;
        beforeLeft.next = prev;
        
        return dummy.next;
    }
    
    public static void main(String[] args) {
        // Create: 1 → 2 → 3 → 4 → 5 → null
        ListNode head = new ListNode(1);
        head.next = new ListNode(2);
        head.next.next = new ListNode(3);
        head.next.next.next = new ListNode(4);
        head.next.next.next.next = new ListNode(5);
        
        head = reverseBetween(head, 2, 4);
        // Result: 1 → 4 → 3 → 2 → 5 → null
    }
}
```

### Template 3: Reverse Every K Group
```java
public class ReverseKGroup {
    public static ListNode reverseKGroup(ListNode head, int k) {
        // Check if we have k nodes to reverse
        ListNode curr = head;
        for (int i = 0; i < k; i++) {
            if (curr == null) {
                return head;  // Not enough nodes
            }
            curr = curr.next;
        }
        
        // Reverse first k nodes
        ListNode prev = null;
        curr = head;
        for (int i = 0; i < k; i++) {
            ListNode next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }
        
        // Recursively reverse remaining groups
        head.next = reverseKGroup(curr, k);
        
        return prev;
    }
    
    public static void main(String[] args) {
        // Create: 1 → 2 → 3 → 4 → 5 → null
        ListNode head = new ListNode(1);
        head.next = new ListNode(2);
        head.next.next = new ListNode(3);
        head.next.next.next = new ListNode(4);
        head.next.next.next.next = new ListNode(5);
        
        head = reverseKGroup(head, 2);
        // Result: 2 → 1 → 4 → 3 → 5 → null
    }
}
```

### Template 4: Palindrome Check
```java
public class PalindromeLinkedList {
    private ListNode left;
    
    public boolean isPalindrome(ListNode head) {
        left = head;
        return check(head);
    }
    
    private boolean check(ListNode right) {
        if (right == null) {
            return true;
        }
        
        // Recurse to the end
        boolean isPalindromeRight = check(right.next);
        if (!isPalindromeRight) {
            return false;
        }
        
        // Check if values match on way back
        boolean result = (left.val == right.val);
        left = left.next;
        
        return result;
    }
    
    public static void main(String[] args) {
        // 1 → 2 → 2 → 1
        ListNode head = new ListNode(1);
        head.next = new ListNode(2);
        head.next.next = new ListNode(2);
        head.next.next.next = new ListNode(1);
        
        PalindromeLinkedList pl = new PalindromeLinkedList();
        System.out.println(pl.isPalindrome(head));  // true
    }
}
```

### Template 5: Reorder List
```java
public class ReorderList {
    public static void reorder(ListNode head) {
        if (head == null || head.next == null) {
            return;
        }
        
        // Find middle
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        
        // Reverse second half
        ListNode second = slow.next;
        slow.next = null;
        second = reverse(second);
        
        // Merge first and reversed second half
        ListNode first = head;
        while (first != null && second != null) {
            ListNode temp1 = first.next;
            ListNode temp2 = second.next;
            
            first.next = second;
            second.next = temp1;
            
            first = temp1;
            second = temp2;
        }
    }
    
    private static ListNode reverse(ListNode head) {
        ListNode prev = null;
        while (head != null) {
            ListNode next = head.next;
            head.next = prev;
            prev = head;
            head = next;
        }
        return prev;
    }
    
    public static void main(String[] args) {
        // 1 → 2 → 3 → 4 → 5 → null
        // becomes: 1 → 5 → 2 → 4 → 3 → null
        ListNode head = new ListNode(1);
        head.next = new ListNode(2);
        head.next.next = new ListNode(3);
        head.next.next.next = new ListNode(4);
        head.next.next.next.next = new ListNode(5);
        
        reorder(head);
    }
}
```

## 🎓 Common Problem Patterns

### 1. **Simple Reversal**
- Reverse entire list
- Reverse iteratively

### 2. **Partial Reversal**
- Reverse between positions
- Reverse K group

### 3. **Complex Operations**
- Palindrome check
- Reorder list
- Swap pairs

## 📝 Example Problems

| Problem | Difficulty | Key Insight |
|---------|-----------|-------------|
| Reverse Linked List | Easy | Three pointers |
| Reverse Between | Medium | Find boundaries |
| Reverse K Group | Hard | Recursion + reversal |
| Palindrome | Medium | Two pointer + recursion |
| Reorder List | Medium | Find middle + reverse |

## 🔑 Key Points to Remember

1. **Three Pointer Pattern**
   ```java
   ListNode prev = null, curr = head;
   while (curr != null) {
       ListNode next = curr.next;
       curr.next = prev;
       prev = curr;
       curr = next;
   }
   ```

2. **Dummy Node**
   - Handles edge cases where head changes
   - Always use for partial reversals

3. **Save Next**
   - Always save `curr.next` before changing pointers
   - Essential to not lose the rest of list

## ⚠️ Common Mistakes

❌ Not saving next pointer before reversal
❌ Losing reference to rest of list
❌ Off-by-one in position counting
❌ Not handling null checks properly

✅ Always save next first
✅ Use dummy node for safety
✅ Test with single and two-node lists
✅ Verify all connections after reversal

## 💡 Pro Tips

1. **Check Pattern**
   ```java
   ListNode curr = head;
   for (int i = 0; i < k; i++) {
       if (curr == null) return head;
       curr = curr.next;
   }
   ```

2. **Dummy Node Pattern**
   ```java
   ListNode dummy = new ListNode(0);
   dummy.next = head;
   // Work with dummy.next as head
   return dummy.next;
   ```

3. **Finding Middle**
   ```java
   ListNode slow = head, fast = head;
   while (fast != null && fast.next != null) {
       slow = slow.next;
       fast = fast.next.next;
   }
   ```

---
**In-place Reversal**: Essential linked list manipulation skill!

# Pattern 3: Fast & Slow Pointers (Hare & Tortoise)

## 🎯 Core Concept
Use two pointers moving at different speeds through an array or linked list. Usually one moves 1 step, the other moves 2 steps. Perfect for detecting cycles and finding middle elements.

## ❓ When to Use
- ✅ Detecting cycles in linked lists
- ✅ Finding start of cycle in linked list
- ✅ Finding middle element of linked list
- ✅ Circular array problems
- ✅ Palindrome in linked list
- ✅ Happy number problem
- ✅ Finding duplicate in array

## 📊 Time & Space Complexity
- **Time**: O(n) - each element visited at most twice
- **Space**: O(1) - no extra space needed

## 🔍 How It Works

### The Core Principle: Meeting Point
If two runners start on a circular track:
- Slow moves 1 step per iteration
- Fast moves 2 steps per iteration
- **They will eventually meet**

This is guaranteed by Floyd's Cycle Detection Algorithm.

### Example: Cycle Detection

```
Linked List: 1 → 2 → 3 → 4 → 2 (cycle)

Step 1:  slow = 1, fast = 2
Step 2:  slow = 2, fast = 4
Step 3:  slow = 3, fast = 2 (moves 2 from 4: to 2, then to 3... wait)
         Actually: slow = 3, fast = 3  🎯 THEY MEET!

Cycle detected!
```

### The Math Behind It
- If slow is at position i and fast at 2i in a cycle of length c
- Gap = 2i - i = i
- Fast moves faster by 1 position per step
- Eventually gap becomes 0 → they meet

## 💻 Implementation Template

### Template 1: Detect Cycle in Linked List
```java
class ListNode {
    int val;
    ListNode next;
    ListNode(int val) { this.val = val; }
}

public class CycleDetection {
    public static boolean hasCycle(ListNode head) {
        if (head == null || head.next == null) {
            return false;
        }
        
        ListNode slow = head;
        ListNode fast = head;
        
        while (fast != null && fast.next != null) {
            slow = slow.next;           // Move 1 step
            fast = fast.next.next;      // Move 2 steps
            
            if (slow == fast) {         // Same node = cycle found
                return true;
            }
        }
        
        return false;
    }
}
```

### Template 2: Find Start of Cycle
```java
public class FindCycleStart {
    public static ListNode findCycleStart(ListNode head) {
        if (head == null || head.next == null) {
            return null;
        }
        
        ListNode slow = head;
        ListNode fast = head;
        
        // Detect cycle
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            
            if (slow == fast) {
                break;  // Cycle found
            }
        }
        
        if (fast == null || fast.next == null) {
            return null;  // No cycle
        }
        
        // Find start of cycle
        slow = head;
        while (slow != fast) {
            slow = slow.next;
            fast = fast.next;
        }
        
        return slow;  // Cycle start
    }
}
```

### Template 3: Find Middle of Linked List
```java
public class FindMiddle {
    public static ListNode findMiddle(ListNode head) {
        ListNode slow = head;
        ListNode fast = head;
        
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        
        return slow;  // Middle node
    }
}
```

### Template 4: Palindrome in Linked List
```java
public class PalindromeLinkedList {
    public static boolean isPalindrome(ListNode head) {
        if (head == null || head.next == null) {
            return true;
        }
        
        // Find middle
        ListNode slow = head;
        ListNode fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        
        // Reverse second half
        ListNode prev = null;
        while (slow != null) {
            ListNode nextTemp = slow.next;
            slow.next = prev;
            prev = slow;
            slow = nextTemp;
        }
        
        // Compare first half with reversed second half
        ListNode first = head;
        ListNode second = prev;
        while (second != null) {
            if (first.val != second.val) {
                return false;
            }
            first = first.next;
            second = second.next;
        }
        
        return true;
    }
}
```

### Template 5: Happy Number (Cycle Detection)
# Happy Number

Write an algorithm to determine if a number `n` is happy.

A **happy number** is a number defined by the following process:

1.  Starting with any positive integer, replace the number by the sum of the squares of its digits.
2.  Repeat the process until the number equals 1 (where it will stay), or it loops endlessly in a cycle which does not include 1.
3.  Those numbers for which this process ends in 1 are happy.

Return `true` if `n` is a happy number, and `false` if not.

### Example 1:
**Input:** `n = 19`  
**Output:** `true`  
**Explanation:**  
$1^2 + 9^2 = 82$  
$8^2 + 2^2 = 68$  
$6^2 + 8^2 = 100$  
$1^2 + 0^2 + 0^2 = 1$

### Example 2:
**Input:** `n = 2`  
**Output:** `false`

```java
public class HappyNumber {
    public static boolean isHappy(int n) {
        int slow = n;
        int fast = n;
        
        while (true) {
            slow = getSumOfSquares(slow);                      // Move 1 step
            fast = getSumOfSquares(getSumOfSquares(fast));     // Move 2 steps
            
            if (fast == 1) {
                return true;  // Found happiness
            }
            
            if (slow == fast) {
                return false;  // Infinite loop
            }
        }
    }
    
    private static int getSumOfSquares(int num) {
        int total = 0;
        while (num > 0) {
            int digit = num % 10;
            total += digit * digit;
            num /= 10;
        }
        return total;
    }
    
    public static void main(String[] args) {
        System.out.println(isHappy(7));   // true
        System.out.println(isHappy(2));   // false
    }
}
```

### Template 6: Find Duplicate in Array
```java
public class FindDuplicate {
    public static int findDuplicate(int[] nums) {
        // Treat array as linked list where nums[i] points to index nums[i]
        
        int slow = nums[0];
        int fast = nums[0];
        
        // Find meeting point
        while (true) {
            slow = nums[slow];
            fast = nums[nums[fast]];
            
            if (slow == fast) {
                break;
            }
        }
        
        // Find cycle start (duplicate)
        slow = nums[0];
        while (slow != fast) {
            slow = nums[slow];
            fast = nums[fast];
        }
        
        return slow;
    }
    
    public static void main(String[] args) {
        int[] nums = {1, 3, 4, 2, 2};
        System.out.println(findDuplicate(nums));  // 2
    }
}
```

## 🎓 Common Problem Patterns

### 1. **Cycle Detection**
- Linked list has cycle
- Find start of cycle
- Cycle length

### 2. **Finding Position**
- Middle element
- Nth from end
- Palindrome

### 3. **Duplicates & Loops**
- Find duplicate in array
- Happy number
- Circular array patterns

## 📝 Example Problems

| Problem | Difficulty | Key Insight |
|---------|-----------|-------------|
| Linked List Cycle | Easy | Simple meeting point |
| Middle of Linked List | Easy | Movement speeds |
| Palindrome Linked List | Medium | Find middle + reverse |
| Happy Number | Medium | Treat as cycle detection |
| Find Duplicate in Array | Medium | Array as linked list |
| Circular Array Loop | Hard | Movement condition |

## 🔑 Key Points to Remember

1. **Speeds Matter**
   - Slow: 1 step (current.next)
   - Fast: 2 steps (current.next.next)
   - Other ratios work too (1:3) but 1:2 is most common

2. **Cycle Detection Guarantee**
   - If cycle exists, they WILL meet
   - Meeting point is not necessarily cycle start

3. **Finding Cycle Start**
   - After meeting, reset one pointer to head
   - Move both at same speed
   - They meet at cycle start

## ⚠️ Common Mistakes

❌ Moving fast pointer before checking null
❌ Confusing meeting point with cycle start
❌ Using fast = fast.next.next without checking both pointers
❌ Not handling single-node cycles

✅ Always check `fast && fast.next` before `fast.next.next`
✅ Remember: meeting point ≠ cycle start (need second pass)
✅ Test with lists of length 1, 2, 3
✅ Understand the mathematical proof

## 💡 Pro Tips

1. **Check Conditions Carefully**
   ```python
   while fast and fast.next:  # Essential!
       slow = slow.next
       fast = fast.next.next
   ```

2. **The Two-Pass Cycle Start Algorithm**
   - Pass 1: Find meeting point
   - Pass 2: Find actual cycle start
   
3. **Works for Any Speed Ratio**
   - 1:2 (most common)
   - 1:3, 2:3 also work
   - As long as speeds are different

4. **Can Apply to Arrays Too**
   - Treat array indices as graph edges
   - Perfect for "find duplicate" problems

---
**Fast & Slow Pointers**: Simple yet powerful for cycle detection!

# Pattern 2: Two Pointers

## 🎯 Core Concept
Use two pointers that move towards each other or in the same direction to solve problems on sorted arrays or linked lists efficiently.

## ❓ When to Use
- ✅ Sorted arrays/linked lists
- ✅ Finding pairs with specific sum
- ✅ Palindrome checking
- ✅ Reversing arrays/linked lists
- ✅ Removing duplicates from sorted array
- ✅ Container with most water
- ✅ 3Sum, 4Sum type problems

## 📊 Time & Space Complexity
- **Time**: O(n) - each element visited at most once
- **Space**: O(1) - usually no extra space needed

## 🔍 How It Works

### Strategy 1: Pointers Moving Towards Each Other
Best for **sorted arrays** where you need pairs or matches.

```
Array: [1, 2, 3, 4, 6], Target = 6

Start = 0 (value: 1)    End = 4 (value: 6)
Sum = 1 + 6 = 7 → too big → move END left

Start = 0 (value: 1)    End = 3 (value: 4)
Sum = 1 + 4 = 5 → too small → move START right

Start = 1 (value: 2)    End = 3 (value: 4)
Sum = 2 + 4 = 6 → FOUND!
```

### Strategy 2: Pointers Moving in Same Direction
Best for problems like removing duplicates or rearranging.

```
Sorted Array with Duplicates: [1, 1, 2, 2, 3]

pointer1 = 0 (write position)
pointer2 = 0 (read position)

Skip duplicates with pointer2, write unique values with pointer1
Result: [1, 2, 3, ?, ?]
```

## 💻 Implementation Template

### Template 1: Towards Each Other (Pair Sum)
```java
public class TwoSum {
    public static int[] twoSum(int[] arr, int target) {
        int left = 0;
        int right = arr.length - 1;
        
        while (left < right) {
            int currentSum = arr[left] + arr[right];
            
            if (currentSum == target) {
                return new int[]{arr[left], arr[right]};
            } else if (currentSum < target) {
                left++;  // Need larger sum
            } else {
                right--;  // Need smaller sum
            }
        }
        
        return null;
    }
    
    public static void main(String[] args) {
        int[] arr = {1, 2, 3, 4, 6};
        int[] result = twoSum(arr, 6);
        System.out.println(Arrays.toString(result));  // Output: [2, 4]
    }
}
```

### Template 2: Remove Duplicates (Same Direction)
```java
public class RemoveDuplicates {
    public static int removeDuplicates(int[] arr) {
        if (arr.length == 0) {
            return 0;
        }
        
        int writePos = 0;  // Position to write unique element
        
        for (int readPos = 1; readPos < arr.length; readPos++) {
            if (arr[readPos] != arr[writePos]) {
                writePos++;
                arr[writePos] = arr[readPos];
            }
        }
        
        return writePos + 1;  // Length of array with unique elements
    }
    
    public static void main(String[] args) {
        int[] arr = {1, 1, 2, 2, 3};
        int uniqueLength = removeDuplicates(arr);
        System.out.println(Arrays.toString(Arrays.copyOf(arr, uniqueLength)));
        // Output: [1, 2, 3]
    }
}
```

### Template 3: Palindrome Checking
```java
public class PalindromeCheck {
    public static boolean isPalindrome(String s) {
        int left = 0;
        int right = s.length() - 1;
        
        while (left < right) {
            // Skip non-alphanumeric characters
            while (left < right && !Character.isLetterOrDigit(s.charAt(left))) {
                left++;
            }
            while (left < right && !Character.isLetterOrDigit(s.charAt(right))) {
                right--;
            }
            
            // Compare characters (case-insensitive)
            if (Character.toLowerCase(s.charAt(left)) != 
                Character.toLowerCase(s.charAt(right))) {
                return false;
            }
            
            left++;
            right--;
        }
        
        return true;
    }
    
    public static void main(String[] args) {
        System.out.println(isPalindrome("A man, a plan, a canal: Panama"));  // true
    }
}
```

### Template 4: 3Sum (Extended Pattern)
```java
Import java.util.*;

public class ThreeSum {
    public static List<List<Integer>> threeSum(int[] arr) {
        Arrays.sort(arr);
        List<List<Integer>> result = new ArrayList<>();
        
        for (int i = 0; i < arr.length - 2; i++) {
            // Avoid duplicates
            if (i > 0 && arr[i] == arr[i - 1]) {
                continue;
            }
            
            int left = i + 1;
            int right = arr.length - 1;
            
            while (left < right) {
                int currentSum = arr[i] + arr[left] + arr[right];
                
                if (currentSum == 0) {
                    result.add(Arrays.asList(arr[i], arr[left], arr[right]));
                    left++;
                    right--;
                    
                    // Skip duplicates
                    while (left < right && arr[left] == arr[left - 1]) {
                        left++;
                    }
                    while (left < right && arr[right] == arr[right + 1]) {
                        right--;
                    }
                } else if (currentSum < 0) {
                    left++;
                } else {
                    right--;
                }
            }
        }
        
        return result;
    }
    
    public static void main(String[] args) {
        int[] arr = {-1, 0, 1, 2, -1, -4};
        System.out.println(threeSum(arr));
        // Output: [[-1, -1, 2], [-1, 0, 1]]
    }
}
```

## 🎓 Common Problem Patterns

### 1. **Pair Problems**
- Two Sum
- Container with Most Water
- Valid Palindrome

### 2. **Array Manipulation**
- Remove Duplicates
- Move Zeros
- Reverse Array

### 3. **Extended Patterns**
- 3Sum, 4Sum
- 3Sum Closest
- 3Sum with count

## 📝 Example Problems

| Problem | Difficulty | Key Insight |
|---------|-----------|-------------|
| Two Sum II | Easy | Sorted array, pointers converge |
| Valid Palindrome | Easy | Skip non-alphanumeric |
| Container with Most Water | Medium | Greedy shrinking |
| 3Sum | Medium | Two pointers inside loop |
| 4Sum | Medium | nested loops + two pointers |
| Reverse String | Easy | Simple swap and move |

## 🔑 Key Points to Remember

1. **Sort First** (if needed)
   - Most two-pointer problems need sorted input

2. **Identify the Direction**
   - Converging (opposite directions)
   - Diverging (same direction but different purposes)

3. **Handle Duplicates**
   - Skip equal elements to avoid duplicate results

4. **Early Termination**
   - When pointers meet, stop

## ⚠️ Common Mistakes

❌ Forgetting to sort the array first
❌ Moving pointers in wrong direction
❌ Not skipping duplicates
❌ Missing edge cases (empty array, single element)

✅ Always check if array needs sorting
✅ Be clear about when to increment/decrement
✅ Handle duplicates explicitly
✅ Test with arrays of different sizes

## 💡 Pro Tips

1. **Decision Logic**
   - If sum too small → move left pointer right
   - If sum too large → move right pointer left
   - If equal → decide based on problem

2. **Duplicate Handling**
   ```python
   while left < right and arr[left] == arr[left + 1]:
       left += 1
   ```

3. **Combine with other techniques**
   - Two pointers + Sorting (3Sum)
   - Two pointers + Hash Map (TwoSum variant)
   - Two pointers + Sliding Window

---
**Two Pointers is elegant**: simple, efficient, and applies to many problems!

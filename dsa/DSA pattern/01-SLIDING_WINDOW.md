# Pattern 1: Sliding Window

## 🎯 Core Concept
Use a fixed or dynamic window that slides through an array or string to solve problems involving contiguous subarrays or substrings.

## ❓ When to Use
- ✅ Finding max/min/average of all contiguous subarrays of size K
- ✅ Longest substring with specific properties
- ✅ Subarray/substring sum problems
- ✅ Questions mentioning "contiguous", "window", or "range"
- ✅ Array or string problems where you need to avoid recalculating

## 📊 Time & Space Complexity
- **Time**: O(n) - each element visited at most twice
- **Space**: O(1) or O(k) depending on what you store

## 🔍 How It Works

### Basic Idea
Instead of recalculating from scratch for each window:
1. Calculate the result for the first window
2. Slide the window one position at a time
3. Remove the element going out
4. Add the new element coming in

### Example: Maximum Sum of Subarray of Size K

**Input**: Array: [1, 2, 3, 4, 5], Window size K = 3

```
Brute Force (O(n*k)):
Window 1: 1 + 2 + 3 = 6
Window 2: 2 + 3 + 4 = 9 (recalculate everything!)
Window 3: 3 + 4 + 5 = 12 (recalculate everything!)

Sliding Window (O(n)):
Window 1: 1 + 2 + 3 = 6
Window 2: 6 - 1 (remove left) + 4 (add right) = 9
Window 3: 9 - 2 (remove left) + 5 (add right) = 12
```

## 💻 Implementation Template

### Fixed Size Window
```java
public class SlidingWindow {
    public static int slidingWindow(int[] arr, int k) {
        if (arr.length < k) {
            return -1;
        }
        
        // Calculate for first window
        int windowSum = 0;
        for (int i = 0; i < k; i++) {
            windowSum += arr[i];
        }
        int maxSum = windowSum;
        
        // Slide the window
        for (int i = 1; i <= arr.length - k; i++) {
            // Remove leftmost element, add new rightmost element
            windowSum = windowSum - arr[i - 1] + arr[i + k - 1];
            maxSum = Math.max(maxSum, windowSum);
        }
        
        return maxSum;
    }
    
    public static void main(String[] args) {
        int[] arr = {1, 2, 3, 4, 5};
        System.out.println(slidingWindow(arr, 3));  // Output: 12
    }
}
```

### Dynamic Size Window (Longest Substring with K Distinct Characters)
```java
import java.util.*;

public class LongestSubstringKDistinct {
    public static int longestSubstringWithKDistinct(String s, int k) {
        Map<Character, Integer> charCount = new HashMap<>();
        int left = 0;
        int maxLength = 0;
        
        for (int right = 0; right < s.length(); right++) {
            // Add right character to window
            char c = s.charAt(right);
            charCount.put(c, charCount.getOrDefault(c, 0) + 1);
            
            // Shrink window from left if we have more than k distinct characters
            while (charCount.size() > k) {
                char leftChar = s.charAt(left);
                charCount.put(leftChar, charCount.get(leftChar) - 1);
                if (charCount.get(leftChar) == 0) {
                    charCount.remove(leftChar);
                }
                left++;
            }
            
            // Update max length
            maxLength = Math.max(maxLength, right - left + 1);
        }
        
        return maxLength;
    }
    
    public static void main(String[] args) {
        System.out.println(longestSubstringWithKDistinct("araaci", 2));  // Output: 4
    }
}
```

## 🎓 Common Problem Patterns

### 1. **Maximum/Minimum in Window**
```python
# Find maximum sum of subarray of size K
# Using: window - removed_element + added_element
```

### 2. **Count/Find Elements**
```python
# Count distinct characters in each window
# Longest substring without repeating characters
# Longest subarray with sum equals K
```

### 3. **Frequency-Based**
```python
# Permutation in string
# Anagram substring
# Character count matching
```

## 📝 Example Problems

| Problem | Difficulty | Key Insight |
|---------|-----------|-------------|
| Max Sum of Subarray K | Easy | Simple arithmetic |
| Longest Substring Without Repeating | Easy | Use set/dict |
| Minimum Window Substring | Hard | Track character frequencies |
| Permutation in String | Medium | Compare character counts |
| Longest Repeating Character Replacement | Medium | Track max frequency |
| Sliding Window Maximum | Hard | Use deque for O(n) |

## 🔑 Key Points to Remember

1. **Always identify if you have a window**
   - Fixed size: simpler, just add and remove
   - Variable size: need conditions to shrink/expand

2. **What goes in the window?**
   - The actual elements? Counts? Frequencies?

3. **What's the condition?**
   - When to expand? When to shrink?

4. **What to track?**
   - Minimum sum? Count? Maximum character?

## ⚠️ Common Mistakes

❌ Forgetting to update the window result before moving
❌ Not handling edge cases (empty array, K > array size)
❌ Mixing up the add/remove order
❌ Not returning to the correct state when shrinking

✅ Always ensure window is valid before processing
✅ Double-check your add/remove logic
✅ Test with edge cases first

## 💡 Pro Tips

- **Use dictionaries/counters** to track frequencies in the window
- **Handle two pointer carefully** in variable-size windows
- **Deque is useful** for tracking maximum in each window (advanced)
- **Practice dynamic windows first** before fixed windows

---
**Master this pattern** and you'll solve array/string problems 10x faster!

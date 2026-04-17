# Pattern 7: Cyclic Sort

## 🎯 Core Concept
Place each number in its correct position in arrays containing numbers in a known range, to solve problems efficiently without extra space.

## ❓ When to Use
- ✅ Arrays with numbers in range 1 to n
- ✅ Finding missing/duplicate numbers
- ✅ Finding all duplicates
- ✅ First missing positive
- ✅ In-place operations required

## 📊 Time & Space Complexity
- **Time**: O(n) - each element moves to correct position once
- **Space**: O(1) - in-place modification

## 🔍 How It Works

### Basic Idea
For array with numbers 1 to n:
- Number `i` should be at index `i-1`
- Number `i` should be at index `i`

```
Array: [3, 4, -1, 1]  (numbers 1 to 4, with -1 out of range)
Goal: Place each 1-4 in correct position

Step 1: 3 at index 0, should be at index 2 → swap
        [-1, 4, 3, 1]
Step 2: -1 can't be placed, move to next
Step 3: 4 at index 1, should be at index 3 → swap
        [1, 4, 3, -1]
Step 4: Index 2 has 3, correct position
Step 5: Now scan for first missing: index 1 (missing 2)
```

## 💻 Implementation Template

### Template 1: Find Missing Number
```java
public class FindMissing {
    public static int findMissing(int[] nums) {
        int n = nums.length;
        
        // Place each number in correct position
        for (int i = 0; i < n; i++) {
            // Number nums[i] should be at index nums[i]
            while (nums[i] > 0 && nums[i] <= n && 
                   nums[nums[i] - 1] != nums[i]) {
                // Swap nums[i] with nums[nums[i]-1]
                int correctIndex = nums[i] - 1;
                int temp = nums[i];
                nums[i] = nums[correctIndex];
                nums[correctIndex] = temp;
            }
        }
        
        // Find first number not in correct position
        for (int i = 0; i < n; i++) {
            if (nums[i] != i + 1) {
                return i + 1;
            }
        }
        
        // If all in correct position, missing number is n+1
        return n + 1;
    }
    
    public static void main(String[] args) {
        int[] nums = {3, 0, 1};
        System.out.println(findMissing(nums));  // Output: 2
        
        int[] nums2 = {9, 6, 4, 2, 3, 5, 7, 0, 1, 8};
        System.out.println(findMissing(nums2));  // Output: 10
    }
}
```

### Template 2: Find All Duplicates
```java
public class FindDuplicates {
    public static List<Integer> findDuplicates(int[] nums) {
        List<Integer> duplicates = new ArrayList<>();
        
        // Mark visited by making corresponding index negative
        for (int num : nums) {
            int index = Math.abs(num) - 1;
            
            if (nums[index] < 0) {
                // Already visited, it's a duplicate
                duplicates.add(Math.abs(num));
            } else {
                // Mark as visited
                nums[index] = -nums[index];
            }
        }
        
        return duplicates;
    }
    
    public static void main(String[] args) {
        int[] nums = {4, 3, 2, 7, 8, 2, 3, 1};
        System.out.println(findDuplicates(nums));  // Output: [2, 3]
    }
}
```

### Template 3: First Missing Positive
```java
public class FirstMissingPositive {
    public static int firstMissingPositive(int[] nums) {
        int n = nums.length;
        
        // Move each number to its correct position
        for (int i = 0; i < n; i++) {
            // Number k should be at index k-1
            while (nums[i] > 0 && nums[i] <= n && 
                   nums[nums[i] - 1] != nums[i]) {
                // Swap
                int correctIndex = nums[i] - 1;
                int temp = nums[i];
                nums[i] = nums[correctIndex];
                nums[correctIndex] = temp;
            }
        }
        
        // Find first position where number doesn't match
        for (int i = 0; i < n; i++) {
            if (nums[i] != i + 1) {
                return i + 1;
            }
        }
        
        return n + 1;
    }
    
    public static void main(String[] args) {
        int[] nums1 = {1, 2, 0};
        System.out.println(firstMissingPositive(nums1));  // Output: 3
        
        int[] nums2 = {3, 4, -1, 1};
        System.out.println(firstMissingPositive(nums2));  // Output: 2
        
        int[] nums3 = {7, 8, 9, 11, 12};
        System.out.println(firstMissingPositive(nums3));  // Output: 1
    }
}
```

### Template 4: Duplicate Number (with constraints)
```java
public class FindDuplicateNumber {
    public static int findDuplicate(int[] nums) {
        // Similar to linked list cycle detection
        // nums[i] is like a pointer to index nums[i]
        
        int slow = nums[0];
        int fast = nums[0];
        
        // Find meeting point
        do {
            slow = nums[slow];
            fast = nums[nums[fast]];
        } while (slow != fast);
        
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
        System.out.println(findDuplicate(nums));  // Output: 2
    }
}
```

## 🎓 Common Problem Patterns

### 1. **Finding Missing**
- Single missing number
- Multiple missing numbers
- First missing positive

### 2. **Finding Duplicates**
- All duplicates
- Single duplicate
- Duplicate with constraint

### 3. **In-Place Operations**
- Sort in-place
- Rearrange in-place
- Mark visited

## 📝 Example Problems

| Problem | Difficulty | Key Insight |
|---------|-----------|-------------|
| Find Missing Number | Easy | Cyclic sort |
| Find All Duplicates | Medium | Mark visited |
| First Missing Positive | Medium | Place in position |
| Duplicate Number | Medium | Cycle detection variant |
| Find Corrupt Pair | Medium | Two passes |

## 🔑 Key Points to Remember

1. **Numbers Range**
   - Must know the range (usually 1 to n)
   - Out of range: skip

2. **Correct Position**
   - Number `k` belongs at index `k-1`
   - Check `nums[i] <= n && nums[i] > 0`

3. **Swap Logic**
   ```java
   while (nums[i] != i + 1 && condition) {
       int correctIndex = nums[i] - 1;
       swap(nums, i, correctIndex);
   }
   ```

4. **Two Approaches**
   - Swap to correct position (modifies array)
   - Mark as visited (negative marking)

## ⚠️ Common Mistakes

❌ Forgetting to check bounds (`nums[i] <= n`)
❌ Infinite loop due to wrong swap condition
❌ Not handling duplicates (same number at same position)
❌ Off-by-one in index calculation

✅ Always check `nums[i] > 0 && nums[i] <= n`
✅ Use `while` with correct termination condition
✅ Test with arrays containing out-of-range numbers
✅ Remember: number k → index k-1

## 💡 Pro Tips

1. **Swap Without Temp**
   ```java
   // In Java, need temp variable
   int correctIndex = nums[i] - 1;
   int temp = nums[i];
   nums[i] = nums[correctIndex];
   nums[correctIndex] = temp;
   ```

2. **Mark Visited Pattern**
   ```java
   int index = Math.abs(num) - 1;
   if (nums[index] < 0) {
       // Duplicate found
   }
   nums[index] = -nums[index];
   ```

3. **Handle Out of Range**
   - Numbers <= 0 or > n: can be ignored
   - They don't fit in range anyway

4. **Space Optimization**
   - This is what cyclic sort is for!
   - No extra space, O(1) space complexity

---
**Cyclic Sort**: Master for range-based array problems!

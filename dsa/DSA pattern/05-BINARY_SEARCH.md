# Pattern 5: Binary Search

## 🎯 Core Concept
Efficiently search sorted data or find answers by halving the search space repeatedly.

## ❓ When to Use
- ✅ Searching in sorted array
- ✅ Finding minimum/maximum satisfying a condition
- ✅ Rotate array search
- ✅ First/Last occurrence
- ✅ Guess number games
- ✅ Peak element
- ✅ "Minimize" or "Maximize" with a condition
- ✅ K closest elements to target

## 📊 Time & Space Complexity
- **Time**: O(log n) - halve search space each time
- **Space**: O(1) iterative, O(log n) recursive

## 🔍 How It Works

### Basic Principle
Instead of checking every element, narrow down the search space:
```
arr = [1, 3, 5, 7, 9, 11, 13, 15], target = 7

Step 1: left=0, right=7, mid=3 → arr[3]=7 → FOUND!

If target = 10:
Step 1: left=0, right=7, mid=3 → arr[3]=7 < 10 → search right half
Step 2: left=4, right=7, mid=5 → arr[5]=11 > 10 → search left half
Step 3: left=4, right=4, mid=4 → arr[4]=9 < 10 → search right half
Step 4: left=5, right=4 → left > right, NOT FOUND
```

### When NOT Sorted: Binary Search on Answer
You can use binary search even without a sorted array if you can check a condition!

```
Find square root of 10:
- Can 3 be sqrt? 3*3=9 < 10, try higher
- Can 4 be sqrt? 4*4=16 > 10, try lower
- Can 3.5 be sqrt? 3.5*3.5=12.25 > 10, try lower
... binary search on answers!
```

## 💻 Implementation Template

### Template 1: Basic Binary Search
```java
public class BinarySearch {
    public static int binarySearch(int[] arr, int target) {
        int left = 0, right = arr.length - 1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            
            if (arr[mid] == target) {
                return mid;
            } else if (arr[mid] < target) {
                left = mid + 1;     // Search right
            } else {
                right = mid - 1;    // Search left
            }
        }
        
        return -1;
    }
    
    public static void main(String[] args) {
        int[] arr = {1, 3, 5, 7, 9, 11};
        System.out.println(binarySearch(arr, 7));   // 3
        System.out.println(binarySearch(arr, 6));   // -1
    }
}
```

### Template 2: First & Last Occurrence
```java
public class FirstLastPosition {
    public static int findFirst(int[] arr, int target) {
        int left = 0, right = arr.length - 1;
        int result = -1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            
            if (arr[mid] == target) {
                result = mid;
                right = mid - 1;    // Keep searching left
            } else if (arr[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        
        return result;
    }
    
    public static int findLast(int[] arr, int target) {
        int left = 0, right = arr.length - 1;
        int result = -1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            
            if (arr[mid] == target) {
                result = mid;
                left = mid + 1;     // Keep searching right
            } else if (arr[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        
        return result;
    }
    
    public static void main(String[] args) {
        int[] arr = {5, 7, 7, 8, 8, 10};
        System.out.println(findFirst(arr, 8));  // 3
        System.out.println(findLast(arr, 8));   // 4
    }
}
```

### Template 3: Search in Rotated Array
```java
public class SearchRotatedArray {
    public static int search(int[] arr, int target) {
        int left = 0, right = arr.length - 1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            
            if (arr[mid] == target) {
                return mid;
            }
            
            if (arr[left] <= arr[mid]) {
                if (arr[left] <= target && target < arr[mid]) {
                    right = mid - 1;    // Search left
                } else {
                    left = mid + 1;     // Search right
                }
            } else {
                if (arr[mid] < target && target <= arr[right]) {
                    left = mid + 1;     // Search right
                } else {
                    right = mid - 1;    // Search left
                }
            }
        }
        
        return -1;
    }
    
    public static void main(String[] args) {
        int[] arr = {4, 5, 6, 7, 0, 1, 2};
        System.out.println(search(arr, 0));  // 4
    }
}
```

### Template 4: Peak Element
```java
public class PeakElement {
    public static int findPeakElement(int[] arr) {
        int left = 0, right = arr.length - 1;
        
        while (left < right) {
            int mid = left + (right - left) / 2;
            
            if (arr[mid] < arr[mid + 1]) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        
        return left;
    }
    
    public static void main(String[] args) {
        int[] arr = {1, 2, 1, 3, 5, 4, 4, 4};
        System.out.println(findPeakElement(arr));
    }
}
```

### Template 5: Square Root
```java
public class Sqrt {
    public static int sqrtInteger(int x) {
        if (x == 0) {
            return 0;
        }
        
        int left = 1, right = x;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            
            if (mid == x / mid) {
                return mid;
            } else if (mid < x / mid) {
                left = mid + 1;     // Try higher
            } else {
                right = mid - 1;    // Try lower
            }
        }
        
        return right;
    }
    
    public static void main(String[] args) {
        System.out.println(sqrtInteger(10));  // 3
        System.out.println(sqrtInteger(4));   // 2
    }
}
```

### Template 6: Minimum Rotated Array
```java
public class FindMinRotated {
    public static int findMin(int[] arr) {
        int left = 0, right = arr.length - 1;
        
        while (left < right) {
            int mid = left + (right - left) / 2;
            
            if (arr[mid] > arr[right]) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        
        return arr[left];
    }
    
    public static void main(String[] args) {
        int[] arr = {4, 5, 6, 7, 0, 1, 2};
        System.out.println(findMin(arr));  // 0
    }
}
```

## 🎓 Common Problem Patterns

### 1. **Direct Search**
- Standard binary search
- Find target
- First/Last occurrence

### 2. **Modified Search**
- Rotated array
- Unknown array properties
- Peak finding

### 3. **Binary Search on Answer**
- Minimize time/cost/distance
- Maximize capacity
- Feasibility check

## 📝 Example Problems

| Problem | Difficulty | Key Insight |
|---------|-----------|-------------|
| Binary Search | Easy | Basic template |
| First Bad Version | Easy | Find boundary |
| Peak Element | Medium | Shrink to peak |
| Search Rotated Array | Medium | Identify sorted half |
| Sqrt(x) | Easy | Binary search on number |
| Capacity To Ship Packages | Medium | Binary search on answer |
| Minimum Days to Bloom | Hard | Feasibility + binary search |

## 🔑 Key Points to Remember

1. **Array Must Be Sorted**
   - Or, there must be a monotonic property
   - Or, you're searching for an answer

2. **Two Types**
   - **Search for value**: Standard template
   - **Search for answer**: Use feasibility function

3. **Boundary Conditions**
   - When to use `<` vs `<=`
   - When to use `mid + 1` vs `mid - 1`
   - Integer overflow: use `left + (right - left) // 2`

4. **Off-by-One Errors**
   - Double-check loop conditions
   - Test with single element arrays

## ⚠️ Common Mistakes

❌ Using `left < right` vs `left <= right` (wrong choice)
❌ Integer overflow: don't use `(left + right) // 2`
❌ Comparing with wrong half in rotated array
❌ Not handling duplicates properly

✅ Use `left + (right - left) // 2` to avoid overflow
✅ Test with size 1 and 2 arrays
✅ Be clear about inclusive/exclusive bounds
✅ Check if duplicates matter

## 💡 Pro Tips

1. **Template for "Search for Answer"**
   ```python
   def canDo(capacity):
       # Check if capacity is enough
       return someCondition
   
   def minCapacity():
       left, right = 1, max_value
       while left < right:
           mid = (left + right) // 2
           if canDo(mid):
               right = mid  # Try smaller
           else:
               left = mid + 1  # Need larger
       return left
   ```

2. **The Three Comparisons**
   - `if arr[mid] == target`: Found
   - `elif arr[mid] < target`: Go right
   - `else`: Go left

3. **Rotated Array Trick**
   - Check which half is sorted
   - Then decide where target is

4. **Interview Ready**
   - State the invariant: what property do left/right maintain?
   - Explain why loop terminates
   - Verify boundary cases

---
**Binary Search**: The most important optimization pattern!

# Pattern 13: Modified Binary Search

## 🎯 Core Concept
Apply binary search to solve problems beyond simple searching, including finding boundaries, peaks, and searching in modified arrays.

## ❓ When to Use
- ✅ Find first/last occurrence of target
- ✅ Search in rotated sorted array
- ✅ Find peak element
- ✅ Find minimum in rotated array
- ✅ Search in 2D matrix
- ✅ Capacity to ship packages (binary search on answer)

## 📊 Time & Space Complexity
- **Time**: O(log n) due to binary search
- **Space**: O(1) for iterative, O(log n) for recursive

## 🔍 How It Works

### Beyond Simple Search
```
Simple: Find if 5 exists in [1, 3, 5, 7, 9]
Modified: Find first position of 8 in [1, 3, 5, 8, 8, 8, 10]
          Find position to insert 8 to maintain order
          Find peak in [1, 3, 5, 4, 2]
```

## 💻 Implementation Template

### Template 1: First and Last Position
```java
public class FirstLastPosition {
    public static int[] searchRange(int[] nums, int target) {
        int[] result = {-1, -1};
        if (nums.length == 0) {
            return result;
        }
        
        result[0] = findFirst(nums, target);
        result[1] = findLast(nums, target);
        
        return result;
    }
    
    private static int findFirst(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        int result = -1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            
            if (nums[mid] == target) {
                result = mid;
                right = mid - 1;  // Keep searching left
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        
        return result;
    }
    
    private static int findLast(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        int result = -1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            
            if (nums[mid] == target) {
                result = mid;
                left = mid + 1;   // Keep searching right
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        
        return result;
    }
    
    public static void main(String[] args) {
        int[] nums = {5, 7, 7, 8, 8, 10};
        System.out.println(Arrays.toString(searchRange(nums, 8)));  // [3, 4]
    }
}
```

### Template 2: Search in Rotated Array
```java
public class SearchRotatedArray {
    public static int search(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            
            if (nums[mid] == target) {
                return mid;
            }
            
            // Determine which half is sorted
            if (nums[left] <= nums[mid]) {
                // Left half is sorted
                if (nums[left] <= target && target < nums[mid]) {
                    right = mid - 1;  // Target in left half
                } else {
                    left = mid + 1;   // Target in right half
                }
            } else {
                // Right half is sorted
                if (nums[mid] < target && target <= nums[right]) {
                    left = mid + 1;   // Target in right half
                } else {
                    right = mid - 1;  // Target in left half
                }
            }
        }
        
        return -1;
    }
    
    public static void main(String[] args) {
        int[] nums = {4, 5, 6, 7, 0, 1, 2};
        System.out.println(search(nums, 0));  // 4
    }
}
```

### Template 3: Find Peak Element
```java
public class FindPeakElement {
    public static int findPeakElement(int[] nums) {
        int left = 0, right = nums.length - 1;
        
        while (left < right) {
            int mid = left + (right - left) / 2;
            
            // If mid < next, peak must be in right
            if (nums[mid] < nums[mid + 1]) {
                left = mid + 1;
            } else {
                // Peak in left or mid
                right = mid;
            }
        }
        
        return left;
    }
    
    public static void main(String[] args) {
        int[] nums = {1, 2, 1, 3, 5, 4, 4, 4};
        System.out.println(findPeakElement(nums));  // 4 or 1
    }
}
```

### Template 4: Find Minimum in Rotated Array II (with duplicates)
```java
public class FindMinRotatedII {
    public static int findMin(int[] nums) {
        int left = 0, right = nums.length - 1;
        
        while (left < right) {
            int mid = left + (right - left) / 2;
            
            if (nums[mid] > nums[right]) {
                // Minimum in right half
                left = mid + 1;
            } else if (nums[mid] < nums[right]) {
                // Minimum in left half or at mid
                right = mid;
            } else {
                // nums[mid] == nums[right], can't determine
                // Shrink right to avoid duplicates
                right--;
            }
        }
        
        return nums[left];
    }
    
    public static void main(String[] args) {
        int[] nums = {1, 3, 1, 1, 1};
        System.out.println(findMin(nums));  // 1
    }
}
```

### Template 5: Search Insert Position
```java
public class SearchInsertPosition {
    public static int searchInsert(int[] nums, int target) {
        int left = 0, right = nums.length;
        
        while (left < right) {
            int mid = left + (right - left) / 2;
            
            if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        
        return left;
    }
    
    public static void main(String[] args) {
        System.out.println(searchInsert(new int[]{1, 3, 5, 6}, 5));      // 2
        System.out.println(searchInsert(new int[]{1, 3, 5, 6}, 7));      // 4
        System.out.println(searchInsert(new int[]{1, 3, 5, 6}, 0));      // 0
    }
}
```

### Template 6: Search in 2D Matrix
```java
public class Search2DMatrix {
    public static boolean searchMatrix(int[][] matrix, int target) {
        if (matrix.length == 0) {
            return false;
        }
        
        int rows = matrix.length;
        int cols = matrix[0].length;
        
        int left = 0, right = rows * cols - 1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int midValue = matrix[mid / cols][mid % cols];
            
            if (midValue == target) {
                return true;
            } else if (midValue < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        
        return false;
    }
    
    public static void main(String[] args) {
        int[][] matrix = {{1, 3, 5, 7}, {10, 11, 16, 20}, {23, 30, 34, 60}};
        System.out.println(searchMatrix(matrix, 3));   // true
        System.out.println(searchMatrix(matrix, 13));  // false
    }
}
```

## 🎓 Common Problem Patterns

### 1. **Boundary Finding**
- First occurrence
- Last occurrence
- Insert position

### 2. **Rotated Arrays**
- Search in rotated
- Find minimum
- Find peak

### 3. **Matrix Operations**
- Search in 2D matrix
- Row/Column search
- Diagonal operations

## 📝 Example Problems

| Problem | Difficulty | Key Insight |
|---------|-----------|-------------|
| First/Last Position | Medium | Keep searching for boundary |
| Search Rotated | Medium | Identify sorted half |
| Peak Element | Medium | Compare with neighbor |
| Minimum Rotated II | Hard | Handle duplicates with shrink |
| Search Matrix | Medium | Flatten 2D with index conversion |

## 🔑 Key Points to Remember

1. **Boundary Finding Template**
   ```java
   // Finding first occurrence
   if (condition_met) {
       result = mid;
       right = mid - 1;  // Keep searching left
   }
   ```

2. **Rotated Array Logic**
   - One half is always sorted
   - Check which half
   - Determine where target is

3. **2D to 1D**
   ```java
   int value = matrix[index / cols][index % cols];
   ```

## ⚠️ Common Mistakes

❌ Using `<=` vs `<` inconsistently
❌ Not handling duplicates in rotated array
❌ Wrong index conversion for 2D matrix
❌ Off-by-one errors with boundaries

✅ Use `left + (right - left) / 2` to avoid overflow
✅ Handle duplicates: shrink array
✅ Test boundary cases
✅ Verify matrix dimensions

## 💡 Pro Tips

1. **Avoid Integer Overflow**
   ```java
   int mid = left + (right - left) / 2;  // Good
   // NOT: int mid = (left + right) / 2;  // Bad
   ```

2. **Rotated Array Trick**
   - Check: `nums[left] <= nums[mid]`
   - This tells which half is sorted

3. **Duplicate Handling**
   ```java
   if (nums[mid] == nums[right]) {
       right--;  // Shrink to avoid duplicates
   }
   ```

4. **2D Matrix as 1D**
   - Treat matrix as sorted array
   - Use index conversion
   - Space efficient

---
**Modified Binary Search**: Extend binary search to many problems!

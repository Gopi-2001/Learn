# Pattern 4: Prefix Sum

## 🎯 Core Concept
Pre-calculate cumulative sums to answer range sum queries in O(1) time instead of recalculating each time.

## ❓ When to Use
- ✅ Range sum queries (sum from index i to j)
- ✅ Subarray sum equals target
- ✅ Count subarrays with specific properties
- ✅ 2D grid sum problems
- ✅ Questions asking "How many" or "Sum from X to Y"

## 📊 Time & Space Complexity
- **Time**: O(n) preprocessing, O(1) per query
- **Space**: O(n) for prefix array

## 🔍 How It Works

### Basic Idea: Build Once, Query Instantly

Without Prefix Sum:
```
arr = [1, 2, 3, 4, 5]

To find sum from index 2 to 4:
Loop through: 3 + 4 + 5 = 12
Time: O(n)

If you have 100 queries: 100 * O(n) = O(100n)
```

With Prefix Sum:
```
arr = [1, 2, 3, 4, 5]
prefix = [0, 1, 3, 6, 10, 15]
         (where prefix[i] = sum of arr[0...i-1])

To find sum from index 2 to 4:
sum = prefix[5] - prefix[2] = 15 - 3 = 12
Time: O(1)

For 100 queries: O(n) + 100 * O(1) = O(n)
```

### The Formula
```
prefix[i] = arr[0] + arr[1] + ... + arr[i-1]

To get sum from index i to j (inclusive):
sum = prefix[j+1] - prefix[i]
```

## 💻 Implementation Template

### Template 1: Basic Range Sum Query
```java
public class RangeSumQuery {
    private int[] prefix;
    
    public RangeSumQuery(int[] arr) {
        prefix = new int[arr.length + 1];
        for (int i = 0; i < arr.length; i++) {
            prefix[i + 1] = prefix[i] + arr[i];
        }
    }
    
    public int rangeSum(int i, int j) {
        return prefix[j + 1] - prefix[i];
    }
    
    public static void main(String[] args) {
        int[] arr = {1, 2, 3, 4, 5};
        RangeSumQuery rs = new RangeSumQuery(arr);
        System.out.println(rs.rangeSum(2, 4));  // 12
        System.out.println(rs.rangeSum(0, 2));  // 6
    }
}
```

### Template 2: Subarray Sum Equals Target
```java
import java.util.*;

public class SubarraySumEqualsK {
    public static int subarraySum(int[] arr, int k) {
        Map<Integer, Integer> prefixCount = new HashMap<>();
        prefixCount.put(0, 1);
        
        int currentSum = 0;
        int result = 0;
        
        for (int num : arr) {
            currentSum += num;
            
            if (prefixCount.containsKey(currentSum - k)) {
                result += prefixCount.get(currentSum - k);
            }
            
            prefixCount.put(currentSum, 
                          prefixCount.getOrDefault(currentSum, 0) + 1);
        }
        
        return result;
    }
    
    public static void main(String[] args) {
        int[] arr = {1, 1, 1, 1, 1};
        System.out.println(subarraySum(arr, 3));  // 3
    }
}
```

### Template 3: Range Sum 2D Query
```java
public class NumMatrix {
    private int[][] prefix;
    
    public NumMatrix(int[][] matrix) {
        int m = matrix.length;
        int n = matrix[0].length;
        prefix = new int[m + 1][n + 1];
        
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                prefix[i][j] = prefix[i-1][j] + prefix[i][j-1] - 
                              prefix[i-1][j-1] + matrix[i-1][j-1];
            }
        }
    }
    
    public int sumRegion(int row1, int col1, int row2, int col2) {
        return prefix[row2+1][col2+1] - prefix[row1][col2+1] - 
               prefix[row2+1][col1] + prefix[row1][col1];
    }
}
```

### Template 4: Maximum Subarray Sum
```java
public class MaxSubarraySum {
    public static int maxSubarraySum(int[] arr) {
        int maxSum = Integer.MIN_VALUE;
        int currentSum = 0;
        int minSoFar = 0;
        
        for (int num : arr) {
            currentSum += num;
            maxSum = Math.max(maxSum, currentSum - minSoFar);
            minSoFar = Math.min(minSoFar, currentSum);
        }
        
        return maxSum;
    }
    
    public static void main(String[] args) {
        int[] arr = {-2, 1, -3, 4, -1, 2, 1, -5, 4};
        System.out.println(maxSubarraySum(arr));  // 6
    }
}
```

### Template 5: Count Subarrays with Product Less Than K
```java
public class NumSubarrayProductLessThanK {
    public static int numSubarrayProductLessThanK(int[] arr, int k) {
        if (k <= 1) {
            return 0;
        }
        
        int left = 0;
        int product = 1;
        int count = 0;
        
        for (int right = 0; right < arr.length; right++) {
            product *= arr[right];
            
            while (product >= k) {
                product /= arr[left];
                left++;
            }
            
            count += right - left + 1;
        }
        
        return count;
    }
    
    public static void main(String[] args) {
        int[] arr = {10, 5, 2, 6};
        System.out.println(numSubarrayProductLessThanK(arr, 100));  // 8
    }
}
```

## 🎓 Common Problem Patterns

### 1. **Direct Range Sum**
- Subarray sum equals target
- Range sum query
- 2D matrix sum

### 2. **Count Problems**
- Count subarrays with sum K
- Count subarrays with product < K
- Contiguous array problems

### 3. **Optimization**
- Find maximum subarray sum
- Find maximum subarray product
- Longest subarray meeting condition

## 📝 Example Problems

| Problem | Difficulty | Key Insight |
|---------|-----------|-------------|
| Range Sum Query | Easy | Basic prefix sum |
| Subarray Sum Equals K | Medium | Prefix + HashMap |
| Product of Array Except Self | Medium | Prefix + Suffix |
| Contiguous Array | Medium | Convert to sum problem |
| Max Subarray Sum | Medium | Min prefix tracking |
| Matrix Region Sum Query | Hard | 2D prefix sum |

## 🔑 Key Points to Remember

1. **Build the Prefix Array**
   ```python
   prefix[i] = prefix[i-1] + arr[i]
   ```

2. **Query Formula**
   ```
   sum(i to j) = prefix[j+1] - prefix[i]
   ```

3. **Hash Map Optimization**
   - Track previously seen prefix sums
   - Instantly check if target subarray exists

4. **2D Prefix Sum**
   - Must subtract overlapping region
   - Formula: sum = tl + br - tr - bl

## ⚠️ Common Mistakes

❌ Off-by-one errors in indexing
❌ Forgetting to initialize prefix with 0
❌ Wrong formula for range sum (use j+1, not j)
❌ Missing base case in hash map (0: 1)

✅ Always double-check index bounds
✅ Draw diagrams for 2D problems
✅ Test with simple arrays first
✅ Verify base cases

## 💡 Pro Tips

1. **Prefix Sum + HashMap**
   - For "sum equals K" problems
   - Track: `current_sum - K` in map

2. **Prefix Product**
   - Same technique works for products
   - Watch for zeros!

3. **Space Optimization**
   - Don't always need full prefix array
   - Can compute on-the-fly for space

4. **Interview Tip**
   - "Brute force: check all subarrays O(n²)"
   - "Optimized: use prefix sums O(n)"
   - Shows algorithmic thinking

---
**Prefix Sum**: Transforms expensive queries into instant lookups!

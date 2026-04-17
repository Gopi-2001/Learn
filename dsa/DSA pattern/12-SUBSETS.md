# Pattern 12: Subsets (BFS on Combinations)

## 🎯 Core Concept
Generate all subsets/combinations by iteratively building upon previously generated subsets.

## ❓ When to Use
- ✅ Generate all subsets
- ✅ Generate all permutations
- ✅ Combination sum
- ✅ Word search with backtracking
- ✅ Partition problems
- ✅ Combinations of size K

## 📊 Time & Space Complexity
- **Time**: O(n * 2^n) - generate 2^n subsets
- **Space**: O(n) for each subset, O(2^n) total for output

## 🔍 How It Works

### BFS Approach: Build Incrementally
```
Array: [1, 2, 3]

Start:     [[]]
Add 1:     [[], [1]]
Add 2:     [[], [1], [2], [1,2]]
Add 3:     [[], [1], [2], [1,2], [3], [1,3], [2,3], [1,2,3]]

For each new element, add it to all existing subsets
```

## 💻 Implementation Template

### Template 1: Generate All Subsets (BFS)
```java
import java.util.*;

public class Subsets {
    public static List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        result.add(new ArrayList<>());  // Start with empty set
        
        // For each number, add it to all existing subsets
        for (int num : nums) {
            int size = result.size();
            for (int i = 0; i < size; i++) {
                // Create new subset by adding num to existing subset
                List<Integer> newSubset = new ArrayList<>(result.get(i));
                newSubset.add(num);
                result.add(newSubset);
            }
        }
        
        return result;
    }
    
    public static void main(String[] args) {
        int[] nums = {1, 2, 3};
        System.out.println(subsets(nums));
        // Output: [[], [1], [2], [1,2], [3], [1,3], [2,3], [1,2,3]]
    }
}
```

### Template 2: Generate All Subsets (Backtracking)
```java
public class SubsetsBacktracking {
    public static List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(result, new ArrayList<>(), nums, 0);
        return result;
    }
    
    private static void backtrack(List<List<Integer>> result,
                                  List<Integer> current,
                                  int[] nums, int start) {
        // Add current subset to result
        result.add(new ArrayList<>(current));
        
        // Try adding each remaining number
        for (int i = start; i < nums.length; i++) {
            current.add(nums[i]);
            backtrack(result, current, nums, i + 1);
            current.remove(current.size() - 1);  // Backtrack
        }
    }
    
    public static void main(String[] args) {
        int[] nums = {1, 2, 3};
        System.out.println(subsets(nums));
    }
}
```

### Template 3: Combination Sum
```java
public class CombinationSum {
    public static List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(candidates);
        backtrack(result, new ArrayList<>(), candidates, target, 0);
        return result;
    }
    
    private static void backtrack(List<List<Integer>> result,
                                  List<Integer> current,
                                  int[] candidates,
                                  int remaining, int start) {
        if (remaining == 0) {
            result.add(new ArrayList<>(current));
            return;
        }
        
        if (remaining < 0) {
            return;
        }
        
        for (int i = start; i < candidates.length; i++) {
            // Skip duplicates
            if (i > start && candidates[i] == candidates[i - 1]) {
                continue;
            }
            
            // Skip if candidate too large
            if (candidates[i] > remaining) {
                break;
            }
            
            current.add(candidates[i]);
            // Note: pass i, not i+1, because we can reuse same number
            backtrack(result, current, candidates, remaining - candidates[i], i);
            current.remove(current.size() - 1);
        }
    }
    
    public static void main(String[] args) {
        int[] candidates = {2, 3, 6, 7};
        System.out.println(combinationSum(candidates, 7));
        // Output: [[2,2,3], [7]]
    }
}
```

### Template 4: Permutations
```java
public class Permutations {
    public static List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(result, new ArrayList<>(), nums);
        return result;
    }
    
    private static void backtrack(List<List<Integer>> result,
                                  List<Integer> current,
                                  int[] nums) {
        if (current.size() == nums.length) {
            result.add(new ArrayList<>(current));
            return;
        }
        
        for (int num : nums) {
            if (!current.contains(num)) {
                current.add(num);
                backtrack(result, current, nums);
                current.remove((Integer) num);
            }
        }
    }
    
    public static void main(String[] args) {
        int[] nums = {1, 2, 3};
        System.out.println(permute(nums));
        // Output: [[1,2,3], [1,3,2], [2,1,3], ...]
    }
}
```

### Template 5: Combinations of Size K
```java
public class Combinations {
    public static List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(result, new ArrayList<>(), 1, n, k);
        return result;
    }
    
    private static void backtrack(List<List<Integer>> result,
                                  List<Integer> current,
                                  int start, int n, int k) {
        if (current.size() == k) {
            result.add(new ArrayList<>(current));
            return;
        }
        
        for (int i = start; i <= n; i++) {
            current.add(i);
            backtrack(result, current, i + 1, n, k);
            current.remove(current.size() - 1);
        }
    }
    
    public static void main(String[] args) {
        System.out.println(combine(4, 2));
        // Output: [[1,2], [1,3], [1,4], [2,3], [2,4], [3,4]]
    }
}
```

## 🎓 Common Problem Patterns

### 1. **Subsets**
- All subsets
- Subsets of size K
- Unique subsets

### 2. **Combinations**
- All combinations
- Combinations with sum K
- K combinations of N

### 3. **Permutations**
- All permutations
- Unique permutations
- Next permutation

## 📝 Example Problems

| Problem | Difficulty | Key Insight |
|---------|-----------|-------------|
| Subsets | Medium | BFS building |
| Permutations | Medium | Choice tracking |
| Combinations | Medium | Start index |
| Combination Sum | Hard | Reuse elements |
| Word Search | Hard | Grid exploration |

## 🔑 Key Points to Remember

1. **Two Approaches**
   - BFS: iterative, build from smaller
   - Backtracking: recursive, explore all paths

2. **Backtracking Template**
   ```java
   void backtrack(List<?> result, List<?> current, ...) {
       if (termination_condition) {
           result.add(new ArrayList<>(current));
           return;
       }
       
       for (...) {
           current.add(...);
           backtrack(...);
           current.remove(...);  // Backtrack
       }
   }
   ```

3. **Key Differences**
   - Subset: add to result at each step
   - Combination: add when size matches
   - Permutation: add when all elements used

## ⚠️ Common Mistakes

❌ Forgetting to backtrack (remove) after recursion
❌ Not creating new ArrayList copy for result
❌ Modifying result list during iteration
❌ Wrong base case for recursion

✅ Always `current.remove()` after recursion
✅ Use `new ArrayList<>(current)` when adding
✅ Deep copy collections
✅ Clear base case conditions

## 💡 Pro Tips

1. **Deep Copy Pattern**
   ```java
   result.add(new ArrayList<>(current));
   ```

2. **BFS for Subsets**
   - Simple and clean
   - No recursion needed
   - Good for understanding

3. **Backtracking for Complex**
   - Needed for constraints
   - Skip duplicates easily
   - More control flow

4. **Optimization**
   - Early termination if sum exceeds target
   - Skip duplicates in sorted array
   - Prune invalid paths early

---
**Subsets Pattern**: Master for combinations and permutations!

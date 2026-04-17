# Pattern 15: Top K Elements

## 🎯 Core Concept
Use a heap to efficiently find the K largest/smallest elements without sorting the entire array.

## ❓ When to Use
- ✅ K largest/smallest elements
- ✅ K frequent elements
- ✅ K closest points to origin
- ✅ Top K visited websites
- ✅ Weakest rows in matrix
- ✅ K largest sum subarrays

## 📊 Time & Space Complexity
- **Time**: O(n log k) - build heap and extract K elements
- **Space**: O(k) for heap

## 🔍 How It Works

### Heap Strategy
```
Find top 3 from [3, 2, 1, 5, 4, 7, 2]

Min Heap of size 3: [2, 3, 5]
- Keep heap size ≤ K
- Remove smallest, add new if larger
- At end, [5, 7, 4] → sorted [4, 5, 7]
```

## 💻 Implementation Template

### Template 1: K Largest Elements
```java
import java.util.*;

public class KLargestElements {
    public static List<Integer> findKLargest(int[] nums, int k) {
        // Min heap to keep track of K largest
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        
        for (int num : nums) {
            minHeap.offer(num);
            
            // Keep heap size equal to k
            if (minHeap.size() > k) {
                minHeap.poll();  // Remove smallest
            }
        }
        
        List<Integer> result = new ArrayList<>(minHeap);
        Collections.reverse(result);
        return result;
    }
    
    public static void main(String[] args) {
        int[] nums = {3, 2, 1, 5, 4, 7, 2};
        System.out.println(findKLargest(nums, 3));  // [7, 5, 4]
    }
}
```

### Template 2: K Most Frequent Elements
```java
public class KMostFrequent {
    public static List<Integer> topKFrequent(int[] nums, int k) {
        // Count frequencies
        Map<Integer, Integer> counted = new HashMap<>();
        for (int num : nums) {
            counted.put(num, counted.getOrDefault(num, 0) + 1);
        }
        
        // Min heap by frequency
        PriorityQueue<Integer> minHeap = new PriorityQueue<>(
            (a, b) -> counted.get(a) - counted.get(b)
        );
        
        for (int num : counted.keySet()) {
            minHeap.offer(num);
            
            // Keep heap size equal to k
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }
        
        return new ArrayList<>(minHeap);
    }
    
    public static void main(String[] args) {
        int[] nums = {1, 1, 1, 2, 2, 3};
        System.out.println(topKFrequent(nums, 2));  // [1, 2]
    }
}
```

### Template 3: K Closest Points to Origin
```java
public class KClosestPoints {
    public static int[][] kClosest(int[][] points, int k) {
        // Max heap by distance (so we can remove farthest)
        PriorityQueue<int[]> maxHeap = new PriorityQueue<>(
            (a, b) -> Integer.compare(
                b[0] * b[0] + b[1] * b[1],
                a[0] * a[0] + a[1] * a[1]
            )
        );
        
        for (int[] point : points) {
            maxHeap.offer(point);
            
            // Keep heap size equal to k
            if (maxHeap.size() > k) {
                maxHeap.poll();  // Remove farthest
            }
        }
        
        int[][] result = new int[k][2];
        int i = 0;
        while (!maxHeap.isEmpty()) {
            result[i++] = maxHeap.poll();
        }
        
        return result;
    }
    
    public static void main(String[] args) {
        int[][] points = {{1, 1}, {-2, 4}, {3, 3}};
        int[][] result = kClosest(points, 2);
        for (int[] p : result) {
            System.out.println(Arrays.toString(p));
        }
    }
}
```

### Template 4: Weakest Rows in Matrix
```java
public class WeakestRows {
    public static int[] getWeakestRows(int[][] mat, int k) {
        // Max heap by soldiers count (then by index for tiebreak)
        PriorityQueue<int[]> maxHeap = new PriorityQueue<>(
            (a, b) -> {
                int countA = a[0];
                int indexA = a[1];
                int countB = b[0];
                int indexB = b[1];
                
                if (countA != countB) {
                    return Integer.compare(countB, countA);  // Max by count
                }
                return Integer.compare(indexB, indexA);     // Max by index
            }
        );
        
        // Count soldiers in each row
        for (int i = 0; i < mat.length; i++) {
            int count = 0;
            for (int j = 0; j < mat[i].length; j++) {
                count += mat[i][j];
            }
            
            maxHeap.offer(new int[]{count, i});
            
            if (maxHeap.size() > k) {
                maxHeap.poll();
            }
        }
        
        int[] result = new int[k];
        int i = k - 1;
        while (!maxHeap.isEmpty()) {
            result[i--] = maxHeap.poll()[1];
        }
        
        return result;
    }
    
    public static void main(String[] args) {
        int[][] mat = {{1, 1, 0, 0, 0},
                       {1, 1, 1, 1, 0},
                       {1, 0, 0, 0, 0},
                       {1, 1, 0, 0, 0},
                       {1, 1, 1, 0, 0}};
        System.out.println(Arrays.toString(getWeakestRows(mat, 3)));
    }
}
```

### Template 5: Connect Ropes (Minimum Cost)
```java
public class ConnectRopes {
    public static int connectRopes(int[] ropes) {
        if (ropes.length == 1) {
            return 0;
        }
        
        // Min heap to get smallest ropes
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        for (int rope : ropes) {
            minHeap.offer(rope);
        }
        
        int totalCost = 0;
        
        while (minHeap.size() > 1) {
            // Connect two smallest ropes
            int rope1 = minHeap.poll();
            int rope2 = minHeap.poll();
            int cost = rope1 + rope2;
            
            totalCost += cost;
            minHeap.offer(cost);
        }
        
        return totalCost;
    }
    
    public static void main(String[] args) {
        int[] ropes = {3, 3, 2, 6};
        System.out.println(connectRopes(ropes));  // 29
    }
}
```

## 🎓 Common Problem Patterns

### 1. **Top K Elements**
- K largest/smallest
- K frequent
- K closest

### 2. **Custom Comparators**
- By frequency
- By distance
- By count then index

### 3. **Heap Optimization**
- Avoid full sort
- Process as we go
- Early termination possible

## 📝 Example Problems

| Problem | Difficulty | Key Insight |
|---------|-----------|-------------|
| K Largest Elements | Easy | Min heap size k |
| K Most Frequent | Medium | Frequency count + heap |
| K Closest Points | Medium | Distance formula heap |
| Top K Visited Pages | Hard | Frequency + timestamp |
| K Largest Sum | Hard | Heap of sums |

## 🔑 Key Points to Remember

1. **Heap Size Management**
   ```java
   if (heap.size() > k) {
       heap.poll();  // Remove smallest
   }
   ```

2. **Comparator Choice**
   - Min heap: keep K largest (remove smallest)
   - Max heap: keep K smallest (remove largest)

3. **Custom Comparators**
   ```java
   (a, b) -> {
       // Custom comparison logic
       if (a != b) return comparison;
       return tiebreaker;
   }
   ```

## ⚠️ Common Mistakes

❌ Using max heap when should use min heap
❌ Forgetting tiebreaker in comparator
❌ Not checking heap size before polling
❌ Converting heap to array incorrectly

✅ Always know which element to remove
✅ Test with K=1, K=n cases
✅ Verify heap property maintains
✅ Check comparator carefully

## 💡 Pro Tips

1. **Quick Tip**
   - K largest: use **min heap**
   - K smallest: use **max heap**

2. **Comparator Pattern**
   ```java
   (a, b) -> Integer.compare(b, a)  // Max heap
   (a, b) -> Integer.compare(a, b)  // Min heap (default)
   ```

3. **Complex Comparator**
   ```java
   (a, b) -> {
       if (freq.get(a) != freq.get(b)) {
           return freq.get(a) - freq.get(b);
       }
       return a - b;  // Tiebreak
   }
   ```

4. **Space vs Time Tradeoff**
   - O(n log k) heap approach
   - Faster than O(n log n) full sort
   - When k << n, huge savings

---
**Top K Elements**: Efficient solution without full sort!

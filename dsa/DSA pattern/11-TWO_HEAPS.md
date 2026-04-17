# Pattern 11: Two Heaps

## 🎯 Core Concept
Use a max heap and min heap together to efficiently manage the two halves of data, enabling instant median or balance queries.

## ❓ When to Use
- ✅ Finding median in data stream
- ✅ Balanced two halves
- ✅ Scheduling problems
- ✅ K closest elements to value
- ✅ Top K frequent elements
- ✅ Reorganize string

## 📊 Time & Space Complexity
- **Time**: O(log n) for insertion, O(1) for median query
- **Space**: O(n) for heaps

## 🔍 How It Works

### Basic Idea: Two Halves
```
Maintain:
- Max Heap: smaller half (max at top)
- Min Heap: larger half (min at top)

Numbers: 1, 2, 3, 4, 5

Max Heap (smaller): [2, 1]
Min Heap (larger):  [3, 4, 5]

Median = (2 + 3) / 2 = 2.5
```

## 💻 Implementation Template

### Template 1: Find Median from Data Stream
```java
import java.util.*;

public class MedianFinder {
    private PriorityQueue<Integer> maxHeap;  // smaller half (max at top)
    private PriorityQueue<Integer> minHeap;  // larger half (min at top)
    
    public MedianFinder() {
        // Java's PriorityQueue is min-heap by default
        // Use comparator for max-heap
        maxHeap = new PriorityQueue<>((a, b) -> b - a);
        minHeap = new PriorityQueue<>();
    }
    
    public void addNum(int num) {
        // Always add to max-heap first
        maxHeap.offer(num);
        
        // Ensure max-heap max <= min-heap min
        if (!maxHeap.isEmpty() && !minHeap.isEmpty() &&
            maxHeap.peek() > minHeap.peek()) {
            int max = maxHeap.poll();
            minHeap.offer(max);
        }
        
        // Balance sizes: maxHeap size = minHeap size or size + 1
        if (maxHeap.size() > minHeap.size() + 1) {
            int max = maxHeap.poll();
            minHeap.offer(max);
        }
        if (minHeap.size() > maxHeap.size()) {
            int min = minHeap.poll();
            maxHeap.offer(min);
        }
    }
    
    public double findMedian() {
        if (maxHeap.size() > minHeap.size()) {
            return maxHeap.peek();
        }
        return (maxHeap.peek() + minHeap.peek()) / 2.0;
    }
    
    public static void main(String[] args) {
        MedianFinder mf = new MedianFinder();
        mf.addNum(1);
        System.out.println(mf.findMedian());  // 1.0
        
        mf.addNum(2);
        System.out.println(mf.findMedian());  // 1.5
        
        mf.addNum(3);
        System.out.println(mf.findMedian());  // 2.0
    }
}
```

### Template 2: Schedule Meeting (Two Heaps Pattern)
```java
public class MeetingScheduler {
    public static List<Integer> employeeFreeTime(int[][][] schedule) {
        // Use min-heap by meeting end time
        PriorityQueue<Event> pq = new PriorityQueue<>(
            (a, b) -> Integer.compare(a.end, b.end)
        );
        
        // Add all meetings to heap
        for (int i = 0; i < schedule.length; i++) {
            for (int[] meeting : schedule[i]) {
                pq.offer(new Event(meeting[0], meeting[1], i));
            }
        }
        
        List<Integer> result = new ArrayList<>();
        int maxEnd = 0;
        
        while (!pq.isEmpty()) {
            Event event = pq.poll();
            
            // Gap found
            if (event.start > maxEnd) {
                result.add(maxEnd);
                result.add(event.start);
            }
            
            maxEnd = Math.max(maxEnd, event.end);
        }
        
        return result;
    }
    
    static class Event {
        int start, end, person;
        Event(int start, int end, int person) {
            this.start = start;
            this.end = end;
            this.person = person;
        }
    }
}
```

### Template 3: Reorganize String (Frequency-based)
```java
public class ReorganizeString {
    public static String reorganizeString(String s) {
        // Count frequencies
        Map<Character, Integer> freq = new HashMap<>();
        for (char c : s.toCharArray()) {
            freq.put(c, freq.getOrDefault(c, 0) + 1);
        }
        
        // Max heap by frequency
        PriorityQueue<Character> maxHeap = new PriorityQueue<>(
            (a, b) -> Integer.compare(freq.get(b), freq.get(a))
        );
        maxHeap.addAll(freq.keySet());
        
        StringBuilder result = new StringBuilder();
        
        while (!maxHeap.isEmpty()) {
            // Try to place two most frequent characters
            char first = maxHeap.poll();
            
            if (maxHeap.isEmpty()) {
                // Only one character left
                if (freq.get(first) > 1) {
                    return "";  // Can't arrange
                }
                result.append(first);
                break;
            }
            
            char second = maxHeap.poll();
            result.append(first).append(second);
            
            // Decrease frequency
            freq.put(first, freq.get(first) - 1);
            freq.put(second, freq.get(second) - 1);
            
            // Add back if count > 0
            if (freq.get(first) > 0) {
                maxHeap.offer(first);
            }
            if (freq.get(second) > 0) {
                maxHeap.offer(second);
            }
        }
        
        return result.toString();
    }
    
    public static void main(String[] args) {
        System.out.println(reorganizeString("abab"));      // "abab" or "baba"
        System.out.println(reorganizeString("aaab"));      // ""
        System.out.println(reorganizeString("aabbcc"));    // "abcabc"
    }
}
```

### Template 4: K Closest Points to Origin
```java
public class KClosestPoints {
    public static int[][] kClosest(int[][] points, int k) {
        // Min heap by distance
        PriorityQueue<int[]> pq = new PriorityQueue<>(
            (a, b) -> (b[0] * b[0] + b[1] * b[1]) - 
                      (a[0] * a[0] + a[1] * a[1])
        );
        
        for (int[] point : points) {
            pq.offer(point);
            
            // Keep only k closest
            if (pq.size() > k) {
                pq.poll();
            }
        }
        
        int[][] result = new int[k][2];
        int i = 0;
        while (!pq.isEmpty()) {
            result[i++] = pq.poll();
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

## 🎓 Common Problem Patterns

### 1. **Median Finding**
- Running median
- Median of stream
- Approximate median

### 2. **Top K Elements**
- K closest
- K frequent
- K largest

### 3. **Balancing**
- Two halves balance
- Alternating placement
- Frequency management

## 📝 Example Problems

| Problem | Difficulty | Key Insight |
|---------|-----------|-------------|
| Find Median Stream | Hard | Balance two heaps |
| Reorganize String | Medium | Frequency heap |
| K Closest Points | Medium | Min heap of size K |
| Top K Frequent | Medium | Heap by frequency |
| Seat Distribution | Hard | Multiple heaps |

## 🔑 Key Points to Remember

1. **Heap Balance**
   ```java
   // maxHeap.size() == minHeap.size() or size() + 1
   if (maxHeap.size() > minHeap.size() + 1) {
       minHeap.offer(maxHeap.poll());
   }
   ```

2. **Java's PriorityQueue**
   - Default: min-heap
   - Reverse: use comparator `(a,b) -> b-a`

3. **When to Use**
   - Need instant access to min AND max
   - Balancing two sides
   - Top K elements efficiently

## ⚠️ Common Mistakes

❌ Forgetting to balance heaps after insertion
❌ Wrong heap type (min vs max)
❌ Not handling single element case
❌ Comparing objects directly without method

✅ Always balance sizes
✅ Test with 1, 2, 3 elements
✅ Use comparators carefully
✅ Verify both heaps maintain properties

## 💡 Pro Tips

1. **Quick Max Heap in Java**
   ```java
   PriorityQueue<Integer> maxHeap = new PriorityQueue<>(
       (a, b) -> Integer.compare(b, a)
   );
   ```

2. **For Frequency/Custom Objects**
   ```java
   PriorityQueue<Element> pq = new PriorityQueue<>(
       (a, b) -> Integer.compare(b.frequency, a.frequency)
   );
   ```

3. **Size Checking**
   - Before polling, check isEmpty()
   - Before peeking, check isEmpty()

---
**Two Heaps**: Essential for median and top-K problems!

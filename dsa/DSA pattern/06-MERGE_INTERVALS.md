# Pattern 6: Merge Intervals

## 🎯 Core Concept
Efficiently handle overlapping intervals by sorting and merging them in a single pass.

## ❓ When to Use
- ✅ Merging overlapping intervals
- ✅ Inserting interval into list
- ✅ Meeting room scheduling
- ✅ Range union/intersection problems
- ✅ Calendar event overlap detection

## 📊 Time & Space Complexity
- **Time**: O(n log n) for sorting, O(n) for merging
- **Space**: O(1) excluding output array

## 🔍 How It Works

### Understanding Overlap
Two intervals `[a, b]` and `[c, d]` overlap if:
- `c <= b` (second starts before or when first ends)

```
No Overlap:        [1, 3]    [5, 7]
Overlap:           [1, 5]    [3, 8]  → merge to [1, 8]
One Inside:        [1, 8]    [3, 5]  → merge to [1, 8]
Adjacent:          [1, 3]    [3, 5]  → merge to [1, 5]
```

### Strategy
1. Sort intervals by start time
2. Iterate through sorted intervals
3. If current overlaps with last merged: extend it
4. Otherwise: add as new interval

## 💻 Implementation Template

### Template 1: Merge All Intervals
```java
import java.util.*;

public class MergeIntervals {
    public static int[][] merge(int[][] intervals) {
        if (intervals.length <= 1) {
            return intervals;
        }
        
        // Sort by start time
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
        
        List<int[]> merged = new ArrayList<>();
        int[] current = intervals[0];
        
        for (int i = 1; i < intervals.length; i++) {
            // Check if current overlaps with next interval
            if (current[1] >= intervals[i][0]) {
                // Merge: extend end to max of both
                current[1] = Math.max(current[1], intervals[i][1]);
            } else {
                // No overlap: add current to result, move to next
                merged.add(current);
                current = intervals[i];
            }
        }
        
        // Don't forget last interval
        merged.add(current);
        
        return merged.toArray(new int[0][]);
    }
    
    public static void main(String[] args) {
        int[][] intervals = {{1, 3}, {2, 6}, {8, 10}, {15, 18}};
        int[][] result = merge(intervals);
        for (int[] interval : result) {
            System.out.println(Arrays.toString(interval));
        }
        // Output: [1, 6], [8, 10], [15, 18]
    }
}
```

### Template 2: Insert Interval
```java
public class InsertInterval {
    public static int[][] insert(int[][] intervals, int[] newInterval) {
        List<int[]> result = new ArrayList<>();
        int i = 0;
        
        // Add all intervals that end before new interval starts
        while (i < intervals.length && intervals[i][1] < newInterval[0]) {
            result.add(intervals[i]);
            i++;
        }
        
        // Merge overlapping intervals
        while (i < intervals.length && intervals[i][0] <= newInterval[1]) {
            newInterval[0] = Math.min(newInterval[0], intervals[i][0]);
            newInterval[1] = Math.max(newInterval[1], intervals[i][1]);
            i++;
        }
        result.add(newInterval);
        
        // Add remaining intervals
        while (i < intervals.length) {
            result.add(intervals[i]);
            i++;
        }
        
        return result.toArray(new int[0][]);
    }
    
    public static void main(String[] args) {
        int[][] intervals = {{1, 2}, {3, 5}, {6, 9}};
        int[] newInterval = {2, 5};
        int[][] result = insert(intervals, newInterval);
        for (int[] interval : result) {
            System.out.println(Arrays.toString(interval));
        }
        // Output: [1, 5], [6, 9]
    }
}
```

### Template 3: Meeting Rooms (Check if overlapping)
```java
public class MeetingRooms {
    public static boolean canAttendMeetings(int[][] intervals) {
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
        
        for (int i = 1; i < intervals.length; i++) {
            // If current starts before previous ends, overlap exists
            if (intervals[i][0] < intervals[i - 1][1]) {
                return false;
            }
        }
        
        return true;
    }
    
    public static void main(String[] args) {
        int[][] meetings1 = {{0, 30}, {5, 10}, {15, 20}};
        int[][] meetings2 = {{7, 10}, {2, 4}};
        
        System.out.println(canAttendMeetings(meetings1));  // false
        System.out.println(canAttendMeetings(meetings2));  // true
    }
}
```

### Template 4: Count Meeting Rooms Needed
```java
public class MeetingRoomsII {
    public static int minMeetingRooms(int[][] intervals) {
        // Two pointers: track check-ins and check-outs
        int[] starts = new int[intervals.length];
        int[] ends = new int[intervals.length];
        
        for (int i = 0; i < intervals.length; i++) {
            starts[i] = intervals[i][0];
            ends[i] = intervals[i][1];
        }
        
        Arrays.sort(starts);
        Arrays.sort(ends);
        
        int rooms = 0;
        int maxRooms = 0;
        int startIdx = 0, endIdx = 0;
        
        // Sweep line algorithm
        while (startIdx < intervals.length) {
            if (starts[startIdx] < ends[endIdx]) {
                // Meeting starts, need a room
                rooms++;
                startIdx++;
            } else {
                // Meeting ends, room freed
                rooms--;
                endIdx++;
            }
            maxRooms = Math.max(maxRooms, rooms);
        }
        
        return maxRooms;
    }
    
    public static void main(String[] args) {
        int[][] meetings = {{0, 30}, {5, 10}, {15, 20}};
        System.out.println(minMeetingRooms(meetings));  // Output: 2
    }
}
```

## 🎓 Common Problem Patterns

### 1. **Basic Merging**
- Merge all overlapping intervals
- Merge in-place

### 2. **Insertion**
- Insert new interval and merge
- Insert with specific constraints

### 3. **Counting/Scheduling**
- Count rooms needed
- Check feasibility
- Find conflicts

## 📝 Example Problems

| Problem | Difficulty | Key Insight |
|---------|-----------|-------------|
| Merge Intervals | Medium | Sort + single pass |
| Insert Interval | Medium | Linear merge |
| Meeting Rooms | Easy | Check all overlaps |
| Meeting Rooms II | Hard | Sweep line algorithm |
| Employee Free Time | Hard | Reverse merge |

## 🔑 Key Points to Remember

1. **Sort First**
   - Always sort by start time first
   - Crucial for linear time merge

2. **Overlap Condition**
   - `current[1] >= next[0]` means overlap
   - Merge by: `[min(start), max(end)]`

3. **Edge Cases**
   - Empty input
   - Single interval
   - Completely overlapping
   - Adjacent intervals

4. **Sweep Line Alternative**
   - For counting problems
   - Create events: +1 at start, -1 at end
   - Sort and process

## ⚠️ Common Mistakes

❌ Using `>` instead of `>=` for overlap check
❌ Forgetting to add last merged interval
❌ Modifying input array when shouldn't
❌ Off-by-one errors with time boundaries

✅ Always test adjacent intervals
✅ Remember to add final merged interval
✅ Be careful with inclusive/exclusive endpoints
✅ Handle empty input first

## 💡 Pro Tips

1. **Quick Overlap Check**
   ```java
   if (current[1] >= next[0]) {
       // Overlaps
   }
   ```

2. **Merge Formula**
   ```java
   int[] merged = {
       Math.min(a[0], b[0]),
       Math.max(a[1], b[1])
   };
   ```

3. **Sweep Line for Counting**
   - Separate start and end times
   - Sort both arrays
   - Two pointers to count concurrent

4. **For Large Inputs**
   - Sweep line is cleaner
   - Better for memory if sorting happens separately

---
**Merge Intervals**: Essential for scheduling and range problems!

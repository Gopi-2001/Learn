# Pattern 16: Topological Sort (Graph)

## 🎯 Core Concept
Order nodes in a directed acyclic graph (DAG) such that for every edge u→v, u comes before v.

## ❓ When to Use
- ✅ Course scheduling with prerequisites
- ✅ Build system dependencies
- ✅ Package dependency resolution
- ✅ Alien dictionary
- ✅ Compile order determination
- ✅ Detect cycle in DAG

## 📊 Time & Space Complexity
- **Time**: O(V + E) where V vertices, E edges
- **Space**: O(V + E) for graph and recursion stack

## 🔍 How It Works

### Two Approaches

**Kahn's Algorithm (BFS):**
```
Courses: A → B → C
         ↓   ↓
         D ← E

In-degree: A=0, B=1, C=1, D=1, E=2

Start with in-degree 0: A
Process A: Add B, E to queue (decrease in-degrees)
Process B: Add C to queue
...
Final order: A, B, E, C, D
```

**DFS Approach:**
```
Visit nodes recursively
Add to result after visiting all descendants
Reverse result for topological order
```

## 💻 Implementation Template

### Template 1: Kahn's Algorithm (BFS)
```java
import java.util.*;

public class TopologicalSort {
    public static List<Integer> topologicalSort(int numCourses, 
                                                int[][] prerequisites) {
        // Build adjacency list and in-degree array
        List<List<Integer>> graph = new ArrayList<>();
        int[] inDegree = new int[numCourses];
        
        for (int i = 0; i < numCourses; i++) {
            graph.add(new ArrayList<>());
        }
        
        for (int[] edge : prerequisites) {
            int course = edge[0];
            int prereq = edge[1];
            graph.get(prereq).add(course);
            inDegree[course]++;
        }
        
        // Queue for nodes with in-degree 0
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < numCourses; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
            }
        }
        
        List<Integer> result = new ArrayList<>();
        
        // Process nodes with in-degree 0
        while (!queue.isEmpty()) {
            int course = queue.poll();
            result.add(course);
            
            // Reduce in-degree of neighbors
            for (int nextCourse : graph.get(course)) {
                inDegree[nextCourse]--;
                
                // If in-degree becomes 0, add to queue
                if (inDegree[nextCourse] == 0) {
                    queue.offer(nextCourse);
                }
            }
        }
        
        // If not all courses processed, there's a cycle
        if (result.size() != numCourses) {
            return new ArrayList<>();  // Cycle detected
        }
        
        return result;
    }
    
    public static void main(String[] args) {
        int[][] prerequisites = {{1, 0}, {2, 1}};
        List<Integer> order = topologicalSort(3, prerequisites);
        System.out.println(order);  // [0, 1, 2]
    }
}
```

### Template 2: DFS Approach
```java
public class TopologicalSortDFS {
    private boolean hasCycle = false;
    
    public List<Integer> topologicalSort(int numCourses, int[][] prerequisites) {
        // Build adjacency list
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) {
            graph.add(new ArrayList<>());
        }
        
        for (int[] edge : prerequisites) {
            graph.get(edge[1]).add(edge[0]);
        }
        
        // State: 0 = unvisited, 1 = visiting, 2 = visited
        int[] state = new int[numCourses];
        Stack<Integer> stack = new Stack<>();
        
        // DFS from each unvisited node
        for (int i = 0; i < numCourses; i++) {
            if (state[i] == 0) {
                dfs(i, graph, state, stack);
            }
        }
        
        if (hasCycle) {
            return new ArrayList<>();
        }
        
        // Stack contains reverse topological order
        List<Integer> result = new ArrayList<>();
        while (!stack.isEmpty()) {
            result.add(stack.pop());
        }
        
        return result;
    }
    
    private void dfs(int node, List<List<Integer>> graph, 
                     int[] state, Stack<Integer> stack) {
        state[node] = 1;  // Mark as visiting
        
        for (int neighbor : graph.get(node)) {
            if (state[neighbor] == 1) {
                hasCycle = true;  // Back edge found
                return;
            }
            
            if (state[neighbor] == 0) {
                dfs(neighbor, graph, state, stack);
            }
        }
        
        state[node] = 2;  // Mark as visited
        stack.push(node);
    }
}
```

### Template 3: Course Schedule II (Return order)
```java
public class CourseScheduleII {
    public static int[] findOrder(int numCourses, int[][] prerequisites) {
        // Build graph
        List<List<Integer>> graph = new ArrayList<>();
        int[] inDegree = new int[numCourses];
        
        for (int i = 0; i < numCourses; i++) {
            graph.add(new ArrayList<>());
        }
        
        for (int[] edge : prerequisites) {
            graph.get(edge[1]).add(edge[0]);
            inDegree[edge[0]]++;
        }
        
        // Kahn's algorithm
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < numCourses; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
            }
        }
        
        int[] order = new int[numCourses];
        int index = 0;
        
        while (!queue.isEmpty()) {
            int course = queue.poll();
            order[index++] = course;
            
            for (int nextCourse : graph.get(course)) {
                inDegree[nextCourse]--;
                if (inDegree[nextCourse] == 0) {
                    queue.offer(nextCourse);
                }
            }
        }
        
        // If not all courses processed, cycle exists
        if (index != numCourses) {
            return new int[]{};
        }
        
        return order;
    }
    
    public static void main(String[] args) {
        int[][] prerequisites = {{1, 0}};
        int[] order = findOrder(2, prerequisites);
        System.out.println(Arrays.toString(order));  // [0, 1]
    }
}
```

### Template 4: Alien Dictionary
```java
public class AlienDictionary {
    public static String alienOrder(String[] words) {
        // Build graph from word relationships
        Map<Character, Set<Character>> graph = new HashMap<>();
        Map<Character, Integer> inDegree = new HashMap<>();
        
        // Initialize all characters
        for (String word : words) {
            for (char c : word.toCharArray()) {
                graph.putIfAbsent(c, new HashSet<>());
                inDegree.putIfAbsent(c, 0);
            }
        }
        
        // Build edges
        for (int i = 0; i < words.length - 1; i++) {
            String word1 = words[i];
            String word2 = words[i + 1];
            
            int minLen = Math.min(word1.length(), word2.length());
            
            for (int j = 0; j < minLen; j++) {
                char c1 = word1.charAt(j);
                char c2 = word2.charAt(j);
                
                if (c1 != c2) {
                    if (!graph.get(c1).contains(c2)) {
                        graph.get(c1).add(c2);
                        inDegree.put(c2, inDegree.get(c2) + 1);
                    }
                    break;
                }
            }
        }
        
        // Kahn's algorithm
        Queue<Character> queue = new LinkedList<>();
        for (char c : inDegree.keySet()) {
            if (inDegree.get(c) == 0) {
                queue.offer(c);
            }
        }
        
        StringBuilder result = new StringBuilder();
        
        while (!queue.isEmpty()) {
            char c = queue.poll();
            result.append(c);
            
            for (char neighbor : graph.get(c)) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.offer(neighbor);
                }
            }
        }
        
        // Check if valid order (all characters included)
        if (result.length() != graph.size()) {
            return "";
        }
        
        return result.toString();
    }
    
    public static void main(String[] args) {
        String[] words = {"wrt", "wrf", "er", "ett", "rftt"};
        System.out.println(alienOrder(words));  // "wertf"
    }
}
```

## 🎓 Common Problem Patterns

### 1. **Scheduling**
- Course scheduling
- Build order
- Task execution

### 2. **Graph Problems**
- Cycle detection
- Order determination
- Dependency resolution

### 3. **Advanced**
- Alien dictionary
- Sequence reconstruction
- Parallel courses

## 📝 Example Problems

| Problem | Difficulty | Key Insight |
|---------|-----------|-------------|
| Course Schedule | Medium | Detect cycle |
| Course Schedule II | Medium | Return order |
| Alien Dictionary | Hard | Graph from words |
| Build Order | Hard | Dependency DAG |
| Topological Sort | Medium | Kahn's or DFS |

## 🔑 Key Points to Remember

1. **Kahn's Algorithm**
   ```java
   while (!queue.isEmpty()) {
       node = queue.poll();
       for (neighbor : graph[node]) {
           inDegree[neighbor]--;
           if (inDegree[neighbor] == 0) {
               queue.offer(neighbor);
           }
       }
   }
   ```

2. **DFS Version**
   - Track visited state (0/1/2)
   - 1 = visiting (detecting back edge = cycle)
   - 2 = visited (safe)

3. **Cycle Detection**
   - Result size < number of nodes = cycle exists
   - Or: detect back edge in DFS

## ⚠️ Common Mistakes

❌ Not building graph correctly from edges
❌ Forgetting to check for cycles
❌ Wrong direction of edges
❌ Not initializing in-degrees properly

✅ Draw graph first
✅ Always check result size equals nodes
✅ For course A requires B: edge B → A
✅ Initialize in-degree for all nodes

## 💡 Pro Tips

1. **Kahn vs DFS**
   - Kahn: iterative, queue-based, good for learning
   - DFS: recursive, stack-based, good for cycle detection

2. **Graph Building**
   ```java
   // Course A requires B means: B must come before A
   // So: B → A in graph
   graph.get(B).add(A);
   inDegree[A]++;
   ```

3. **Cycle Detection**
   - If result size < total nodes, cycle exists
   - Or check back edges in DFS (state == 1)

4. **Space Optimization**
   - Use adjacency list (not matrix)
   - Especially important for sparse graphs

---
**Topological Sort**: Essential for dependency and scheduling problems!

# Pattern 9: Tree Breadth First Search (BFS)

## 🎯 Core Concept
Traverse a tree level by level using a queue, visiting all nodes at depth k before visiting nodes at depth k+1.

## ❓ When to Use
- ✅ Level-order traversal
- ✅ Right view of tree
- ✅ Average of levels
- ✅ Zigzag traversal
- ✅ Connecting same-level nodes
- ✅ Shortest path in unweighted tree

## 📊 Time & Space Complexity
- **Time**: O(n) - visit each node once
- **Space**: O(w) where w is maximum width (nodes at one level)

## 🔍 How It Works

### Basic Idea: Queue Processing
```
Tree:        1
           /   \
          2     3
         / \
        4   5

BFS Order: 1, 2, 3, 4, 5
Process: Queue = [1] → [2,3] → [4,5,3] → ...
```

## 💻 Implementation Template

### Template 1: Level Order Traversal
```java
import java.util.*;

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode(int val) { this.val = val; }
}

public class LevelOrderTraversal {
    public static List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) {
            return result;
        }
        
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        
        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            List<Integer> currentLevel = new ArrayList<>();
            
            // Process all nodes at current level
            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                currentLevel.add(node.val);
                
                if (node.left != null) {
                    queue.offer(node.left);
                }
                if (node.right != null) {
                    queue.offer(node.right);
                }
            }
            
            result.add(currentLevel);
        }
        
        return result;
    }
    
    public static void main(String[] args) {
        TreeNode root = new TreeNode(3);
        root.left = new TreeNode(9);
        root.right = new TreeNode(20);
        root.right.left = new TreeNode(15);
        root.right.right = new TreeNode(7);
        
        System.out.println(levelOrder(root));
        // Output: [[3], [9, 20], [15, 7]]
    }
}
```

### Template 2: Right View of Tree
```java
public class RightViewOfTree {
    public static List<Integer> rightSideView(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) {
            return result;
        }
        
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        
        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            
            // Process current level
            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                
                // Add last node of each level
                if (i == levelSize - 1) {
                    result.add(node.val);
                }
                
                if (node.left != null) {
                    queue.offer(node.left);
                }
                if (node.right != null) {
                    queue.offer(node.right);
                }
            }
        }
        
        return result;
    }
    
    public static void main(String[] args) {
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.left.right = new TreeNode(5);
        root.right.right = new TreeNode(4);
        
        System.out.println(rightSideView(root));
        // Output: [1, 3, 4]
    }
}
```

### Template 3: Average of Levels
```java
public class AverageOfLevels {
    public static List<Double> averageOfLevels(TreeNode root) {
        List<Double> result = new ArrayList<>();
        if (root == null) {
            return result;
        }
        
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        
        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            long sum = 0;
            
            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                sum += node.val;
                
                if (node.left != null) {
                    queue.offer(node.left);
                }
                if (node.right != null) {
                    queue.offer(node.right);
                }
            }
            
            result.add((double) sum / levelSize);
        }
        
        return result;
    }
}
```

### Template 4: Zigzag Traversal
```java
public class ZigzagTraversal {
    public static List<List<Integer>> zigzagLevelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) {
            return result;
        }
        
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        boolean leftToRight = true;
        
        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            Deque<Integer> currentLevel = new LinkedList<>();
            
            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                
                // Add to front or back based on direction
                if (leftToRight) {
                    currentLevel.addLast(node.val);
                } else {
                    currentLevel.addFirst(node.val);
                }
                
                if (node.left != null) {
                    queue.offer(node.left);
                }
                if (node.right != null) {
                    queue.offer(node.right);
                }
            }
            
            result.add(new ArrayList<>(currentLevel));
            leftToRight = !leftToRight;
        }
        
        return result;
    }
}
```

### Template 5: Vertical Order Traversal
```java
public class VerticalOrderTraversal {
    public static List<List<Integer>> verticalOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) {
            return result;
        }
        
        Map<Integer, List<Integer>> columnMap = new TreeMap<>();
        Queue<TreeNode> nodeQueue = new LinkedList<>();
        Queue<Integer> columnQueue = new LinkedList<>();
        
        nodeQueue.offer(root);
        columnQueue.offer(0);
        
        while (!nodeQueue.isEmpty()) {
            TreeNode node = nodeQueue.poll();
            int column = columnQueue.poll();
            
            columnMap.putIfAbsent(column, new ArrayList<>());
            columnMap.get(column).add(node.val);
            
            if (node.left != null) {
                nodeQueue.offer(node.left);
                columnQueue.offer(column - 1);
            }
            if (node.right != null) {
                nodeQueue.offer(node.right);
                columnQueue.offer(column + 1);
            }
        }
        
        result.addAll(columnMap.values());
        return result;
    }
}
```

## 🎓 Common Problem Patterns

### 1. **Level Traversal**
- Level order
- Level order reverse
- N-ary tree level order

### 2. **View Problems**
- Right/left view
- Top/bottom view
- Vertical order

### 3. **Calculation**
- Average of levels
- Sum at each level
- Maximum at each level

## 📝 Example Problems

| Problem | Difficulty | Key Insight |
|---------|-----------|-------------|
| Level Order | Easy | Standard BFS |
| Right View | Easy | Last node logic |
| Average of Levels | Easy | Track level size |
| Zigzag | Medium | Deque direction |
| Vertical Order | Medium | Column tracking |

## 🔑 Key Points to Remember

1. **Track Level Size**
   ```java
   int levelSize = queue.size();
   for (int i = 0; i < levelSize; i++) {
       // Process one level
   }
   ```

2. **Queue Operations**
   - `offer()`: add to end
   - `poll()`: remove from front
   - `isEmpty()`: check if empty

3. **Managing Metadata**
   - Use parallel queue for column/distance/etc
   - Or use Node wrapper class

## ⚠️ Common Mistakes

❌ Not tracking level size correctly
❌ Processing all queue items without level awareness
❌ Modifying queue while processing
❌ Not checking null children

✅ Always save `levelSize` before processing
✅ Use for loop for level traversal
✅ Check `!= null` before adding to queue
✅ Use appropriate queue methods

## 💡 Pro Tips

1. **Two Queue Pattern**
   ```java
   Queue<TreeNode> nodeQueue = new LinkedList<>();
   Queue<Integer> columnQueue = new LinkedList<>();
   // Keep parallel information
   ```

2. **Deque for Zigzag**
   ```java
   Deque<Integer> deque;
   if (leftToRight) {
       deque.addLast(node.val);
   } else {
       deque.addFirst(node.val);
   }
   ```

3. **Level Size Awareness**
   - Crucial for per-level operations
   - Don't iterate without tracking

---
**BFS**: Perfect for level-based tree problems!

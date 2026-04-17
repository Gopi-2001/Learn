# Pattern 10: Tree Depth First Search (DFS)

## 🎯 Core Concept
Traverse a tree deeply along one branch before backtracking, exploring all nodes using recursion or stack.

## ❓ When to Use
- ✅ In-order, pre-order, post-order traversal
- ✅ Finding paths/max depth
- ✅ Tree serialization
- ✅ Validate binary search tree
- ✅ Closest value to target
- ✅ Entire tree problems (not level-specific)

## 📊 Time & Space Complexity
- **Time**: O(n) - visit each node once
- **Space**: O(h) where h is height (recursion stack)

## 🔍 How It Works

### Three Types of DFS Traversal
```
Tree:        1
           /   \
          2     3

Pre-order (Node, Left, Right):   1, 2, 3
In-order (Left, Node, Right):    2, 1, 3
Post-order (Left, Right, Node):  2, 3, 1
```

## 💻 Implementation Template

### Template 1: Recursive DFS - All Traversals
```java
class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode(int val) { this.val = val; }
}

public class TreeTraversals {
    
    // Pre-order: Node, Left, Right
    public static void preOrder(TreeNode root, List<Integer> result) {
        if (root == null) {
            return;
        }
        result.add(root.val);           // Visit node
        preOrder(root.left, result);     // Left
        preOrder(root.right, result);    // Right
    }
    
    // In-order: Left, Node, Right
    public static void inOrder(TreeNode root, List<Integer> result) {
        if (root == null) {
            return;
        }
        inOrder(root.left, result);      // Left
        result.add(root.val);            // Visit node
        inOrder(root.right, result);     // Right
    }
    
    // Post-order: Left, Right, Node
    public static void postOrder(TreeNode root, List<Integer> result) {
        if (root == null) {
            return;
        }
        postOrder(root.left, result);    // Left
        postOrder(root.right, result);   // Right
        result.add(root.val);            // Visit node
    }
    
    public static void main(String[] args) {
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        
        List<Integer> preResult = new ArrayList<>();
        List<Integer> inResult = new ArrayList<>();
        List<Integer> postResult = new ArrayList<>();
        
        preOrder(root, preResult);
        inOrder(root, inResult);
        postOrder(root, postResult);
        
        System.out.println("Pre-order:  " + preResult);   // [1, 2, 3]
        System.out.println("In-order:   " + inResult);    // [2, 1, 3]
        System.out.println("Post-order: " + postResult);  // [2, 3, 1]
    }
}
```

### Template 2: Maximum Path Sum
```java
public class MaxPathSum {
    private int maxSum = Integer.MIN_VALUE;
    
    public int maxPathSum(TreeNode root) {
        dfs(root);
        return maxSum;
    }
    
    private int dfs(TreeNode node) {
        if (node == null) {
            return 0;
        }
        
        // Get max sum from left and right (at least 0 if negative)
        int leftSum = Math.max(0, dfs(node.left));
        int rightSum = Math.max(0, dfs(node.right));
        
        // Update global max including this node as highest point
        maxSum = Math.max(maxSum, leftSum + node.val + rightSum);
        
        // Return max sum going down from this node
        return node.val + Math.max(leftSum, rightSum);
    }
    
    public static void main(String[] args) {
        TreeNode root = new TreeNode(-10);
        root.left = new TreeNode(9);
        root.right = new TreeNode(20);
        root.right.left = new TreeNode(15);
        root.right.right = new TreeNode(7);
        
        MaxPathSum mps = new MaxPathSum();
        System.out.println(mps.maxPathSum(root));  // 42
    }
}
```

### Template 3: Validate Binary Search Tree
```java
public class ValidateBST {
    public static boolean isValidBST(TreeNode root) {
        return dfs(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }
    
    private static boolean dfs(TreeNode node, long min, long max) {
        if (node == null) {
            return true;
        }
        
        // Check if current node violates BST property
        if (node.val <= min || node.val >= max) {
            return false;
        }
        
        // Left subtree: values must be less than current
        // Right subtree: values must be greater than current
        return dfs(node.left, min, node.val) && 
               dfs(node.right, node.val, max);
    }
    
    public static void main(String[] args) {
        TreeNode valid = new TreeNode(2);
        valid.left = new TreeNode(1);
        valid.right = new TreeNode(3);
        
        System.out.println(isValidBST(valid));  // true
        
        TreeNode invalid = new TreeNode(5);
        invalid.left = new TreeNode(1);
        invalid.right = new TreeNode(4);
        invalid.right.left = new TreeNode(3);
        invalid.right.right = new TreeNode(6);
        
        System.out.println(isValidBST(invalid));  // false
    }
}
```

### Template 4: Path Sum
```java
public class PathSum {
    public static boolean hasPathSum(TreeNode root, int targetSum) {
        return dfs(root, targetSum);
    }
    
    private static boolean dfs(TreeNode node, int remaining) {
        if (node == null) {
            return false;
        }
        
        remaining -= node.val;
        
        // Leaf node check
        if (node.left == null && node.right == null) {
            return remaining == 0;
        }
        
        return dfs(node.left, remaining) || dfs(node.right, remaining);
    }
    
    public static void main(String[] args) {
        TreeNode root = new TreeNode(5);
        root.left = new TreeNode(4);
        root.right = new TreeNode(8);
        root.left.left = new TreeNode(11);
        root.left.left.left = new TreeNode(7);
        root.left.left.right = new TreeNode(2);
        
        System.out.println(hasPathSum(root, 22));  // true
        System.out.println(hasPathSum(root, 26));  // false
    }
}
```

### Template 5: Symmetric Tree
```java
public class SymmetricTree {
    public static boolean isSymmetric(TreeNode root) {
        if (root == null) {
            return true;
        }
        return isMirror(root.left, root.right);
    }
    
    private static boolean isMirror(TreeNode node1, TreeNode node2) {
        if (node1 == null && node2 == null) {
            return true;
        }
        
        if (node1 == null || node2 == null) {
            return false;
        }
        
        return (node1.val == node2.val) &&
               isMirror(node1.left, node2.right) &&
               isMirror(node1.right, node2.left);
    }
    
    public static void main(String[] args) {
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(2);
        root.left.left = new TreeNode(3);
        root.left.right = new TreeNode(4);
        root.right.left = new TreeNode(4);
        root.right.right = new TreeNode(3);
        
        System.out.println(isSymmetric(root));  // true
    }
}
```

### Template 6: Lowest Common Ancestor
```java
public class LowestCommonAncestor {
    public static TreeNode findLCA(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null) {
            return null;
        }
        
        // If either p or q matches root, root is LCA
        if (root == p || root == q) {
            return root;
        }
        
        // Look for p and q in left and right
        TreeNode leftLCA = findLCA(root.left, p, q);
        TreeNode rightLCA = findLCA(root.right, p, q);
        
        // If both found in different subtrees
        if (leftLCA != null && rightLCA != null) {
            return root;
        }
        
        // If found in one subtree
        return leftLCA != null ? leftLCA : rightLCA;
    }
    
    public static void main(String[] args) {
        TreeNode root = new TreeNode(3);
        TreeNode node5 = new TreeNode(5);
        TreeNode node1 = new TreeNode(1);
        root.left = node5;
        root.right = node1;
        root.left.left = new TreeNode(6);
        root.left.right = new TreeNode(2);
        root.left.right.left = new TreeNode(7);
        root.left.right.right = new TreeNode(4);
        root.right.left = new TreeNode(0);
        root.right.right = new TreeNode(8);
        
        System.out.println(findLCA(root, node5, node1).val);  // 3
    }
}
```

## 🎓 Common Problem Patterns

### 1. **Traversals**
- Pre/In/Post-order
- All paths
- Path sum

### 2. **Tree Properties**
- Height/Diameter
- Balanced tree
- Valid BST

### 3. **Advanced**
- Serialize/Deserialize
- LCA (Lowest Common Ancestor)
- Tag with metadata

## 📝 Example Problems

| Problem | Difficulty | Key Insight |
|---------|-----------|-------------|
| Inorder Traversal | Easy | Recursive template |
| Max Path Sum | Hard | Return vs global |
| Validate BST | Medium | Min/Max tracking |
| Path Sum | Medium | Reduce remaining |
| Symmetric Tree | Easy | Mirror comparison |
| LCA | Medium | Find in both subtrees |

## 🔑 Key Points to Remember

1. **Base Case**
   ```java
   if (node == null) {
       return; // or return value
   }
   ```

2. **Three Traversal Orders**
   - Pre-order: process before children
   - In-order: process between children (BST order)
   - Post-order: process after children

3. **Return vs Global**
   - Returning: info needed by parent
   - Global: answer to problem

## ⚠️ Common Mistakes

❌ Using `if (node == null) return null;` inconsistently
❌ Forgetting to return combined results
❌ Not handling leaf node specially when needed
❌ Wrong order of operations for in-order vs post-order

✅ State base case clearly
✅ Know when to return/when to update global
✅ Test with single node
✅ Visualize tree before coding

## 💡 Pro Tips

1. **Template Pattern**
   ```java
   private int dfs(TreeNode node) {
       if (node == null) return baseValue;
       
       int left = dfs(node.left);
       int right = dfs(node.right);
       
       // Combine results
       return combine(node.val, left, right);
   }
   ```

2. **When to Use Each Order**
   - Pre-order: serialization, copies
   - In-order: BST ordered values
   - Post-order: deletion, parent dependence

3. **Adding Global if Needed**
   ```java
   private int answer;
   private int dfs(TreeNode node) {
       // ... process node
       answer = Math.max(answer, result);
       return result;
   }
   ```

---
**DFS**: Master this and you'll solve most tree problems!

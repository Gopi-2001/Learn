# Thinking Process Guide - How to Identify Patterns

## 🧠 Mental Model for Problem Solving

When you encounter a DSA problem, ask yourself these questions in order:

### Step 1: Understand the Problem (2-3 minutes)
- **What is the input?** (array, string, tree, graph)
- **What is the output?** (value, list, boolean, path)
- **What constraints exist?** (size limits, value ranges, time complexity)
- **Are there examples?** (trace through examples mentally)

### Step 2: Recognize the Pattern (1-2 minutes)
Ask these key questions:

#### A. Is it about arrays/strings?
```
- "Contiguous subarray"? → SLIDING WINDOW
- "Sorted array, find pair/value"? → TWO POINTERS
- "Range sum queries"? → PREFIX SUM
- "Find max/min subarray"? → KADANE'S (Pattern 16 in Medium article)
```

#### B. Is it about linked lists?
```
- "Has cycle"? → FAST & SLOW POINTERS
- "Reverse"? → IN-PLACE REVERSAL
- "Find kth node from end"? → FAST & SLOW POINTERS
```

#### C. Is it about searching/optimization?
```
- "Sorted array, search"? → BINARY SEARCH
- "Find minimum/maximum satisfying condition"? → BINARY SEARCH ON ANSWER
- "Rotated/modified array"? → MODIFIED BINARY SEARCH
```

#### D. Is it about trees?
```
- "Level by level"? → BFS (BREADTH FIRST SEARCH)
- "Path, sum, traversal"? → DFS (DEPTH FIRST SEARCH)
- "All nodes, any order"? → TREE TRAVERSAL (DFS or BFS)
```

#### E. Is it about graphs?
```
- "Connected components"? → DFS/BFS
- "Build order, prerequisites"? → TOPOLOGICAL SORT
- "Union, grouping"? → UNION FIND
- "Shortest path in unweighted"? → BFS
```

#### F. Is it about combinations/permutations?
```
- "Generate all combinations"? → SUBSETS (Backtracking)
- "All permutations"? → PERMUTATIONS (Backtracking)
- "All subsets"? → SUBSETS (Backtracking or BFS)
```

#### G. Is it about finding K elements?
```
- "Top K largest/smallest"? → TOP K ELEMENTS (Heap)
- "Kth largest"? → TWO HEAPS or HEAP
- "Median of stream"? → TWO HEAPS
```

#### H. Is it about ranges/intervals?
```
- "Overlapping/merging intervals"? → MERGE INTERVALS
- "Meeting rooms, scheduling"? → MERGE INTERVALS
```

#### I. Is it about optimization/counting?
```
- "Maximum ways, sum equals K"? → DYNAMIC PROGRAMMING
- "Minimum operations"? → GREEDY or DP
- "Count occurrences"? → HASH MAP or COUNTING
```

#### J. Is it about numbers/bit operations?
```
- "All numbers appear twice except one"? → BITWISE XOR
- "Power of 2"? → BITWISE XOR
- "Bit manipulation"? → BITWISE XOR
```

### Step 3: Choose Your Approach (1-2 minutes)

Once you've identified the pattern, think about:
1. **Time complexity needed** (is O(n²) acceptable?)
2. **Space complexity** (extra memory allowed?)
3. **Edge cases** (empty input, single element, etc.)

### Step 4: Code the Solution (5-15 minutes)

Write clean code following the pattern template:
1. Start with the template
2. Modify for specific problem
3. Handle edge cases
4. Test with examples

---

## 📊 Quick Decision Tree

```
Is it searching/finding?
├─ YES, sorted array? → BINARY SEARCH
├─ YES, array/string, contiguous? → SLIDING WINDOW
├─ YES, find pair/triplet? → TWO POINTERS
└─ NO

Is it a graph/tree problem?
├─ YES, level-order? → BFS
├─ YES, path/depth? → DFS
├─ YES, dependencies? → TOPOLOGICAL SORT
├─ YES, union groups? → UNION FIND
└─ NO

Is it about subsequences/combinations?
├─ YES → SUBSETS/BACKTRACKING
└─ NO

Is it about K elements?
├─ YES → TOP K ELEMENTS (Heap)
├─ YES, median? → TWO HEAPS
└─ NO

Is it about optimization with overlapping subproblems?
├─ YES → DYNAMIC PROGRAMMING
└─ NO

Is it about intervals?
├─ YES → MERGE INTERVALS
└─ NO

Is it about ranges/sums/queries?
├─ YES → PREFIX SUM
└─ NO

Is it about bit operations/pairing?
├─ YES → BITWISE XOR
└─ NO

Is it a greedy problem?
├─ YES, local optimal = global → GREEDY
└─ NO (probably needs more info)
```

---

## 🎯 Pattern Recognition Examples

### Example 1: "Find Maximum Sum of Any Subarray"
```
Questions to ask:
- Subarray? → Yes (contiguous elements)
- Sorting? → No
- Special operation? → No

Pattern match:
↓
KADANE'S ALGORITHM (part of Dynamic Programming)
OR: can think of as max sum with Sliding Window

Solution: O(n) single pass, track current and max sum
```

### Example 2: "Find Meeting Rooms Needed"
```
Questions to ask:
- Intervals? → Yes
- Overlapping? → Yes
- Need count? → Yes

Pattern match:
↓
MERGE INTERVALS (specifically counting parallel intervals)

Solution: Sort by start, use sweep line algorithm
```

### Example 3: "K Closest Points to Origin"
```
Questions to ask:
- Find K elements? → Yes
- Top/closest K? → Yes
- Need exact K count? → Yes

Pattern match:
↓
TOP K ELEMENTS (Heap pattern)

Solution: Max heap of size K, O(n log k)
```

### Example 4: "Generate All Subsets"
```
Questions to ask:
- Generate all? → Yes
- Combinations/subsets? → Yes
- Need all possibilities? → Yes

Pattern match:
↓
SUBSETS (Backtracking/BFS)

Solution: Either BFS building or DFS recursion
```

### Example 5: "Courses with Prerequisites"
```
Questions to ask:
- Dependencies? → Yes
- Build order? → Yes
- Conditions/prerequisites? → Yes

Pattern match:
↓
TOPOLOGICAL SORT

Solution: Kahn's algorithm with in-degree or DFS with recursion stack
```

---

## ⚡ Speed Tips for Interviews

1. **Spend 30 seconds identifying the pattern**
   - Don't code immediately
   - Recognize what you've seen before

2. **Use the template approach**
   - Each pattern has a standard template
   - Modify the template for this problem
   - Faster and less error-prone

3. **Mention time/space complexity early**
   - Show you're thinking about optimization
   - Justify your approach

4. **Code the happy path first**
   - Get basic solution working
   - Then handle edge cases

5. **Always test with examples**
   - Before submitting
   - Trace through one example completely

---

## 🔑 Memorize These Core Questions

When stuck, ask:
1. Is the input sorted?
2. Do I need to find/count something?
3. Do I need to modify the input in-place?
4. Is there a constraint on space?
5. Could this use a previous solution? (DP)
6. What's the brute force? Can I optimize?
7. Are there overlapping subproblems?
8. Can I use a data structure to help? (Heap, Hash, Stack)

---

## 📈 Progression Path

**Start with:**
- Sliding Window
- Two Pointers
- Binary Search

**Then learn:**
- Tree/Graph traversals (BFS/DFS)
- Dynamic Programming basics

**Then master:**
- Topological Sort
- Two Heaps
- Advanced patterns

**Interview mindset:**
> "I've seen this pattern before. Let me apply the template..."

---

## 💡 Final Wisdom

**Pattern Recognition** is the difference between:
- ❌ Struggling for 45 minutes
- ✅ Solving in 10 minutes + explaining approach

**The 16 patterns** cover ~80% of coding interview questions.

**Your goal**: When you read a problem, immediately think "This is a [Pattern] problem" and know the solution structure.

---

### Pattern Memory Hooks

| Pattern | Key Words | Think | Solution Time |
|---------|-----------|-------|---|
| Sliding Window | "contiguous", "subarray", "max/min" | Move window smoothly | 3-5 min |
| Two Pointers | "sorted", "pair", "palindrome" | Converging pointers | 3-5 min |
| Fast&Slow | "cycle", "linked list", "meeting" | Different speeds | 3-5 min |
| Prefix Sum | "range", "sum from X to Y" | Pre-calculate | 3-5 min |
| Binary Search | "sorted", "search", "target" | Halve the range | 3-5 min |
| Two Heaps | "median", "top k", "two halves" | Max & Min heaps | 5-7 min |
| Bit XOR | "pair", "single", "unique" | Eliminate pairs | 2-3 min |
| DP | "ways", "max/min", "overlapping" | Store subproblems | 5-10 min |
| Backtracking | "combinations", "permutations" | Try & undo | 5-7 min |
| DFS/BFS | "connected", "paths", "traversal" | Recursive/Queue | 5-7 min |
| Union Find | "groups", "connected", "parent" | Find & union | 5-7 min |
| Topological | "dependencies", "order", "schedule" | Kahn's algorithm | 5-7 min |
| Greedy | "optimal", "no backtrack" | Make best choice | 3-5 min |
| Stack/Monotonic | "next greater", "history" | Stack tracking | 3-5 min |
| Trie | "prefix", "words", "search" | Tree of letters | 5-7 min |
| Kadane | "max subarray", "dynamic" | Track sums | 3-5 min |


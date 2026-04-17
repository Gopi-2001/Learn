# Pattern 14: Bitwise XOR

## 🎯 Core Concept
Use XOR operator properties to solve problems involving binary manipulation, finding unique elements, and pair operations.

## ❓ When to Use
- ✅ Find single number (others appear twice)
- ✅ Find element appearing once (others appear 3x)
- ✅ Find missing number in array
- ✅ Detect bit difference
- ✅ Swap numbers without temp variable
- ✅ Check power of 2

## 📊 Time & Space Complexity
- **Time**: O(n) - single pass through data
- **Space**: O(1) - no extra space needed

## 🔍 How It Works

### XOR Properties
```
a ^ a = 0        (same number XORed = 0)
a ^ 0 = a        (any number XOR 0 = itself)
a ^ b = b ^ a    (commutative)
(a ^ b) ^ c = a ^ (b ^ c)  (associative)
```

### Example
```
Array: [2, 3, 2]
Find the number that appears once.

2 ^ 3 ^ 2 = (2 ^ 2) ^ 3 = 0 ^ 3 = 3
```

## 💻 Implementation Template

### Template 1: Find Single Number (Appears Once)
```java
public class SingleNumber {
    public static int singleNumber(int[] nums) {
        int result = 0;
        
        // XOR all numbers
        // Duplicates cancel out, single number remains
        for (int num : nums) {
            result ^= num;
        }
        
        return result;
    }
    
    public static void main(String[] args) {
        int[] nums1 = {2, 2, 1};
        System.out.println(singleNumber(nums1));  // 1
        
        int[] nums2 = {4, 1, 2, 1, 2};
        System.out.println(singleNumber(nums2));  // 4
    }
}
```

### Template 2: Find Missing Number
```java
public class MissingNumber {
    public static int missingNumber(int[] nums) {
        // XOR with index positions
        int result = nums.length;  // Start with n
        
        for (int i = 0; i < nums.length; i++) {
            result ^= i;
            result ^= nums[i];
        }
        
        return result;
    }
    
    public static void main(String[] args) {
        int[] nums = {3, 0, 1};
        System.out.println(missingNumber(nums));  // 2
        
        int[] nums2 = {9, 6, 4, 2, 3, 5, 7, 0, 1, 8};
        System.out.println(missingNumber(nums2));  // 10
    }
}
```

### Template 3: Find Single Number III (Two unique numbers)
```java
public class SingleNumberIII {
    public static int[] singleNumber(int[] nums) {
        // First pass: XOR all numbers
        // Result is a ^ b (two unique numbers)
        int xor = 0;
        for (int num : nums) {
            xor ^= num;
        }
        
        // Get rightmost set bit (where a and b differ)
        int rightBit = xor & (-xor);
        
        // Partition numbers by this bit
        int num1 = 0, num2 = 0;
        for (int num : nums) {
            if ((num & rightBit) == 0) {
                num1 ^= num;
            } else {
                num2 ^= num;
            }
        }
        
        return new int[]{num1, num2};
    }
    
    public static void main(String[] args) {
        int[] nums = {1, 2, 1, 3, 2, 5};
        int[] result = singleNumber(nums);
        System.out.println(Arrays.toString(result));  // [3, 5] or [5, 3]
    }
}
```

### Template 4: Hamming Distance (Count bit differences)
```java
public class HammingDistance {
    public static int hammingDistance(int x, int y) {
        // XOR gives different bits
        int xor = x ^ y;
        
        // Count number of 1s in binary
        int count = 0;
        while (xor > 0) {
            if ((xor & 1) == 1) {
                count++;
            }
            xor >>= 1;  // Right shift
        }
        
        return count;
    }
    
    public static void main(String[] args) {
        System.out.println(hammingDistance(1, 4));  // 2
        // 1 = 001
        // 4 = 100
        // XOR = 101, count = 2
    }
}
```

### Template 5: Power of 2
```java
public class PowerOfTwo {
    public static boolean isPowerOfTwo(int n) {
        if (n <= 0) {
            return false;
        }
        
        // Power of 2 has exactly one set bit
        // n & (n-1) removes rightmost set bit
        // If result is 0, n was power of 2
        return (n & (n - 1)) == 0;
    }
    
    public static void main(String[] args) {
        System.out.println(isPowerOfTwo(1));   // true (2^0)
        System.out.println(isPowerOfTwo(2));   // true (2^1)
        System.out.println(isPowerOfTwo(3));   // false
        System.out.println(isPowerOfTwo(16));  // true (2^4)
    }
}
```

### Template 6: Count Set Bits
```java
public class CountBits {
    public static int[] countBits(int n) {
        int[] result = new int[n + 1];
        
        for (int i = 0; i <= n; i++) {
            // Count number of 1s in binary representation
            result[i] = Integer.bitCount(i);
            
            // Or use Brian Kernighan's algorithm:
            // int count = 0;
            // int num = i;
            // while (num > 0) {
            //     num &= (num - 1);
            //     count++;
            // }
            // result[i] = count;
        }
        
        return result;
    }
    
    public static void main(String[] args) {
        int[] result = countBits(5);
        System.out.println(Arrays.toString(result));  // [0, 1, 1, 2, 1, 2]
    }
}
```

## 🎓 Common Problem Patterns

### 1. **Finding Unique**
- Single number
- Missing number
- Duplicate number

### 2. **Bit Manipulation**
- Count set bits
- Hamming distance
- Power of 2

### 3. **Binary Operations**
- Swap without temp
- Bit reversal
- Number complement

## 📝 Example Problems

| Problem | Difficulty | Key Insight |
|---------|-----------|-------------|
| Single Number | Easy | XOR cancels pairs |
| Missing Number | Easy | XOR with indices |
| Single Number III | Medium | Separate by bit |
| Hamming Distance | Easy | XOR then count |
| Power of 2 | Easy | Check single bit |
| Number of 1 Bits | Easy | Count loop |

## 🔑 Key Points to Remember

1. **Core XOR Properties**
   ```
   a ^ a = 0
   a ^ 0 = a
   a ^ b ^ a = b
   ```

2. **Bit Operations**
   ```java
   n & 1          // Check if odd
   n >> 1         // Divide by 2
   n << 1         // Multiply by 2
   n & (n-1)      // Remove rightmost set bit
   n | (n+1)      // Set rightmost unset bit
   ```

3. **Counting Bits**
   ```java
   Integer.bitCount(n)  // Count 1s
   Integer.highestOneBit(n)
   Integer.lowestOneBit(n)
   ```

## ⚠️ Common Mistakes

❌ Confusing XOR with OR
❌ Not understanding commutativity
❌ Wrong bit manipulation operators
❌ Integer overflow in bit operations

✅ Know XOR properties cold
✅ Draw truth tables if unsure
✅ Test with small numbers
✅ Understand bit shift carefully

## 💡 Pro Tips

1. **XOR Cancel Pattern**
   ```java
   // To find unpaired element
   for (int num : array) {
       result ^= num;
   }
   ```

2. **Bit Difference**
   ```java
   // Find rightmost set bit
   int rightBit = xor & (-xor);
   ```

3. **Common Tricks**
   ```
   Power of 2: (n & (n-1)) == 0
   Count bits: Integer.bitCount(n)
   Swap: a = a ^ b; b = a ^ b; a = a ^ b;
   ```

4. **Optimization Pattern**
   - Often reduces solution from O(n^2) to O(n)
   - Only uses O(1) space

---
**Bitwise XOR**: Elegant solution for pairing and bit problems!

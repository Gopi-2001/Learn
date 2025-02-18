
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

// https://algo.monster/liteproblems/2127
// codestorywithMik: https://youtu.be/LVV5_MOzdq4?si=kN-8ZYxu9jVFqeIq

// Is it a graph?
// Yes: The problem describes relationships between employees that can 
// be visualized as a graph where each employee is a node, and their 
// preferences represent directed edges from one employee to another.


// Is it a tree?
// No: The graph potentially contains cycles (employees can ultimately 
// prefer themselves through a chain of preferences), indicating it is 
// not a hierarchical tree structure.

// Is the problem related to directed acyclic graphs (DAGs)?
// No: We are dealing with potential cycles due to the nature of preferences, 
// making it a cyclic graph, not acyclic.

// Is the problem related to shortest paths?
// No: The problem is to find the maximum number of employees that can 
// attend the meeting, which doesn’t involve finding shortest paths.

// Does the problem involve connectivity?
// Yes: We are interested in the largest connected component, particularly 
// the largest grouping where relationships/cycles of invitations exist.

// Is the graph unweighted?
// Yes: There are no weights involved in these relationships; it's merely about 
// connectivity.

public class MaximumEmployeesInvitedToMeeting {
	
	
    public static int maximumInvitations(int[] favorite) {
        
        int n = favorite.length;
		Map<Integer,ArrayList<Integer>> adj = new HashMap<>();
		
		for(int i=0;i<n;i++) adj.put(i,new ArrayList<>());
		
		// Reverse graph - so that we can find the path length after traversal
		for(int i=0;i<n;i++) {
			int u = i;
			int v = favorite[i];
			
			// u --> v , given direction of graph
			// v --> u , reversal of graph direction
			adj.get(v).add(u);
			
		}
		
		int longestCycleCount = 0;
		int twoLengthCycleCount = 0; // if cycle length = 2, what is the traversal length
		
		boolean[] visited = new boolean[n];
		
		for(int i=0;i<n;i++) {
			
			if(!visited[i]) {
				// count the node
				Map<Integer,Integer> mp = new HashMap<>();
				
				int currNode = i;
				int currNodeCount = 0;
				
				while(!visited[currNode]) {
					visited[currNode] = true;
					mp.put(currNode,currNodeCount);
					
					int nextNode = favorite[currNode];  // favorite node of current node
					
					currNodeCount++;
					
					if(mp.containsKey(nextNode)) {
						// cycle is ditected
						int cycleLength = currNodeCount - mp.get(nextNode);
						
						longestCycleCount = Math.max(longestCycleCount, cycleLength);
						
						if(cycleLength==2) {
							// currNode <---> nextNode
							boolean[] vis = new boolean[n];
                            
							vis[currNode] = true;
							vis[nextNode] = true;
							
							twoLengthCycleCount += 2 + bfs(currNode,adj,vis) + bfs(nextNode,adj,vis);
							
						}
						break;
					}
					currNode = nextNode;
				}
				
				
			}
			
			
		}
		
		return Math.max(twoLengthCycleCount,longestCycleCount);
	}

	private static int bfs(int currNode, Map<Integer, ArrayList<Integer>> adj, boolean[] vis) {
	  Queue<int[]>  queue = new ArrayDeque<>();
	  
	  queue.add(new int[]{currNode,0});
	  
	  int maxDistance = 0;
	  
	  while(!queue.isEmpty()) {
		  int[] arr = queue.poll();
		  
		  for(Integer i : adj.get(arr[0])) {
			  if(!vis[i]) {
				  vis[i] = true;
				  queue.add(new int[]{i,arr[1]+1});
				  
				  maxDistance = Math.max(maxDistance, arr[1]+1);
			  }
			  
		  }
		  
	  }
	  
		
	  return maxDistance;
	}
	
	public static void main(String[] args) {
		int[] favorite = {3,0,1,4,1};
		int result = maximumInvitations(favorite);
		
		System.out.println(result);
		
	}

}

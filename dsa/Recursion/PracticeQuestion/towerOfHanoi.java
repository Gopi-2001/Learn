// 1. https://youtu.be/rf6uf3jNjbo?si=4M6tzSoFbKSUNlC-
// 2. https://youtu.be/q6RicK1FCUs?si=nkbhN8B83S0gSCZ0


public class towerOfHanoi {
    public static void main(String[] args){
        solveTowerOfHanoi(3,'A','B','C');
    }

    static void solveTowerOfHanoi(int n, char A,char B,char C){
        // A initial source tower
        // B Auxiliary tower
        // C destination tower
        if(n>0){ // A to C
            solveTowerOfHanoi(n-1, A, C, B); // A to B 
            System.out.println("Move a disk from tower" + A + " to tower" + C); // A to C
            solveTowerOfHanoi(n-1, B, A, C); // B to C
        }
    }
}

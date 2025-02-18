// 1. https://youtu.be/rf6uf3jNjbo?si=4M6tzSoFbKSUNlC-
// 2. https://youtu.be/q6RicK1FCUs?si=nkbhN8B83S0gSCZ0


public class towerOfHanoi {
    public static void main(String[] args){
        solveTowerOfHanoi(2,'A','B','C');
    }

    static void solveTowerOfHanoi(int n, char src,char aux,char des){
        // src initial source tower
        // aux Auxiliary tower
        // des destination tower
        if(n>0){ // src to des
            solveTowerOfHanoi(n-1, src, des, aux); // src to aux 
            System.out.println("Move a disk from tower-" + src + " to tower-" + des); // src to des
            solveTowerOfHanoi(n-1, aux, src, des); // aux to des
        }
    }
}

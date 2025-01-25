public class printNTimes {
    public static void main (String[] args){
        //print(1,5);
        //print1toN(1, 5);
        //printNto1(5, 5);
        //backtrack1toN(5, 5);
        backtrackNto1(1, 5);
    }

    static void print(int i,int n){
        if(i>n) return;
        System.out.println(i + ". Gopi");
        print(i+1,n);

        // Here Printing is done after the Recursion function called
        // Made sure the last Guy to be Executed first
        System.out.println(i + ". Gopi-backtrack");
    }

    static void print1toN(int i,int n){
        if(i>n) return;
        System.out.println(i);
        print1toN(i+1, n);
    }
        
    static void printNto1(int i,int n){
        if(i<1) return;
        System.out.println(i);
        printNto1(i-1, n);
    }

    static void backtrack1toN(int i,int n){
        if(i<1) return;
        backtrack1toN(i-1, n);
        System.out.println(i);
    }

    static void backtrackNto1(int i,int n){
        if(i>n) return;
        backtrackNto1(i+1, n);
        System.out.println(i);
    }
}

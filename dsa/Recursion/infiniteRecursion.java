public class infiniteRecursion {

    public static void main(String[] args){
        func(2);
    }

    static void func(int n){
        //if(n<0) return;
        System.out.println(1);
        func(n-1);
    }

}
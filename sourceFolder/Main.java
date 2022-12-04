package sourcePackage;

public class Main {

    public static void main(String[] args)
    {

        add(3, 5);


    }

    public static void printAddition (int a, int b)
    {
        //System.out.println(add(a,b));
    }

    public static int add (int a, int b)
    {
        int c = a + b;
        c = c - 1;
        return c;
    }
}

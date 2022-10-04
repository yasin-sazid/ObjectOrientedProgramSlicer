package sourcePackage;

public class Main {

    public static void main(String[] args)
    {
        printAddition(5, 8);
        Maths maths = new Maths();
        maths.add(5,6);
    }

    public static void printAddition (int a, int b)
    {
        System.out.println(add(a,b));
    }

    public static int add (int a, int b)
    {
        return a+b;
    }
}

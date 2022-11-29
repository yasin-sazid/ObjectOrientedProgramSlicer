package sourcePackage;

public class Main {

    public static void main(String[] args)
    {
        Maths maths = new ExtraExtendedMaths();

        int a;

        a = maths.add(5,4);
    }

    public static void printAddition (int a, int b)
    {
        //System.out.println(add(a,b));
    }

    public static int add (int a, int b)
    {
        return a+b;
    }
}

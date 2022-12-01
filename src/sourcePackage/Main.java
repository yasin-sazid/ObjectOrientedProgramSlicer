package sourcePackage;

public class Main {

    public static void main(String[] args)
    {
        //int a = add(3,4);
        Maths maths = new ExtraExtendedMaths();

        int a = maths.add(3, 7);
        maths.add(3, 4);

        add(3, 5);

        maths.add("asa");
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

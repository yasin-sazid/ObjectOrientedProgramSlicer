package sourcePackage;

public class Main {

    public static void main(String[] args)
    {
        //printAddition(5, 8);

        Maths maths = new ExtraExtendedMaths();

        int a = maths.add(5,6);
        //int a = 5;

        /*if (a==5)
        {
            printAddition(3, 5);
        }*/
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

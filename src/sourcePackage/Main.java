package sourcePackage;

public class Main {

    public static void main(String[] args)
    {
        Maths em = new ExtraExtendedMaths();

        int a = em.add(5, 7);
    }

    public static int add (int a, int b)
    {
        return a+b;
    }
}

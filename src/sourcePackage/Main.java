package sourcePackage;

public class Main {

    public static void main(String[] args)
    {
        for (int i=0; i<6; i++)
        {
            if (i==3)
            {
                printHelloWorld();
            }
        }
    }

    public static void printHelloWorld ()
    {
        System.out.println("Hello World");
    }
}

package sourcePackage;

public class Main {

    public static void main(String[] args) {

        printHelloWorld();

        int j = 5;

        for (int i=0; i<10; i++)
        {
            while (i > 3 && i < 7)
            {
                System.out.println("I am " + i);
                if (i==5)
                {
                    if (i==j)
                    {
                        System.out.println("I am j");
                    }
                    System.out.println("I am 5");
                }
                else
                {
                    printHelloWorld();
                }
            }
            if (i%2==0)
            {
                System.out.println("I am even");
            }
            else if (i%2!=0)
            {
                System.out.println("I am odd");
            }
        }
    }

    public static void printHelloWorld ()
    {
        System.out.println("Hello World");
    }
}

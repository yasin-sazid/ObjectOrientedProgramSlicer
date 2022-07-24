package sourcePackage;

public class Saal {

    public static void main(String[] args) {
        int a = 2, b = 1;
        int [] array = new int [10];
        array[1] = 7;
        if (a%2==0)
        {
            System.out.println(a +" is even.");
            array[0] = 5;
        }
        else
        {
            System.out.println(a +" is odd.");
        }

        System.out.println(array[0]);

        add(5, 7);
    }

    public static int add (int a, int b)
    {
        return a+b;
    }
}

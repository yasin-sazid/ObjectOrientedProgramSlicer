package sourcePackage;

public class Main {

    public static int add (int a, int b)
    {
        return a+b;
    }

    public static void main(String[] args)
    {
        //interprocedural
        int a = add(3, 4);
        a = 6;
        for (int i=0; i<3; i++)
        {
            //a = add(a, a);
        }

        //procedural
        /*int a = 0;

        int [] array = new int [10];

        for (int i=0; i< 10; i++)
        {
            array [i] = i+1;
            if (i%2==0)
            {
                System.out.println("Even");
                a = 1;
            }
            else
            {
                System.out.println("Odd");
            }
        }

        System.out.println(a);

        for(int i=0; i<10; i++)
        {
            System.out.println(array[i]);
        }*/
    }
}

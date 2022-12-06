package sourcePackage;

public class Main {

    /*public static int add (int a, int b)
    {
        return a+b;
    }

    public static void print (String string)
    {
        System.out.println(string);
    }

    public static int add (Maths maths, int a, int b)
    {
        int temp = maths.add(a, b);
        return temp;
    }

    public static int sub (ExtendedMaths maths, int a, int b)
    {
        int temp = maths.sub(a, b);
        return temp;
    }*/

    public static void main(String[] args)
    {
        //polymorphism
        /*int c = add(new ExtraExtendedMaths(), 2, 3);
        int d = sub(new ExtendedMaths(), 2, 3);*/
        /*Maths maths = new ExtraExtendedMaths();
        int a = maths.add(3,4);
        System.out.println(a);
        ExtraExtendedMaths eem = (ExtraExtendedMaths) maths;
        int b = eem.add(3, 4, 5);
        System.out.println(b);*/

        //derived class and class interactions
        /*ExtendedMaths em = new ExtendedMaths();
        int a = em.add(2, 3);
        int b = em.sub(2, 3);
        System.out.println(a);
        System.out.println(b);*/

        //interprocedural
        /*int a = 0;
        int b = 3;
        int c = add(a, b);
        c = a + b;
        print("Hellon World");*/

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

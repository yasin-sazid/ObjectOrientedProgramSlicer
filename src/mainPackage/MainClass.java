package mainPackage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.*;
import othersPackage.*;


public class MainClass {

    public static void main(String args[]) throws IOException {

        SDG sdg = new SDG("C:\\Users\\Hp\\Desktop\\SPL-UI-Test\\src\\sourcePackage", "C:\\Users\\Hp\\Desktop\\SPL-UI-Test\\src\\sourcePackage\\Maths.java", 11);

        sdg.operations();
    }
}
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

    public static void main(String args[]) {

        SDG sdg = new SDG();

        sdg.operations();
    }
}
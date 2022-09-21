package mainPackage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.*;
import othersPackage.*;


public class MainClass {

    public static void main(String args[]) {

        FolderProcessor folderProcessor = new FolderProcessor("src/sourcePackage");

        for (File javaFile: folderProcessor.getFiles())
        {
            System.out.println(javaFile);
            Operation op = new Operation();
            op.operations(javaFile.getAbsolutePath());
        }


    }
}
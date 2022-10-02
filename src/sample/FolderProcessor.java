package sample;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FolderProcessor
{
    private File folder;
    private List<File> files = new ArrayList<>();
    private Map<String,String[]> pathCodeMap = new HashMap<>();

    public FolderProcessor(String folderPath) throws IOException {
        this.folder = new File(folderPath);
        getFilesFromFolder(this.folder);

        for (File javaFile: this.files)
        {
            String [] lines = readFileToString(javaFile.getAbsolutePath()).split("\r?\n|\r");
            pathCodeMap.put(javaFile.getAbsolutePath(),lines);
        }
    }

    public FolderProcessor ()
    {

    }

    public void setFolder (String folderPath) throws IOException {
        this.folder = new File(folderPath);
        files.clear();
        pathCodeMap.clear();
        getFilesFromFolder(this.folder);

        for (File javaFile: this.files)
        {
            String [] lines = readFileToString(javaFile.getAbsolutePath()).split("\r?\n|\r");
            pathCodeMap.put(javaFile.getAbsolutePath(),lines);
        }
    }

    public Map<String, String[]> getPathCodeMap() {
        return pathCodeMap;
    }

    public void getFilesFromFolder (File filePath)
    {
        //File folder = new File("C:\\Users\\yasinsazid\\Downloads\\ProgramSlicing-master\\ProgramSlicing\\src");
        File[] listOfFiles = filePath.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                if (file.getAbsolutePath().substring(file.getAbsolutePath().length()-5,file.getAbsolutePath().length()).equals(".java"))
                {
                    files.add(file);
                }
            }
            else if (file.isDirectory())
            {
                getFilesFromFolder(file);
            }
        }
    }

    public String readFileToString(String filePath) throws IOException {
        StringBuilder fileData = new StringBuilder(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        char[] buf = new char[10];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }

        reader.close();

        return  fileData.toString();
    }

    public List<File> getFiles() {
        return files;
    }

    public File getFolder() {
        return folder;
    }
}

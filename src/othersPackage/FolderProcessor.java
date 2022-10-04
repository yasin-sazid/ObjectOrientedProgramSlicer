package othersPackage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FolderProcessor
{
    private File folder;
    private List<File> files = new ArrayList<>();
    private List<String> environment = new ArrayList<>();

    public FolderProcessor(String folderPath) {
        this.folder = new File(folderPath);
        getFilesFromFolder(this.folder);
    }

    public void getFilesFromFolder (File filePath)
    {
        //File folder = new File("C:\\Users\\yasinsazid\\Downloads\\ProgramSlicing-master\\ProgramSlicing\\src");
        if (filePath.isDirectory())
        {
            environment.add(filePath.getAbsolutePath());
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
    }

    public List<File> getFiles() {
        return files;
    }

    public String[] getEnvironment() {
        return environment.toArray(new String[0]);
    }
}

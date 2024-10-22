package parser;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.StringTokenizer;

public class CloneGitRepos
{
    private String owner;
    private String url;

    public CloneGitRepos() throws IOException {
        gitReposCloning();
    }

    private void cloneRepo()
    {
        System.out.println("in");
        Git git = Git.cloneRepository()
                .setURI(url)
                .setDirectory(new File("C:\\Users\\Hp\\Desktop\\ClonedRepos\\" + owner + "\\" + url.substring(20, url.length())))
                .call();
        System.out.println("out");
    }

    public void gitReposCloning() throws IOException
    {
        String data = "";
        data = new String(Files.readAllBytes(Paths.get("C:\\Users\\Hp\\Desktop\\BugTriaging\\src\\files\\GitRepos")));

        StringTokenizer st = new StringTokenizer(data);
        while (st.hasMoreTokens())
        {
            String s = st.nextToken();
            if (s.length()>=19)
            {
                if(s.substring(0,19).equals("https://github.com/"))
                {
                    url = s;
                    cloneRepo();
                }
                else
                {
                    owner = s;
                }
            }
            else
            {
                owner = s;
            }

            System.out.println(owner + "----" + url);
        }
    }
}
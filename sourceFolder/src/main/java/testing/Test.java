package testing;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import parser.*;
import profiles.Bug;
import profiles.Developer;
import profiles.FreshGraduate;
import profiles.NewDeveloper;

import javax.print.Doc;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

public class Test
{
    private LocalDate testingDate;
    private List<Developer> experiencedDevelopers = new ArrayList<>();
    private List<NewDeveloper> newExperiencedDevelopers = new ArrayList<>();
    private List<FreshGraduate> freshGraduates = new ArrayList<>();
    private Map<String, Bug> mapOfBugs;
    private List<String> listOfSourceCodeLibraryImports;
    private Map<String, String> mapOfDevelopersWithGithubURLs = new HashMap<>();
    private List<Bug> testBugs = new ArrayList<>();

    List<String> eds = new ArrayList<>();
    List<String> neds = new ArrayList<>();
    List<String> fgs = new ArrayList<>();

    List<String> teamPrecision = new ArrayList<>();
    List<String> teamRecall = new ArrayList<>();
    List<String> teamFScore = new ArrayList<>();
    List<String> avgTopNForSortedResult = new ArrayList<>();
    List<String> avgEfficiencyList = new ArrayList<>();
    List<String> avgMRRList = new ArrayList<>();

    List<Result> teamResults = new ArrayList<>();

    public Test(LocalDate testingDate)
    {
        this.testingDate = testingDate;
    }

    public void createFreshGraduate (Developer developer)
    {
        List<String> list = new ArrayList<>();

        for(String bugID: developer.getListOfBugIds())
        {
            list.addAll(mapOfBugs.get(bugID).getListOfKeywords());
        }

        Collections.shuffle(list);

        list = list.subList(0, list.size()/10);

        FreshGraduate fg = new FreshGraduate(developer);
        fg.getListOfKeyWords().addAll(list);

        freshGraduates.add(fg);
    }

    public void chooseNewDeveloperOrFreshGraduate (Developer developer) throws Exception
    {
        /*System.out.println("-----" + mapOfDevelopersWithGithubURLs.get(developer.getName()));*/
        if(mapOfDevelopersWithGithubURLs.get(developer.getName()).equals("0"))
        {
            createFreshGraduate(developer);
        }
        else
        {
            System.out.println(developer.getName());
            RepoParser rp = new RepoParser("C:\\Users\\Hp\\Desktop\\ClonedRepos\\" + developer.getName());

            //GithubParser gp = new GithubParser(mapOfDevelopersWithGithubURLs.get(developer.getName()),LocalDate.parse("2013-04-15"));
            System.out.println(rp.getListOfLibraryImports().size()+ "----" + rp.getListOfRepositoryKeywords().size());
            if(rp.getListOfRepositoryKeywords().size()==0&&rp.getListOfLibraryImports().size()==0)
            {
                createFreshGraduate(developer);
            }
            else
            {
                newExperiencedDevelopers.add(new NewDeveloper(developer,rp.getListOfRepositoryKeywords(),rp.getListOfLibraryImports()));
            }
        }
    }

    public void testing () throws Exception {
        XMLParser parser = new XMLParser();
        parser.parsing();
        this.mapOfBugs = parser.getMapOfBugs();

        /*Set<String> set = new HashSet<>();
        for(Map.Entry m : mapOfBugs.entrySet())
        {
            Bug b = (Bug) m.getValue();
            set.add(b.getProduct());
        }*/

        /*GithubListParser glp = new GithubListParser();
        glp.parseGithubList();
        this.mapOfDevelopersWithGithubURLs = glp.getMapOfDevelopersWithGithubURLs();*/

        //System.out.println("cp");

        for(Developer developer: parser.getMapOfDevelopers().values())
        {
            if(developer.getStartDate().compareTo(testingDate)<0)
            {
                experiencedDevelopers.add(developer);
            }
            else
            {
                chooseNewDeveloperOrFreshGraduate(developer);
            }
        }

        System.out.println("--" + experiencedDevelopers.size());



        for(Map.Entry b: mapOfBugs.entrySet())
        {
            Bug bug = (Bug) b.getValue();

            if(bug.getSolutionDate().compareTo(testingDate) > 0)
            {
                testBugs.add(bug);
            }
        }

        System.out.println("cp3");

        SourceCodeParser scp = new SourceCodeParser();

        listOfSourceCodeLibraryImports = scp.getListOfLibraryImports();

        //indexing ();

        teamResult();

        outputResult();
    }

    private void indexing() throws IOException, ParseException {
        edIndexing();
        nedIndexing();
        fgIndexing();
    }

    private void teamResult () throws IOException, ParseException {
        List<Bug> poorBugs = new ArrayList<>();
        System.out.println(testBugs.size());
        for (Bug testBug: testBugs)
        {
            readEdIndex(testBug);
            readNedIndex(testBug);
            readFgIndex(testBug);

            if(eds.size()<5||neds.size()<5||fgs.size()<5)
            {
                poorBugs.add(testBug);
                continue;
            }

            /*System.out.println("-----"+eds.size());
            System.out.println(neds.size());
            System.out.println(fgs.size()+"-----");*/

            Result result = new Result(eds,neds,fgs,testBug);

            teamResults.add(result);

            eds.clear();
            neds.clear();
            fgs.clear();
        }

        testBugs.removeAll(poorBugs);

        System.out.println(testBugs.size());

        averageresult();
    }

    private void averageresult()
    {
        for(int i=0; i<5; i++)
        {
            double avgPrecision = 0;
            double avgRecall = 0;
            double avgFScore = 0;
            double avgTopN = 0;
            double avgEfficiency = 0;
            double avgMRR = 0;

            for(Result result: teamResults)
            {
                avgPrecision = avgPrecision + Double.parseDouble(result.getPrecision().get(i));
                avgRecall = avgRecall + Double.parseDouble(result.getRecall().get(i));
                avgFScore = avgFScore + Double.parseDouble(result.getfScore().get(i));
                avgTopN = avgTopN + Double.parseDouble(result.getTopNResultListForSortedDevs().get(i));
                avgEfficiency = avgEfficiency + Double.parseDouble(result.getRankListForSortedDevs().get(i));

                if (!Double.isNaN(Double.parseDouble(result.getRankListForSortedDevs().get(i)))&&!(Double.parseDouble(result.getRankListForSortedDevs().get(i))==0.0))
                {
                    System.out.println("------" + result.getRankListForSortedDevs().get(i));
                    avgMRR = avgMRR + (1.0/Double.parseDouble(result.getRankListForSortedDevs().get(i)));
                }
            }

            avgPrecision = avgPrecision/teamResults.size();
            avgRecall = avgRecall/teamResults.size();
            avgFScore = avgFScore/teamResults.size();
            avgTopN = avgTopN/teamResults.size();
            avgEfficiency = avgEfficiency/teamResults.size();
            System.out.println(avgMRR);
            avgMRR = avgMRR/teamResults.size();
            System.out.println(avgMRR);

            teamPrecision.add(Double.toString(avgPrecision));
            teamRecall.add(Double.toString(avgRecall));
            teamFScore.add(Double.toString(avgFScore));
            avgTopNForSortedResult.add(Double.toString(avgTopN));
            avgEfficiencyList.add(Double.toString(avgEfficiency));
            avgMRRList.add(Double.toString(avgMRR));
        }
    }

    public void outputResult () throws IOException {
        String content = "No. of ED: " + experiencedDevelopers.size() + "\n"
                + "No. of NED: " + newExperiencedDevelopers.size() + "\n"
                + "No. of FG: " + freshGraduates.size() + "\n"
                + "No. of existing bug reports: " + (mapOfBugs.size()-testBugs.size()) + "\n"
                + "No. of existing bug report product: " + existingProductCounter() + "\n"
                + "No. of existing bug report component: " + existingComponentCounter() + "\n"
                + "No. of testing bug reports: " + testBugs.size() + "\n"
                + "No. of testing bug report product: " + testingProductCounter() + "\n"
                + "No. of testing bug report component: " + testingComponentCounter() + "\n"
                + "\n" + "\n"
                + "----" + "k" + "----" + "avg recall" + "----" + "avg precision" + "----" + "avg f-score" + "----" + "\n"
                + "----" + 3 + "----" + teamRecall.get(0).substring(0,5) + "----" + teamPrecision.get(0).substring(0,5) + "----" + teamFScore.get(0).substring(0,5) + "----" + "\n"
                + "----" + 6 + "----" + teamRecall.get(1).substring(0,5) + "----" + teamPrecision.get(1).substring(0,5) + "----" + teamFScore.get(1).substring(0,5) + "----" + "\n"
                + "----" + 9 + "----" + teamRecall.get(2).substring(0,5) + "----" + teamPrecision.get(2).substring(0,5) + "----" + teamFScore.get(2).substring(0,5) + "----" + "\n"
                + "----" + 12 + "----" + teamRecall.get(3).substring(0,5) + "----" + teamPrecision.get(3).substring(0,5) + "----" + teamFScore.get(3).substring(0,5) + "----" + "\n"
                + "----" + 15 + "----" + teamRecall.get(4).substring(0,5) + "----" + teamPrecision.get(4).substring(0,5) + "----" + teamFScore.get(4).substring(0,5) + "----" + "\n"
                + "\n" + "\n"
                + "No. of test reports: " + testBugs.size() + "\n"
                + "Top 1: " + avgTopNForSortedResult.get(0).substring(0,5) + "\n"
                + "Top 2: " + avgTopNForSortedResult.get(1).substring(0,5) + "\n"
                + "Top 3: " + avgTopNForSortedResult.get(2).substring(0,5) + "\n"
                + "Top 4: " + avgTopNForSortedResult.get(3).substring(0,5) + "\n"
                + "Top 5: " + avgTopNForSortedResult.get(4).substring(0,5) + "\n"
                + "\n" + "\n"
                + "Average Effectiveness: " + "\n"
                + "Top 1: " + avgEfficiencyList.get(0).substring(0,5) + "\n"
                + "Top 2: " + avgEfficiencyList.get(1).substring(0,5) + "\n"
                + "Top 3: " + avgEfficiencyList.get(2).substring(0,5) + "\n"
                + "Top 4: " + avgEfficiencyList.get(3).substring(0,5) + "\n"
                + "Top 5: " + avgEfficiencyList.get(4).substring(0,5) + "\n"
                + "\n" + "\n"
                + "Mean Reciprocal Rank: " + "\n"
                + "Top 1: " + avgMRRList.get(0).substring(0,5) + "\n"
                + "Top 2: " + avgMRRList.get(1).substring(0,5) + "\n"
                + "Top 3: " + avgMRRList.get(2).substring(0,5) + "\n"
                + "Top 4: " + avgMRRList.get(3).substring(0,5) + "\n"
                + "Top 5: " + avgMRRList.get(4).substring(0,5) + "\n";

        File file = new File("C:\\Users\\Hp\\Desktop\\BugTriaging\\src\\files\\output");
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(content);
        bw.close();

        System.out.println("Done");
    }

    public int existingProductCounter ()
    {
        Set<String> products = new HashSet<>();

        for(Map.Entry m: mapOfBugs.entrySet())
        {
            Bug bug = (Bug) m.getValue();

            if(testBugs.contains(bug))
            {
                continue;
            }
            else
            {
                products.add(bug.getProduct());
            }
        }

        return products.size();
    }

    public int existingComponentCounter ()
    {
        Set<String> components = new HashSet<>();

        for(Map.Entry m: mapOfBugs.entrySet())
        {
            Bug bug = (Bug) m.getValue();

            if(testBugs.contains(bug))
            {
                continue;
            }
            else
            {
                components.add(bug.getComponent());
            }
        }

        return components.size();
    }

    public int testingProductCounter ()
    {
        Set<String> products = new HashSet<>();

        for(Bug bug: testBugs)
        {
            products.add(bug.getProduct());
        }

        return products.size();
    }

    public int testingComponentCounter ()
    {
        Set<String> components = new HashSet<>();

        for(Bug bug: testBugs)
        {
            components.add(bug.getComponent());
        }

        return components.size();
    }

    public void readEdIndex (Bug testBug) throws IOException, ParseException {
        String indexPath = "C:\\Users\\Hp\\Desktop\\BugTriaging\\src\\files\\edIndex";

        Directory dir = FSDirectory.open(Paths.get(indexPath));

        DirectoryReader directoryReader = DirectoryReader.open(dir);

        IndexSearcher searcher = new IndexSearcher (directoryReader);

        BooleanQuery.setMaxClauseCount(16384);

        BooleanQuery.Builder bq = new BooleanQuery.Builder();

        for (String s: testBug.getListOfKeywords())
        {
            bq.add(new BooleanClause(new TermQuery(new Term("content", s)), BooleanClause.Occur.SHOULD));
        }

        //bq.add(new BooleanClause(new TermQuery(new Term("content", "*")), BooleanClause.Occur.SHOULD));

        //QueryParser qp = new QueryParser("content", new StandardAnalyzer());

        //System.out.println(convertListToQuery(testBugs.get(0).getListOfKeywords()));

        //Query query = qp.parse(convertListToQuery(testBugs.get(0).getListOfKeywords())); //syntax

        TopDocs results = searcher.search(bq.build(), 5);

        for(ScoreDoc scoreDoc: results.scoreDocs)
        {
            Document document = searcher.doc(scoreDoc.doc);
            eds.add(document.get("name"));
            /*System.out.println(document.get("name"));
            System.out.println(scoreDoc.doc);
            System.out.println(scoreDoc.score);
            System.out.println("----------");*/
        }

        directoryReader.close();
    }

    private void readNedIndex (Bug testBug) throws IOException, ParseException {
        String indexPath = "C:\\Users\\Hp\\Desktop\\BugTriaging\\src\\files\\nedIndex";

        Directory dir = FSDirectory.open(Paths.get(indexPath));

        DirectoryReader directoryReader = DirectoryReader.open(dir);

        IndexSearcher searcher = new IndexSearcher (directoryReader);

        BooleanQuery.setMaxClauseCount(16384);

        BooleanQuery.Builder bq = new BooleanQuery.Builder();

        for (String s: testBug.getListOfKeywords())
        {
            bq.add(new BooleanClause(new TermQuery(new Term("content", s)), BooleanClause.Occur.SHOULD));
        }

        for (String s: listOfSourceCodeLibraryImports)
        {
            bq.add(new BooleanClause(new TermQuery(new Term("content", s)), BooleanClause.Occur.SHOULD));
        }

        //bq.add(new BooleanClause(new TermQuery(new Term("content", "*")), BooleanClause.Occur.SHOULD));

        TopDocs results = searcher.search(bq.build(), 5);

        for(ScoreDoc scoreDoc: results.scoreDocs)
        {
            Document document = searcher.doc(scoreDoc.doc);
            neds.add(document.get("name"));
            //System.out.println("------------------" + neds.size());
            /*System.out.println(document.get("name"));
            System.out.println(scoreDoc.doc);
            System.out.println(scoreDoc.score);*/
        }

        directoryReader.close();
    }

    private void readFgIndex (Bug testBug) throws IOException, ParseException {
        String indexPath = "C:\\Users\\Hp\\Desktop\\BugTriaging\\src\\files\\fgIndex";

        Directory dir = FSDirectory.open(Paths.get(indexPath));

        DirectoryReader directoryReader = DirectoryReader.open(dir);

        IndexSearcher searcher = new IndexSearcher (directoryReader);

        BooleanQuery.setMaxClauseCount(16384);

        BooleanQuery.Builder bq = new BooleanQuery.Builder();

        for (String s: testBug.getListOfKeywords())
        {
            bq.add(new BooleanClause(new TermQuery(new Term("content", s)), BooleanClause.Occur.SHOULD));
        }

        //bq.add(new BooleanClause(new TermQuery(new Term("content", "*")), BooleanClause.Occur.SHOULD));

        TopDocs results = searcher.search(bq.build(), 5);
        //System.out.println(results.totalHits.value);

        for(ScoreDoc scoreDoc: results.scoreDocs)
        {
            Document document = searcher.doc(scoreDoc.doc);
            //System.out.println(document);
            fgs.add(document.get("name"));
            //System.out.println("------------------" + fgs.size());
            /*System.out.println(document.get("name"));
            System.out.println(scoreDoc.doc);
            System.out.println(scoreDoc.score);*/
        }

        directoryReader.close();
    }

    private String convertListToString (List<String> list)
    {
        String listString = "";

        for(String s: list)
        {
            listString = listString + s + " ";
        }

        return listString;
    }

    /*private String convertListToQuery (List<String> list)
    {
        String queryString = "";

        for(String s: list)
        {
            queryString = queryString + "content:" + s + " OR ";
        }

        queryString = queryString.substring(0,queryString.length()-4);

        return queryString;
    }*/

    private void deleteIndex (String path)
    {
        File file = new File (path);

        for(File subFile: file.listFiles())
        {
            subFile.delete();
        }
    }

    public void edIndexing() throws IOException {
        String indexPath = "C:\\Users\\Hp\\Desktop\\BugTriaging\\src\\files\\edIndex";

        deleteIndex(indexPath);

        Directory dir = FSDirectory.open(Paths.get(indexPath));

        Analyzer analyzer = new StandardAnalyzer();

        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

        IndexWriter indexWriter = new IndexWriter(dir, iwc);

        for(Developer developer: experiencedDevelopers)
        {
            Document document = new Document();

            String content = "";

            for(String bugID: developer.getListOfBugIds())
            {
                Bug bug = mapOfBugs.get(bugID);
                if (bug.getSolutionDate().compareTo(testingDate) < 0)
                {
                    content = content + " " + convertListToString(bug.getListOfKeywords());
                    content = content + " " + bug.getProduct();
                    content = content + " " + bug.getComponent();
                }
            }

            document.add(new TextField("content", content, Field.Store.NO));
            document.add(new StringField("name", developer.getName(), Field.Store.YES));
            document.add(new StringField("startingDate", developer.getStartDate().toString(), Field.Store.YES));

            indexWriter.addDocument(document);
        }

        indexWriter.close();
    }

    private void nedIndexing() throws IOException {
        String indexPath = "C:\\Users\\Hp\\Desktop\\BugTriaging\\src\\files\\nedIndex";

        deleteIndex(indexPath);

        Directory dir = FSDirectory.open(Paths.get(indexPath));

        Analyzer analyzer = new StandardAnalyzer();

        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

        IndexWriter indexWriter = new IndexWriter(dir, iwc);

        for(NewDeveloper developer: newExperiencedDevelopers)
        {
            Document document = new Document();

            String content = "";

            content = content + " " + convertListToString(developer.getListOfLibraryImports());
            content = content + " " + convertListToString(developer.getListOfRepositoryKeywords());


            document.add(new TextField("content", content, Field.Store.NO));
            document.add(new StringField("name", developer.getDeveloperCore().getName(), Field.Store.YES));
            document.add(new StringField("startingDate", developer.getDeveloperCore().getStartDate().toString(), Field.Store.YES));

            indexWriter.addDocument(document);
        }

        indexWriter.close();
    }

    private void fgIndexing() throws IOException {
        String indexPath = "C:\\Users\\Hp\\Desktop\\BugTriaging\\src\\files\\fgIndex";

        deleteIndex(indexPath);

        Directory dir = FSDirectory.open(Paths.get(indexPath));

        Analyzer analyzer = new StandardAnalyzer();

        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

        IndexWriter indexWriter = new IndexWriter(dir, iwc);

        for(FreshGraduate developer: freshGraduates)
        {
            Document document = new Document();

            String content = "";

            content = content + " " + convertListToString(developer.getListOfKeyWords());


            document.add(new TextField("content", content, Field.Store.NO));
            document.add(new StringField("name", developer.getDeveloperCore().getName(), Field.Store.YES));
            document.add(new StringField("startingDate", developer.getDeveloperCore().getStartDate().toString(), Field.Store.YES));

            indexWriter.addDocument(document);
        }

        indexWriter.close();
    }
}

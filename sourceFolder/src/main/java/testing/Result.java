package testing;

import profiles.Bug;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Result
{
    List<String> eds;
    List<String> neds;
    List<String> fgs;
    Bug testBug;
    List<String> precision = new ArrayList<>();
    List<String> recall = new ArrayList<>();
    List<String> fScore = new ArrayList<>();

    List<String> sortedDevs = new ArrayList<>();
    List<String> topNResultListForSortedDevs = new ArrayList<>();

    List<String> rankListForSortedDevs = new ArrayList<>();

    public Result(List<String> eds, List<String> neds, List<String> fgs, Bug testBug) {
        this.eds = eds;
        this.neds = neds;
        this.fgs = fgs;
        this.testBug = testBug;
        calcPrecisionAndRecall();
        calcFScore();
        sortDevs();
        topNResult();
    }

    /*private void topNResult()
    {
        for (int i=0; i<2; i++)
        {
            sortDevs(i+1);
            topNResultListForSortedDevs.add("0");
            rankListForSortedDevs.add("0");
            for (int j=0; j < sortedDevs.size(); j++)
            {
                if(testBug.getListOfSolvers().contains(sortedDevs.get(j)))
                {
                    topNResultListForSortedDevs.set(i, "1");
                    rankListForSortedDevs.set(i, Integer.toString(j+1));
                    break;
                }
            }
            sortedDevs.clear();
        }
    }

    private void sortDevs(int n)
    {
        if(testBug.getSeverity().equals("high"))
        {
            sortedDevs.addAll(eds.subList(0, 7*n));
            sortedDevs.addAll(neds.subList(0, 2*n));
            sortedDevs.addAll(fgs.subList(0, 1*n));
        }
        else if(testBug.getSeverity().equals("medium"))
        {
            sortedDevs.addAll(eds.subList(0, 5*n));
            sortedDevs.addAll(neds.subList(0, 3*n));
            sortedDevs.addAll(fgs.subList(0, 2*n));
        }
        else if(testBug.getSeverity().equals("low"))
        {
            sortedDevs.addAll(eds.subList(0, 4*n));
            sortedDevs.addAll(neds.subList(0, 3*n));
            sortedDevs.addAll(fgs.subList(0, 3*n));
        }
    }

    public List<String> getPrecision() {
        return precision;
    }

    public List<String> getRecall() {
        return recall;
    }

    public List<String> getfScore() {
        return fScore;
    }

    private void calcPrecisionAndRecall ()
    {
        for(int i=1; i<=2; i++)
        {
            sortDevs(i);
            double temp = 0;
            for (int j=0; j < sortedDevs.size(); j++)
            {
                if (testBug.getListOfSolvers().contains(sortedDevs.get(j)))
                {
                    temp = temp + 1;
                }
            }
            //double tempPrecision = temp/((i+1));
            double tempPrecision = temp/(i*10);
            double tempRecall = temp/testBug.getListOfSolvers().size();
            precision.add(Double.toString(tempPrecision*100));
            recall.add(Double.toString(tempRecall*100));
            sortedDevs.clear();
        }
    }

    private void calcFScore ()
    {
        for (int i=0; i<2; i++)
        {
            double tempPrecision = Double.parseDouble(precision.get(i));
            double tempRecall = Double.parseDouble(recall.get(i));

            double tempFScore = 2*((tempPrecision*tempRecall)/(tempPrecision+tempRecall));

            if(Double.isNaN(tempFScore))
            {
                fScore.add("0.0");
            }
            else
            {
                fScore.add(Double.toString(tempFScore));
            }
        }
    }*/

    private void topNResult()
    {
        for (int i=0; i<5; i++)
        {
            topNResultListForSortedDevs.add("0");
            rankListForSortedDevs.add("0");
            for (int j=0; j<=i; j++)
            {
                if(testBug.getListOfSolvers().contains(sortedDevs.get(j)))
                {
                    topNResultListForSortedDevs.set(i, "1");
                    rankListForSortedDevs.set(i, Integer.toString(j+1));
                    break;
                }
            }
        }
    }

    private void sortDevs()
    {
        //sortedDevs = eds;
        if(testBug.getSeverity().equals("high"))
        {
            sortedDevs = eds;
        }
        else if(testBug.getSeverity().equals("medium"))
        {
            sortedDevs = neds;
        }
        else if(testBug.getSeverity().equals("low"))
        {
            sortedDevs = fgs;
        }
    }

    public List<String> getPrecision() {
        return precision;
    }

    public List<String> getRecall() {
        return recall;
    }

    public List<String> getfScore() {
        return fScore;
    }

    private void calcPrecisionAndRecall ()
    {
        for(int i=0; i<5; i++)
        {
            double temp = 0;
            for (int j=0; j<=i; j++)
            {
                if(!(eds.size()<=j))
                {
                    if (testBug.getListOfSolvers().contains(eds.get(j)))
                    {
                        temp = temp + 1;
                    }
                }

                if(!(neds.size()<=j))
                {
                    if (testBug.getListOfSolvers().contains(neds.get(j)))
                    {
                        temp = temp + 1;
                    }
                }

                if(!(fgs.size()<=j))
                {
                    if (testBug.getListOfSolvers().contains(fgs.get(j)))
                    {
                        temp = temp + 1;
                    }
                }
            }
            //double tempPrecision = temp/((i+1));
            double tempPrecision = temp/((i+1)*3);
            double tempRecall = temp/testBug.getListOfSolvers().size();
            precision.add(Double.toString(tempPrecision*100));
            recall.add(Double.toString(tempRecall*100));
        }
    }

    private void calcFScore ()
    {
        for (int i=0; i<5; i++)
        {
            double tempPrecision = Double.parseDouble(precision.get(i));
            double tempRecall = Double.parseDouble(recall.get(i));

            double tempFScore = 2*((tempPrecision*tempRecall)/(tempPrecision+tempRecall));

            if(Double.isNaN(tempFScore))
            {
                fScore.add("0.0");
            }
            else
            {
                fScore.add(Double.toString(tempFScore));
            }
        }
    }

    public List<String> getTopNResultListForSortedDevs() {
        return topNResultListForSortedDevs;
    }

    public List<String> getRankListForSortedDevs() {
        return rankListForSortedDevs;
    }
}

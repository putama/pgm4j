package putama.hmm;

import putama.util.DataUtil;
import java.util.*;

/**
 * Created by prutama on 7/29/17.
 */
public class SimpleTrigramHMM {
    HashMap<String, Integer> worddict;
    HashMap<Integer, String> worddictRev;

    HashMap<String, Integer> tagdict;
    HashMap<Integer, String> tagdictRev;

    ArrayList<ArrayList<Integer>> words;
    ArrayList<ArrayList<Integer>> tags;

    ArrayList<ArrayList<Integer>> valWords;
    ArrayList<ArrayList<Integer>> valTags;

    double [][][] qParams;
    double [][] eParams;

    public static void main(String [] args) {
        System.out.println("hello HMM!");
        String basePath = System.getProperty("user.dir");
        String trainPath = basePath+"/dataset/pos/wsj2-21.txt";
        String testPath = basePath+"/dataset/pos/wsj22.txt";

        SimpleTrigramHMM simpleTrigram = new SimpleTrigramHMM(trainPath, testPath);

        // estimate parameters
        simpleTrigram.estimateParameters();

        // MAP for decoding
        ArrayList<Integer> res = simpleTrigram.viterbiDecoding(simpleTrigram.valWords.get(0));

        for (Integer tag : res) {
            System.out.print(simpleTrigram.tagdictRev.get(tag) + ", ");
        }

        System.out.println();
    }

    public SimpleTrigramHMM(String trainPath, String testPath) {
        // load and tokenize dataset
        worddict = new HashMap<String, Integer>();
        worddictRev = new HashMap<Integer, String>();
        tagdict = new HashMap<String, Integer>();
        tagdictRev = new HashMap<Integer, String>();

        words = new ArrayList<ArrayList<Integer>>();
        tags = new ArrayList<ArrayList<Integer>>();

        DataUtil util = new DataUtil();
        util.buildDict(trainPath, worddict, worddictRev, tagdict, tagdictRev);
        util.tokenize(trainPath, words, tags, worddict, tagdict, 2);

        // initiate parameters
        qParams = new double[tagdict.size()][tagdict.size()][tagdict.size()]; // qParams[s][u][v] = q(s|u,v)
        eParams = new double[worddict.size()][tagdict.size()]; // eParams[x][s] = e(x|s)

        valWords = new ArrayList<ArrayList<Integer>>();
        valTags = new ArrayList<ArrayList<Integer>>();
        util.tokenize(testPath, valWords, valTags, worddict, tagdict, 2);

        //ArrayList<ArrayList<Integer>> batchPreds = batchViterbiDecoding(valWords);
        //double score = evaluatePrediction(batchPreds);
    }

    public ArrayList<ArrayList<Integer>> batchViterbiDecoding(ArrayList<ArrayList<Integer>> testBatch) {
        ArrayList<ArrayList<Integer>> batchPreds = new ArrayList<ArrayList<Integer>>();

        for (int i = 0; i < testBatch.size(); i++) {
            ArrayList<Integer> preds = viterbiDecoding(testBatch.get(i));
            batchPreds.add(preds);
        }

        return batchPreds;
    }

    public ArrayList<Integer> viterbiDecoding(ArrayList<Integer> testLine) {
        ArrayList<Integer> preds = new ArrayList<Integer>();

        double [][] table = new double[tagdict.size()][testLine.size()];
        int [] maxIds = new int[testLine.size()];

        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[i].length; j++) {
                table[i][j] = -Double.MAX_VALUE;
            }
        }

        maxIds[0] = tagdict.get("*START*");
        maxIds[1] = tagdict.get("*START*");

        // the table is indexed by i & j
        // base case: the first two tokens should be definitely *START*
        // hence prob *START* = 1
        table[tagdict.get("*START*")][0] = Math.log(1.0);
        table[tagdict.get("*START*")][1] = Math.log(1.0);
        // start iterate table from column 2
        for (int i = 2; i < testLine.size(); i++) {
            int curMaxId = -1;
            for (int j = 0; j < tagdict.size(); j++) {
                if (qParams[j][maxIds[i-2]][maxIds[i-1]] == 0) continue;

                // e(x_i|y_i) * q(y_i|y_i-1, y_i-2) * max_prev
                double prevMaxProb = table[maxIds[i-1]][i-1];
                table[j][i] = Math.log(eParams[testLine.get(i)][j]) +
                        Math.log(qParams[j][maxIds[i-2]][maxIds[i-1]]) +
                        prevMaxProb;
            }
            curMaxId = findMaxOfColumn(table, i);
            maxIds[i] = curMaxId;

            if (i >= 2) {
                preds.add(curMaxId);
            }
        }

        return preds;
    }

    public int findMaxOfColumn(double [][] table, int col) {
        double max = -Double.MAX_VALUE;
        int maxIdx = -1;
        for (int i = 0; i < table.length; i++) {
            double cur = table[i][col];
            if (cur > max) {
                max = cur;
                maxIdx = i;
            }
        }
        return maxIdx;
    }

    public double evaluatePrediction(ArrayList<ArrayList<Integer>> batchPredictions) {
        return 0.0;
    }

    public void estimateParameters() {
        int [][][] trigramTagCount = new int[tagdict.size()][tagdict.size()][tagdict.size()];
        int [][] bigramTagCount = new int[tagdict.size()][tagdict.size()];
        int [] unigramTagCount = new int[tagdict.size()];
        int [][] wordtagCount = new int[tagdict.size()][worddict.size()];

        // iterate through each token in every line
        for (int i = 0; i < words.size(); i++) {
            for (int j = 0; j < words.get(i).size()-2; j++) {
                int word = words.get(i).get(j);
                int u = tags.get(i).get(j);
                int v = tags.get(i).get(j+1);
                int s = tags.get(i).get(j+2);
                trigramTagCount[u][v][s] = trigramTagCount[u][v][s] + 1;
                bigramTagCount[u][v] = bigramTagCount[u][v] + 1;
                unigramTagCount[u] = unigramTagCount[u] + 1;
                wordtagCount[u][word] = wordtagCount[u][word] + 1;
            }
        }

        // maximum likelihood of parameters qParams[s][u][v] = q(s|u,v)
        for (int u = 0; u < tagdict.size(); u++) {
            for (int v = 0; v < tagdict.size(); v++) {
                if (bigramTagCount[u][v] != 0) {
                    for (int s = 0; s < tagdict.size(); s++) {
                        qParams[s][u][v] = (double) trigramTagCount[u][v][s] / bigramTagCount[u][v];
                    }
                }
            }
        }

        // maximum likelihood estimation of eParams[x][s] = e(x|s)
        // applies laplace smoothing
        int vocabCount = worddict.size();
        for (int x = 0; x < worddict.size(); x++) {
            for (int s = 0; s < tagdict.size(); s++) {
                eParams[x][s] = ((double) wordtagCount[s][x]+1) / (unigramTagCount[s]+vocabCount);
            }
        }

        System.out.println();
    }

    public boolean isValidProbDistribution() {
        // check the parameters of prob. dist. normalized
        double eps = 0.001;
        for (int u = 0; u < tagdict.size(); u++) {
            for (int v = 0; v < tagdict.size(); v++) {
                if (qParams[0][u][v] != 0) {
                    double p = 0;
                    for (int s = 0; s < tagdict.size(); s++) {
                        p += qParams[s][u][v];
                    }
                    if (Math.abs(p - 1) > eps) {
                        System.out.println("caution! prob. don't sum to 1.0");
                        System.out.printf("u: %d, v: %d -> p: %.3f\n", u, v, p);
                        return false;
                    }
                }
            }
        }

        for (int s = 0; s < tagdict.size(); s++) {
            double p = 0;
            for (int x = 0; x < worddict.size(); x++) {
                p += eParams[x][s];
            }
            if (Math.abs(p - 1) > eps) {
                System.out.println("caution! prob. don't sum to 1.0");
                System.out.printf("s: %d -> p: %.3f\n", s, p);
                return false;
            }
        }

        return true;
    }
}

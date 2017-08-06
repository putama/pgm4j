package putama.util;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by prutama on 8/5/17.
 */
public class EvaluationUtil {
    public static void evaluatePosTags(ArrayList<ArrayList<Integer>> gold, ArrayList<ArrayList<Integer>> pred) {
        int counter = 0;
        int correct = 0;
        for (int i = 0; i < pred.size() & i < gold.size(); i++) {
            for (int j = 0; j < pred.get(i).size() & j < gold.get(i).size(); j++) {
                counter++;
                if (pred.get(i).get(j) == gold.get(i).get(j)) {
                    correct++;
                }
            }
        }
        System.out.printf("Predictions: %d, Correct: %d, Accuracy: %.3f%%",
                counter, correct, ((double) correct/counter) * 100);
    }

    public static void printPosTagsPredictions(
            ArrayList<ArrayList<Integer>> golds,
            ArrayList<ArrayList<Integer>> preds,
            HashMap<Integer, String> tagdictrev) {
        for (int i = 0; i < preds.size(); i++) {
            String gold = "GOLD:\t";
            String pred = "PREDS:\t";
            for (int j = 0; j < preds.get(i).size(); j++) {
                gold += tagdictrev.get(golds.get(i).get(j))+"\t";
                pred += tagdictrev.get(preds.get(i).get(j))+"\t";
            }
            System.out.println(gold);
            System.out.println(pred);
        }
    }
}

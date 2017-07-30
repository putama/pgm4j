package putama.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

/**
 * Created by prutama on 7/29/17.
 */
public class DataUtil {
    public void buildDict(String path, HashMap<String, Integer> dictWord, HashMap<Integer, String> dictWordRev,
                          HashMap<String, Integer> dictTag, HashMap<Integer, String> dictTagRev) {
        try {
            HashMap<String, Integer> wordCount = new HashMap<String, Integer>();
            HashMap<String, Integer> tagCount = new HashMap<String, Integer>();

            File f = new File(path);
            BufferedReader b;

            b = new BufferedReader(new FileReader(f));

            String curLine;
            while ((curLine = b.readLine()) != null) {
                String [] tokens = curLine.split(" ");

                for (int i = 0; i < tokens.length; i++) {
                    if (i % 2 == 0) {
                        if (wordCount.containsKey(tokens[i])) {
                            wordCount.put(tokens[i], wordCount.get(tokens[i])+1);
                        } else {
                            wordCount.put(tokens[i], 1);
                        }
                    } else {
                        if (tagCount.containsKey(tokens[i])) {
                            tagCount.put(tokens[i], tagCount.get(tokens[i])+1);
                        } else {
                            tagCount.put(tokens[i], 1);
                        }
                    }
                }
            }

            // sort both word and tag by their occurrence frequency
            Comparator<String> comparator;
            TreeMap<String, Integer> sorter;

            comparator = new ValueComparator(wordCount);
            sorter = new TreeMap<String, Integer>(comparator);
            sorter.putAll(wordCount);
            int i = 0;
            // add padding token
            dictWord.put("*START*", i++);
            for (Iterator<String> iter = sorter.keySet().iterator(); iter.hasNext(); i++) {
                dictWord.put(iter.next(), i);
            }
            // add *UNK* token
            dictWord.put("*UNK*", i);

            comparator = new ValueComparator(tagCount);
            sorter = new TreeMap<String, Integer>(comparator);
            sorter.putAll(tagCount);
            int j = 0;
            // add padding token
            dictTag.put("*START*", j++);
            for (Iterator<String> iter = sorter.keySet().iterator(); iter.hasNext(); j++) {
                dictTag.put(iter.next(), j);
            }

            // reverse dictionary
            for (Iterator<String> iter = dictWord.keySet().iterator(); iter.hasNext();) {
                String next = iter.next();
                dictWordRev.put(dictWord.get(next), next);
            }
            for (Iterator<String> iter = dictTag.keySet().iterator(); iter.hasNext();) {
                String next = iter.next();
                dictTagRev.put(dictTag.get(next), next);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void tokenize(String path, ArrayList<ArrayList<Integer>> words, ArrayList<ArrayList<Integer>> tags,
                         HashMap<String, Integer> worddict, HashMap<String, Integer> tagdict, int padding) {
        try {
            File f = new File(path);
            BufferedReader b;

            b = new BufferedReader(new FileReader(f));

            String curLine;
            while ((curLine = b.readLine()) != null) {
                ArrayList<Integer> wordline = new ArrayList<Integer>();
                ArrayList<Integer> tagline = new ArrayList<Integer>();

                for (int i = 0; i < padding; i++) {
                    wordline.add(worddict.get("*START*"));
                    tagline.add(worddict.get("*START*"));
                }

                String [] tokens = curLine.split(" ");
                for (int i = 0; i < tokens.length; i++) {
                    if (i % 2 == 0) {
                        wordline.add(worddict.get(tokens[i]));
                    } else {
                        tagline.add(tagdict.get(tokens[i]));
                    }
                }
                words.add(wordline);
                tags.add(tagline);
            }
        } catch (Exception e) {
            System.out.println("Error reading file");
        }
    }
}

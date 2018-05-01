package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class PUtils {
    private static String originalPath = "../";
    private static String resourcesPath = originalPath+"resources/";
    private static String baseRulesPath = resourcesPath+"BaseRules.txt";
    private static String tracesPath = resourcesPath+"Traces.txt";
    private static String configPath = originalPath+"resources/parameters.config";
    
    public static void generateNBaseRules(int n) {
        List<String> lines = new ArrayList<>();
        try {
            File file = new File(baseRulesPath);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            br.close();
            for (int i = 0; i < n; i++) {
                File _file = new File(resourcesPath+i+"_BaseRules.txt");
                PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(_file)));
                for (String _line : lines) {
                    String[] strs = _line.split(",", 0);
                    for (int j = 0; j < 3; j++) {
                        strs[j] = i+"_"+strs[j];
                    }
                    pw.println(String.join(",", strs));
                }
                pw.close();
            }
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }
    
    public static void generateParallelTraces(int n) {
        List<String> lines = new ArrayList<>();
        try {
            File file = new File(tracesPath);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            br.close();
            List<Queue<String>> allTraces = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                allTraces.add(new ArrayDeque<>());
                Queue<String> traces = allTraces.get(i);
                for (String _line : lines) {
                    traces.add(i+"_"+_line);
                }
            }
            List<String> parallelTraces = new ArrayList<>();
            while (allTraces.size() > 0) {
                Random rnd = new Random();
                int rvalue = rnd.nextInt(allTraces.size());
                parallelTraces.add(allTraces.get(rvalue).poll());
                if (allTraces.get(rvalue).size() == 0) {
                    allTraces.remove(rvalue);
                }
            }
            System.out.println(parallelTraces.size());
            File _file = new File(resourcesPath+"ParallelTraces.txt");
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(_file)));
            for (String _line : parallelTraces) {
                pw.println(_line);
            }
            pw.close();
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }
}

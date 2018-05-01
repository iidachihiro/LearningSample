package main;

import util.PUtils;

public class PSampleMain {
    private static final int PNUM = 10;
    
    public static void main(String[] args) {
        PUtils.generateNBaseRules(PNUM);
        PUtils.generateParallelTraces(PNUM);
    }
}

package com.lune.stone.constraints.examples;

import com.lune.stone.constraints.Problem;

import java.util.Map;

//https://en.wikipedia.org/wiki/Rook_polynomial#Rooks_problems
public class EightRooks {

    public static void main(String[] argv) {
        Problem problem = new Problem();

        for (int i = 0; i < 8; i++) //for eight pieces we assume the col == piece number
        {
            problem.addVariable("x" + (i + 1), new int[]{1, 2, 3, 4, 5, 6, 7, 8});
        }

        for (int row1 : new int[]{1, 2, 3, 4, 5, 6, 7, 8})
            for (int row2 : new int[]{1, 2, 3, 4, 5, 6, 7, 8})
                if (row1 < row2)
                    problem.addConstraint(new String[]{"x" + row1, "x" + row2}, x -> x[0] != x[1]); //not the same row as any previous piece

        long start = System.currentTimeMillis();
        Map<String, Integer> solution = problem.findAnySolution().get();
        System.out.println("found " + solution + " in " + (System.currentTimeMillis() - start) + "ms");
        // found {x8=8, x1=1, x2=2, x3=3, x4=4, x5=5, x6=6, x7=7} in 1ms

        start = System.currentTimeMillis();
        int size = problem.findAllSolutions().size();
        System.out.println("found all (" + size + ") solutions in " + (System.currentTimeMillis() - start) + "ms");
        // found all (40320) solutions in 2619ms

    }
}

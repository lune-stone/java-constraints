package com.lune.stone.constraints.examples;

import com.lune.stone.constraints.Problem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

//https://en.wikipedia.org/wiki/Fox,_goose_and_bag_of_beans_puzzle
public class FarmerCrossesRiver {

    static final int NEAR_SHORE = 0;
    static final int FAR_SHORE = 1;

    static final int[] LOCATIONS = new int[]{NEAR_SHORE, FAR_SHORE};

    public static void main(String[] args) {
        for (int time = 1; time < 10; time++) //we want to find the shortest solution, so solving for fixed time is one way to do that
        {
            Problem problem = new Problem();

            List<String> varPrefixes = new ArrayList<>();
            varPrefixes.add("fox_t");
            varPrefixes.add("goose_t");
            varPrefixes.add("beans_t");
            varPrefixes.add("farmer_t");

            for (int t = 0; t < time; t++)
                for (String prefix : varPrefixes)
                    problem.addVariable(prefix + t, LOCATIONS);

            for (String prefix : varPrefixes) {
                problem.addConstraint(new String[]{prefix + "0"}, x -> x[0] == NEAR_SHORE); //all start on near shore
                problem.addConstraint(new String[]{prefix + (time - 1)}, x -> x[0] == FAR_SHORE); //all end on far shore
            }

            for (int t = 0; t < time; t++) {
                problem.addConstraint(new String[]{"fox_t" + t, "goose_t" + t, "farmer_t" + t}, x -> x[0] != x[1] || x[0] == x[2]); //fox can not be alone with goose
                problem.addConstraint(new String[]{"goose_t" + t, "beans_t" + t, "farmer_t" + t}, x -> x[0] != x[1] || x[0] == x[2]); //goose can not be alone with beans
            }

            for (int t = 1; t < time; t++) {
                problem.addConstraint(new String[]{"farmer_t" + t, "farmer_t" + (t - 1)}, x -> x[0] != x[1]); //farmer must move on every turn
                problem.addConstraint(new String[]{"fox_t" + t, "goose_t" + t, "beans_t" + t, "fox_t" + (t - 1), "goose_t" + (t - 1), "beans_t" + (t - 1)}, x -> (x[0] ^ x[3]) + (x[1] ^ x[4]) + (x[2] ^ x[5]) < 2); //at most one purchase may be transported by farmer
            }

            Optional<Map<String, Integer>> solution = problem.findAnySolution();

            if (solution.isPresent()) {
                System.out.println("solution found for time = " + time + " solution = " + solution.get());
                break;
            }

            System.out.println("no solution found for time = " + time);
        }
        
        /*
        no solution found for time = 1
        no solution found for time = 2
        no solution found for time = 3
        no solution found for time = 4
        no solution found for time = 5
        no solution found for time = 6
        no solution found for time = 7
        solution found for time = 8 solution = {
            beans_t0=0, farmer_t0=0, fox_t0=0, goose_t0=0, // start
            beans_t1=0, farmer_t1=1, fox_t1=0, goose_t1=1, // take goose
            beans_t2=0, farmer_t2=0, fox_t2=0, goose_t2=1, // return
            beans_t3=1, farmer_t3=1, fox_t3=0, goose_t3=1, // take beans
            beans_t4=1, farmer_t4=0, fox_t4=0, goose_t4=0, // returns with goose
            beans_t5=1, farmer_t5=1, fox_t5=1, goose_t5=0, // takes fox
            beans_t6=1, farmer_t6=0, fox_t6=1, goose_t6=0, // returns
            beans_t7=1, farmer_t7=1, fox_t7=1, goose_t7=1  // takes goose 
        }
        */
    }

}

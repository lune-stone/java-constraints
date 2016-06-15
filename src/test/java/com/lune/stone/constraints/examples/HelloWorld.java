package com.lune.stone.constraints.examples;


import com.lune.stone.constraints.Problem;

public class HelloWorld {

    public static void main(String[] args) {
        Problem problem = new Problem();
        problem.addVariable("a", new int[]{1, 2, 3});
        problem.addVariable("b", new int[]{4, 5, 6});

        System.out.println(problem.findAllSolutions());
        //[{a=1, b=6}, {a=3, b=6}, {a=1, b=4}, {a=2, b=6}, {a=1, b=5}, {a=3, b=4}, {a=3, b=5}, {a=2, b=4}, {a=2, b=5}]

        problem.addConstraint(new String[]{"b", "a"}, x -> x[0] == 2 * x[1]); //b == 2a
        System.out.println(problem.findAllSolutions());
        //[{a=3, b=6}, {a=2, b=4}]
    }

}

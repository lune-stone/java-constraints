package com.lune.stone.constraints;

import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProblemTest {
    @Test
    public void testNoConstraint() {
        Problem problem = new Problem();

        problem.addVariable("a", new int[]{1, 2, 3});
        problem.addVariable("b", new int[]{1, 2, 3, 4});

        assertEquals(12, problem.findAllSolutions().size());
    }

    @Test
    public void testConstraint() {
        Problem problem = new Problem();

        problem.addVariable("a", new int[]{1, 2, 3});
        problem.addConstraint(new String[]{"a"}, x -> x[0] > 1);
        Set<Integer> solutions = problem.findAllSolutions().stream().map(x -> x.get("a")).collect(Collectors.toSet());
        assertEquals(2, solutions.size());
        assertTrue(solutions.contains(2));
        assertTrue(solutions.contains(3));
    }

    @Test
    public void testNegativeOneSolution() {
        Problem problem = new Problem();

        problem.addVariable("a", new int[]{-1, 0, 1});
        problem.addConstraint(new String[]{"a"}, x -> x[0] < 0);
        Set<Integer> solutions = problem.findAllSolutions().stream().map(x -> x.get("a")).collect(Collectors.toSet());
        assertTrue(solutions.contains(-1));
        assertEquals(1, solutions.size());
    }

}

// Copyright 2016 - 2022, Andrew W.
// SPDX-License-Identifier: GPL-3.0-or-later

package com.lune.stone.constraints;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Represents a constraint satisfaction problem.
 * <p>
 * https://en.wikipedia.org/wiki/Constraint_satisfaction_problem
 */
public class Problem {
    private final List<String> variableNames;
    private final List<int[]> variableValues;
    private final List<Constraint> constraints;

    public Problem() {
        this.variableNames = new ArrayList<>();
        this.constraints = new ArrayList<>();
        this.variableValues = new ArrayList<>();
    }

    /**
     * @param n              the number of solutions to find MAX_INT for all
     * @param variableValues all possible values for variables
     * @param constraints
     * @return
     */
    private static List<int[]> findNSolutions(int n, int[][] variableValues, Constraint[] constraints) {
        List<int[]> ret = new ArrayList<>();

        if (n < 1)
            return ret;

        Stack<int[]> workRemaining = new Stack<>();
        int[] allVarsUnset = new int[variableValues.length];
        Arrays.fill(allVarsUnset, -1);
        workRemaining.push(allVarsUnset);

        while (!workRemaining.isEmpty() && ret.size() < n) {
            int[] varIndexes = workRemaining.pop(); //variableValues indexes

            boolean anyConstraintIsFalse = false;

            for (Constraint c : constraints) {
                Boolean b = eval(c, varIndexes, variableValues);
                if (b != null && !b) {
                    anyConstraintIsFalse = true;
                    break;
                }
            }

            int varToSet = indexOf(varIndexes, -1);
            boolean allVarsSet = varToSet == -1;
            boolean isSolution = allVarsSet && !anyConstraintIsFalse;
            boolean isDeadEnd = anyConstraintIsFalse;

            if (isSolution) {
                ret.add(toValues(varIndexes, variableValues));
            } else if (!allVarsSet && !isDeadEnd) {
                for (int possibleValueIndex = 0; possibleValueIndex < variableValues[varToSet].length; possibleValueIndex++) {
                    int[] newWork = new int[varIndexes.length];
                    System.arraycopy(varIndexes, 0, newWork, 0, varIndexes.length);
                    newWork[varToSet] = possibleValueIndex;
                    workRemaining.push(newWork);
                }
            }
        }

        return ret;
    }

    private static int[] toValues(int[] indexes, int[][] values) {
        int[] ret = new int[indexes.length];
        for (int i = 0; i < ret.length; i++)
            ret[i] = values[i][indexes[i]];

        return ret;
    }

    /**
     * @param constraint
     * @param variableValues
     * @return constraint.apply() if all needed vars given, null otherwise
     */
    private static Boolean eval(Constraint constraint, int[] valueIndexes, int[][] variableValues) {
        int[] v = new int[constraint.variableIndexes.length];

        for (int i = 0; i < v.length; i++) {
            int variableIndex = constraint.variableIndexes[i];
            if (valueIndexes[variableIndex] == -1)
                return null; //needed var is not set

            int value = variableValues[variableIndex][valueIndexes[variableIndex]];
            v[i] = value;
        }

        return constraint.constraint.apply(v);
    }

    private static int indexOf(int[] array, int find) {
        for (int i = 0; i < array.length; i++)
            if (array[i] == find)
                return i;
        return -1;
    }

    private static Set<Integer> set(int[] values) {
        return new HashSet<Integer>() {{
            for (int i : values) add(i);
        }};
    }

    /**
     * @param name   unique label for the variable
     * @param values contains all possible values the variable can take
     * @return this
     */
    public Problem addVariable(String name, int[] values) {
        if (variableNames.contains(name)) {
            throw new IllegalArgumentException("Variable names must be unique.");
        }

        if (set(values).size() != values.length) {
            throw new IllegalArgumentException("Variable values must be unique.");
        }

        variableNames.add(name);
        variableValues.add(values);

        return this;
    }

    /**
     * @param args       list of variables to be used in the constraint function
     * @param constraint function representing the constraint. Input values given in order defined by `args`
     * @return this
     */
    public Problem addConstraint(String[] args, Function<int[], Boolean> constraint) {
        constraints.add(new Constraint(this, args, constraint));
        return this;
    }

    /**
     * @return A solution that satisfies the constraints if one exists
     */
    public Optional<Map<String, Integer>> findAnySolution() {
        return findNSolutions(1, variableValues.toArray(new int[0][]), constraints.toArray(new Constraint[0]))
                .stream()
                .map(this::toSolutionMap)
                .findAny();
    }

    /**
     * @return All solutions that satisfies the constraints
     */
    public Set<Map<String, Integer>> findAllSolutions() {
        return findNSolutions(Integer.MAX_VALUE, variableValues.toArray(new int[0][]), constraints.toArray(new Constraint[0]))
                .stream()
                .map(this::toSolutionMap)
                .collect(Collectors.toSet());
    }

    private Map<String, Integer> toSolutionMap(int[] solution) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < solution.length; i++) {
            map.put(variableNames.get(i), solution[i]);
        }
        return map;
    }

    private static class Constraint {
        private final int[] variableIndexes;
        private final Function<int[], Boolean> constraint;

        public Constraint(Problem problem, String[] args, Function<int[], Boolean> constraint) {
            this.variableIndexes = new int[args.length];
            this.constraint = constraint;

            for (int i = 0; i < args.length; i++) {
                int idx = problem.variableNames.indexOf(args[i]);

                if (idx == -1) {
                    throw new IllegalArgumentException("No variable named: " + args[i]);
                }

                variableIndexes[i] = idx;
            }
        }
    }
}

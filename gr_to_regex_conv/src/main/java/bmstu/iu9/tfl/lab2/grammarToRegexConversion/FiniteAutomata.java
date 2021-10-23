package bmstu.iu9.tfl.lab2.grammarToRegexConversion;

import bmstu.iu9.tfl.lab2.systemOfRegularExpressionEquations.Equation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class FiniteAutomata {
    private static final char FINITE_STATE = '#';

    private final HashMap<Character, Integer> nodeIndices = new HashMap<>();
    private final HashMap<Integer, Character> nodeNames = new HashMap<>();

    private final int verticesAmount;

    private ArrayList<Integer[]>[] edges;

    public FiniteAutomata(Rule[] rules) {
        this.verticesAmount = Rule.getAmountOfUsedTerms() + 1;
        allocateSpaceForEdges();
        int order = 0;

        nodeIndices.put(FINITE_STATE, order++);
        for (Rule rule: rules) {
             char startingVertex = rule.getStartingNTerm();
             if (!nodeIndices.containsKey(startingVertex)) {
                 nodeIndices.put(startingVertex, order);
                 nodeNames.put(order++, startingVertex);
             }
             char endingVertex = rule.getEndingNTerm();
             if (endingVertex != '0') {
                 if (!nodeIndices.containsKey(endingVertex)) {
                     nodeIndices.put(endingVertex, order);
                     nodeNames.put(order++, endingVertex);
                 }
                 addEdge(nodeIndices.get(startingVertex), nodeIndices.get(endingVertex), rule.getLetter());
             } else {
                 addEdge(nodeIndices.get(startingVertex), nodeIndices.get(FINITE_STATE), rule.getLetter());
             }
        }
        if (!nodeIndices.containsKey('S')) {
            throw new Error("No starting rule S");
        }
    }

    private void allocateSpaceForEdges() {
        edges = new ArrayList[verticesAmount];

        for (int i = 0; i < this.edges.length; i++) {
            edges[i] = new ArrayList<>();
        }
    }

    private void addEdge(int u, int v, int mark) {
        int EDGE_CAPACITY = 2;
        Integer[] edge = new Integer[EDGE_CAPACITY];
        edge[0] = v;
        edge[1] = mark;
        edges[u].add(edge);
    }

    protected HashMap<Integer, Boolean> findRegexMembers() {
        HashMap<Integer, Boolean> regexNTerms = new HashMap<>();
        int start = nodeIndices.get('S');
        int finiteState = 0;
        for (int i: findPathMembers(start, finiteState)) {
            regexNTerms.put(i, true);
        }
        if (regexNTerms.size() == 0) {
            throw new Error ("No reachable finite state");
        }
        for (int nTerm: nodeIndices.values()) {
            if (!regexNTerms.containsKey(nTerm)) {
                ArrayList<Integer> members = new ArrayList<>();
                for (int i: regexNTerms.keySet()) {
                    members.addAll(findPathMembers(nTerm, i));
                }
                for (int i: members) {
                    regexNTerms.put(i, true);
                }
            }
        }
        return regexNTerms;
    }

    protected List<Integer> findPathMembers(int src, int finiteState) {
        boolean[] isVisited = new boolean[verticesAmount];
        ArrayList<Integer> pathList = new ArrayList<>();
        ArrayList<Integer> regexNTerms = new ArrayList<>();
        pathList.add(src);

        graphDFS(src, finiteState, isVisited, pathList, regexNTerms);
        return regexNTerms;
    }

    private void graphDFS(Integer amid, Integer dest, boolean[] isVisited,
                          List<Integer> localPathList, List<Integer> regexNTerms) {
        if (amid.equals(dest)) {
            regexNTerms.addAll(localPathList);
            return;
        }

        isVisited[amid] = true;

        for (Integer[] edge : edges[amid]) {
            int i = edge[0];
            if (!isVisited[i]) {
                localPathList.add(i);
                graphDFS(i, dest, isVisited, localPathList, regexNTerms);

                localPathList.remove(edge[0]);
            }
        }

        isVisited[amid] = false;
    }

    protected Equation[] generateRegexEquationsSystem(HashMap<Integer, Boolean> regexNTerms) {
        ArrayList<Equation> system = new ArrayList<>();
        for (int i = 1; i < edges.length; i++) {
            if (regexNTerms.containsKey(i)) {
                ArrayList<String> regex = new ArrayList<>();
                HashMap<Character, ArrayList<String>> variables = new HashMap<>();
                ArrayList<Integer[]> vertexEdges = edges[i];
                for (Integer[] edge: vertexEdges) {
                    if (edge[0] == 0) {
                        regex.add(String.valueOf((char) edge[1].intValue()));
                    } else if (regexNTerms.containsKey(edge[0])){
                        char nTerm = nodeNames.get(edge[0]);
                        if (variables.containsKey(nTerm)) {
                            variables.get(nTerm).add(String.valueOf((char) edge[1].intValue()));
                        } else {
                            ArrayList<String> coefficient = new ArrayList<>();
                            coefficient.add(String.valueOf((char) edge[1].intValue()));
                            variables.put(nTerm, coefficient);
                        }
                    }
                }
                system.add(
                        new Equation(nodeNames.get(i), variables, regex)
                );
            }
        }
        return system.toArray(new Equation[0]);
    }
}

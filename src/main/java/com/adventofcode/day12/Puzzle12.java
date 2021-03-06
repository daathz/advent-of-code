package com.adventofcode.day12;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.adventofcode.helper.InputReaderUtil.createListFromFile;

public class Puzzle12 {
    private static List<Node> nodes = new ArrayList<>();
    private static List<Node> traversedNodes = new ArrayList<>();
    private static List<Node> traverseMap = new ArrayList<>();
    private static Set<String> routes = new HashSet<>();


    public static void main(String[] args) {
        List<String> inputs = createListFromFile("src/main/resources/puzzle_input_12.txt");

        for (String input : inputs) {
            String[] nodePair = input.split("-");
            if (!isNodeExists(nodePair[0])) {
                nodes.add(new Node(nodePair[0]));
            }

            if (!isNodeExists(nodePair[1])) {
                nodes.add(new Node(nodePair[1]));
            }

            getNodeByName(nodePair[0]).getNeighbours().add(getNodeByName(nodePair[1]));
            getNodeByName(nodePair[1]).getNeighbours().add(getNodeByName(nodePair[0]));
        }

        sortNeighbours();
        findRoutes();
        printRoutes();
    }

    private static boolean isNodeExists(String name) {
        return nodes.stream().anyMatch(node -> node.getName().equals(name));
    }

    private static Node getNodeByName(String name) {
        return nodes.stream().filter(node -> node.getName().equals(name)).findFirst().get();
    }

    private static void findRoutes() {
        Node startNode = getNodeByName("start");
        // findRoute(startNode);
        List<String> smallCaves = getSmallCaves();
        for (String smallCave : smallCaves) {
            traversedNodes = new ArrayList<>();
            traverseMap = new ArrayList<>();
            findComplexRoute(startNode, smallCave);
        }
    }

    private static void findRoute(Node node) {
        traversedNodes.add(node);
        traverseMap.add(node);
        for (Node neighbour : node.getNeighbours()) {
            if (neighbour.getName().equals("end")) {
                addRoute();
                return;
            } else if (isUpperCase(neighbour.getName()) || !traversedNodes.contains(neighbour)) {
                findRoute(neighbour);
                traversedNodes.remove(neighbour);
                traverseMap.remove(traverseMap.size() - 1);
            }
        }
    }

    private static void findComplexRoute(Node node, String smallCave) {
        traversedNodes.add(node);
        traverseMap.add(node);
        for (Node neighbour : node.getNeighbours()) {
            if (neighbour.getName().equals("end")) {
                addRoute();
                return;
            } else if ((neighbour.getName().equals(smallCave) && !smallCaveCanBeTraversed(smallCave))
                    || isUpperCase(neighbour.getName())
                    || !traversedNodes.contains(neighbour)) {
                findComplexRoute(neighbour, smallCave);
                traversedNodes.remove(neighbour);
                traverseMap.remove(traverseMap.size() - 1);
            }
        }
    }

    private static boolean smallCaveCanBeTraversed(String name) {
        int count = 0;
        for (Node node : traversedNodes) {
            if (node.getName().equals(name)) count++;
        }

        return count == 2;
    }

    private static boolean isUpperCase(String name) {
        Pattern pattern = Pattern.compile("^[A-Z]+$");
        Matcher matcher = pattern.matcher(name);

        return matcher.matches();
    }

    private static void addRoute() {
        StringBuilder sb = new StringBuilder();
        for (Node node : traverseMap) {
            sb.append(node.getName()).append("-");
        }
        String result = sb.append("end").toString();

        routes.add(result);
    }

    private static void printRoutes() {
        for (String route : routes) {
            System.out.println(route);
        }
        System.out.println(routes.size());
    }

    private static void sortNeighbours() {
        for (Node node : nodes) {
            node.getNeighbours().sort((o1, o2) -> {
                if (o1.getName().equals("end")) {
                    return 1;
                } else if (o2.getName().equals("end")) {
                    return -1;
                } else {
                    return 0;
                }
            });
        }
    }

    private static List<String> getSmallCaves() {
        List<String> result = new ArrayList<>();
        for (Node node : nodes) {
            if (!isUpperCase(node.getName()) && !node.getName().equals("start") && !node.getName().equals("end")) {
                result.add(node.getName());
            }
        }

        return result;
    }
}

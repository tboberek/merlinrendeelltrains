package com.wackycow.cish4210.trains;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


public class McCuskerDispatcher extends SchedulingDispatcher {

    public McCuskerDispatcher() {
    }

    private Map<String,Node> buildDependencyGraph(Collection<Train> trains) {
        // The dependency graph.
        Map<String, Node> nodes = new HashMap<String, Node>();
        
        for (Train t : trains) {
            List<String> route = t.getRoute();
            // For every train but t's route:
            for (Train otherTrain :trains) {
                if (t == otherTrain) continue;
                List<String> otherRoute = otherTrain.getRoute();
                for (int i=1;i < route.size(); ++i) {
                    for (int j=1;j < otherRoute.size()-1; ++j) {
                        // If there is an overlap in the graph, there is a 
                        // possible dependency. Add it to the dependency graph.
                        if (route.get(i).equals(otherRoute.get(j))) {
                            Node selfNode = getNode(nodes,t,i);
                            Node otherNode = getNode(nodes,otherTrain,j+1);
                            selfNode.hasDependency.add(otherNode);
                            otherNode.dependencyOf.add(selfNode);
                        }
                    }
                }
            }
        }
        return nodes;
    }
    
    private Set<Node> extractPreviousCycleChain(Map<String,Node> nodes, Node node) {
        Set<Node> result = new HashSet<Node>();
        for (int position:node.positions){
            Node previousNode = getNode(nodes,node.train,position-1);
            if (previousNode != null && previousNode.inCycle()) {
                result.addAll(previousNode.getUpstream());
                result.addAll(extractPreviousCycleChain(nodes,previousNode));
            }
        }
        return result;
    }
    
    private Set<Node> extractSubsequentCycleChain(Map<String,Node> nodes, Node node) {
        Set<Node> result = new HashSet<Node>();
        for (int position:node.positions){
            Node previousNode = getNode(nodes,node.train,position+1);
            if (previousNode != null && previousNode.inCycle()) {
                result.addAll(previousNode.getUpstream());
                result.addAll(extractPreviousCycleChain(nodes,previousNode));
            }
        }
        return result;
    }
    
    private List<Set<Node>> extractCycles(Map<String,Node> nodes) {
        List<Set<Node>> cycles = new ArrayList<Set<Node>>();
        for (Node node : nodes.values()) {
            if (node.inCycle() && !isListed(cycles,node)) {
                Set<Node> cycle = new HashSet<Node>(node.getUpstream());
                cycle.addAll(extractPreviousCycleChain(nodes,node));
                cycle.addAll(extractSubsequentCycleChain(nodes,node));
                cycles.add(cycle);
            }
        }
        return cycles;
    }
    
    private List<ScheduleItem> createSolution(Set<Node> cycle, 
                                                     Map<String,Node> nodes) {
        List<ScheduleItem> solution = new ArrayList<ScheduleItem>();
        System.out.println("cycle:");
        for (Node node : cycle) {
            System.out.println(node.toString());
        }
        for (Node node : cycle) {
            for (Node depNode : node.hasDependency) {
                for (int position : node.positions) {
                    for (int depPosition : depNode.positions) {
                        Node depSolutionNode = getNode(nodes, node.train,position-1);
                        if (depSolutionNode != null && depSolutionNode.inCycle())
                            continue;
                        Node solutionNode = getNode(nodes, depNode.train,depPosition+1);
                        // The solution for the cycle of solutionNode supplants this one.
                        if (solutionNode.inCycle()) continue;
                        solution.add(new ScheduleItem(solutionNode.train,
                                depNode.train.getRoute().get(depPosition+1),
                                depPosition+1));
                        solution.add(new ScheduleItem(node.train,
                                node.train.getRoute().get(position-1),
                                position-1));
                        return solution;
                    }
                }
            }
        }
        return solution;
    }
        
    @Override
    protected List<ScheduleItem> getSchedule(Collection<Train> trains) {
        List<ScheduleItem> result = new ArrayList<ScheduleItem>();
        
        Map<String,Node> nodes = buildDependencyGraph(trains);
        System.out.println("Generated "+nodes.size()+" dependency nodes.");
        List<Set<Node>> cycles = extractCycles(nodes);
        System.out.println("Extracted "+cycles.size()+" dependency cycles.");
        
        for (Set<Node> cycle : cycles) {
            result.addAll(createSolution(cycle,nodes));
        }
        System.out.println("Created schedule:");
        for (ScheduleItem item : result) {
            System.out.println(item.toString());
        }
        
        return result;
    }

    private boolean isListed(List<Set<Node>> cycles, Node node) {
        boolean listed = false;
        for (Set<Node> cycle : cycles) {
            if (cycle.contains(node)) {
                listed = true;
                break;
            }
        }
        return listed;
    }
    
    private Node getNode(Map<String,Node> nodes, Train t, int position) {
        if (position -1 < 0) return null;
        String nodeKey = t.getId()+":"+t.getRoute().get(position-1)
                        +":"+t.getRoute().get(position);
        if (!nodes.containsKey(nodeKey)) {
            Node n = new Node();
            n.train = t;
            n.fromStation = t.getRoute().get(position-1);
            n.toStation = t.getRoute().get(position);
            nodes.put(n.toString(), n);
        }
        Node result = nodes.get(nodeKey);
        result.positions.add(position);
        return result;
    }
    
    private class Node {
        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof Node))
                return false;
            Node other = (Node)obj;
            return other.train.getId().equals(train.getId()) 
                && other.fromStation.equals(fromStation)
                && other.toStation.equals(toStation);
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }

        @Override
        public String toString() {
            return train.getId()+":"+fromStation+":"+toStation;
        }

        public Train train;
        public String fromStation;
        public String toStation;
        public Set<Integer> positions = new TreeSet<Integer>();
        
        public Set<Node> dependencyOf = new HashSet<Node>();
        public Set<Node> hasDependency = new HashSet<Node>();
        
        private Set<Node> upstream = new HashSet<Node>();
        public boolean inCycle() {
            return getUpstream().contains(this);
        }
        public Set<Node> getUpstream() {
            for (Node dep : dependencyOf) {
                if (!upstream.contains(dep)) {
                    upstream.add(dep);
                    upstream.addAll(dep.getUpstream());
                }
            }
            return upstream;
        }
    }
    
    @Override
    public String getName() {
        return "McCusker Dispatcher";
    }

}

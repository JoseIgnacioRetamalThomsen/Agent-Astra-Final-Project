import astra.core.Module;
import astra.formula.Predicate;
import astra.term.ListTerm;
import astra.term.Primitive;
import astra.term.Term;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class ManagerControl extends Module {
    public Map<String, Point> cleaners = new HashMap<>();

    private boolean registrationDone = false;
    private Predicate registrationBelief;
    private Predicate lazyCleaner1;

    @ACTION
    public boolean registerCleaner(String name, long x, long y) {
        cleaners.put(name, new Point((int) x, (int) y));
        System.out.printf("Cleaner %s registered with x=%d, y=%d, all=%s%n", name, x, y, cleaners.toString());
        return true;
    }

    @SENSOR
    public void managerSensor() {
        //todo implement registration finish after time t
        if (cleaners.size() == 4 & !registrationDone) {
            if (registrationBelief != null) {
                agent.beliefs().dropBelief(registrationBelief);
            }
            registrationBelief = new Predicate("registration", new Term[]{Primitive.newPrimitive("true")});
            agent.beliefs().addBelief(registrationBelief);
            registrationDone = true;
        }
    }

    @ACTION
    public boolean assignRoles() {
        System.out.println("Assign Roles");
        AgentPair lazyCleaners = getLazyCleaners();
        System.out.println("LazyCleaners= " + lazyCleaners);
        Map<String, Point> lazyCleanersMap = new HashMap<String, Point>() {
            {
                put(lazyCleaners.agent1, cleaners.get(lazyCleaners.agent1));
                put(lazyCleaners.agent2, cleaners.get(lazyCleaners.agent2));
            }
        };
        Map<String, Point> helpers = new HashMap<>(cleaners);
        helpers.remove(lazyCleaners.agent1);
        helpers.remove(lazyCleaners.agent2);
        System.out.println("helpers= " + helpers);
        List<AgentPair> nearbyList = getNearbyAgentPairs(lazyCleanersMap, helpers);
        System.out.println("Nearby agents=" + nearbyList);

        Term[] helpers1 = new Term[1];
        helpers1[0] = Primitive.newPrimitive(nearbyList.get(0).agent2);
        Term[] helpers2 = new Term[1];
        helpers2[0] = Primitive.newPrimitive(nearbyList.get(1).agent2);

        int midX = 8;
        String direction1 = "left";
        String direction2 = "right";
        if (cleaners.get(lazyCleaners.agent1).x >= 8) {
            direction1 = "right";
            direction2 = "left";

        }

        Predicate lazyCleaner1 = new Predicate("lazyCleaner", new Term[]{Primitive.newPrimitive(lazyCleaners.agent1)
            , Primitive.newPrimitive(direction1), Primitive.newPrimitive(midX), new ListTerm(helpers1)});
        Predicate lazyCleaner2 = new Predicate("lazyCleaner", new Term[]{Primitive.newPrimitive(lazyCleaners.agent2)
            , Primitive.newPrimitive(direction2), Primitive.newPrimitive(midX), new ListTerm(helpers2)});
        agent.beliefs().addBelief(lazyCleaner1);
        agent.beliefs().addBelief(lazyCleaner2);
        return true;
    }

    private List<AgentPair> getNearbyAgentPairs(Map<String, Point> lazyCleanersMap, Map<String, Point> helpers) {
        TreeMap<Double, AgentPair> data = new TreeMap<>();
        List<String> lazyCleanersList = new ArrayList<>(lazyCleanersMap.keySet());
        List<String> cleanersList = new ArrayList<>(helpers.keySet());

        for (int i = 0; i < lazyCleanersList.size(); i++) {
            for (int j = 0; j < cleanersList.size(); j++) {
                System.out.println(lazyCleanersList.get(i) + " " + cleanersList.get(j));

                data.put(distance(cleaners.get(lazyCleanersList.get(i)), cleaners.get(cleanersList.get(j))),
                    new AgentPair(lazyCleanersList.get(i), cleanersList.get(j)));
            }
        }
        AgentPair nearby1 = data.firstEntry().getValue();
        data.remove(data.firstEntry().getKey());
        List<Double> toRemove = new ArrayList<>();
        for (Double d : data.keySet()) {
            AgentPair a = data.get(d);
            if (a.agent1.equals(nearby1.agent1) || a.agent2.equals(nearby1.agent2)) {
                toRemove.add(d);
            }
        }
        for (Double d : toRemove) {
            data.remove(d);
        }
        AgentPair nearby2 = data.firstEntry().getValue();
        List<AgentPair> nearbyList = new ArrayList<>();
        nearbyList.add(nearby1);
        nearbyList.add(nearby2);
        return nearbyList;
    }

    private AgentPair getLazyCleaners() {
        TreeMap<Double, AgentPair> data = new TreeMap<>();
        List<String> allList = new ArrayList<>(cleaners.keySet());
        for (int i = 0; i < allList.size(); i++) {
            for (int j = i + 1; j < allList.size(); j++) {
                System.out.println(allList.get(i) + " " + allList.get(j));

                data.put(distance(cleaners.get(allList.get(i)), cleaners.get(allList.get(j))), new AgentPair(allList.get(i), allList.get(j)));
            }
        }
        AgentPair lazyCleaners = data.lastEntry().getValue();
        return lazyCleaners;
    }

    public double distance(Point a, Point b) {

        return Point2D.distance(a.x, a.y, b.x, b.y);
    }
}

class AgentPair {
    public String agent1;
    public String agent2;

    public AgentPair(String agent1, String agent2) {
        this.agent1 = agent1;
        this.agent2 = agent2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgentPair agentPair = (AgentPair) o;
        return Objects.equals(agent1, agentPair.agent1) && Objects.equals(agent2, agentPair.agent2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agent1, agent2);
    }

    @Override
    public String toString() {
        return "AgentPair{" +
            "agent1='" + agent1 + '\'' +
            ", agent2='" + agent2 + '\'' +
            '}';
    }
}
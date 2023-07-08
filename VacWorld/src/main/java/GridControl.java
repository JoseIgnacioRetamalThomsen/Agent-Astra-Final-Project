import astra.core.ActionParam;
import astra.core.Module;
import astra.formula.Predicate;
import astra.term.ListTerm;
import astra.term.Primitive;
import astra.term.Term;

import java.awt.Point;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GridControl extends Module {

    public Set<Point> helpersJob = new HashSet<>();
    private Grid grid;

    @ACTION
    public boolean init(int maxX, int maxY) {
        grid = new Grid(maxX, maxY);
        return true;
    }


    @ACTION
    public boolean addLocation(long x, long y, String direction, String left, String forwardLeft,
                               String forward, String forwardRight, String right) {
        grid.addLocation(x, y, direction, left, forwardLeft, forward, forwardRight, right);
        return true;
    }

    @ACTION
    public boolean printGrid() {
        System.out.println(PrettyPrinter.print(grid.toGridInteger(), obj -> obj.toString()));
        return true;
    }

    @ACTION
    public boolean printStepCountGrid() {
        System.out.println(PrettyPrinter.print(grid.stepsToInteger(), obj -> obj.toString()));
        return true;
    }


    @ACTION
    public boolean getNextMoveWhenGoingTo(long target_x, long target_y, long actual_x, long actual_y, String actualDirection,
                                          ActionParam<String> out) {
        String shortestMove = grid.getShortestMove(target_x, target_y, actual_x, actual_y, actualDirection);
        //System.out.println("getNextMoveWhenGoingTo shortestMove=" + shortestMove);
        out.set(shortestMove);
        return true;
    }

    @ACTION
    public boolean addGridMask(String direction, int maxX) {
        //System.out.println("addGridMask direction=" + direction + " ,maxX=" + maxX);
        grid.applyGridMask(direction, maxX);
        return true;
    }

    @ACTION
    public boolean processState(long x, long y, String direction, String left, String forwardLeft,
                                String forward, String forwardRight, String right) {
        //System.out.println("Process state x=" + x + " ,y=" + y);
        List<Point> dustList = grid.getDustList((int) x, (int) y, direction, left, forwardLeft, forward, forwardRight, right);
        //System.out.println("Dust List in + " + agent.name() + " ,list= " + dustList);
        for (Point p : dustList) {
            Predicate spotToClean = new Predicate("spotToClean", new Term[]{Primitive.newPrimitive((long) p.x)
                , Primitive.newPrimitive((long) p.y)});
            agent.beliefs().addBelief(spotToClean);
        }
        return true;
    }

    @ACTION
    public boolean getStepCounts(String direction, long xp, long yp, ListTerm possibleMoves, ActionParam<ListTerm> out) {
        //System.out.println("Step Count");
        List<Integer> results = grid.getStepCounts(direction, xp, yp);
        //System.out.println(results);
        int forwardStepsCount = results.get(0);
        int forwardWeight = (Integer) ((Primitive<Integer>) possibleMoves.get(0)).value();
        int leftStepsCount = results.get(1);
        int leftWeight = (Integer) ((Primitive<Integer>) possibleMoves.get(1)).value();
        int rightStepsCount = results.get(2);
        int rightWeight = ((Primitive<Integer>) possibleMoves.get(2)).value();
        if (leftStepsCount == 0 & leftWeight == 1) {
            leftWeight = 1000000;
        } else {
            if (leftStepsCount != 0) {
                leftWeight = 10000 / (leftStepsCount * leftStepsCount);
            } else {
                leftWeight = 0;
            }
        }
        if (forwardStepsCount == 0 & forwardWeight == 1) {
            forwardWeight = 1000000;
        } else {
            if (forwardWeight != 0) {
                forwardWeight = 10000 / (forwardStepsCount * forwardStepsCount);
            } else {
                forwardWeight = 0;
            }
        }
        if (rightStepsCount == 0 & rightWeight == 1) {
            rightWeight = 1000000;
        } else {
            if (rightStepsCount != 0) {
                rightWeight = 10000 / (rightStepsCount * rightStepsCount);
            } else {
                rightWeight = 0;
            }
        }
        Term[] terms = new Term[3];
        forwardWeight *= 2;
        terms[0] = Primitive.newPrimitive(forwardWeight);
        terms[1] = Primitive.newPrimitive(leftWeight);
        terms[2] = Primitive.newPrimitive(rightWeight);
        out.set(new ListTerm(terms));
        return true;
    }

}


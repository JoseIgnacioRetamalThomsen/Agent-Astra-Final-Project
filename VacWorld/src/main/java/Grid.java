import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Grid {

    public static String directions[] = {"forward", "right", "back", "left"};
    static Map<String, Integer> relativedirectionToPosition = new HashMap<String, Integer>() {{
        put("north", 0);
        put("right", 1);
        put("south", 2);
        put("left", 3);
    }};
    static Map<String, Integer> directionToPosition = new HashMap<String, Integer>() {{
        put("north", 0);
        put("west", 1);
        put("south", 2);
        put("east", 3);
    }};
    public int grid[][];
    public int[][] stepCount;
    public int[][] gridMask;
    public int maxY;
    public int maxX;
    public int lastX;
    public int lastY;
    public Point actualPosition = new Point(0, 0);
    public Set<Point> knowDust = new HashSet<>();
    public Map<String, Integer> maps = new HashMap<String, Integer>() {
        {
            put("north", 8888);
            put("south", 2222);
            put("east", 6666);
            put("west", 4444);
            put("obstacle", -1);
            put("vac", 1111);
            put("dust", 7777);
            put("empty", 0);
        }
    };


    public Grid(int maxX, int maxY) {
        this.maxX = maxX;
        this.maxY = maxY;
        gridMask = new int[maxY][maxX];
        stepCount = new int[maxY][maxX];
        grid = new int[maxY][maxX];
        for (int i = 0; i < maxY; i++) {
            grid[i][0] = -1;
        }
        for (int i = 0; i < maxX; i++) {
            grid[0][i] = -1;
        }
    }

    public static String getTranslationfromNorth(int direction, int value) {
        return directions[(direction + value) % 4];
    }

    public static Integer[][] toInteger(int grid[][]) {
        Integer result[][] = new Integer[grid.length + 1][];
        result[0] = new Integer[grid[0].length + 1];
        for (int i = 0; i < grid[0].length; i++) {
            result[0][i + 1] = 999 + i;
        }
        for (int i = 1; i <= grid.length; i++) {
            result[i] = new Integer[grid[0].length + 1];
            result[i][0] = 998 + i;
            for (int j = 0; j < grid[0].length; j++) {
                result[i][j + 1] = grid[i - 1][j];
            }
        }
        return result;
    }

    public List<Point> getDustList(int x, int y,String direction, String left, String forwardLeft, String forward, String forwardRight, String right) {
        List<Point> dustList = new ArrayList<>();
        if(left == "dust"){
            Point leftMove = getLeft(direction);
            dustList.add(new Point(x + leftMove.x,y+leftMove.y));
        }
        if(forwardLeft == "dust"){
            Point move = getforwardLeft(direction);
            dustList.add(new Point(x + move.x,y+move.y));
        }
        if(forward == "dust"){
            Point move = getForward(direction);
            dustList.add(new Point(x + move.x,y+move.y));
        }
        if(forwardRight == "dust"){
            Point move = getForwardRight(direction);
            dustList.add(new Point(x + move.x,y+move.y));
        }
        if(right == "dust"){
            Point move = getRight(direction);
            dustList.add(new Point(x + move.x,y+move.y));
        }
        return dustList;
    }

    public void addLocation(long x, long y, String direction, String left, String forwardLeft, String forward,
                            String forwardRight, String right) {
        if (this.lastX != -1) {
            grid[this.lastY][this.lastX] = 0;
        }

        Point leftMove = getLeft(direction);
        Point forwardLeftMove = getforwardLeft(direction);
        Point forwardMove = getForward(direction);
        Point forwardRightMove = getForwardRight(direction);
        Point rightMove = getRight(direction);

        stepCount[(int) y][(int) x]++;

        int xT = (int) x + 1;
        int yT = (int) y + 1;
        actualPosition.setLocation(xT, yT);


        grid[yT][xT] = maps.get(direction);
        grid[yT + leftMove.y][xT + leftMove.x] = maps.get(left);
        grid[yT + forwardLeftMove.y][xT + forwardLeftMove.x] = maps.get(forwardLeft);
        grid[yT + forwardMove.y][xT + forwardMove.x] = maps.get(forward);
        grid[yT + forwardRightMove.y][xT + forwardRightMove.x] = maps.get(forwardRight);
        grid[yT + rightMove.y][xT + rightMove.x] = maps.get(right);

        if (left.equals("dust")) {
            knowDust.add(new Point(xT + leftMove.x, yT + leftMove.y));
        }
        if (forwardLeft.equals("dust")) {
            knowDust.add(new Point(xT + forwardLeftMove.x, yT + forwardLeftMove.y));
        }
        if (forward.equals("dust")) {
            knowDust.add(new Point(xT + forwardMove.x, yT + forwardMove.y));
        }
        if (forwardRight.equals("dust")) {
            knowDust.add(new Point(xT + forwardRightMove.x, yT + forwardRightMove.y));
        }
        if (right.equals("dust")) {
            knowDust.add(new Point(xT + rightMove.x, yT + rightMove.y));
        }

        Point actualPoint = new Point(xT, yT);
        if (knowDust.contains(actualPoint)) {
            knowDust.remove(actualPoint);
        }

        //todo
       // System.out.println("DIRTY SET= " + knowDust);
        lastX = xT;
        lastY = yT;

        // System.out.println(PrettyPrinter.print(toGridInteger(), obj -> obj.toString()));
        //  System.out.println(PrettyPrinter.print(toGridInteger(), obj -> obj.toString()));
    }

    //result[,forwardtCount,leftCount,forwardtCount,rightCount]
    public List<Integer> getStepCounts(String direction, long xp, long yp) {
        List<Integer> result = new ArrayList<>();
        int x = (int) xp;
        int y = (int) yp;
        Point left = getLeft(direction);
        Point forward = getForward(direction);
        Point right = getRight(direction);
        int leftCount = 0;
        try {
            leftCount = stepCount[y + left.y][x + left.x];
//            System.out.println("left=" + leftCount);
        } catch (Exception e) {
        }
        int forwardtCount = 0;
        try {
            forwardtCount = stepCount[y + forward.y][x + forward.x];
//            System.out.println("forwardtCount=" + forwardtCount);
        } catch (Exception e) {
        }
        int rightCount = 0;
        try {
            rightCount = stepCount[y + right.y][x + right.x];
//            System.out.println("rightCount=" + rightCount);
        } catch (Exception e) {
        }

        result.add(forwardtCount);
        result.add(leftCount);
        result.add(rightCount);
        return result;
    }

    private Point getforwardLeft(String direction) {
        if (maps.get(direction) == 8888) {
            return (new Point(-1, -1));
        } else if (maps.get(direction) == 2222) {
            return (new Point(1, 1));
        } else if (maps.get(direction) == 4444) {
            return (new Point(-1, 1));
        } else if (maps.get(direction) == 6666) {
            return (new Point(+1, -1));
        }
        return null;
    }

    private Point getLeft(String direction) {
        if (maps.get(direction) == 8888) {
            return (new Point(-1, 0));
        } else if (maps.get(direction) == 2222) {
            return (new Point(1, 0));
        } else if (maps.get(direction) == 4444) {
            return (new Point(0, 1));
        } else if (maps.get(direction) == 6666) {
            return (new Point(0, -1));
        }
        return null;
    }

    private Point getForwardRight(String direction) {
        if (maps.get(direction) == 8888) {
            return (new Point(1, -1));
        } else if (maps.get(direction) == 2222) {
            return (new Point(-1, 1));
        } else if (maps.get(direction) == 4444) {
            return (new Point(-1, -1));
        } else if (maps.get(direction) == 6666) {
            return (new Point(1, 1));
        }
        return null;
    }

    private Point getForward(String direction) {
        if (maps.get(direction) == 8888) {
            return (new Point(0, -1));
        } else if (maps.get(direction) == 2222) {
            return (new Point(0, 1));
        } else if (maps.get(direction) == 4444) {
            return (new Point(-1, 0));
        } else if (maps.get(direction) == 6666) {
            return (new Point(1, 0));
        }
        return null;
    }

    private Point getRight(String direction) {
        if (maps.get(direction) == 8888) {
            return (new Point(1, 0));
        } else if (maps.get(direction) == 2222) {
            return (new Point(-1, 0));
        } else if (maps.get(direction) == 4444) {
            return (new Point(0, -1));
        } else if (maps.get(direction) == 6666) {
            return (new Point(0, 1));
        }
        return null;
    }

    public Integer[][] toGridInteger() {
        Integer result[][] = new Integer[grid.length + 1][];
        result[0] = new Integer[grid[0].length + 1];
        for (int i = 0; i < grid[0].length; i++) {
            result[0][i + 1] = 999 + i;
        }
        for (int i = 1; i <= grid.length; i++) {
            result[i] = new Integer[grid[0].length + 1];
            result[i][0] = 998 + i;
            for (int j = 0; j < grid[0].length; j++) {
                result[i][j + 1] = grid[i - 1][j];
            }
        }
        return result;
    }

    public Integer[][] stepsToInteger() {
        Integer result[][] = new Integer[stepCount.length + 1][];
        result[0] = new Integer[stepCount[0].length + 1];
        for (int i = 0; i < stepCount[0].length; i++) {
            result[0][i + 1] = 999 + i;
        }
        for (int i = 1; i <= stepCount.length; i++) {
            result[i] = new Integer[stepCount[0].length + 1];
            result[i][0] = 998 + i;
            for (int j = 0; j < stepCount[0].length; j++) {
                result[i][j + 1] = stepCount[i - 1][j];
            }
        }
        return result;
    }

    public String getShortestMove(long target_x, long target_y, long actual_x, long actual_y, String actualDirection) {
        int[][] clonedGrid = cloneGrid();
        // System.out.println(PrettyPrinter.print(toGridInteger(), obj -> obj.toString()));
        // System.out.println(PrettyPrinter.print(toInteger(clonedGrid), obj -> obj.toString()));
        int target_yT = (int) target_x + 1;
        int target_xT = (int) target_y + 1;
        FloodFill.floodFill(clonedGrid, clonedGrid.length, clonedGrid[0].length, (int) target_y + 1, (int) target_x + 1, 0, 1);
        //System.out.println(PrettyPrinter.print(toInteger(clonedGrid), obj -> obj.toString()));
        int xT = (int) actual_x + 1;
        int yT = (int) actual_y + 1;
        int left = clonedGrid[yT][xT - 1];
        int forward = clonedGrid[yT - 1][xT];
        int back = clonedGrid[yT + 1][xT];
        int right = clonedGrid[yT][xT + 1];

        TreeMap<Integer, String> data = new TreeMap<>();
        if (left > 0) {
            data.put(left, "left");
        }
        if (forward > 0) {
            data.put(forward, "north");
        }
        if (back > 0) {
            data.put(back, "south");
        }
        if (right > 0) {
            data.put(right, "right");
        }

        if (!data.isEmpty()) {
            String result = data.firstEntry().getValue();
            String directionResult = getTranslationfromNorth(relativedirectionToPosition.get(result),
                directionToPosition.get(actualDirection));
            return directionResult;
        } else {
            return "done";
        }
    }

    private int[][] cloneGrid() {
        int clonedGrid[][] = new int[maxY][];
        for (int i = 0; i < maxY; i++) {
            clonedGrid[i] = grid[i].clone();
        }
        clonedGrid[actualPosition.y][actualPosition.x] = 0;
        for (Point p : knowDust) {
            clonedGrid[p.y][p.x] = 0;
        }
        return clonedGrid;
    }

    public void applyGridMask(String direction, int maxX) {
        System.out.println("Applied mask");
        if (direction.equals("right")) {
            int amount = 10;
            for (int j = maxX; j >= 0; j--) {
                for (int i = 0; i < gridMask.length; i++) {

                    gridMask[i][j] = amount;
                }
                amount *= 2;
            }
        } else if (direction.equals("left")) {
            int amount = 10;
            for (int j = maxX; j < gridMask[0].length; j++) {
                for (int i = 0; i < gridMask.length; i++) {

                    gridMask[i][j] = amount;
                }
                amount *= 2;
            }
        }
//        int amount = 2;
//        for (int j = minX; j < maxX + 2; j++) {
//            for (int i = 0; i < gridMask.length; i++) {
//
//                gridMask[i][j] = amount;
//            }
//            amount *= 2;
//        }
        //  System.out.println(PrettyPrinter.print(toInteger(gridMask), obj -> obj.toString()));
        for (int j = 0; j < stepCount[0].length; j++) {
            for (int i = 0; i < stepCount.length; i++) {
                stepCount[i][j] += gridMask[i][j];
            }
        }
    }



}

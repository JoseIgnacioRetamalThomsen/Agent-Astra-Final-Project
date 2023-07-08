import java.awt.*;
import java.util.Vector;

public class FloodFill {

  static boolean isValid(int[][] screen, int m, int n, int x, int y, int prevC, int newC) {
    if (x < 0 || x >= m || y < 0 || y >= n || screen[x][y] != prevC
        || screen[x][y] == newC)
      return false;
    return true;
  }

  // FloodFill function
  public static void floodFill(int[][] screen, int maxX, int maxY, int x, int y, int prevC, int newC) {
    Vector<Point> queue = new Vector<Point>();

    // Append the position of starting
    // pixel of the component
    queue.add(new Point(x, y));

    // Color the pixel with the new color
    screen[x][y] = newC;

    // While the queue is not empty i.e. the
    // whole component having prevC color
    // is not colored with newC color
    while (queue.size() > 0) {
      // Dequeue the front node
      Point currPixel = queue.get(queue.size() - 1);
      queue.remove(queue.size() - 1);

      int posX = currPixel.x;
      int posY = currPixel.y;
      newC++;
      if (isValid(screen, maxX, maxY, posX, posY + 1, prevC, newC)) {
        screen[posX][posY + 1] = newC;
        queue.add(new Point(posX, posY + 1));
      }

      if (isValid(screen, maxX, maxY, posX, posY - 1, prevC, newC)) {
        screen[posX][posY - 1] = newC;
        queue.add(new Point(posX, posY - 1));
      }

      // Check if the adjacent
      // pixels are valid
      if (isValid(screen, maxX, maxY, posX + 1, posY, prevC, newC)) {
        // Color with newC
        // if valid and enqueue
        screen[posX + 1][posY] = newC;
        queue.add(new Point(posX + 1, posY));
      }

      if (isValid(screen, maxX, maxY, posX - 1, posY, prevC, newC)) {
        screen[posX - 1][posY] = newC;
        queue.add(new Point(posX - 1, posY));
      }

    }
  }
}

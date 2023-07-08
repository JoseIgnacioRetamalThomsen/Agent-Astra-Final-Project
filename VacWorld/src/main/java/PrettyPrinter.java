import static java.lang.String.format;

//from https://stackoverflow.com/questions/11383070/pretty-print-2d-array-in-java
public final class PrettyPrinter {

  private static final char BORDER_KNOT = '+';
  private static final char HORIZONTAL_BORDER = '-';
  private static final char VERTICAL_BORDER = '|';

  private static final Printer<Object> DEFAULT = new Printer<Object>() {
    @Override
    public String print(Object obj) {
      return obj.toString();
    }
  };

  private static final String DEFAULT_AS_NULL = "(NULL)";

  public static String print(Object[][] table) {
    return print(table, DEFAULT);
  }

  public static <T> String print(T[][] table, Printer<T> printer) {
    if ( table == null ) {
      throw new IllegalArgumentException("No tabular data provided");
    }
    if ( table.length == 0 ) {
      return "";
    }
    if( printer == null ) {
      throw new IllegalArgumentException("No instance of Printer provided");
    }
    final int[] widths = new int[getMaxColumns(table)];
    adjustColumnWidths(table, widths, printer);
    return printPreparedTable(table, widths, getHorizontalBorder(widths), printer);
  }

  private static <T> String printPreparedTable(T[][] table, int widths[], String horizontalBorder, Printer<T> printer) {
    final int lineLength = horizontalBorder.length();
    StringBuilder sb = new StringBuilder();
    sb.append(horizontalBorder);
    sb.append('\n');
    for ( final T[] row : table ) {
      if ( row != null ) {
        sb.append(getRow(row, widths, lineLength, printer));
        sb.append('\n');
        sb.append(horizontalBorder);
        sb.append('\n');
      }
    }
    return sb.toString();
  }

  private static <T> String getRow(T[] row, int[] widths, int lineLength, Printer<T> printer) {
    final StringBuilder builder = new StringBuilder(lineLength).append(VERTICAL_BORDER);
    final int maxWidths = widths.length;
    for ( int i = 0; i < maxWidths; i++ ) {
      builder.append(padRight(getCellValue(safeGet(row, i, printer), printer), widths[i])).append(VERTICAL_BORDER);
    }
    return builder.toString();
  }

  private static String getHorizontalBorder(int[] widths) {
    final StringBuilder builder = new StringBuilder(256);
    builder.append(BORDER_KNOT);
    for ( final int w : widths ) {
      for ( int i = 0; i < w; i++ ) {
        builder.append(HORIZONTAL_BORDER);
      }
      builder.append(BORDER_KNOT);
    }
    return builder.toString();
  }

  private static int getMaxColumns(Object[][] rows) {
    int max = 0;
    for ( final Object[] row : rows ) {
      if ( row != null && row.length > max ) {
        max = row.length;
      }
    }
    return max;
  }

  private static <T> void adjustColumnWidths(T[][] rows, int[] widths, Printer<T> printer) {
    for ( final T[] row : rows ) {
      if ( row != null ) {
        for ( int c = 0; c < widths.length; c++ ) {
          final String cv = getCellValue(safeGet(row, c, printer), printer);
          final int l = cv.length();
          if ( widths[c] < l ) {
            widths[c] = l;
          }
        }
      }
    }
  }

  private static <T> String padRight(String s, int n) {
    return format("%1$-" + n + "s", s);
  }

  private static <T> T safeGet(T[] array, int index, Printer<T> printer) {
    return index < array.length ? array[index] : null;
  }

  private static <T> String getCellValue(T value, Printer<T> printer) {
    return value == null ? DEFAULT_AS_NULL : printer.print(value);
  }

  public static Integer[][] toInteger(int[][] array) {
    Integer result[][] = new Integer[array.length + 1][];
    result[0] = new Integer[array[0].length + 1];
    for (int i = 0; i < array[0].length; i++) {
      result[0][i + 1] = 999 + i;
    }
//    for(int i =0;i<grid[0].length;i++){
//    }
    for (int i = 1; i <= array.length; i++) {
      // result[i][0] =10000+ i;
      //result[i] = IntStream.of(grid[i]).boxed().toArray(Integer[]::new);
      result[i] = new Integer[array[0].length + 1];
      result[i][0] = 998 + i;
      for (int j = 0; j < array[0].length; j++) {
        result[i][j + 1] = array[i - 1][j];
      }
    }
    return result;
  }

}

interface Printer<T> {
  String print(T obj);
}
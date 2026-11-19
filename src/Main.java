import Sort.DataGenerator;
import Sort.Merge;

import java.util.Arrays;

public class Main {
  public static void main(String[] args) {
    Integer[] testArray = {10, 2, 8, 4, 7, 1, 9, 3, 5, 6};
    Merge.CUTOFF = 3;
    Merge.sort(testArray);
    System.out.println("Mảng sau khi sắp xếp (1 2 3 4 5 6 7 8 9 10): " + Arrays.toString(testArray));

    int[] sizes = {0, 1, 2, 3, 7, 10, 11, 100, 1000, 10007};
    int failures = 0, total = 0;
    for (String type : DataGenerator.TYPES) {
      for (int n : sizes) {
        Integer[] original = DataGenerator.generate(n, type);
        Integer[] expected = original.clone();
        Arrays.sort(expected);
        for (int c = 0; c <= 101; c++) {
          Merge.CUTOFF = c;
          Integer[] actual = original.clone();
          Merge.sort(actual);
          total++;
          if (!Arrays.equals(expected, actual)) {
            failures++;
            System.out.println("SAI: type=" + type + " n=" + n + " cutoff=" + c);
          }
        }
      }
    }
    System.out.println(failures == 0
      ? "TAT CA " + total + " TEST DEU DUNG."
      : failures + "/" + total + " TEST SAI!");
  }
}
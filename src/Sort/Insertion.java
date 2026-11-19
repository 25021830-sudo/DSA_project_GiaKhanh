package Sort;

public class Insertion {

  //mang sx tu low denm high bang insertion sort
  public static void sort(Comparable[] a, int low, int high) {
    for (int i = low + 1; i <= high; i++) {
      for (int j = i; j > low && less(a[j], a[j - 1]); j--) {
        exch(a, j, j - 1);
      }
    }
  }
  // so sanh
  private static boolean less(Comparable u, Comparable v) {
    return u.compareTo(v) < 0;
  }
  // doi cho
  private static void exch(Comparable[] a, int i, int j) {
    Comparable swap = a[i];
    a[i] = a[j];
    a[j] = swap;
  }
}
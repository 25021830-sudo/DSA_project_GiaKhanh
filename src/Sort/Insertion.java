package Sort;

public class Insertion {

  //mang sx tu low denm high bang insertion sort
  public static void sort(Comparable[] a, int low, int high) {
    int N = a.length;
    for (int i = low; i <= high; i++) {
      for (int j = i; j > low && less(a[j], a[j-1]) ; j--) {
        if (less(a[j], a[j - 1])) {
          exch(a, j, j - 1);
        } else {
          break;
        }
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
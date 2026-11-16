import Sort.Merge;

public static void main(String[] args) {
  Integer[] testArray = {10, 2, 8, 4, 7, 1, 9, 3, 5, 6};

  // Thử chạy với CUTOFF = 3
  Merge.CUTOFF = 3;
  Merge.sort(testArray);

  System.out.println("Mảng sau khi sắp xếp(1 2 3 4 5 6 7 8 9 10):");
  for (int i : testArray) {
    System.out.print(i + " ");
  }
}
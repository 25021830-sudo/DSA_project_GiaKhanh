package Sort;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class DataGenerator {
  public static final long SEED = 12345L;
  public static final String DATA_DIR = "data/";
  public static final String[] TYPES = {"random", "sorted", "reverse", "few_unique"};

  // Hàm sinh mảng trong RAM để lát nữa Benchmark chạy trực tiếp
  public static Integer[] generate(int n, String type) {
    Integer[] arr = new Integer[n];
    Random rand = new Random(SEED);

    for (int i = 0; i < n; i++) {
      switch (type.toLowerCase()) {
        case "random":
          arr[i] = rand.nextInt(n * 10); // Ngẫu nhiên
          break;
        case "sorted":
          arr[i] = i; // Đã sắp xếp tăng dần
          break;
        case "reverse":
          arr[i] = n - i; // Sắp xếp giảm dần (Trường hợp xấu nhất của Insertion)
          break;
        case "few_unique":
          arr[i] = rand.nextInt(5); // Chỉ quẩn quanh 5 giá trị (Nhiều phần tử trùng lặp)
          break;
        default:
          throw new IllegalArgumentException("Loai du lieu khong hop le: " + type);
      }
    }
    return arr;
  }

  // Hàm xuất mảng ra file CSV để nộp bài
  public static void exportToCSV(Integer[] arr, String filename) {
    try (FileWriter writer = new FileWriter(filename)) {
      for (int i = 0; i < arr.length; i++) {
        writer.write(arr[i].toString());
        if (i < arr.length - 1) {
          writer.write("\n");
        }
      }
      System.out.println("Đã xuất file: " + filename);
    } catch (IOException e) {
      System.out.println("Lỗi ghi file: " + e.getMessage());
    }
  }

  public static void main(String[] args) {
    new java.io.File(DATA_DIR).mkdirs();

    int[] sizes = {10000};
    if (args.length > 0 && args[0].equals("full")) {
      sizes = new int[]{10000, 100000, 500000, 1000000};
    }
    for (int size : sizes) {
      String label = (size % 1000 == 0 && size < 1000000) ? (size / 1000) + "k" : "1M";
      for (String type : TYPES) {
        exportToCSV(generate(size, type), DATA_DIR + "data_" + label + "_" + type + ".csv");
      }
    }
  }
}
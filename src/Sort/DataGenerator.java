package Sort;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class DataGenerator {

  // Hàm sinh mảng trong RAM để lát nữa Benchmark chạy trực tiếp
  public static Integer[] generate(int n, String type) {
    Integer[] arr = new Integer[n];
    Random rand = new Random();

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
          arr[i] = 0;
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
    int size = 10000;

    // 1. Khai báo đường dẫn thư mục
    String folderPath = "src/data/";

    // 2. Lệnh tự động tạo thư mục "data" nếu nó chưa tồn tại
    java.io.File directory = new java.io.File("data");
    if (!directory.exists()) {
      directory.mkdir();
      System.out.println("Đã tạo thư mục: data/");
    }

    // 3. Nối folderPath vào trước tên file để đẩy file vào đúng thư mục
    exportToCSV(generate(size, "random"), folderPath + "data_10k_random.csv");
    exportToCSV(generate(size, "sorted"), folderPath + "data_10k_sorted.csv");
    exportToCSV(generate(size, "reverse"), folderPath + "data_10k_reverse.csv");
    exportToCSV(generate(size, "few_unique"), folderPath + "data_10k_few_unique.csv");
  }
}
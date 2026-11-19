package Sort;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Benchmark {
  private static int[] SIZES = {100000, 500000, 1000000};
  private static int[] CUTOFFS = buildCutoffs();
  private static int MEASURE_RUNS = 15;
  private static int WARMUP_ROUNDS = 20;
  private static final long SHUFFLE_SEED = 42L;
  private static final String OUTPUT = "data/experiment_results.csv";

  private static int[] buildCutoffs() {
    int[] extra = {35, 40, 50, 60, 80, 100};
    int[] c = new int[30 + extra.length];
    for (int i = 1; i <= 30; i++) c[i - 1] = i;
    System.arraycopy(extra, 0, c, 30, extra.length);
    return c;
  }

  public static void main(String[] args) throws IOException {
    if (args.length > 0 && args[0].equals("quick")) {   // chạy thử nhanh
      SIZES = new int[]{20000, 50000};
      MEASURE_RUNS = 3;
      WARMUP_ROUNDS = 3;
    }
    new File("data").mkdirs();
    warmUp();

    try (FileWriter writer = new FileWriter(OUTPUT)) {
      writer.write("Dataset_Type,Array_Size,Cutoff_Value,Time_ms,Median_ms,Std_ms,Min_ms\n");

      for (String type : DataGenerator.TYPES) {
        for (int size : SIZES) {
          System.out.println("Đang đo: " + type + " | N = " + size);
          Integer[] original = DataGenerator.generate(size, type);
          System.gc();

          double[][] times = new double[CUTOFFS.length][MEASURE_RUNS];
          List<Integer> order = new ArrayList<>();
          for (int i = 0; i < CUTOFFS.length; i++) order.add(i);
          Random shuffler = new Random(SHUFFLE_SEED);

          for (int run = 0; run < MEASURE_RUNS; run++) {
            Collections.shuffle(order, shuffler);
            for (int idx : order) {
              Merge.CUTOFF = CUTOFFS[idx];
              Integer[] data = original.clone();

              long start = System.nanoTime();
              Merge.sort(data);
              long end = System.nanoTime();

              times[idx][run] = (end - start) / 1_000_000.0;

              if (run == 0 && !isSorted(data)) {
                throw new IllegalStateException("Sai ket qua: " + type + " N=" + size + " CUTOFF=" + CUTOFFS[idx]);
              }
            }
          }

          for (int idx = 0; idx < CUTOFFS.length; idx++) {
            double[] t = times[idx];
            writer.write(String.format(Locale.US, "%s,%d,%d,%.4f,%.4f,%.4f,%.4f\n",
              type, size, CUTOFFS[idx], mean(t), median(t), std(t), min(t)));
          }
          writer.flush();
        }
      }
    }
    System.out.println("Hoàn tất! Kết quả: " + OUTPUT);
  }
  private static void warmUp() {
    System.out.println("Đang warm-up JVM...");
    int[] warmCutoffs = {1, 10, 30};
    for (String type : DataGenerator.TYPES) {
      Integer[] data = DataGenerator.generate(100000, type);
      for (int round = 0; round < WARMUP_ROUNDS; round++) {
        for (int c : warmCutoffs) {
          Merge.CUTOFF = c;
          Merge.sort(data.clone());
        }
      }
    }
  }

  private static boolean isSorted(Integer[] a) {
    for (int i = 1; i < a.length; i++) if (a[i] < a[i - 1]) return false;
    return true;
  }

  private static double mean(double[] t) {
    double s = 0;
    for (double v : t) s += v;
    return s / t.length;
  }

  private static double median(double[] t) {
    double[] c = t.clone();
    Arrays.sort(c);
    int n = c.length;
    return n % 2 == 1 ? c[n / 2] : (c[n / 2 - 1] + c[n / 2]) / 2.0;
  }

  private static double std(double[] t) {
    if (t.length < 2) return 0;
    double m = mean(t), s = 0;
    for (double v : t) s += (v - m) * (v - m);
    return Math.sqrt(s / (t.length - 1));
  }

  private static double min(double[] t) {
    double m = t[0];
    for (double v : t) if (v < m) m = v;
    return m;
  }
}
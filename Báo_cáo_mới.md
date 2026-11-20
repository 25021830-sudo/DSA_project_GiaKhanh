# Báo cáo thí nghiệm: Tìm giá trị CUTOFF tốt nhất cho Mergesort

Môn VNU1001 – Lớp K70-I-CS4 \| Họ tên: Trần Mậu Gia Khánh \| MSSV: 25021830

## 1. Mục tiêu

Mergesort có thể chạy nhanh hơn nếu với các mảng con nhỏ ta chuyển sang Insertion sort. Độ dài lớn nhất của mảng con còn dùng Insertion sort gọi là CUTOFF. Giáo trình nhắc đến 7 (bản cũ) và 10 (bản mới). Bài này đo thử để tìm CUTOFF tốt nhất trên máy của Em và so sánh với hai giá trị đó.

## 2. Cài đặt

- Merge.java: Mergesort Em tự cài theo bài giảng (có mảng phụ aux). Nếu mảng con có độ dài ≤ CUTOFF thì gọi Insertion sort.

- Insertion.java: Insertion sort cho đoạn a\[low..high\].

- CUTOFF = 0 và CUTOFF = 1 là như nhau (không dùng Insertion sort), nên Em lấy CUTOFF = 1 làm mốc so sánh.

- Kiểm tra đúng: Main chạy mọi CUTOFF từ 0 đến 101 trên 4 loại dữ liệu và so với Arrays.sort, cả 4080 test đều đúng.

## 3. Cách làm thí nghiệm

- Máy: CPU Intel core I7-7700HQ, RAM 16 GB RAM, hệ điều hành Windows 10, JDK 25.0.2, Python 3.13.

- Dữ liệu: 4 loại là random, sorted (đã sắp xếp), reverse (ngược) và few_unique (chỉ có 5 giá trị khác nhau). Mỗi loại thử 3 kích thước N = 100.000, 500.000 và 1.000.000. Dữ liệu sinh bằng DataGenerator với seed cố định 12345.

- CUTOFF thử: từ 1 đến 30, thêm 35, 40, 50, 60, 80, 100.

- Mỗi cấu hình đo 15 lần và lấy median, vì thỉnh thoảng có vài lần chạy chậm bất thường. Trước khi đo có chạy warm-up cho JVM. Thứ tự các CUTOFF được xáo ngẫu nhiên ở mỗi lượt để không bị lệch do máy nóng lên hay GC.

- Chạy Sort.Benchmark rồi Phan_tich_CSV.py, không bật -ea.

## 4. Kết quả

Bảng dưới tóm tắt kết quả (median của 15 lần đo). Cột "chậm hơn tối ưu" là trung bình của 3 kích thước. Bảng đầy đủ nằm trong summary_best_cutoff.csv.

| Loại dữ liệu | CUTOFF tốt nhất (N = 100k / 500k / 1M) | Giảm thời gian so với CUTOFF 1 | CUTOFF 7 chậm hơn tối ưu | CUTOFF 10 chậm hơn tối ưu |
|--------------|----------------------------------------|--------------------------------|--------------------------|---------------------------|
| random       | 16 / 14 / 12                           | 6–18%                          | 7,0%                     | 3,1%                      |
| sorted       | 100 / 80 / 100                         | 31–46%                         | 31,7%                    | 29,1%                     |
| reverse      | 10 / 4 / 5                             | 6–14%                          | 2,3%                     | 3,9%                      |
| few_unique   | 17 / 8 / 14                            | 10–20%                         | 5,4%                     | 4,2%                      |

- Hình 1 (cutoff_analysis.png): thời gian chạy theo CUTOFF, dấu sao là CUTOFF tốt nhất.

- Hình 2 (cutoff_normalized.png): thời gian chia cho thời gian tốt nhất của từng cấu hình, để so sánh giữa các N.

## 5. Nhận xét

- Random: tốt nhất quanh 12–16. Đường cong khá phẳng từ khoảng 6 đến 29, sau đó chậm dần (CUTOFF 100 chậm hơn tối ưu khoảng 40%).

- Sorted: CUTOFF càng lớn càng nhanh, tốt nhất ở 80–100. Điều này hợp lý vì Insertion sort chạy rất nhanh trên đoạn đã sắp xếp.

- Reverse: tốt nhất ở giá trị nhỏ (4–10), CUTOFF lớn thì chậm hẳn (CUTOFF 100 chậm hơn hơn 2 lần). Đây là trường hợp xấu nhất của Insertion sort.

- few_unique: tốt nhất khoảng 8–17, gần giống random.

- Dùng Insertion sort cho mảng con giúp giảm khoảng 6–20% thời gian, riêng dữ liệu sorted giảm 31–46%.

- CUTOFF 7 và 10 chỉ chênh nhau khoảng 1–2% (trung bình), nhỏ hơn sai số đo, nên không kết luận được cái nào tốt hơn.

## 6. Hạn chế

- Kết quả đo có nhiễu: độ lệch chuẩn thường khoảng 6% giá trị trung bình, riêng random N = 100.000 khoảng 25%. Vì vậy chênh lệch dưới khoảng 5% không đáng tin.

- Với dữ liệu sorted, CUTOFF tốt nhất nằm sát biên 100 nên chưa biết giá trị tối ưu thật.

- Mỗi cấu hình chỉ dùng một mảng, và kết quả chỉ đúng cho máy và JVM của Em.

## 7. Kết luận

Trên máy của Em nên chọn CUTOFF khoảng 12 (trong khoảng 10–14). Với dữ liệu random, reverse và few_unique, CUTOFF 12 chỉ chậm hơn mức tối ưu khoảng 3% và nhanh hơn không dùng Insertion sort khoảng 9%. Giá trị 7 và 10 của giáo trình cũng gần tối ưu, nhưng khác biệt nhỏ nên không rõ ràng.

## 8. Dùng AI và các file nộp

Em dùng AI để thảo luận cách thiết kế thí nghiệm, kiểm tra lỗi code và viết script đo, vẽ đồ thị. Mergesort do Em tự cài từ bài giảng.

File nộp: Merge.java, Insertion.java, Benchmark.java, DataGenerator.java, Main.java, Phan_tich_CSV.py, các file CSV trong thư mục data/ (data_10k\_\*.csv, experiment_results.csv, summary\_\*.csv), hai ảnh đồ thị.

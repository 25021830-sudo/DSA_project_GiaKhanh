import os
import sys

import pandas as pd
import matplotlib.pyplot as plt

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
PROJECT_DIR = os.path.dirname(SCRIPT_DIR) if os.path.basename(SCRIPT_DIR) == 'src' else SCRIPT_DIR
os.chdir(PROJECT_DIR)

FILE_PATH = 'data/experiment_results.csv'
if not os.path.exists(FILE_PATH):
    print(f"Lỗi: Không tìm thấy {os.path.join(PROJECT_DIR, FILE_PATH)}. Hãy chạy Java Benchmark trước!")
    sys.exit(1)

df = pd.read_csv(FILE_PATH)


COL = 'Median_ms' if 'Median_ms' in df.columns else 'Time_ms'
HAS_STD = 'Std_ms' in df.columns
BASE = df['Cutoff_Value'].min()
TYPES = [t for t in ['random', 'sorted', 'reverse', 'few_unique'] if t in set(df['Dataset_Type'])]
SIZES = sorted(df['Array_Size'].unique())

print(f"Đọc {len(df)} dòng từ {FILE_PATH} | cột thời gian dùng để so sánh: {COL}")

rows = []
for (t, n), g in df.groupby(['Dataset_Type', 'Array_Size']):
    g = g.set_index('Cutoff_Value')
    best = g[COL].idxmin()
    best_ms = g.loc[best, COL]
    near = g.index[g[COL] <= best_ms * 1.03]
    row = {
        'Dataset_Type': t,
        'Array_Size': n,
        'Best_Cutoff': best,
        'Best_ms': round(best_ms, 2),
        f'Cutoff{BASE}_ms': round(g.loc[BASE, COL], 2),
        'Improve_vs_base_%': round((1 - best_ms / g.loc[BASE, COL]) * 100, 1),
        'Within3%_range': f"{near.min()}-{near.max()}",
    }
    for c in (7, 10):
        if c in g.index:
            row[f'Cutoff{c}_slower_%'] = round((g.loc[c, COL] / best_ms - 1) * 100, 1)
    rows.append(row)

summary = pd.DataFrame(rows)
summary['Dataset_Type'] = pd.Categorical(summary['Dataset_Type'], TYPES, ordered=True)
summary = summary.sort_values(['Dataset_Type', 'Array_Size'])
summary.to_csv('data/summary_best_cutoff.csv', index=False)

print("\n=== BẢNG THỐNG KÊ THEO TỪNG CẤU HÌNH ===")
print(summary.to_string(index=False))

df['Normalized'] = df[COL] / df.groupby(['Dataset_Type', 'Array_Size'])[COL].transform('min')
overall = df.groupby('Cutoff_Value')['Normalized'].mean()
overall.rename('Mean_normalized_time').to_csv('data/summary_overall.csv')

print(f"\nCUTOFF tốt nhất tính chung cho mọi cấu hình: {overall.idxmin()} "
      f"(chậm hơn tối ưu từng cấu hình trung bình {(overall.min() - 1) * 100:.1f}%)")
print("Theo từng loại dữ liệu:")
for t in TYPES:
    s = df[df['Dataset_Type'] == t].groupby('Cutoff_Value')['Normalized'].mean()
    msg = f"  {t:<11}: CUTOFF tốt nhất = {s.idxmin()}"
    for c in (7, 10):
        if c in s.index:
            msg += f" | CUTOFF {c}: +{(s[c] - 1) * 100:.1f}%"
    print(msg + " so với tối ưu")

def draw(ycol, ylabel, title, out, with_std):
    fig, axes = plt.subplots(2, 2, figsize=(13, 9), squeeze=False)
    for ax, t in zip(axes.flat, TYPES):
        for n in SIZES:
            g = df[(df['Dataset_Type'] == t) & (df['Array_Size'] == n)].sort_values('Cutoff_Value')
            line, = ax.plot(g['Cutoff_Value'], g[ycol], marker='o', ms=3, label=f"N = {n:,}")
            if with_std and HAS_STD:
                ax.fill_between(g['Cutoff_Value'], g[ycol] - g['Std_ms'], g[ycol] + g['Std_ms'],
                                color=line.get_color(), alpha=0.12)
            b = g.loc[g[ycol].idxmin()]
            ax.scatter([b['Cutoff_Value']], [b[ycol]], marker='*', s=140,
                       color=line.get_color(), edgecolor='black', zorder=5)
        for c in (7, 10):
            ax.axvline(c, color='gray', ls=':', lw=0.8)
        ax.set_title(f"Dữ liệu: {t}")
        ax.set_xlabel("Giá trị CUTOFF")
        ax.set_ylabel(ylabel)
        ax.grid(alpha=0.3)
        ax.legend(title="Kích thước mảng", fontsize=8)
    for ax in axes.flat[len(TYPES):]:       # ẩn ô trống nếu thiếu loại dữ liệu
        ax.axis('off')
    fig.suptitle(title + "\n(★ = CUTOFF tốt nhất; đường chấm = 7 và 10)")
    fig.tight_layout()
    fig.savefig(out, dpi=300, bbox_inches='tight')
    plt.close(fig)
    print(f"Đã lưu: {out}")


print()
draw(COL, f"Thời gian chạy (ms, {'median' if COL == 'Median_ms' else 'trung bình'})",
     "Hiệu năng Mergesort theo CUTOFF (Insertion sort cho mảng con)",
     'data/cutoff_analysis.png', with_std=True)
draw('Normalized', "Thời gian / thời gian tốt nhất (1.0 = tối ưu)",
     "Thời gian chuẩn hoá theo CUTOFF",
     'data/cutoff_normalized.png', with_std=False)
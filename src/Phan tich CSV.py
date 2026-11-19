col = 'Median_ms' if 'Median_ms' in df.columns else 'Time_ms'
rows = []
for (t, n), g in df.groupby(['Dataset_Type', 'Array_Size']):
    g = g.set_index('Cutoff_Value')
    best = g[col].idxmin()
    base = g.loc[g.index.min(), col]
    rows.append({'Dataset_Type': t, 'Array_Size': n, 'Best_Cutoff': best,
                 'Best_ms': round(g.loc[best, col], 2),
                 'Improve_vs_base_%': round((1 - g.loc[best, col] / base) * 100, 1),
                 'Cutoff7_slower_%': round((g.loc[7, col] / g.loc[best, col] - 1) * 100, 1),
                 'Cutoff10_slower_%': round((g.loc[10, col] / g.loc[best, col] - 1) * 100, 1)})
summary = pd.DataFrame(rows)
print(summary.to_string(index=False))
summary.to_csv('data/summary_best_cutoff.csv', index=False)
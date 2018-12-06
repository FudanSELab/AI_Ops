import pandas as pd
from pandas import DataFrame
from imblearn.over_sampling import RandomOverSampler # doctest: +NORMALIZE_WHITESPACE

input_after_clean_path = "transform/final_after_wash.csv"
input_data_y_column = "new_trace_y.y_issue_ms"

output_after_sampling_path = "transform/final_after_sampling.csv"


data_after_clean = pd.read_csv(input_after_clean_path,
                               header=0,
                               index_col=0)

print(input_after_clean_path, "has", len(data_after_clean.keys()), "columns")
print(input_after_clean_path, "has", len(data_after_clean.values), "values")

keys = data_after_clean.keys()

X, y = data_after_clean, data_after_clean.pop(input_data_y_column)

X_keys = X.keys()

ros = RandomOverSampler()

# The final number of sampling result is class_num * majority_class_num
X_res, y_res = ros.fit_resample(X, y)

df_X_res = DataFrame(data=X_res,
                     columns=X_keys)

# Add y to X
df_X_res[input_data_y_column] = y_res

df_all_res = df_X_res

print(output_after_sampling_path, "has", len(df_all_res.keys()), "columns")
print(output_after_sampling_path, "has", len(df_all_res.values), "values")
# Output the result file.
df_all_res.to_csv(output_after_sampling_path)

import pandas as pd
from pandas import DataFrame
from imblearn.over_sampling import RandomOverSampler  # doctest: +NORMALIZE_WHITESPACE
from sklearn.utils import shuffle

input_after_clean_path = "transform/final_after_wash.csv"

y_result = "new_trace_y.y_exec_result"
y_ms = "new_trace_y.y_issue_ms"
y_dimension = "new_trace_y.y_issue_dim_type"

input_data_y_column = y_dimension

# output_after_sampling_path = "transform/y_result/y_result_after_sampling.csv"
# output_after_sampling_path = "transform/y_ms/y_ms_after_sampling.csv"
output_after_sampling_path = "transform/y_dimension/y_dimension_after_sampling.csv"


data_after_clean = pd.read_csv(input_after_clean_path,
                               header=0,
                               index_col=0)
print(input_after_clean_path, "has", len(data_after_clean.keys()), "columns")
print(input_after_clean_path, "has", len(data_after_clean.values), "values")

X, y = data_after_clean, data_after_clean.pop(input_data_y_column)
X_keys = X.keys()  # Save x-keys.
# The final number of sampling result is class_num * majority_class_num
X_res, y_res = RandomOverSampler().fit_resample(X, y)
df_X_res = DataFrame(data=X_res, columns=X_keys)
# Add y to X
df_X_res[input_data_y_column] = y_res
df_all_res = df_X_res
df_all_res = shuffle(df_all_res)


print(output_after_sampling_path, "has", len(df_all_res.keys()), "columns")
print(output_after_sampling_path, "has", len(df_all_res.values), "values")
# Output the result file.
df_all_res.to_csv(output_after_sampling_path)

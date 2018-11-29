import pandas as pd

input_after_join_path = "transform/sample_after_join.csv"
output_after_clean_path = "transform/sample_after_clean.csv"

data_after_join = pd.read_csv(input_after_join_path,
                              header=0,
                              index_col=0)

print(input_after_join_path, "has", len(data_after_join.keys()), "columns")

# Drop the column which all element are null.
data_after_join = data_after_join.dropna(axis=1, how='all')

print("After drop NAN/NULL data,", input_after_join_path, "has", len(data_after_join.keys()), "columns")

# TODO: Drop any duplicate useless column

print("After drop duplicate data,", input_after_join_path, "has", len(data_after_join.keys()), "columns")

# TODO: Drop any useless column
data_after_join.pop("real_trace2.test_trace_id")

print("After drop useless data,", input_after_join_path, "has", len(data_after_join.keys()), "columns")

# Output the result file.
data_after_join.to_csv(output_after_clean_path)

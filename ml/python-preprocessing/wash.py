import pandas as pd

input_after_join_path = "transform/final_after_join.csv"

# The NULL value in such 3 columns means SUCCESS, we will fill it.
y_result = "new_trace_y.y_exec_result"
y_ms = "new_trace_y.y_issue_ms"
y_dimension = "new_trace_y.y_issue_dim_type"

output_after_wash_path = "transform/final_after_wash.csv"

data_after_join = pd.read_csv(input_after_join_path,
                              header=0,
                              index_col=0)

print(input_after_join_path, "has", len(data_after_join.keys()), "columns")

# Drop the column which all element are null.
data_after_join = data_after_join.dropna(axis=1, how='all')

# Fill the column which the y-column is NULL( Null means success).
data_after_join[y_ms] = data_after_join[y_ms].fillna("Success")
data_after_join[y_dimension] = data_after_join[y_dimension].fillna("Success")

# data_after_join = data_after_join[~data_after_join['new_trace_y.y_issue_ms'].isin([np.nan])]

print("After drop NAN/NULL data,", input_after_join_path, "has", len(data_after_join.keys()), "columns")

# TODO: Drop any duplicate useless column
print("After drop duplicate data,", input_after_join_path, "has", len(data_after_join.keys()), "columns")

# TODO: Drop any useless column
data_after_join.pop("new_trace_y.test_trace_id")
data_after_join.pop("final_seq2.test_trace_id1")
data_after_join.pop("final_seq2.test_case_id1")
data_after_join.pop("new_trace_y.y_issue_dim_content")

print("After drop useless data,", output_after_wash_path, "has", len(data_after_join.keys()), "columns")

# Output the result file.
data_after_join.to_csv(output_after_wash_path)

import pandas as pd
from sklearn.preprocessing import MinMaxScaler

input_after_transform_path = "transform/final_after_transform.csv"

input_data_y1_column = "new_trace_y.y_exec_result"
input_data_y2_column = "new_trace_y.y_issue_ms"
input_data_y3_column = "new_trace_y.y_issue_dim_content"
input_data_y4_column = "new_trace_y.y_issue_dim_type"

output_after_dimensionless_path = "transform/final_after_dimensionless.csv"

data_after_transform = pd.read_csv(input_after_transform_path,
                                   header=0,
                                   index_col=0)

scaler = MinMaxScaler()

y1 = data_after_transform.pop(input_data_y1_column)
y2 = data_after_transform.pop(input_data_y2_column)
y3 = data_after_transform.pop(input_data_y3_column)
y4 = data_after_transform.pop(input_data_y4_column)

dimensionless_columns = data_after_transform.columns

data_after_transform[data_after_transform.columns] = \
    scaler.fit_transform(data_after_transform[data_after_transform.columns])

data_after_transform[input_data_y1_column] = y1
data_after_transform[input_data_y2_column] = y2
data_after_transform[input_data_y3_column] = y3
data_after_transform[input_data_y4_column] = y4

print(output_after_dimensionless_path, "has", len(data_after_transform.keys()), "columns")

# Output the result file.
data_after_transform.to_csv(output_after_dimensionless_path)



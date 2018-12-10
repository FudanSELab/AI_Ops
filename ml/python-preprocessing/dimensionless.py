import pandas as pd
from sklearn.preprocessing import MinMaxScaler

# input_after_transform_path = "transform/y_result/y_result_after_transform.csv"
# output_after_dimensionless_path = "transform/y_result/y_result_after_dimensionless.csv"

# input_after_transform_path = "transform/y_ms/y_ms_after_transform.csv"
# output_after_dimensionless_path = "transform/y_ms/y_ms_after_dimensionless.csv"

input_after_transform_path = "transform/y_dimension/y_dimension_after_transform.csv"
output_after_dimensionless_path = "transform/y_dimension/y_dimension_after_dimensionless.csv"

y_result = "new_trace_y.y_exec_result"
y_ms = "new_trace_y.y_issue_ms"
y_dimension = "new_trace_y.y_issue_dim_type"

data_after_transform = pd.read_csv(input_after_transform_path,
                                   header=0,
                                   index_col=0)

scaler = MinMaxScaler()

y1 = data_after_transform.pop(y_result)
y2 = data_after_transform.pop(y_ms)
y3 = data_after_transform.pop(y_dimension)

data_after_transform[data_after_transform.columns] = \
    scaler.fit_transform(data_after_transform[data_after_transform.columns])

data_after_transform[y_result] = y1
data_after_transform[y_ms] = y2
data_after_transform[y_dimension] = y3

print(output_after_dimensionless_path, "has", len(data_after_transform.keys()), "columns")

# Output the result file.
data_after_transform.to_csv(output_after_dimensionless_path)



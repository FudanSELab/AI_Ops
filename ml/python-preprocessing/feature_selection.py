import pandas as pd
from sklearn.feature_selection import SelectKBest
from sklearn.feature_selection import chi2

input_after_dimensionless_path = "transform/final_after_dimensionless.csv"
input_data_y_column = "new_trace_y.y_issue_ms"
input_data_y1_column = "new_trace_y.y_exec_result"
input_data_y3_column = "new_trace_y.y_issue_dim_content"
input_data_y4_column = "new_trace_y.y_issue_dim_type"

output_after_feature_selection_path = "transform/final_after_feature_selection.csv"

data_after_dimensionless = pd.read_csv(input_after_dimensionless_path,
                                       header=0,
                                       index_col=0)

data_after_dimensionless.pop(input_data_y1_column)
data_after_dimensionless.pop(input_data_y3_column)
data_after_dimensionless.pop(input_data_y4_column)

X, y = data_after_dimensionless, data_after_dimensionless.pop(input_data_y_column)

model_cq = SelectKBest(chi2, k=5)  # Select k best features
after_data = model_cq.fit_transform(X.values, y)

selected_feature_indexs = model_cq.get_support(True)

selected_column = data_after_dimensionless.columns[selected_feature_indexs]

data_after_feature_extraction = data_after_dimensionless[selected_column]
data_after_feature_extraction[input_data_y_column] = y

print(output_after_feature_selection_path, "has", len(data_after_feature_extraction.keys()), "columns")

# Output the result file.
data_after_feature_extraction.to_csv(output_after_feature_selection_path)




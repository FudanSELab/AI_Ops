import pandas as pd
from pandas import DataFrame
from sklearn.decomposition import PCA

input_after_feature_selection_path = "transform/y_result/y_result_after_dimensionless.csv"
output_after_dimensionality_reduction_path = "transform/y_result/y_result_after_dimensionality_reduction.csv"

# input_after_feature_selection_path = "transform/y_ms/y_ms_after_dimensionless.csv"
# output_after_dimensionality_reduction_path = "transform/y_ms/y_ms_after_dimensionality_reduction.csv"

# input_after_feature_selection_path = "transform/y_dimension/y_dimension_after_dimensionless.csv"
# output_after_dimensionality_reduction_path = "transform/y_dimension/y_dimension_after_dimensionality_reduction.csv"

# In this part of data, we only use "new_trace_y.y_issue_ms". The other 3 y-columns will be drop.
input_data_y_result_column = "new_trace_y.y_exec_result"
input_data_y_ms_column = "new_trace_y.y_issue_ms"
input_data_y_dimension_column = "new_trace_y.y_issue_dim_type"
input_data_y_column = input_data_y_result_column


data_after_feature_selection = pd.read_csv(input_after_feature_selection_path,
                                           header=0,
                                           index_col=0)

# data_after_feature_selection.pop(input_data_y_result_column)
data_after_feature_selection.pop(input_data_y_ms_column)
data_after_feature_selection.pop(input_data_y_dimension_column)

X, y = data_after_feature_selection, data_after_feature_selection.pop(input_data_y_column)

pca = PCA(n_components=600,
          whiten=True)
pca.fit(X)

print("Convert Matrix:")
print(pca.components_)

X_new = pca.transform(X)

df = DataFrame(X_new)
df[input_data_y_column] = y

# print("New Columns:")
# print(df.keys())

print("Pca Explained Variance Ratio:")
print(pca.explained_variance_ratio_)

print(output_after_dimensionality_reduction_path, "has", len(df.keys()), "columns")

# Output the result file.
df.to_csv(output_after_dimensionality_reduction_path)

# X = np.array([[0, 1, 0], [0, 2, 0], [0, 3, 0], [0, 4, 0], [0, 5, 0], [0, 100, 0]])
# pca = PCA(n_components=1)
# pca.fit(X)
# X_new = pca.transform(X)
# print(X_new)
# print(pca.components_)
#
# print(pca.inverse_transform(X_new))

import pandas as pd
from pandas import DataFrame
from sklearn.decomposition import PCA

input_after_feature_selection_path = "transform/final_after_dimensionless.csv"
input_data_y_column = "new_trace_y.y_issue_ms"

# In this part of data, we only use "new_trace_y.y_issue_ms". The other 3 y-columns will be drop.
input_data_useless_y1_column = "new_trace_y.y_exec_result"
input_data_useless_y2_column = "new_trace_y.y_issue_dim_content"
input_data_useless_y3_column = "new_trace_y.y_issue_dim_type"

output_after_dimensionality_reduction_path = "transform/final_after_dimensionality_reduction.csv"

data_after_feature_selection = pd.read_csv(input_after_feature_selection_path,
                                           header=0,
                                           index_col=0)

data_after_feature_selection.pop(input_data_useless_y1_column)
data_after_feature_selection.pop(input_data_useless_y2_column)
data_after_feature_selection.pop(input_data_useless_y3_column)

X, y = data_after_feature_selection, data_after_feature_selection.pop(input_data_y_column)

pca = PCA(n_components=1000,
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

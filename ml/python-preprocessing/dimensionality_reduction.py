import pandas as pd
from pandas import DataFrame
from sklearn.decomposition import PCA, IncrementalPCA

input_after_feature_selection_path = "transform/final_after_feature_selection.csv"
input_data_y_column = "new_trace_y.y_issue_ms"

output_after_dimensionality_reduction_path = "transform/final_after_dimensionality_reduction.csv"

data_after_feature_selection = pd.read_csv(input_after_feature_selection_path,
                                           header=0,
                                           index_col=0)

X, y = data_after_feature_selection, data_after_feature_selection.pop(input_data_y_column)

pca = PCA(n_components=3)
pca.fit(X)

print("Convert Matrix:")
print(pca.components_)

X_new = pca.transform(X)

df = DataFrame(X_new)
df[input_data_y_column] = y

print("New Columns:")
print(df.keys())

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

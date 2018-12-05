import pandas as pd
from sklearn.feature_selection import SelectKBest
from sklearn.feature_selection import chi2

input_after_dimensionless_path = "transform/final_after_dimensionless.csv"
input_data_y_column = "new_trace_y.y_issue_ms"

output_after_feature_selection_path = "transform/final_after_feature_selection.csv"

data_after_feature_extraction = pd.read_csv(input_after_dimensionless_path,
                                            header=0,
                                            index_col=0)

X, y = data_after_feature_extraction, data_after_feature_extraction.pop(input_data_y_column)

model_cq = SelectKBest(chi2, k=200)  # Select k best features
after_data = model_cq.fit_transform(X.values, y)

# TODO: array to data frame


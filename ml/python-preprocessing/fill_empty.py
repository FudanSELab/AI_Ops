import pandas as pd

# input_after_feature_extraction_path = "transform/y_result/y_result_after_feature_extraction.csv"
# output_after_fill_empty_path = "transform/y_result/y_result_after_fill_empty.csv"

# input_after_feature_extraction_path = "transform/y_ms/y_ms_after_feature_extraction.csv"
# output_after_fill_empty_path = "transform/y_ms/y_ms_after_fill_empty.csv"

input_after_feature_extraction_path = "transform/y_dimension/y_dimension_after_feature_extraction.csv"
output_after_fill_empty_path = "transform/y_dimension/y_dimension_after_fill_empty.csv"

data_after_feature_extraction = pd.read_csv(input_after_feature_extraction_path,
                                            header=0,
                                            index_col=0)

print(input_after_feature_extraction_path, "has", len(data_after_feature_extraction.keys()), "columns")

# Fill by mean number
data_after_feature_extraction = data_after_feature_extraction.fillna(data_after_feature_extraction.mean())
# imp = SimpleImputer(missing_values='NaN', strategy='mean')
# imp.fit(data_after_feature_extraction)
# data_after_feature_extraction = imp.transform(data_after_feature_extraction)

print(output_after_fill_empty_path, "has", len(data_after_feature_extraction.keys()), "columns")

# Output the result file.
data_after_feature_extraction.to_csv(output_after_fill_empty_path)

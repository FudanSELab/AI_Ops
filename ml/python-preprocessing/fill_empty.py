import pandas as pd

input_after_feature_extraction_path = "transform/sample_after_feature_extraction.csv"
output_after_fill_empty_path = "transform/sample_after_fill_empty.csv"

data_after_feature_extraction = pd.read_csv(input_after_feature_extraction_path,
                                            header=0,
                                            index_col=0)

print(input_after_feature_extraction_path, "has", len(data_after_feature_extraction.keys()), "columns")

# TODO

print(output_after_fill_empty_path, "has", len(data_after_feature_extraction.keys()), "columns")

# Output the result file.
data_after_feature_extraction.to_csv(output_after_fill_empty_path)
import pandas as pd

input_after_fill_empty_path = "transform/final_after_fill_empty.csv"
output_after_transform_path = "transform/final_after_transform.csv"

data_after_feature_extraction = pd.read_csv(input_after_fill_empty_path,
                                            header=0,
                                            index_col=0)

print(input_after_fill_empty_path, "has", len(data_after_feature_extraction.keys()), "columns")

# TODO: Feature Transform

print(output_after_transform_path, "has", len(data_after_feature_extraction.keys()), "columns")

# Output the result file.
data_after_feature_extraction.to_csv(output_after_transform_path)
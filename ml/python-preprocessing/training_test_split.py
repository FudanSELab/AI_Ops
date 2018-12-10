import pandas as pd

# input_data_path = "transform/y_result/y_result_after_dimensionality_reduction.csv"
# output_training_data_path = "transform/y_result/y_result_training.csv"
# output_testing_data_path = "transform/y_result/y_result_testing.csv"

# input_data_path = "transform/y_ms/y_ms_after_dimensionality_reduction.csv"
# output_training_data_path = "transform/y_ms/y_ms_training.csv"
# output_testing_data_path = "transform/y_ms/y_ms_testing.csv"

input_data_path = "transform/y_dimension/y_dimension_after_dimensionality_reduction.csv"
output_training_data_path = "transform/y_dimension/y_dimension_training.csv"
output_testing_data_path = "transform/y_dimension/y_dimension_testing.csv"

data_before_split = pd.read_csv(input_data_path,
                                header=0,
                                index_col=0)

train = data_before_split.sample(frac=0.9)
test = data_before_split.drop(train.index)

train.to_csv(output_training_data_path)
test.to_csv(output_testing_data_path)


import pandas as pd
import tensorflow as tf
import os
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'

predict_columns = [
    "trace_id",
    "session_id",
    "testcase_id",
    "scenario_id",
    "entry_service",
    "entry_api",
    "entry_timestamp",
    "service_1_inst_delta",
    "service_1_conf_mem_limit_delta",
    "service_1_conf_cpu_limit_delta",
    "service_2_inst_delta",
    "service_2_conf_mem_limit_delta",
    "service_2_conf_cpu_limit_delta",
    "service_3_inst_delta",
    "service_3_conf_mem_limit_delta",
    "service_3_conf_cpu_limit_delta",
    "y_is_error_lazy",
    "y_is_error_predict",
    "y_is_error",
    "y_issue_ms",
    "y_issue_dimension"
]


def eval_input_fn(features, labels, batch_size):
    """An input function for evaluation or prediction"""
    features = dict(features)
    if labels is None:
        # No labels, use only features.
        inputs = features
    else:
        inputs = (features, labels)

    # Convert the inputs to a Dataset.
    dataset = tf.data.Dataset.from_tensor_slices(inputs)
    # Batch the examples
    assert batch_size is not None, "batch_size must not be None"
    dataset = dataset.batch(batch_size)
    # Return the dataset.
    return dataset


def convertStrToNumber(name, data):
    mapping_keys = data[name].drop_duplicates().values
    mapping = {}
    for i in range(len(mapping_keys)):
        mapping[mapping_keys[i]] = i
    return data[name].map(mapping), mapping


predict_path = "mock.csv"
train = pd.read_csv(predict_path,
                    names=predict_columns,
                    header=0)


train.pop("trace_id")
train.pop("session_id")
train.pop("scenario_id")
train.pop("entry_timestamp")
train.pop("y_is_error_lazy")
train.pop("y_is_error_predict")
train.pop("y_is_error")

train["testcase_id"], testcase_id_mapping = convertStrToNumber("testcase_id", train)
train["entry_service"], entry_service_mapping = convertStrToNumber("entry_service", train)
train["entry_api"], entry_service_mapping = convertStrToNumber("entry_api", train)
train["y_issue_ms"], y_issue_ms_mapping = convertStrToNumber("y_issue_ms", train)
train["y_issue_dimension"], y_issue_dimension_mapping = convertStrToNumber("y_issue_dimension", train)

train_y_ms = train.pop("y_issue_ms")
train_y_issue_dimension = train.pop("y_issue_dimension")

train_x = train

# Feature columns describe how to use the input.
my_feature_columns = []
for key in train_x.keys():
    if key.count("inst") > 0:
        my_feature_columns.append(tf.feature_column.numeric_column(key=key))
    # elif key.count("mem") > 0:
    #     # numeric_feature_column = tf.feature_column.numeric_column(key);
    #     # my_feature_columns.append(tf.feature_column.bucketized_column(
    #     #     source_column=numeric_feature_column,
    #     #     boundaries=[100, 200, 300]))
    # elif key.count("time") > 0:
    #     print(key)
    #     vocabulary_feature_column = tf.feature_column.categorical_column_with_vocabulary_list(
    #         key=key,
    #         vocabulary_list=["below 1", "below 2", "below 3", "below 6", "below 10", "above 10"])
    #     my_feature_columns.append(tf.feature_column.indicator_column(vocabulary_feature_column));
    else:
        my_feature_columns.append(tf.feature_column.numeric_column(key=key))

# Build 2 hidden layer DNN with 10, 10 units respectively.
checkpointing_config = tf.estimator.RunConfig(
    save_checkpoints_secs=60,  # Save checkpoints every 60 seconds.
    keep_checkpoint_max=10,  # Retain the 10 most recent checkpoints.
)

classifier_ms = tf.estimator.DNNClassifier(
    model_dir="model/test_dnn_ms",
    config=checkpointing_config,
    feature_columns=my_feature_columns,
    # Two hidden layers of 10 nodes each.
    hidden_units=[50, 50],
    # The model must choose between 3 classes.
    n_classes=3)


# Evaluate the model.
eval_result = classifier_ms.evaluate(
    input_fn=lambda: eval_input_fn(train_x, train_y_ms, 10))

print('\nTest set accuracy: {accuracy:0.3f}\n'.format(**eval_result))


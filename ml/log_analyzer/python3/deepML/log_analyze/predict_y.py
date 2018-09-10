from __future__ import absolute_import
from __future__ import division
from __future__ import print_function
import pandas as pd
import tensorflow as tf
import argparse
import data_load
import columns
import data_save

parser = argparse.ArgumentParser()
parser.add_argument('--batch_size', default=100, type=int, help='batch size')
parser.add_argument('--train_steps', default=5, type=int, help='number of training steps')


def get_features_to_be_predicted():
    predict_feature = ['212212', 1, 2, 3, 4, 5, 6, 7]
    column = columns.CSV_FEATURE_ORIGINAL
    data_item = tuple(predict_feature)
    data = [data_item]
    print(data)
    print(column)
    frame = pd.DataFrame(data, columns=column)
    feature, feature_id = frame, frame.pop("feature_id")

    print(feature_id)
    return feature, feature_id


def predict_input_fn(features, batch_size):
    """An input function for evaluation or prediction"""
    features = dict(features)
    inputs = features
    # Convert the inputs to a Dataset.
    dataset = tf.data.Dataset.from_tensor_slices(inputs)
    # Batch the examples
    assert batch_size is not None, "batch_size must not be None"
    dataset = dataset.batch(batch_size)
    # Return the dataset.
    return dataset


def main(argv):

    args = parser.parse_args(argv[1:])

    # Get Feature Columns
    feature_columns = data_load.get_feature_column("data/original_features.csv")
    # Build 2 hidden layer DNN with 20, 20 units respectively.
    checkpointing_config = tf.estimator.RunConfig(
        save_checkpoints_secs=60,  # Save checkpoints every 60 seconds.
        keep_checkpoint_max=10,  # Retain the 10 most recent checkpoints.
    )
    classifier = tf.estimator.DNNClassifier(
        model_dir="model/y_dnn",
        config=checkpointing_config,
        feature_columns=feature_columns,
        # Two hidden layers of 20 nodes each.
        hidden_units=[20, 20],
        # The model must choose between 2 classes.
        n_classes=2)

    data_to_be_predicted, feature_id = get_features_to_be_predicted()

    predictions = classifier.predict(
        input_fn=lambda: predict_input_fn(data_to_be_predicted,
                                          batch_size=args.batch_size)
    )

    predictions = list(predictions)

    data_set = []

    for i in range(0, len(predictions)):
        features = data_to_be_predicted.values[i]
        f_id = feature_id[i]
        pre = predictions[i].get("class_ids")[0]
        new_data_item = [f_id]
        for feature_item in features:
            new_data_item.append(feature_item)
        new_data_item.append(pre)
        data_set.append(new_data_item)
        print(new_data_item)

    print(data_set)

    data_save.write_to_csv(
        "data/y2.csv",
        data=data_set,
        header=None)
    #
    # for i range len(predictions_list:
    #     print(pre.get("class_ids")[0])
    # Write the predictions into the y2.csv


if __name__ == '__main__':
    tf.logging.set_verbosity(tf.logging.INFO)
    tf.app.run(main)
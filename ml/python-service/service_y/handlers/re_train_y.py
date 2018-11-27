from __future__ import absolute_import
from __future__ import division
from __future__ import print_function
import tensorflow as tf
import pandas as pd
import data_load
import columns


def get_data_to_be_re_train(new_feature_array_with_id):
    column = columns.CSV_FEATURE_AND_VERIFIED_RESULT
    data_item = tuple(new_feature_array_with_id)
    data = [data_item]
    frame = pd.DataFrame(data, columns=column)
    frame.pop("feature_id")

    feature, y3 = frame, frame.pop("y3")
    return feature, y3


def train_input_fn(features, labels, batch_size):
    """An input function for training"""
    # Convert the inputs to a Dataset.
    dataset = tf.data.Dataset.from_tensor_slices((dict(features), labels))
    # Shuffle, repeat, and batch the examples.
    dataset = dataset.shuffle(100).repeat().batch(batch_size)
    # Return the dataset.
    return dataset


def re_train(new_feature_array_with_id):

    # Get Feature Columns
    feature_columns = data_load.get_feature_column("../data/original_features.csv")
    # Build 2 hidden layer DNN with 20, 20 units respectively.
    checkpointing_config = tf.estimator.RunConfig(
        save_checkpoints_secs=60,  # Save checkpoints every 60 seconds.
        keep_checkpoint_max=10,  # Retain the 10 most recent checkpoints.
    )
    classifier = tf.estimator.DNNClassifier(
        model_dir="../model/y_dnn",
        config=checkpointing_config,
        feature_columns=feature_columns,
        # Two hidden layers of 20 nodes each.
        hidden_units=[20, 20],
        # The model must choose between 2 classes.
        n_classes=2)

    train_x, train_y = get_data_to_be_re_train(new_feature_array_with_id)
    # Train the Model.
    classifier.train(
        input_fn=lambda: train_input_fn(train_x, train_y,
                                        batch_size=5),
        steps=5)

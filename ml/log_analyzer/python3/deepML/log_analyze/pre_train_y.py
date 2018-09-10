from __future__ import absolute_import
from __future__ import division
from __future__ import print_function
import tensorflow as tf
import argparse
import data_load
import columns


parser = argparse.ArgumentParser()
parser.add_argument('--batch_size', default=100, type=int, help='batch size')
parser.add_argument('--train_steps', default=1000, type=int, help='number of training steps')


def train_input_fn(features, labels, batch_size):
    """An input function for training"""
    # Convert the inputs to a Dataset.
    dataset = tf.data.Dataset.from_tensor_slices((dict(features), labels))
    # Shuffle, repeat, and batch the examples.
    dataset = dataset.shuffle(100).repeat().batch(batch_size)
    # Return the dataset.
    return dataset


def main(argv):

    args = parser.parse_args(argv[1:])

    # Load training data from data/y1.csv
    train_x, train_y = data_load.load_data_original_with_label(
        "data/y1.csv",
        columns.CSV_FEATURE_AND_LABEL_RESULT,
        "y1")

    print("=====[View Train Data]=====")
    print(train_x.head())
    print("===========================")

    # Get Feature Columns just from original_features.csv
    # because original_features.csv only include features and feature_id
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

    # Train the Model.
    classifier.train(
        input_fn=lambda: train_input_fn(train_x, train_y,
                                        args.batch_size),
        steps=args.train_steps)

    # Print where the model saved.
    print("[Check Point]" + classifier.latest_checkpoint())


if __name__ == '__main__':
    tf.logging.set_verbosity(tf.logging.INFO)
    tf.app.run(main)









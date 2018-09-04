from __future__ import absolute_import
from __future__ import division
from __future__ import print_function
import tensorflow as tf
import argparse
import data_load

parser = argparse.ArgumentParser()
parser.add_argument('--batch_size', default=100, type=int, help='batch size')
parser.add_argument('--train_steps', default=1000, type=int, help='number of training steps')


def get_features_to_be_predicted():
    features, feature_id = data_load.load_feature_original_without_label("data/original_features.csv")
    return features, feature_id


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

    print(data_to_be_predicted)
    print(feature_id)

    predictions = classifier.predict(
        input_fn=lambda: predict_input_fn(data_to_be_predicted,
                                          batch_size=args.batch_size)
    )

    # The below is just to print the result, no more other meanings.
    expected = [0,1]
    template = '\nPrediction is "{}" ({:.1f}%), expected "{}"'

    for pred_dict, expec in zip(predictions, expected):
        class_id = pred_dict['class_ids'][0]
        probability = pred_dict['probabilities'][class_id]
        print(template.format(class_id,
                              100 * probability, expec))

    for pre in predictions:
        print(pre.get("probabilities"))
    # Write the predictions into the y2.csv





if __name__ == '__main__':
    tf.logging.set_verbosity(tf.logging.INFO)
    tf.app.run(main)
from __future__ import absolute_import
from __future__ import division
from __future__ import print_function
import tensorflow as tf
import argparse
import data_save


parser = argparse.ArgumentParser()


def verify_and_get_result(features):
    # Send a remote to call and get the result of the verify
    print("Send a remote call to verify the result.")
    result = 0
    return result


def do_verify_and_save(features):
    verify_result = verify_and_get_result(features)
    features.append(verify_result)
    # Append line to csv.
    data_save.write_to_csv(
        "y3.csv",
        data=features,
        header=None)


def main(argv):

    args = parser.parse_args(argv[1:])

    verify_feature = ['feature_id', 'x1', 'x2', "x3", "x4", "x5", "x6", "x7"]
    data_set_verify_feature = []
    data_set_verify_feature.append(verify_feature)
    do_verify_and_save(data_set_verify_feature)


if __name__ == '__main__':
    tf.logging.set_verbosity(tf.logging.INFO)
    tf.app.run(main)

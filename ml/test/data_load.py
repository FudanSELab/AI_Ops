import pandas as pd
import columns
import tensorflow as tf


def load_feature_original_without_label(csv_path):
    original_feature = pd.read_csv(csv_path, names=columns.CSV_FEATURE_ORIGINAL, header=0)
    # feature_id is not a part of feature. So drop it.
    feature, feature_id = original_feature, original_feature.pop("feature_id")
    return original_feature, feature_id


def load_data_original_with_label(csv_path, columns_name, label_column_name):
    features_and_label = pd.read_csv(csv_path, names=columns_name, header=0)
    # feature_id is not a part of feature. So drop it.
    features_and_label.pop("feature_id")
    features, label = features_and_label, features_and_label.pop(label_column_name)
    return features, label


def get_feature_column(csv_path):
    features, feature_id = load_feature_original_without_label(csv_path)
    feature_columns = []
    print("=====[Feature Columns]=====")
    for key in features.keys():
        if key.count("x") > 0:
            print(tf.feature_column.numeric_column(key=key))
            feature_columns.append(tf.feature_column.numeric_column(key=key))
        else:
            print("Feature_columns: None")
    print("===========================")
    return feature_columns

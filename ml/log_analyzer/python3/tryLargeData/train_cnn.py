import tensorflow as tf
import pandas as pd
import datetime
import os
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'

def train_input_fn(features, labels, batch_size):
    """An input function for training"""
    # Convert the inputs to a Dataset.
    dataset = tf.data.Dataset.from_tensor_slices((dict(features), labels))
    # Shuffle, repeat, and batch the examples.
    dataset = dataset.shuffle(100).repeat().batch(batch_size)
    # Return the dataset.
    return dataset


def load_data_original_with_label(csv_path, label_column_name):
    features_and_label = pd.read_csv(csv_path, header=0)
    features, label = features_and_label, features_and_label.pop(label_column_name)
    return features, label


def get_feature_column():
    feature_columns = []
    print("=====[Feature Columns]=====")
    for i in range(1500):
        feature_columns.append(tf.feature_column.numeric_column("column_" + str(i)))
    return feature_columns


def read_data(file_queue):
    reader = tf.TextLineReader(skip_header_lines=1)
    key, value = reader.read(file_queue)
    record_defaults = list([0] for i in range(5))  # 这里你有多少列数据就写多少
    row = tf.decode_csv(value, record_defaults=record_defaults)
    label = row.pop(4)
    # 丢弃序号列
    row.pop(0)
    return tf.stack([row]), label


def create_pipeline(filename, batch_size, num_epochs=None):
    file_queue = tf.train.string_input_producer([filename], num_epochs=num_epochs)
    example, label = read_data(file_queue)
    min_after_dequeue = 1000
    capacity = min_after_dequeue + batch_size
    example_batch, label_batch = tf.train.shuffle_batch(
        [example, label], batch_size=batch_size, capacity=capacity,
        min_after_dequeue=min_after_dequeue
    )
    return example_batch, label_batch


x_train_batch, y_train_batch = create_pipeline('mock4.csv', 10, num_epochs=1)

init_op = tf.global_variables_initializer()
local_init_op = tf.local_variables_initializer()  # local variables like epoch_num, batch_size

with tf.Session() as sess:
    sess.run(init_op)
    sess.run(local_init_op)

    # Start populating the filename queue.
    coord = tf.train.Coordinator()
    threads = tf.train.start_queue_runners(coord=coord)

    try:
        while True:
            example, label = sess.run([x_train_batch, y_train_batch])
            print(example)
            print(label)
    except tf.errors.OutOfRangeError:
        print('Done reading')
    finally:
        coord.request_stop()

    coord.join(threads)
    sess.close()

# beginTxtName = "begin.txt"
# endTxtName = "end.txt"
#
#
# f = open(beginTxtName, "a")
# f.write(datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S'))
# print(datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S'))
#
# # Load training data from data/y1.csv
# train_x, train_y = load_data_original_with_label(
#     "mock.csv",
#     "y1")
#
# checkpointing_config = tf.estimator.RunConfig(
#     save_checkpoints_secs=60,
#     keep_checkpoint_max=10,
# )
#
# feature_columns = get_feature_column()
#
# classifier = tf.estimator.DNNClassifier(
#     model_dir="model/y_dnn",
#     config=checkpointing_config,
#     feature_columns=feature_columns,
#     # Two hidden layers of 20 nodes each.
#     hidden_units=[50, 50],
#     # The model must choose between 2 classes.
#     n_classes=10)
#
# # Train the Model.
# classifier.train(
#     input_fn=lambda: train_input_fn(train_x, train_y,
#                                     batch_size=100))
#
# print(datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S'))
# f = open(endTxtName, "a")
# f.write(datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S'))

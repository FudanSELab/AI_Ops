import tensorflow as tf
import pandas as pd
import numpy
import os
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'

# 下面是模型的一些超参数，如学习速率、数据学习的轮数
learning_rate = 0.01
batch_size = 5
num_epoch = 50

# 下面参数是有关模型的输入、隐层节点数与输出种类
num_input = 1200
n_hidden_1 = 50
n_hidden_2 = 50
num_classes = 10

# 数据集的位置以及check_point存储的位置
file_path = "mock4.csv"
check_point_save_dir = "./ckpt/model"


def load_data_original_with_label(csv_path):
    features_and_label = pd.read_csv(csv_path, header=0, index_col=0)
    print(features_and_label.keys())
    features, label = features_and_label, features_and_label.pop("y1")
    # features.pop(0)
    return features, label


def main(_):

    # 构建双层神经网络
    x = tf.placeholder(tf.float32, shape=[None, num_input], name="x")
    layer_1_output = tf.layers.dense(x, n_hidden_1,
                                     activation=tf.nn.relu)
    layer_2_output = tf.layers.dense(layer_1_output, n_hidden_2,
                                     activation=tf.nn.relu)
    output_layer_output = tf.layers.dense(layer_2_output, num_classes)

    y_ = tf.placeholder(tf.int32, name="y_")

    loss_op = tf.reduce_mean(
        tf.nn.sparse_softmax_cross_entropy_with_logits(
            logits=output_layer_output,
            labels=y_))
    optimizer = tf.train.GradientDescentOptimizer(
        learning_rate=learning_rate)
    global_step = tf.train.get_or_create_global_step()
    train_op = optimizer.minimize(loss_op,
                                  global_step=global_step)

    saver = tf.train.Saver()

    train_x, train_y = load_data_original_with_label(
        "mock4.csv")

    print("session前")

    with tf.Session() as sess:
        print("准备restore")
        saver.restore(sess, check_point_save_dir)
        print("准备run")
        result = sess.run(output_layer_output, feed_dict={x: train_x.values})
        print(result)

        sess.close()


if __name__ == "__main__":
    tf.app.run()

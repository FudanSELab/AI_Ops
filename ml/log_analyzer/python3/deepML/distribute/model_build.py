import tensorflow as tf
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
file_path = "mock3.csv"
check_point_save_dir = "./ckpt/model"


def read_data(file_queue):
    reader = tf.TextLineReader(skip_header_lines=1)
    key, value = reader.read(file_queue)
    # 本次读取的文件共有1202列
    record_defaults = list([0] for i in range(1202))
    row = tf.decode_csv(value,
                        record_defaults=record_defaults)
    # 在总计1202列中，第1列为序号列；中间1200列为属性；第1202列为label列
    label = row.pop(1200 + 1 + 1 - 1)
    row.pop(0)
    return tf.stack([row]), label


def create_pipeline(filename, pipeline_batch_size, num_epochs=None):
    # 要读取的文件名，以及整个数据集要被训练几次
    file_queue = tf.train.string_input_producer([filename],
                                                num_epochs=num_epochs)
    feature, label = read_data(file_queue)
    min_after_dequeue = 1000
    capacity = min_after_dequeue + pipeline_batch_size
    feature_batch, label_batch = tf.train.shuffle_batch([feature, label],
                                                        batch_size=pipeline_batch_size,
                                                        capacity=capacity,
                                                        min_after_dequeue=min_after_dequeue)
    return feature_batch, label_batch


def main(_):

    x_train_batch, y_train_batch = create_pipeline(
        filename=file_path,
        pipeline_batch_size=batch_size,
        num_epochs=num_epoch)

    # 构建双层神经网络
    x = tf.placeholder(tf.float32, shape=[None, num_input], name="x")
    layer_1_output = tf.layers.dense(x, n_hidden_1,
                                     activation=tf.nn.sigmoid)
    layer_2_output = tf.layers.dense(layer_1_output, n_hidden_2,
                                     activation=tf.nn.sigmoid)
    output_layer_output = tf.layers.dense(layer_2_output, num_classes,
                                          activation=tf.nn.relu)

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

    # Add ops to save and restore all the variables.
    saver = tf.train.Saver()

    # Later, launch the model, use the saver to restore variables from disk, and
    # do some work with the model.

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
                example = numpy.reshape(example, [batch_size, num_input])
                _, step = sess.run([train_op, global_step], feed_dict={x: example, y_: label})
                print(step)
        except tf.errors.OutOfRangeError:
            save_path = saver.save(sess, check_point_save_dir)
            print("Model saved in path: %s" % save_path)
        finally:
            coord.request_stop()

        coord.join(threads)
        sess.close()


if __name__ == "__main__":
    tf.app.run()

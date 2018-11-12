import tensorflow as tf
import numpy

FLAGS = tf.app.flags.FLAGS
tf.app.flags.DEFINE_string("ps_hosts", "", "Comma-separated list of hostname:port pairs")
tf.app.flags.DEFINE_string("worker_hosts", "", "Comma-separated list of hostname:port pairs")
tf.app.flags.DEFINE_string("job_name", "", "One of 'ps', 'worker'")
tf.app.flags.DEFINE_integer("task_index", 0, "Index of task within the job")

# 下面是模型的一些超参数，如学习速率、数据学习的轮数
learning_rate = 0.01
batch_size = 5
num_epoch = 2

# 下面参数是有关模型的输入、隐层节点数与输出种类
num_input = 1700
n_hidden_1 = 50
n_hidden_2 = 50
num_classes = 10

# 数据集的位置以及check_point存储的位置
file_path = "mock1700.csv"
check_point_save_dir = "./check_point"


def read_data(file_queue):
    reader = tf.TextLineReader(skip_header_lines=1)
    key, value = reader.read(file_queue)
    # 本次读取的文件共有1202列
    record_defaults = list([0] for i in range(1702))
    row = tf.decode_csv(value,
                        record_defaults=record_defaults)
    # 在总计1202列中，第1列为序号列；中间1200列为属性；第1202列为label列
    label = row.pop(1700 + 1 + 1 - 1)
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
                                                        min_after_dequeue=min_after_dequeue
    )
    return feature_batch, label_batch


def main(_):

    print("Parse Params")
    ps_hosts = FLAGS.ps_hosts.split(",")
    worker_hosts = FLAGS.worker_hosts.split(",")

    cluster = tf.train.ClusterSpec({"ps": ps_hosts, "worker": worker_hosts})
    server = tf.train.Server(cluster,
                             job_name=FLAGS.job_name,
                             task_index=FLAGS.task_index)

    if FLAGS.job_name == "ps":
        print("Running Param Server")
        server.join()
    elif FLAGS.job_name == "worker":
        print("Running Worker Server")
        with tf.device(tf.train.replica_device_setter(
                        worker_device="/job:worker/task:%d" % FLAGS.task_index,
                        cluster=cluster)):

            init_op = tf.global_variables_initializer()
            local_init_op = tf.local_variables_initializer()

            x_train_batch, y_train_batch = create_pipeline(
                filename=file_path,
                pipeline_batch_size=batch_size,
                num_epochs=num_epoch)

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

        with tf.train.MonitoredTrainingSession(master=server.target,
                                               is_chief=(FLAGS.task_index == 0),
                                               checkpoint_dir=check_point_save_dir,
                                               save_checkpoint_secs=60
                                               ) as mon_sess:


            mon_sess.run(init_op)
            mon_sess.run(local_init_op)
            coord = tf.train.Coordinator()
            threads = tf.train.start_queue_runners(coord=coord, sess=mon_sess)
            try:
                while True:
                    example, label = mon_sess.run([x_train_batch, y_train_batch])
                    example = numpy.reshape(example, [batch_size, num_input])
                    _, step = mon_sess.run([train_op, global_step], feed_dict={x: example, y_: label})
                    print(step)
            except tf.errors.OutOfRangeError:
                print("Done reading")
            finally:
                coord.request_stop()

            coord.join(threads)


if __name__ == "__main__":
    tf.app.run()

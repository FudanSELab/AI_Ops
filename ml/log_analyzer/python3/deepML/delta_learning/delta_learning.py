




from __future__ import print_function

import numpy as np
import tensorflow as tf

# generate train data
# 1000 (20 delta + 10 action)
deltas_x = np.random.randint(0,2,(1000,30))
deltas_y = np.random.randint(0,2,size=(1000,20))

deltas_x_tests = np.random.randint(0,2,(100,30))
deltas_y_tests = np.random.randint(0,2,size=(100,20))

# Parameters
learning_rate = 0.01
training_epochs = 25
batch_size = 100
display_step = 1

# tf Graph Input
x = tf.placeholder(tf.float32, [None, 30]) # 20 delta + 10 action
y = tf.placeholder(tf.float32, [None, 20]) # 20 deltas(classes) output

# Set model weights
W = tf.Variable(tf.zeros([30, 20]))
b = tf.Variable(tf.zeros([20]))

# Construct model
pred = tf.nn.softmax(tf.matmul(x, W) + b) # Softmax

# Minimize error using cross entropy
cost = tf.reduce_mean(-tf.reduce_sum(y*tf.log(pred), reduction_indices=1))
# Gradient Descent
optimizer = tf.train.GradientDescentOptimizer(learning_rate).minimize(cost)

# Initialize the variables (i.e. assign their default value)
init = tf.global_variables_initializer()

# Start training
with tf.Session() as sess:

    # Run the initializer
    sess.run(init)

    # Training cycle
    for epoch in range(training_epochs):
        avg_cost = 0.
        total_batch = int(len(deltas_x)/batch_size)
        # Loop over all batches
        for i in range(total_batch):
            batch_xs = deltas_x[i*batch_size : (i+1)*batch_size]
            batch_ys = deltas_y[i*batch_size : (i+1)*batch_size]
            print(batch_xs.shape)
            print(batch_ys.shape)
            print(batch_ys[0])
            # batch_ys.reshape(100,1)
            _, c = sess.run([optimizer, cost], feed_dict={x: batch_xs, y: batch_ys})
            # Compute average loss
            avg_cost += c / total_batch
        # Display logs per epoch step
        if (epoch+1) % display_step == 0:
            print("Epoch:", '%04d' % (epoch+1), "cost=", "{:.9f}".format(avg_cost))

    print("Optimization Finished!")

    # Test model
    correct_prediction = tf.equal(tf.argmax(pred, 1), tf.argmax(y, 1))
    # Calculate accuracy
    accuracy = tf.reduce_mean(tf.cast(correct_prediction, tf.float32))
    print("Accuracy:", accuracy.eval({x: deltas_x_tests, y: deltas_y_tests}))



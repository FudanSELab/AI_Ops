


from __future__ import division, print_function, absolute_import
import numpy as np

# Import MNIST data
from tensorflow.examples.tutorials.mnist import input_data
mnist = input_data.read_data_sets("/tmp/data/", one_hot=False)


print("----------------------")
print(len(mnist.train.images))
print(len(mnist.train.images[0]))
print(mnist.train.images)
print(len(mnist.train.labels))
print(mnist.train.labels)



print("----------------------")
batch_size = 100
total_batch = int(mnist.train.num_examples/batch_size)
# Loop over all batches
for i in range(total_batch):
    batch_xs, batch_ys = mnist.train.next_batch(batch_size)
    print(batch_xs)
    print(batch_ys)
    print(batch_xs.shape)
    print(batch_ys.shape)



print("random data----------------")
print(np.random.rand(6,6))
print(np.random.randint(2,6,(2,3))) #生成一个2x3整数数组,取值范围：[2,6)随机整数 
print(np.random.randint(0,2,(10,20)))



print("--------------generate data-----------------")
rand_values = np.random.randint(0,2,(30,30,30))
print(rand_values[0:10])
batch_size = 10
total_batch = int(len(rand_values)/batch_size)
print(total_batch)
for i in range(total_batch):
	print(i)
	print(rand_values[i*10:(i+1)*10])
print(rand_values[29])



print("--------------uniform----------------")
z = np.random.uniform(-1., 1., size=[12, 12])
print(z)











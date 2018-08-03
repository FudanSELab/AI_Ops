




from __future__ import absolute_import
from __future__ import division
from __future__ import print_function

import argparse
import tensorflow as tf

import my_data_load


parser = argparse.ArgumentParser()
parser.add_argument('--batch_size', default=100, type=int, help='batch size')
parser.add_argument('--train_steps', default=1000, type=int,
					help='number of training steps')

def main(argv):
	args = parser.parse_args(argv[1:])

	# Fetch the data
	(train_x, train_y), (test_x, test_y) = my_data_load.load_data()

	# print(train_x, train_y)

	# Feature columns describe how to use the input.
	my_feature_columns = []
	for key in train_x.keys():
		if key.count("inst") > 0:
			my_feature_columns.append(tf.feature_column.numeric_column(key=key))
		elif key.count("mem") > 0:
			numeric_feature_column = tf.feature_column.numeric_column(key);
			my_feature_columns.append(tf.feature_column.bucketized_column(
				source_column = numeric_feature_column,
				boundaries = [100, 200, 300]))
		elif key.count("time") > 0:
			print(key);
			vocabulary_feature_column = tf.feature_column.categorical_column_with_vocabulary_list(
			        key=key,
			        vocabulary_list=["below 1","below 2","below 3","below 6","below 10","above 10"])
			my_feature_columns.append(tf.feature_column.indicator_column(vocabulary_feature_column));
		else:
			print("none");

	print(my_feature_columns)

	# Build 2 hidden layer DNN with 10, 10 units respectively.
	classifier = tf.estimator.DNNClassifier(
	    feature_columns=my_feature_columns,
	    # Two hidden layers of 10 nodes each.
	    hidden_units=[10, 10],
	    # The model must choose between 3 classes.
	    n_classes=2)

	# Train the Model.
	classifier.train(
	    input_fn=lambda:my_data_load.train_input_fn(train_x, train_y,
	                                             args.batch_size),
	    steps=args.train_steps)

	# Evaluate the model.
	eval_result = classifier.evaluate(
	    input_fn=lambda:my_data_load.eval_input_fn(test_x, test_y,
	                                            args.batch_size))

	print('\nTest set accuracy: {accuracy:0.3f}\n'.format(**eval_result))


	# Generate predictions from the model
	(predict_x, expected) = my_data_load.load_data_predict();
	# print(dict(predict_x), list(expected))

	predictions = classifier.predict(
	    input_fn=lambda:my_data_load.eval_input_fn(predict_x,
	                                            labels=None,
	                                            batch_size=args.batch_size))

	template = ('\nPrediction is "{}" ({:.1f}%), expected "{}"')

	for pred_dict, expec in zip(predictions, expected):
	    class_id = pred_dict['class_ids'][0]
	    probability = pred_dict['probabilities'][class_id]

	    print(template.format(class_id,
	                          100 * probability, expec))


if __name__ == '__main__':
	tf.logging.set_verbosity(tf.logging.INFO)
	tf.app.run(main)

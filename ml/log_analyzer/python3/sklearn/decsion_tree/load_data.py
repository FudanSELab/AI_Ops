import pandas as pd

CSV_COLUMN_NAMES = ["service1_inst", "service2_inst", "service3_inst", "service1_mem", "service2_mem", "service3_mem", "time_span", "result"]

CSV_COLUMN_NAMES_PREDICT = ["service1_inst", "service2_inst", "service3_inst", "service1_mem", "service2_mem", "service3_mem", "time_span"]

def load_data(y_name='result'):
    """Returns the iris dataset as (train_x, train_y), (test_x, test_y)."""
    train_path, test_path = "train.csv", "test.csv"

    train = pd.read_csv(train_path, names=CSV_COLUMN_NAMES, header=0)
    train_x, train_y = train, train.pop(y_name)

    test = pd.read_csv(test_path, names=CSV_COLUMN_NAMES, header=0)
    test_x, test_y = test, test.pop(y_name)

    return (train_x, train_y), (test_x, test_y)

def load_data_predict(y_name='result'):
    """Returns the iris dataset as (train_x, train_y), (test_x, test_y)."""
    predict_path = "predict.csv";

    predict = pd.read_csv(predict_path, names=CSV_COLUMN_NAMES, header=0)
    predict_x, predict_y = predict, predict.pop(y_name)

    return (predict_x, predict_y)


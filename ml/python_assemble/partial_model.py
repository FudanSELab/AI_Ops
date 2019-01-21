import pandas as pd
from sklearn.neural_network import MLPClassifier

all_dim_label = [
    "seq",
    "config",
    "instance",
    'unknown'
]

if __name__ == "__main__":
    chunker = pd.read_csv("ready_use_max_without_sampling.csv", chunksize=1000, header=0, index_col="trace_id")
    i = 0
    clf = MLPClassifier()
    for piece in chunker:
        piece.pop("y_final_result")
        piece.pop("y_issue_ms")
        x, y = piece, piece.pop("y_issue_dim_type")
        x_val = x.values
        y_val = y.values
        clf.partial_fit(x_val, y_val, all_dim_label)

    data = pd.read_csv("ready_use_max_without_sampling.csv", header=0, index_col="trace_id")
    data.pop("y_final_result")
    data.pop("y_issue_ms")
    y = data.pop("y_issue_dim_type")

    result = clf.predict(data.values)

    for i in range(len(y)):
        if result[i] == y[i]:
            print("命中")
        else:
            print("未命中")


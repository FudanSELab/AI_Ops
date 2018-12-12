import pandas as pd
from sklearn.neural_network import MLPClassifier
from sklearn.model_selection import GridSearchCV
from sklearn.utils import shuffle

# file_path = "../y_result_after_dimensionality_reduction.csv"
# log_file_path = "../log/mlp/log_y_result.txt"
# y_name = "new_trace_y.y_exec_result"

# file_path = "../y_ms_after_dimensionality_reduction.csv"
# log_file_path = "../log/mlp/log_y_ms.txt"
# y_name = "new_trace_y.y_issue_ms"

file_path = "../y_dimension_after_dimensionality_reduction.csv"
log_file_path = "../log/mlp/log_y_dimension.txt"
y_name = "new_trace_y.y_issue_dim_type"


def print_best_score(gsearch, param_test):
    f = open(log_file_path, 'w+')
    print("Best score: %0.3f" % gsearch.best_score_,
          file=f)
    print("Best parameters set:",
          file=f)
    best_parameters = gsearch.best_params_
    for param_name in sorted(param_test.keys()):
        print("\t%s: %r" % (param_name, best_parameters[param_name]),
              file=f)


features_label = pd.read_csv(file_path, header=0, index_col=0)
features_label = shuffle(features_label)

X, Y = features_label, features_label.pop(y_name)

clf = MLPClassifier()
param_test = {
    "hidden_layer_sizes": [(30, 30), (10, 10), (50, 50)],
    "max_iter": [200, 500, 1000, 2000]
}

grid_search_cv = GridSearchCV(clf,
                              param_grid=param_test,
                              cv=10)

grid_search_cv.fit(X=X, y=Y)
print_best_score(grid_search_cv, param_test)

# accs = sk_model_selection.cross_val_score(clf, X, y=Y, scoring=None, cv=5, n_jobs=1)
#
# print("#X:", len(X.values))
# print("#Y:", len(Y))
#
# print("Cross Validator Result:", sorted(accs, reverse=True))

# result = clf.predict(X)
#
# count = 0
#
# for i in range(len(result)):
#     print(str(result[i]) + " - " + str(Y[i]))
#     if result[i] == Y[i]:
#         count += 1
# print(str(count) + " : " + str(len(result)))

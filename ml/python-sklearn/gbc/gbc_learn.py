import pandas as pd
from sklearn.ensemble import GradientBoostingClassifier
from sklearn.model_selection import GridSearchCV

# file_path = "../y_result_after_dimensionality_reduction.csv"
# log_file_path = "../log/gbc/log_y_result.txt"
# y_name = "new_trace_y.y_exec_result"

# file_path = "../y_ms_after_dimensionality_reduction.csv"
# log_file_path = "../log/gbc/log_y_ms.txt"
# y_name = "new_trace_y.y_issue_ms"

file_path = "../y_dimension_after_dimensionality_reduction.csv"
log_file_path = "../log/gbc/log_y_dimension.txt"
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
X, Y = features_label, features_label.pop(y_name)

clf = GradientBoostingClassifier()

param_test = {
    "n_estimators": [5, 10, 20, 30, 50, 100, 500],
    "learning_rate": [0.0001, 0.01, 0.1, 1],
    "max_depth": [1, 3, 5, 10]
}

grid_search_cv = GridSearchCV(clf,
                              param_grid=param_test,
                              cv=10)

grid_search_cv.fit(X=X, y=Y)
print_best_score(grid_search_cv, param_test)

# clf.fit(X, Y)
#
# result = clf.predict(X)
#
# count = 0
#
# for i in range(len(result)):
#     print(str(result[i]) + " - " + str(Y[i]))
#     if result[i] == Y[i]:
#         count += 1
# print(str(count) + " : " + str(len(result)))

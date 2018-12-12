import pandas as pd
from sklearn.tree import DecisionTreeClassifier
from sklearn.neural_network import MLPClassifier
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import GridSearchCV


def print_best_score(gsearch, param_test):
    f = open("log.txt", 'w+')
    print("Best score: %0.3f" % gsearch.best_score_,
          file=f)
    print("Best parameters set:",
          file=f)
    best_parameters = gsearch.best_params_
    for param_name in sorted(param_test.keys()):
        print("\t%s: %r" % (param_name, best_parameters[param_name]),
              file=f)


data_file_path = "../mock/mock.csv"
df = pd.read_csv(data_file_path,
                 header=0,
                 index_col=0)

y_name = "result"
X, Y = df, df.pop(y_name)

# clf = MLPClassifier()
# param_test = {
#     "hidden_layer_sizes": [(30, 30), (10, 10), (50, 50)],
#     "max_iter": [200, 500, 1000, 2000]
# }
clf = RandomForestClassifier()


param_test = {
    "max_depth": [None, 10, 20, 30],
    "n_estimators": [5, 10, 20, 50, 100]
}
grid_search_cv = GridSearchCV(clf,
                              param_grid=param_test,
                              cv=10)

grid_search_cv.fit(X=X, y=Y)
print_best_score(grid_search_cv, param_test)

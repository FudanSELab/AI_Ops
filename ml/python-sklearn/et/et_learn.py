import pandas as pd
from sklearn.ensemble import ExtraTreesClassifier
from sklearn.model_selection import GridSearchCV
from sklearn.utils import shuffle

file_path = "../final_after_dimensionality_reduction.csv"


def print_best_score(gsearch, param_test):
    print("Best score: %0.3f" % gsearch.best_score_)
    print("Best parameters set:")
    best_parameters = gsearch.best_estimator_.get_params()
    for param_name in sorted(param_test.keys()):
        print("\t%s: %r" % (param_name, best_parameters[param_name]))


features_label = pd.read_csv(file_path, header=0, index_col=None)
features_label = shuffle(features_label)

X, Y = features_label, features_label.pop("y")

clf = ExtraTreesClassifier(max_depth=None,
                           min_samples_split=2,
                           random_state=0)

param_test = {
    'n_estimators': [1, 2, 3, 4, 5, 6, 10, 15, 20, 25, 30],
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

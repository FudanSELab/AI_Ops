import pandas as pd
from sklearn.ensemble import ExtraTreesClassifier
import sklearn.model_selection as sk_model_selection
from sklearn.utils import shuffle

file_path = "../final_after_dimensionality_reduction.csv"

features_label = pd.read_csv(file_path, header=0, index_col=None)
features_label = shuffle(features_label)

X, Y = features_label, features_label.pop("y")

clf = ExtraTreesClassifier(n_estimators=10,
                           max_depth=None,
                           min_samples_split=2,
                           random_state=0)

accs = sk_model_selection.cross_val_score(clf, X, y=Y, scoring=None, cv=5, n_jobs=1)

print("#X:", len(X.values))
print("#Y:", len(Y))

print("Cross Validator Result:", sorted(accs, reverse=True))

# result = clf.predict(X)
#
# count = 0
#
# for i in range(len(result)):
#     print(str(result[i]) + " - " + str(Y[i]))
#     if result[i] == Y[i]:
#         count += 1
# print(str(count) + " : " + str(len(result)))

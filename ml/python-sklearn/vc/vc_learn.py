import pandas as pd
from sklearn.linear_model import LogisticRegression
from sklearn.naive_bayes import GaussianNB
from sklearn.ensemble import RandomForestClassifier, VotingClassifier
import sklearn.model_selection as sk_model_selection
from sklearn.neural_network import MLPClassifier
from sklearn.utils import shuffle

file_path = "../y_result_after_dimensionality_reduction.csv"
log_file_path = "../log/vc/log_y_result.txt"
y_name = "new_trace_y.y_exec_result"

# file_path = "../y_ms_after_dimensionality_reduction.csv"
# log_file_path = "../log/vc/log_y_ms.txt"
# y_name = "new_trace_y.y_issue_ms"

# file_path = "../y_dimension_after_dimensionality_reduction.csv"
# log_file_path = "../log/vc/log_y_dimension.txt"
# y_name = "new_trace_y.y_issue_dim_type"

# file_path = "../final_after_dimensionality_reduction.csv"

f = open(log_file_path, 'w+')

features_label = pd.read_csv(file_path, header=0, index_col=None)
features_label = shuffle(features_label)

clf1 = MLPClassifier()

clf2 = RandomForestClassifier(n_estimators=50, random_state=1)

clf3 = GaussianNB()

eclf1 = VotingClassifier(
    estimators=[
        ('lr', clf1), ('rf', clf2), ('gnb', clf3)
    ],
    voting='hard')

X, Y = features_label, features_label.pop(y_name)

accs = sk_model_selection.cross_val_score(eclf1, X, y=Y, scoring=None, cv=10, n_jobs=1)

print("#X:", len(X.values), file=f)
print("#Y:", len(Y), file=f)

print("Cross Validator Result:", sorted(accs, reverse=True), file=f)


# eclf1 = eclf1.fit(X, Y)
#
# result = eclf1.predict(X)
#
# count = 0
#
# for i in range(len(result)):
#     print(str(result[i]) + " - " + str(Y[i]))
#     if result[i] == Y[i]:
#         count += 1
# print(str(count) + " : " + str(len(result)))
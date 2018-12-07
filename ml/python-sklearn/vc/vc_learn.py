import pandas as pd
from sklearn.linear_model import LogisticRegression
from sklearn.naive_bayes import GaussianNB
from sklearn.ensemble import RandomForestClassifier, VotingClassifier
import sklearn.model_selection as sk_model_selection
from sklearn.utils import shuffle


file_path = "../final_after_dimensionality_reduction.csv"

features_label = pd.read_csv(file_path, header=0, index_col=None)
features_label = shuffle(features_label)

clf1 = LogisticRegression(solver='lbfgs',
                          multi_class='multinomial',
                          random_state=1)

clf2 = RandomForestClassifier(n_estimators=50, random_state=1)

clf3 = GaussianNB()

eclf1 = VotingClassifier(
    estimators=[
        ('lr', clf1), ('rf', clf2), ('gnb', clf3)
    ],
    voting='hard')

X, Y = features_label, features_label.pop("y")

accs = sk_model_selection.cross_val_score(eclf1, X, y=Y, scoring=None, cv=5, n_jobs=1)

print("#X:", len(X.values))
print("#Y:", len(Y))

print("Cross Validator Result:", sorted(accs, reverse=True))


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
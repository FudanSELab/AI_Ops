import pandas as pd
from sklearn.ensemble import ExtraTreesClassifier

features_label = pd.read_csv("mock4.csv", header=0, index_col=None)
X, Y = features_label, features_label.pop("y1")

clf = ExtraTreesClassifier(n_estimators=10,
                           max_depth=None,
                           min_samples_split=2,
                           random_state=0)
clf.fit(X, Y)

result = clf.predict(X)

count = 0

for i in range(len(result)):
    print(str(result[i]) + " - " + str(Y[i]))
    if result[i] == Y[i]:
        count += 1
print(str(count) + " : " + str(len(result)))

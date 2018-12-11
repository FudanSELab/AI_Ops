import pandas as pd
from sklearn.ensemble import GradientBoostingClassifier

features_label = pd.read_csv("../mock4.csv", header=0, index_col=0)
X, Y = features_label, features_label.pop("y1")

clf = GradientBoostingClassifier(n_estimators=100,
                                 learning_rate=1.0,
                                 max_depth=1,
                                 random_state=0)
clf.fit(X, Y)

result = clf.predict(X)

count = 0

for i in range(len(result)):
    print(str(result[i]) + " - " + str(Y[i]))
    if result[i] == Y[i]:
        count += 1
print(str(count) + " : " + str(len(result)))

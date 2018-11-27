import pandas as pd
from sklearn.linear_model import LogisticRegression
from sklearn.naive_bayes import GaussianNB
from sklearn.ensemble import RandomForestClassifier, VotingClassifier

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

features_label = pd.read_csv("../mock4.csv", header=0, index_col=None)
X, Y = features_label, features_label.pop("y1")

eclf1 = eclf1.fit(X, Y)

result = eclf1.predict(X)

count = 0

for i in range(len(result)):
    print(str(result[i]) + " - " + str(Y[i]))
    if result[i] == Y[i]:
        count += 1
print(str(count) + " : " + str(len(result)))
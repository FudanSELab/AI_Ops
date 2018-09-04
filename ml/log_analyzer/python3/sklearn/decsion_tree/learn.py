from sklearn import tree
import load_data
import graphviz # doctest: +SKIP

(train_x, train_y), (test_x, test_y) = load_data.load_data()

clf = tree.DecisionTreeClassifier()

clf = clf.fit(train_x, train_y)

#
# dot_data = tree.export_graphviz(clf, out_file=None) # doctest: +SKIP
# graph = graphviz.Source(dot_data) # doctest: +SKIP
# graph.render("iris") # doctest: +SKIP

result = clf.predict(test_x)
result = clf.predict_proba(test_x)

for key in result:
    print(key)











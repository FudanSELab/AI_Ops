from sklearn import tree
import load_data
import graphviz
from sklearn.externals.six import StringIO
import pydotplus

(train_x, train_y), (test_x, test_y) = load_data.load_data()

clf = tree.DecisionTreeClassifier()

clf = clf.fit(train_x, train_y)

#
# dot_data = tree.export_graphviz(clf, out_file=None) # doctest: +SKIP
# graph = graphviz.Source(dot_data) # doctest: +SKIP
# graph.render("iris") # doctest: +SKIP


feature_name = ['service1_inst',
                'service2_inst',
                'service3_inst',
                'service1_mem',
                'service2_mem',
                'service3_mem',
                'time_span']
target_name = ['Success', 'Failure']


dot_data = StringIO()
tree.export_graphviz(clf,
                     out_file = dot_data,
                     feature_names=feature_name,
                     class_names=target_name,
                     filled=True,
                     rounded=True,
                     special_characters=True)
graph = pydotplus.graph_from_dot_data(dot_data.getvalue())
graph.write_pdf("dst.pdf")


result = clf.predict(test_x)

count = 0

for i in range(len(result)):
    print(str(result[i]) + " - " + str(test_y[i]))
    if result[i] == test_y[i]:
        count += 1
print(str(count) + " : " + str(len(result)))













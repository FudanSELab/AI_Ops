import pandas as pd

# ml_name = ["rf", "knn", "mlp"]
# target_type = ["LE", "MS", "FT", "TOTAL"]
from sklearn.ensemble import RandomForestClassifier
from sklearn.neighbors import KNeighborsClassifier
from sklearn.neural_network import MLPClassifier
import preprocessing_set


def big_model(tf_file_path, fault_file_path, model_2_file_path,
              ml_name):

    clf_LE = None
    clf_MS = None
    clf_FT = None
    clf_Model2 = None

    # Train LE Model
    print("LE Model训练开始")
    y_le = "y_final_result"
    df_tf_all = pd.read_csv(tf_file_path, header=0, index_col="trace_id")
    df_tf_all.pop("y_issue_ms")
    df_tf_all.pop("y_issue_dim_type")
    df_tf_all.pop("trace_api")
    df_tf_all.pop("trace_service")
    le_train_x, le_train_y = preprocessing_set.convert_y_multi_label_by_name(df_tf_all, y_le)
    if ml_name == "rf":
        print("Big Model", "LE", "RF")
        clf_LE = RandomForestClassifier(min_samples_leaf=1200, n_estimators=10)
    elif ml_name == "knn":
        print("Big Model", "LE", "KNN")
        clf_LE = KNeighborsClassifier(n_neighbors=200)
    else:
        print("Big Model", "LE", "MLP")
        clf_LE = MLPClassifier(hidden_layer_sizes=[10, 10], max_iter=200)
    clf_LE.fit(le_train_x, le_train_y)
    print("LE Model训练完毕")

    # Train MS Model
    print("MS Model训练开始")
    y_ms = "y_issue_ms"
    df_fault_all_ms = pd.read_csv(fault_file_path, header=0, index_col="trace_id")
    df_fault_all_ms.pop("y_issue_dim_type")
    ms_train_x, ms_train_y = preprocessing_set.convert_y_multi_label_by_name(df_fault_all_ms, y_ms)
    if ml_name == "rf":
        print("Big Model", "MS", "RF")
        clf_MS = RandomForestClassifier(min_samples_leaf=1200, n_estimators=10)
    elif ml_name == "knn":
        print("Big Model", "MS", "KNN")
        clf_MS = KNeighborsClassifier(n_neighbors=200)
    else:
        print("Big Model", "MS", "MLP")
        clf_MS = MLPClassifier(hidden_layer_sizes=[10, 10], max_iter=200)
    clf_MS.fit(X=ms_train_x, y=ms_train_y)
    print("MS Model训练结束")

    # Train FT Model
    print("FT Model训练开始")
    y_ft = "y_issue_dim_type"
    df_fault_all_ft = pd.read_csv(fault_file_path, header=0, index_col="trace_id")
    df_fault_all_ft.pop("y_issue_ms")
    ft_train_x, ft_train_y = preprocessing_set.convert_y_multi_label_by_name(df_fault_all_ft, y_ft)
    if ml_name == "rf":
        print("Big Model", "FT", "RF")
        clf_FT = RandomForestClassifier(min_samples_leaf=1200, n_estimators=10)
    elif ml_name == "knn":
        print("Big Model", "FT", "KNN")
        clf_FT = KNeighborsClassifier(n_neighbors=200)
    else:
        print("Big Model", "FT", "MLP")
        clf_FT = MLPClassifier(hidden_layer_sizes=[10, 10], max_iter=200)
    clf_FT.fit(X=ft_train_x, y=ft_train_y)
    print("FT Model训练结束")

    # Train Model_2
    # TODO
    print("Model2 Model训练开始")
    y_model2 = "y_issue_dim_type"
    df_model2_all = pd.read_csv(model_2_file_path, header=0, index_col="trace_id")
    df_model2_all.pop("y_issue_ms")
    model2_train_x, model2_train_y = preprocessing_set.convert_y_multi_label_by_name(df_model2_all, y_model2)
    if ml_name == "rf":
        print("Big Model", "MODEL_2", "RF")
        clf_Model2 = RandomForestClassifier(min_samples_leaf=1200, n_estimators=10)
    elif ml_name == "knn":
        print("Big Model", "MODEL_2", "KNN")
        clf_Model2 = KNeighborsClassifier(n_neighbors=200)
    else:
        print("Big Model", "MODEL_2", "MLP")
        clf_Model2 = MLPClassifier(hidden_layer_sizes=[10, 10], max_iter=200)
    clf_Model2.fit(X=model2_train_x, y=model2_train_y)
    print("Model2 Model训练结束")

    print("四个小模型训练完成，开始进行测试集训练")
    le_accuracy = 0.0
    ms_




from joblib import dump, load


def model_save(clf, file_path):
    print("Save Model in:", file_path)
    dump(clf, file_path)


def model_load(file_path):
    print("Load Model in:", file_path)
    clf = load(file_path)
    return clf

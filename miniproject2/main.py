# Code inspired by https://blog.sicara.com/naive-bayes-classifier-sklearn-python-example-tips-42d100429e44

# Ignore warnings from sklearn: https://stackoverflow.com/a/33616192
def warn(*args, **kwargs):
    pass


import warnings

warnings.warn = warn

import pandas as pd
from sklearn.naive_bayes import GaussianNB, BernoulliNB, MultinomialNB, ComplementNB
from sklearn.tree import DecisionTreeClassifier, DecisionTreeRegressor, ExtraTreeClassifier, ExtraTreeRegressor


def predict(dataset_number, name, algorithm):
    # Read the train file
    df = pd.read_csv("dataset/ds"+dataset_number+"/ds"+dataset_number+"Train.csv", header=None, sep=',')

    # Everything but the last column for the train data (ie: all but last column of the CSV)
    X_train = df.iloc[:, :-1]

    # The actual value for the train data (ie: last column of the CSV)
    y_train = df.iloc[:, -1]

    # Read the validation file
    df2 = pd.read_csv("dataset/ds"+dataset_number+"/ds"+dataset_number+"Val.csv", header=None, sep=',')

    # Everything but the last column for the validation data (ie: all but last column of the CSV)
    X_validate = df2.iloc[:, :-1]

    # The actual value for the validation data (ie: last column of the CSV)
    y_validate = df2.iloc[:, -1]

    # Actual values as array
    y_validate_values = y_validate.values

    # Counting number of lines of the validation file (1 line = 1 test)
    numberOfValidation = X_validate.shape[0]

    # Train classifier
    algorithm.fit(
        X_train,
        y_train
    )

    y_pred_gaussian = algorithm.predict(X_validate)

    # TODO: write results in results/*.csv

    print(name)
    print("Number of mislabeled points out of a total {} points : {}, performance {:05.2f}%"
        .format(
        numberOfValidation,
        (y_validate_values != y_pred_gaussian).sum(),
        100 * (1 - (y_validate_values != y_pred_gaussian).sum() / numberOfValidation)
    ))
    print(algorithm)
    print("\n")


# Dataset 1

# Gaussian Naive Bayes
# TODO: tune algorithm
# https://datascience.stackexchange.com/questions/36049/how-to-adjust-the-hyperparameters-of-mlp-classifier-to-get-more-perfect-performa
# http://scikit-learn.org/stable/modules/grid_search.html
# https://machinelearningmastery.com/how-to-tune-algorithm-parameters-with-scikit-learn/
predict("1", "Gaussian Naive Bayes", GaussianNB())

# Bernoulli Naive Bayes
predict("1", "Bernoulli Naive Bayes", BernoulliNB())

# Multinomial Naive Bayes
predict("1", "Multinomial Naive Bayes", MultinomialNB())

# Complement Naive Bayes
predict("1", "Complement Naive Bayes", ComplementNB())

# Decision Tree Classifier
predict("1", "Decision Tree Classifier", DecisionTreeClassifier())

# Decision Tree Regressor
predict("1", "Decision Tree Regressor", DecisionTreeRegressor())

# Extra Tree Classifier
predict("1", "Extra Tree Classifier", ExtraTreeClassifier())

# Extra Tree Regressor
predict("1", "Extra Tree Regressor", ExtraTreeRegressor())

# Dataset 2

# Gaussian Naive Bayes
predict("2", "Gaussian Naive Bayes", GaussianNB())

# Bernoulli Naive Bayes
predict("2", "Bernoulli Naive Bayes", BernoulliNB())

# Multinomial Naive Bayes
predict("2", "Multinomial Naive Bayes", MultinomialNB())

# Complement Naive Bayes
predict("2", "Complement Naive Bayes", ComplementNB())

# Decision Tree Classifier
predict("2", "Decision Tree Classifier", DecisionTreeClassifier())

# Decision Tree Regressor
predict("2", "Decision Tree Regressor", DecisionTreeRegressor())

# Extra Tree Classifier
predict("2", "Extra Tree Classifier", ExtraTreeClassifier())

# Extra Tree Regressor
predict("2", "Extra Tree Regressor", ExtraTreeRegressor())

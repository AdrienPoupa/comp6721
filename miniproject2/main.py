# Code inspired by https://blog.sicara.com/naive-bayes-classifier-sklearn-python-example-tips-42d100429e44
# Also: https://hub.packtpub.com/implementing-3-naive-bayes-classifiers-in-scikit-learn/

# Ignore warnings from sklearn: https://stackoverflow.com/a/33616192
def warn(*args, **kwargs):
    pass


import warnings

warnings.warn = warn

import pandas as pd
from sklearn.naive_bayes import GaussianNB, BernoulliNB, MultinomialNB, ComplementNB
from sklearn.tree import DecisionTreeClassifier, DecisionTreeRegressor, ExtraTreeClassifier, ExtraTreeRegressor


def predict(dataset_number, name, filename, algorithm):
    # Read the train file
    df = pd.read_csv("dataset/ds" + dataset_number + "/ds" + dataset_number + "Train.csv", header=None, sep=',')

    # Everything but the last column for the train data (ie: all but last column of the CSV)
    X_train = df.iloc[:, :-1]

    # The actual value for the train data (ie: last column of the CSV)
    y_train = df.iloc[:, -1]

    # Read the validation file
    df2 = pd.read_csv("dataset/ds" + dataset_number + "/ds" + dataset_number + "Val.csv", header=None, sep=',')

    # Everything but the last column for the validation data (ie: all but last column of the CSV)
    X_validate = df2.iloc[:, :-1]

    # The actual value for the validation data (ie: last column of the CSV)
    y_validate = df2.iloc[:, -1]

    # Actual values as array
    y_validate_values = y_validate.values

    # Train classifier
    algorithm.fit(
        X_train,
        y_train
    )

    # Predict the results
    y_pred_gaussian = algorithm.predict(X_validate)

    # Create a dataframe to write the result into a CSV
    csv_dataframe = pd.DataFrame(y_pred_gaussian)

    # Start the index at 1 rather than 0
    csv_dataframe.index = csv_dataframe.index + 1

    # Actually write the file
    csv_dataframe.to_csv("results/ds" + dataset_number + "Val-" + filename + ".csv", header=None)

    print(name)
    print("Number of mislabeled points out of a total {} points : {}, performance {:05.2f}%"
        .format(
        X_validate.shape[0],  # Counting number of lines of the validation file (1 line = 1 test)
        (y_validate_values != y_pred_gaussian).sum(),
        round(algorithm.score(X_validate, y_validate) * 100, 2)
    ))
    print(algorithm)
    print("\n")

# TODO: tune algorithm

# Dataset 1

# Gaussian Naive Bayes
# https://datascience.stackexchange.com/questions/36049/how-to-adjust-the-hyperparameters-of-mlp-classifier-to-get-more-perfect-performa
# http://scikit-learn.org/stable/modules/grid_search.html
# https://machinelearningmastery.com/how-to-tune-algorithm-parameters-with-scikit-learn/
# predict("1", "Gaussian Naive Bayes", "nb", GaussianNB())

# Bernoulli Naive Bayes
# predict("1", "Bernoulli Naive Bayes", "nb", BernoulliNB())

# Multinomial Naive Bayes
predict("1", "Multinomial Naive Bayes", "nb", MultinomialNB())

# # Complement Naive Bayes
# predict("1", "Complement Naive Bayes", "nb", ComplementNB())
#
# # Decision Tree Classifier
# predict("1", "Decision Tree Classifier", "dt", DecisionTreeClassifier())
#
# # Decision Tree Regressor
# predict("1", "Decision Tree Regressor", "dt", DecisionTreeRegressor())
#
# # Extra Tree Classifier
# predict("1", "Extra Tree Classifier", "dt", ExtraTreeClassifier())
#
# # Extra Tree Regressor
# predict("1", "Extra Tree Regressor", "dt", ExtraTreeRegressor())
#
# # Dataset 2
#
# # Gaussian Naive Bayes
# predict("2", "Gaussian Naive Bayes", "nb", GaussianNB())
#
# # Bernoulli Naive Bayes
# predict("2", "Bernoulli Naive Bayes", "nb", BernoulliNB())
#
# # Multinomial Naive Bayes
# predict("2", "Multinomial Naive Bayes", "nb", MultinomialNB())
#
# # Complement Naive Bayes
# predict("2", "Complement Naive Bayes", "nb", ComplementNB())
#
# # Decision Tree Classifier
# predict("2", "Decision Tree Classifier", "dt", DecisionTreeClassifier())
#
# # Decision Tree Regressor
# predict("2", "Decision Tree Regressor", "dt", DecisionTreeRegressor())
#
# # Extra Tree Classifier
# predict("2", "Extra Tree Classifier", "dt", ExtraTreeClassifier())
#
# # Extra Tree Regressor
# predict("2", "Extra Tree Regressor", "dt", ExtraTreeRegressor())

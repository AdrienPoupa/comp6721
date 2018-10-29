# Code inspired by https://blog.sicara.com/naive-bayes-classifier-sklearn-python-example-tips-42d100429e44
# Also: https://hub.packtpub.com/implementing-3-naive-bayes-classifiers-in-scikit-learn/

# Ignore warnings from sklearn: https://stackoverflow.com/a/33616192
import numpy


def warn(*args, **kwargs):
    pass


import warnings

warnings.warn = warn

import pandas as pd
from sklearn.naive_bayes import BernoulliNB
from sklearn.tree import DecisionTreeClassifier
from sklearn.model_selection import GridSearchCV, RandomizedSearchCV

# Fix the random seed so that RandomizedSearchCV does not change: https://stackoverflow.com/a/49146736/10017187
numpy.random.seed(0)

def predict(dataset_number, filename, algorithm):
    X_train, y_train = get_csv_data("ds" + dataset_number + "/ds" + dataset_number + "Train.csv")

    X_validate, y_validate = get_csv_data("ds" + dataset_number + "/ds" + dataset_number + "Val.csv")

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

    # print(algorithm)
    print("Number of mislabeled points out of a total {} points : {}, performance for the val set {:05.2f}%"
        .format(
        X_validate.shape[0],  # Counting number of lines of the validation file (1 line = 1 test)
        (y_validate_values != y_pred_gaussian).sum(),
        round(algorithm.score(X_validate, y_validate) * 100, 2)
    ))
    print("\n")


def get_csv_data(filename):
    # Read the csv file
    df = pd.read_csv("dataset/" + filename, header=None, sep=',')

    # Everything but the last column for the train data (ie: all but last column of the CSV)
    X = df.iloc[:, :-1]

    # The actual value for the train data (ie: last column of the CSV)
    y = df.iloc[:, -1]

    return X, y


def get_best_parameters(dataset_number, algorithm, param_grid):
    X_train, y_train = get_csv_data("ds" + dataset_number + "/ds" + dataset_number + "Train.csv")

    # Setup the parameters for the GridSearchCV, using the algorithm, the parameters
    # and a cross-validation splitting strategy of 5 (3 by default gives worse results)
    grid = GridSearchCV(estimator=algorithm, param_grid=param_grid, cv=5)
    #grid = RandomizedSearchCV(estimator=algorithm, param_distributions=param_grid, cv=5)

    # Fit GridSearchCV
    grid.fit(
        X_train,
        y_train
    )

    # Print the best score for the train set. It will not be equal to the val set:
    # https://stackoverflow.com/questions/30442259/why-does-not-gridsearchcv-give-best-score-scikit-learn
    print("Best params for the train set: " + str(grid.best_params_))
    print("Best possible score for the train set: " + str(round(grid.best_score_ * 100, 2)) + "%")

    return grid.best_params_


# Setup parameters to try in the grid search
# https://datascience.stackexchange.com/questions/36049/how-to-adjust-the-hyperparameters-of-mlp-classifier-to-get-more-perfect-performa
# http://scikit-learn.org/stable/modules/grid_search.html
# https://machinelearningmastery.com/how-to-tune-algorithm-parameters-with-scikit-learn/
alphas = [0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0]
param_grid_nb = {'alpha': alphas, 'fit_prior': [True, False]}

param_grid_dt = {'criterion': ["gini", "entropy"], 'splitter': ["best", "random"],
                 'presort': [True, False]}

# Dataset 1

# Without tuning: performance for the val set 59.92%
print("Bernoulli Naive Bayes")
best_parameters = get_best_parameters("1", BernoulliNB(), param_grid_nb)
predict("1", "nb", BernoulliNB(**best_parameters))

# Without tuning: performance for the val set 27.63%
print("Decision Tree Classifier")
best_parameters = get_best_parameters("1", DecisionTreeClassifier(), param_grid_dt)
predict("1", "dt", DecisionTreeClassifier(**best_parameters))


# Dataset 2

# Without tuning: performance for the val set 80.05%
print("Bernoulli Naive Bayes")
best_parameters = get_best_parameters("2", BernoulliNB(), param_grid_nb)
predict("2", "nb", BernoulliNB(**best_parameters))

# Without tuning: performance for the val set 76.95%
print("Decision Tree Classifier")
best_parameters = get_best_parameters("2", DecisionTreeClassifier(), param_grid_dt)
predict("2", "dt", DecisionTreeClassifier(**best_parameters))

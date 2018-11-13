# Code inspired by https://blog.sicara.com/naive-bayes-classifier-sklearn-python-example-tips-42d100429e44
# Also: https://hub.packtpub.com/implementing-3-naive-bayes-classifiers-in-scikit-learn/

# Ignore warnings from sklearn: https://stackoverflow.com/a/33616192


def warn(*args, **kwargs):
    pass


import warnings

warnings.warn = warn

import numpy
import pandas as pd
from sklearn.externals import joblib
from sklearn.metrics import accuracy_score
from sklearn.model_selection import GridSearchCV, RandomizedSearchCV
from sklearn.naive_bayes import BernoulliNB
from sklearn.neural_network import MLPClassifier
from sklearn.tree import DecisionTreeClassifier

# Fix the random seed so that RandomizedSearchCV does not change: https://stackoverflow.com/a/49146736/10017187
numpy.random.seed(0)


def train(dataset_number, filename, classifier):
    # Get X and y train
    X_train, y_train = get_csv_data("ds" + dataset_number + "/ds" + dataset_number + "Train.csv")

    # Train classifier
    classifier.fit(
        X_train,
        y_train
    )

    # Save model
    joblib.dump(classifier, "models/ds" + dataset_number + "Model-" + filename + ".joblib")

    return classifier


def load_classifier(dataset_number, filename):
    return joblib.load("models/ds" + dataset_number + "Model-" + filename + ".joblib")


def predict(dataset_number, filename, type, classifier):
    if type == 'Val':
        # Get the X and y validate
        X_validate, y_validate = get_csv_data("ds" + dataset_number + "/ds" + dataset_number + type + ".csv")

        # Actual values as array
        y_validate_values = y_validate.values
    else:
        X_validate = pd.read_csv("dataset/ds" + dataset_number + "/ds" + dataset_number + type + ".csv",
                                 header=None, sep=',')

    # Predict the results
    y_pred_gaussian = classifier.predict(X_validate)

    # Create a dataframe to write the result into a CSV
    csv_dataframe = pd.DataFrame(y_pred_gaussian)

    # Start the index at 1 rather than 0
    csv_dataframe.index = csv_dataframe.index + 1

    # Actually write the file
    csv_dataframe.to_csv("results/ds" + dataset_number + type + "-" + filename + ".csv", header=None)

    if type == 'Val':
        # Compute accuracy
        accuracy = accuracy_score(y_validate, y_pred_gaussian)

        # print(classifier)
        print("Number of mislabeled points out of a total {} points : {}, performance for the val set {:05.2f}%"
            .format(
            X_validate.shape[0],  # Counting number of lines of the validation file (1 line = 1 test)
            (y_validate_values != y_pred_gaussian).sum(),
            round(accuracy * 100, 2)
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


def get_best_parameters(dataset_number, classifier, param_grid):
    X_train, y_train = get_csv_data("ds" + dataset_number + "/ds" + dataset_number + "Train.csv")

    # Setup the parameters for the GridSearchCV, using the algorithm, the parameters
    # and a cross-validation splitting strategy of 5 (3 by default gives worse results)
    grid = GridSearchCV(estimator=classifier, param_grid=param_grid, cv=5)
    # grid = RandomizedSearchCV(estimator=classifier, param_distributions=param_grid, cv=5)

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


# Setup hyperparameters to try in the grid search
# https://datascience.stackexchange.com/questions/36049/how-to-adjust-the-hyperparameters-of-mlp-classifier-to-get-more-perfect-performa
# http://scikit-learn.org/stable/modules/grid_search.html
# https://machinelearningmastery.com/how-to-tune-algorithm-parameters-with-scikit-learn/

# Hyperparameters for Naive Bayes
alphas = [0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0]
param_grid_nb = {'alpha': alphas, 'fit_prior': [True, False]}

# Hyperparameters for Decision Tree
param_grid_dt = {'criterion': ['gini', 'entropy'],
                 'splitter': ['best', 'random'],
                 'presort': [True, False]}

# Hyperparameters for Neural Network
param_grid_mlp = {'activation': ['identity', 'logistic', 'tanh', 'relu'],
                  'solver': ['lbfgs', 'sgd', 'adam'],
                  'learning_rate': ['constant', 'invscaling', 'adaptive']}

#
# Dataset 1
#
print("Dataset 1")

# Without tuning: {'alpha': 1.0, 'fit_prior': True}
#                 performance for the val set 59.92%
# With tuning: {'alpha': 0.1, 'fit_prior': False}
#                performance for the val set 60.12%
print("Bernoulli Naive Bayes")
# best_parameters = get_best_parameters("1", BernoulliNB(), param_grid_nb)
# classifier = train("1", "nb", BernoulliNB(**best_parameters))
classifier = load_classifier("1", "nb")
predict("1", "nb", "Val", classifier)
predict("1", "nb", "Test", classifier)

# Without tuning: {'criterion': 'gini', 'presort': False, 'splitter': 'best'}
#                 performance for the val set 27.63%
# With tuning: {'criterion': 'gini', 'presort': False, 'splitter': 'best'}
#                performance for the val set 29.77%
print("Decision Tree Classifier")
# best_parameters = get_best_parameters("1", DecisionTreeClassifier(), param_grid_dt)
# classifier = train("1", "dt", DecisionTreeClassifier(**best_parameters))
classifier = load_classifier("1", "dt")
predict("1", "dt", "Val", classifier)
predict("1", "dt", "Test", classifier)

# Without tuning: {'activation': 'relu', 'learning_rate': 'constant', 'solver': 'adam'}
#                 performance for the val set 63.42%
# With tuning: {'activation': 'logistic', 'learning_rate': 'invscaling', 'solver': 'adam'}
#                performance for the val set 64.98%
print("MLP Classifier")
# best_parameters = get_best_parameters("1", MLPClassifier(), param_grid_mlp)
# classifier = train("1", "mlp", MLPClassifier(**best_parameters))
classifier = load_classifier("1", "mlp")
predict("1", "mlp", "Test", classifier)

#
# Dataset 2
#
print("Dataset 2")

# Without tuning: {'alpha': 1.0, 'fit_prior': True}
#                 performance for the val set 80.05%
# With tuning: {'alpha': 0.0, 'fit_prior': True}
#                performance for the val set 80.35%
print("Bernoulli Naive Bayes")
# best_parameters = get_best_parameters("2", BernoulliNB(), param_grid_nb)
# classifier = train("2", "nb", BernoulliNB(**best_parameters))
classifier = load_classifier("2", "nb")
predict("2", "nb", "Val", classifier)
predict("2", "nb", "Test", classifier)

# Without tuning: {'criterion': 'gini', 'presort': False, 'splitter': 'best'}
#                 performance for the val set 76.95%
# With tuning: {'criterion': 'gini', 'presort': False, 'splitter': 'random'}
#                performance for the val set 77.05%
print("Decision Tree Classifier")
# best_parameters = get_best_parameters("2", DecisionTreeClassifier(), param_grid_dt)
# classifier = train("2", "dt", DecisionTreeClassifier(**best_parameters))
classifier = load_classifier("2", "dt")
predict("2", "dt", "Val", classifier)
predict("2", "dt", "Test", classifier)

# Without tuning: {'activation': 'relu', 'learning_rate': 'constant', 'solver': 'adam'}
#                 performance for the val set 88.40%
# With tuning: {'activation': 'logistic', 'learning_rate': 'constant', 'solver': 'adam'}
#                performance for the val set 90.55%
print("MLP Classifier")
# best_parameters = get_best_parameters("1", MLPClassifier(), param_grid_mlp)
# classifier = train("2", "mlp", MLPClassifier(**best_parameters))
classifier = load_classifier("2", "mlp")
predict("2", "mlp", "Val", classifier)
predict("2", "mlp", "Test", classifier)

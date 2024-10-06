import pandas as pd	
from datasets import load_dataset
from sklearn.model_selection import	train_test_split
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import accuracy_score, classification_report	
import joblib

# Load the IMDB dataset	
dataset	= load_dataset("imdb")	

# Convert the dataset to a pandas DataFrame	
train_df = pd.DataFrame(dataset['train'])	
test_df = pd.DataFrame(dataset['test'])	

# Combine train and test for simplicity (you might want to keep them separate in practice)
df = pd.concat([train_df, test_df], axis=0, ignore_index=True)

# Display the first few rows
print(df.head())

# Split the data into training and testing sets
X_train, X_test, y_train, y_test = train_test_split(df['text'], df['label'],
test_size=0.2, random_state=42)

# Convert text to numerical features using bag-of-words
vectorizer = CountVectorizer(max_features=5000) # Limit to top 5000 words
X_train_vectorized = vectorizer.fit_transform(X_train)
X_test_vectorized = vectorizer.transform(X_test)

# Train a logistic regression model
model = LogisticRegression(random_state=42, max_iter=1000)
model.fit(X_train_vectorized, y_train)

# Make predictions on the test set
y_pred = model.predict(X_test_vectorized)

# Evaluate the model
print("Accuracy:", accuracy_score(y_test, y_pred))
print("\nClassification Report:")
print(classification_report(y_test, y_pred))
# Function to predict sentiment
def predict_sentiment(text):
    # Vectorize the input text
    text_vectorized = vectorizer.transform([text])
    # Predict the sentiment
    prediction = model.predict(text_vectorized)
    # Return the sentiment label
    return "Positive" if prediction[0] == 1 else "Negative"

# Test the model with some example reviews
print("\nTesting the model:")
print("Sentiment:", predict_sentiment("This movie was fantastic! I really enjoyed it."))
print("Sentiment:", predict_sentiment("I didn't like this film at all. It was boring and poorly acted."))

# Save the model and vectorizer
joblib.dump(model, 'sentiment_model.joblib')
joblib.dump(vectorizer, 'vectorizer.joblib')
print("\nModel and vectorizer saved in the local directory.")
# Demonstrate loading and using the saved model
loaded_model = joblib.load('sentiment_model.joblib')
loaded_vectorizer = joblib.load('vectorizer.joblib')
print("\nTesting the loaded model:")
def predict_sentiment_loaded(text):
    text_vectorized = loaded_vectorizer.transform([text])
    prediction = loaded_model.predict(text_vectorized)
    return "Positive" if prediction[0] == 1 else "Negative"
print("Sentiment:", predict_sentiment_loaded("A great watch! Highly recommended."))
print("Sentiment:", predict_sentiment_loaded("Don't waste your time on this one."))
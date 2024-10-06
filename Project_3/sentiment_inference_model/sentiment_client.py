import requests
import sys

def analyze_sentiment(ip_address, text):
    url = f"http://{ip_address}:32000/predict"
    payload = {"text": text}
    try:
        response = requests.post(url, json=payload)
        response.raise_for_status()
        result = response.json()
        return result['sentiment']
    except requests.exceptions.RequestException as e:
        return f"An error occurred: {e}"

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: python sentiment_client.py <ec2-public-ip> \"<text-to-analyze>\"")
        sys.exit(1)
    ip_address = sys.argv[1]
    text = sys.argv[2]
    sentiment = analyze_sentiment(ip_address, text)
    print(f"Text: {text}")
    print(f"Sentiment: {sentiment}")
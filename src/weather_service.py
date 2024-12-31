import requests
def get_weather(city):
    response = requests.get(f"http://api.weather.com/{city}")
    return response.json()

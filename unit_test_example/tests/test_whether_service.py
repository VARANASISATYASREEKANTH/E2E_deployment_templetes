import unittest
from unittest.mock import patch
from weather_service import get_weather

class TestWeatherService(unittest.TestCase):

    @patch('weather_service.requests.get')
    def test_get_weather(self, mock_get):
        mock_response = {'temp': 72, 'city': 'San Francisco'}
        mock_get.return_value.json.return_value = mock_response

        result = get_weather('San Francisco')
        self.assertEqual(result, mock_response)

if __name__ == '__main__':
    unittest.main()

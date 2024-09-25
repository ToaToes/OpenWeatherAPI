package com.example.weatherapp

import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Website.URL
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.weatherapp.ui.theme.WeatherAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class MainActivity : ComponentActivity() {

    private val apiKey = "c924bda618f6702916db8816683b1ac2"
    private val city = "Toronto"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            WeatherApp(apiKey, city)
        }
    }
}

@Composable
fun WeatherApp(apiKey: String, city: String) {

   var weatherInfo by remember{ mutableStateOf("Loading")}
        LaunchedEffect(Unit) {
            fetchWeather(apiKey, city){
                info ->
                weatherInfo = info
            }
        }
       Surface(
           modifier = Modifier.fillMaxSize(),
           color = MaterialTheme.colorScheme.background
       ){
           Box(
               contentAlignment = Alignment.Center,
               modifier = Modifier.fillMaxSize()
           ){
               Text(
                   text = weatherInfo,
                   style = MaterialTheme.typography.bodyLarge
               )
           }
       }
}

private suspend fun fetchWeather(apiKey: String, city: String, callback: (String) -> Unit){

    val response: String? = withContext(Dispatchers.IO) {
        try {
            URL("https://api.openweathermap.org/data/2.5/weather?q=$city,uk&APPID=$apiKey&units=metric")
                .readText(Charsets.UTF_8)
        } catch (e: java.io.FileNotFoundException) {
            Log.e("Weather", "City not Found or API key is invalid", e)
            null
        } catch (e: Exception) {
            Log.e("Weather App", "Error fetching weather data", e)
            null
        }
    }

    response?.let {
        try {
            val jsonObj = JSONObject(it)
            val main = jsonObj.getJSONObject("main")
            val temp = main.getString("temp")
            val weatherDescription =
                jsonObj.getJSONArray("weather").getJSONObject(0).getString("description")
            val weatherInfo = "Temperature: $temp \nDescription: $weatherDescription"
            callback(weatherInfo)
        }catch(e: Exception){
            Log.e("WeatherApp", "Error parsing weather data", e)
            callback("Failed to receive weather data")
        }
    }?:callback("Failed to receive weather data")

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
}
package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.notification.Condition
import android.widget.SearchView
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fetchWeatherData("Lahore")
        SearchCity()
    }

    private fun SearchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query!=null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityName:String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(apiInterface::class.java)
        val response = retrofit.getWeatherData(cityName,"ebb527fae89b2f8eac015b6fbb563b21", "matric")
        response.enqueue(object: Callback<WeatherApp>{

            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody!=null){
                    val temperatureKelvin = responseBody.main.temp
                    val temperatureCelsius = kelvinToCelsius(temperatureKelvin)
                    val temperatureCelsiusFormatted = "%.2f".format(temperatureCelsius)
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?:"unknown"
                    val maxTempKelvin = responseBody.main.temp_max
                    val minTempKelvin = responseBody.main.temp_min

                    val maxTempCelsius = kelvinToCelsius(maxTempKelvin)
                    val minTempCelsius = kelvinToCelsius(minTempKelvin)

                    val maxTempCelsiusFormatted = "%.2f".format(maxTempCelsius)
                    val minTempCelsiusFormatted = "%.2f".format(minTempCelsius)

                    binding.temp.text = "$temperatureCelsiusFormatted °C"
                    binding.weather.text = condition
                    binding.maxTemp.text = "Max temp: $maxTempCelsiusFormatted °C"
                    binding.minTemp.text = "Min temp: $minTempCelsiusFormatted °C"
                    binding.humidity.text = "$humidity %"
                    binding.windSpeed.text = "$windSpeed m/s"
                    binding.sunSet.text = "${time(sunSet)}"
                    binding.sunRise.text = "${time(sunRise)}"
                    binding.sea.text = "$seaLevel hPa"
                    binding.conditions.text = "$condition"
                    binding.day.text =dayName(System.currentTimeMillis())
                        binding.date.text=date()
                        binding.cityName.text="$cityName"

                    changeImagesAccordingToWeatherCondition(condition)
                }

            }


            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {

            }

        })

    }

    private fun changeImagesAccordingToWeatherCondition(condition: String) {
        when (condition){
            "Fogy","Mist","Overcast","Clouds","Partly Clouds"->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)

            }
            "Clear Sky","Clear","Sunny"->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow","Heavy Snow","Moderate Snow","Blizzard"->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }


        }
        binding.lottieAnimationView.playAnimation()
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("EEEE",Locale.getDefault())
        return sdf.format(Date())
    }

    private fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm",Locale.getDefault())
        return sdf.format(Date(timestamp*1000))
    }

    fun dayName(timestamp:Long):String{
        val sdf = SimpleDateFormat("dd MMMM yyyy",Locale.getDefault())
        return sdf.format(Date())
    }
    fun kelvinToCelsius(kelvin: Double): Double {
        return kelvin - 273.15
    }


}
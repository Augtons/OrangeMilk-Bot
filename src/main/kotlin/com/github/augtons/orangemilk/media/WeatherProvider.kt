package com.github.augtons.orangemilk.media

import com.github.augtons.orangemilk.utils.httpGetString
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.springframework.stereotype.Service

@Service
class WeatherProvider {

    suspend fun getWeather(keyword: String, countryCode: Int? = 4982): String? {
        try {
            val url = "https://weathernew.pae.baidu.com/weathernew/pc?query=${keyword}天气&srcid=${countryCode ?: 4982}"

            val body = httpGetString(url)

            val positionJson = """(?<=data\["position"]=).*?(?=;)""".toRegex().find(body)?.value
            val weatherJson = """(?<=data\["weather"]=).*?(?=;)""".toRegex().find(body)?.value
            val featureJson = """(?<=data\["feature"]=).*?(?=;)""".toRegex().find(body)?.value
            val pm25Json = """(?<=data\["psPm25"]=).*?(?=;)""".toRegex().find(body)?.value
            val weather15DayData = """(?<=data\["weather15DayData"]=).*?(?=;)""".toRegex().find(body)?.value
            val temp15day = """(?<=data\["temperatureDayList"]=).*?(?=;)""".toRegex().find(body)?.value
            val temp15night = """(?<=data\["temperatureNightList"]=).*?(?=;)""".toRegex().find(body)?.value

            val position = Gson().fromJson(positionJson, Position::class.java)
            val weather = Gson().fromJson(weatherJson, Weather::class.java)
            val feature = Gson().fromJson(featureJson, Feature::class.java)
            val pm25 = Gson().fromJson(pm25Json, PM25::class.java)
            val weather15day = Gson().fromJson<MutableList<Day15Weather>>(
                weather15DayData,
                object : TypeToken<MutableList<Day15Weather>>() {}.type
            )
            val temp15Day = Gson().fromJson<MutableList<Day15Temperature>>(
                temp15day,
                object : TypeToken<MutableList<Day15Temperature>>() {}.type
            )
            val temp15Night = Gson().fromJson<MutableList<Day15Temperature>>(
                temp15night,
                object : TypeToken<MutableList<Day15Temperature>>() {}.type
            )

            val tomorrowWeather = weather15day[2]
            val tomorrowDayTemp = temp15Day[2].temperature.replace("°", "℃")
            val tomorrowNightTemp = temp15Night[2].temperature.replace("°", "℃")

            with(weather) {
//                |----------
//                |降水预报：
//                |  降水概率: $precipitation%, $precipitation_type
                return (
                        if (countryCode == null || countryCode == 4982) { // 中国
                            """
                        |成功获取“${position.country}-${position.city}”的天气
                        |当前天气状况：
                        |  ${this.weather}，$temperature℃。$wind_direction$wind_power
                        |  体感温度: $real_feel_temperature℃, $bodytemp_info
                        |  紫外线$uv, $uv_info。
                        |----------
                        |当前空气状况：
                        |  空气湿度：$humidity%, 能见度：$visibility m, 气压: ${pressure} hPa
                        |  污染状况：${pm25.level}, 'PM2.5': ${pm25.ps_pm25}
                        |==========
                        |今天：(日出${feature.sunriseTime}; 日落${feature.sunsetTime})
                        |  白天: $weather_day, $temperature_day℃。$wind_direction_day$wind_power_day
                        |  夜间: $weather_night, $temperature_night℃。$wind_direction_night$wind_power_night
                        |==========
                        |明天：
                        |  ${tomorrowWeather.weatherText}, 污染状况: ${tomorrowWeather.weatherPm25}
                        |  白天: $tomorrowDayTemp。${tomorrowWeather.weatherWind.windDirectionDay}${tomorrowWeather.weatherWind.windPowerDay}
                        |  夜间: $tomorrowNightTemp。${tomorrowWeather.weatherWind.windDirectionNight}${tomorrowWeather.weatherWind.windPowerNight}
                        """.trimMargin()

                        }else {
                            """
                        |成功获取“${position.country}-${position.city}”的天气
                        |当前天气状况：
                        |  ${this.weather}，$temperature℃。$wind_direction$wind_power
                        |----------
                        |当前空气状况：
                        |  空气湿度：$humidity%
                        |==========
                        |今天：(日出${feature.sunriseTime}; 日落${feature.sunsetTime})
                        |  白天: $weather_day, $temperature_day℃。$wind_direction_day$wind_power_day
                        |  夜间: $weather_night, $temperature_night℃。$wind_direction_night$wind_power_night
                        |==========
                        |明天：${tomorrowWeather.weatherText}
                        |  白天: $tomorrowDayTemp。${tomorrowWeather.weatherWind.windDirectionDay}${tomorrowWeather.weatherWind.windPowerDay}
                        |  夜间: $tomorrowNightTemp。${tomorrowWeather.weatherWind.windDirectionNight}${tomorrowWeather.weatherWind.windPowerNight}
                        """.trimMargin()
                        }
                        )
            }
        }catch (_: Exception){  // 注意避免无限循环递归
            // 如果是在中国搜的，就尝试一下全世界搜，否则返回null
            return if (countryCode == null || countryCode == 4982){
                getWeather(keyword, 4999)
            }else{
                null
            }
        }
    }

    data class Position(
        val city: String,
        val country: String
    )

    data class Weather(
        val update_time: String,
        val publish_time: String,// 发布时间 2022-02-24 17:50
        val bodytemp_info: String,//"人体感觉天气寒凉"
        val temperature: String,// "12"
        val wind_power: String,// "1级"
        val uv_info: String,
        val wind_direction: String,
        val uv: String,
        val site: String,
        val visibility: String,
        val humidity: String,
        val pressure: String,
        val uv_num: String,
        val weather: String,
        val dew_temperature: String,
        val prec_monitor_time: String,
        val precipitation_type: String,
        val wind_power_num: String,
        val wind_direction_num: String,
        val precipitation: String,
        val real_feel_temperature: String,

        val weather_day: String,
        val weather_night: String,
        val wind_direction_day: String,
        val wind_power_day: String,
        val wind_direction_night: String,
        val wind_power_night: String,
        val temperature_night: String,
        val temperature_day: String,
    )

    data class Feature(
        val humidity: String,
        val wind: String,
        val sunriseTime: String,
        val sunsetTime: String,
        val ultraviolet: String
    )

    data class PM25(
        val level: String,
        val ps_pm25: String
    )

    data class Day15Weather(
        val date: String,
        val formatWeek: String,
        val weatherWind: Wind,
        val weatherPm25: String,
        val weatherText: String
    ){
        data class Wind(
            val windDirectionDay: String,
            val windDirectionNight: String,
            val windPowerDay: String,
            val windPowerNight: String
        )
    }

    data class Day15Temperature(
        val temperature: String
    )
}
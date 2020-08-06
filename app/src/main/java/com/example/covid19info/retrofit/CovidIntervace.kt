package com.example.covid19info.retrofit

import com.example.covid19info.POJO.ResponSummary
import com.example.covid19info.POJO.ResponseCountry
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CovidIntervace {
    @GET("summary")
    suspend fun getSummary() : Response<ResponSummary>

    // Membuat path url untuk chart country
    // Tujuan membuat URL seperti ini
    // https://api.covid19api.com/dayone/country/indonesia
    // Bagian indonesia itu bisa diganti dengan nama negara lain
    // maka kita perlu membuatnya jadi variabel dinamis
    // variabel dinamis ditandai dengan lingkupan kurung kurawal berisi nama variabel { }
    @GET("dayone/country/{country_name}")

    // @Path berfungsi untuk mengubah nilai variabel menjadi nama negara yang diinputkan
    // Response-nya berupa list dari ResponseCountry karena data JSON jenisnya list
    suspend fun getCountryData(@Path("country_name") country_name: String) : Response<List<ResponseCountry>>
}
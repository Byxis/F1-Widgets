package fr.byxis.f1w.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class OpenF1Session(
    val session_key: Int,
    val session_name: String,
    val session_type: String,
    val date_start: String,
    val date_end: String,
    val country_name: String,
    val location: String
)

interface F1Service {
    @GET("sessions")
    suspend fun getSessionsByYear(
        @Query("year") year: Int
    ): List<OpenF1Session>
}

object NetworkClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openf1.org/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: F1Service = retrofit.create(F1Service::class.java)
}
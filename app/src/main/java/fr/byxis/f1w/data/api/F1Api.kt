package fr.byxis.f1w.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class OpenF1Session(
    val sessionKey: Int,
    val sessionName: String,
    val sessionType: String,
    val dateStart: String,
    val dateEnd: String,
    val countryName: String,
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
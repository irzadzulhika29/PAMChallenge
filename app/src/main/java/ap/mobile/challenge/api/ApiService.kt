package ap.mobile.challenge.api

import retrofit2.Call
import retrofit2.http.*

interface ApiService {

  @GET("history")
  fun getHistory(): Call<List<History>>

  @POST("history")
  fun addHistory(@Body history: History): Call<Void>

  @PUT("history")
  fun updateHistory(@Path("id") id: String, @Body history: History)
      : Call<Void>

  @DELETE("history")
  fun deleteHistory(@Query("id") id: String): Call<Void>

}
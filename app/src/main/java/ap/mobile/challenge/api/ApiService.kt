package ap.mobile.challenge.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

  @GET("history")
  fun getHistory(): Call<List<History>>

  @Headers("Prefer: return=representation")
  @POST("history")
  fun addHistory(@Body history: History): Call<List<History>>

  @PUT("history")
  fun updateHistory(@Path("id") id: String, @Body history: History)
      : Call<Void>

  @DELETE("history")
  fun deleteHistory(@Query("id") id: String): Call<Void>

}
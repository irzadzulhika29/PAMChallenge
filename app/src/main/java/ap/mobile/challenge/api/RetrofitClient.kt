package ap.mobile.challenge.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
  private const val BASE_URL =
    "https://yyyqwtjiapxgpavhnkzq.supabase.co/rest/v1/"
  private const val API_KEY =
    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inl5eXF3dGppYXB4Z3Bhdmhua3pxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjQyMzc5ODcsImV4cCI6MjA3OTgxMzk4N30.09bCPfQhJMQnHz08ke2stNbLsbniLNIT7fbCH8VyHmc"

  // val logging = HttpLoggingInterceptor().apply {
  //   setLevel(HttpLoggingInterceptor.Level.BODY) // Logs headers and body
  // }

  private val okHttpClient =
    OkHttpClient.Builder()
      .addInterceptor(
        object : Interceptor {
          override fun intercept(chain: Interceptor.Chain): Response {
            val request: Request = chain.request()
              .newBuilder()
              // .header("accept", "application/json")
              .header("apikey", API_KEY)
              .build()
            return chain.proceed(request)
          }
        }
      )
      .build()

  val apiService: ApiService by lazy {
    Retrofit.Builder()
      .baseUrl(BASE_URL)
      .addConverterFactory(GsonConverterFactory.create())
      .client(okHttpClient)
      .build()
      .create(ApiService::class.java)
  }
}
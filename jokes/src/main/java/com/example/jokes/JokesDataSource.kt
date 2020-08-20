package com.example.jokes

import com.example.jokes.mappers.JokeMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class JokesDataSource {

	private val api: JokesApi

	init {
		val client = createClient()
		val retrofit = createRetrofit(client)
		api = retrofit.create(JokesApi::class.java)
	}

	/**
	 * Returns a joke from an online jokes database. The api errors are ignored for simplicity purposes.
	 */
	suspend fun getJoke() = withContext(Dispatchers.IO) {
		val response = api.getRandomJoke()
		val body = response.body()
		val joke = body?.let {
			val mapper = JokeMapper()
			mapper.convertToJoke(body)
		}
		return@withContext joke
	}

	/**
	 * Creates an OkHttpClient client. A logger is added in a debug build.
	 */
	private fun createClient(): OkHttpClient {
		val builder = OkHttpClient.Builder()
		if (BuildConfig.DEBUG) {
			val loggingInterceptor = HttpLoggingInterceptor()
			loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
			builder.addInterceptor(loggingInterceptor)
		}
		val client = builder.build()
		return client
	}

	private fun createRetrofit(client: OkHttpClient): Retrofit {
		return Retrofit.Builder()
			.baseUrl(BASE_URL)
			.client(client)
			.addConverterFactory(MoshiConverterFactory.create())
			.build()
	}

	companion object {

		private const val BASE_URL = "http://api.icndb.com/jokes/"
	}
}

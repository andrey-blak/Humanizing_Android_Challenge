package com.example.jokes

import com.example.jokes.responses.GetRandomJokeResponse
import retrofit2.Response
import retrofit2.http.GET

internal interface JokesApi {

	@GET("random")
	suspend fun getRandomJoke(): Response<GetRandomJokeResponse>
}

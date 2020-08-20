package com.example.jokes.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class JokeValue(
	@Json(name = "joke")
	val joke: String
)

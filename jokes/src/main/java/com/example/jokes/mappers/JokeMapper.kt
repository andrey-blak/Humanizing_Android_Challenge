package com.example.jokes.mappers

import com.example.jokes.dto.Joke
import com.example.jokes.responses.GetRandomJokeResponse

internal class JokeMapper {
	fun convertToJoke(response: GetRandomJokeResponse): Joke {
		return with(response) {
			Joke(content = value.joke)
		}
	}
}

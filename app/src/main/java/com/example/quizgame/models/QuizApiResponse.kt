package com.example.quizgame.models

data class QuizApiResponse(
    val response_code: Int,
    val results: List<TriviaQuestion>
)
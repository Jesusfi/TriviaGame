package com.example.quizgame.Models

data class QuizApiResponse(
    val response_code: Int,
    val results: List<TriviaQuestion>
)
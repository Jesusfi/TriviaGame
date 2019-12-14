package com.example.quizgame.Interfaces

import com.example.quizgame.Models.QuizApiResponse

interface TriviaApiListener {
    fun onCompleteListerner(response: QuizApiResponse)
    fun onErrorListener()
}
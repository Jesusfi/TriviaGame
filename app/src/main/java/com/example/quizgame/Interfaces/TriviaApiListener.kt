package com.example.quizgame.Interfaces

import com.example.quizgame.models.QuizApiResponse

interface TriviaApiListener {
    fun onCompleteListerner(response: QuizApiResponse)
    fun onErrorListener()
}
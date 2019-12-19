package com.example.quizgame.screens.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quizgame.models.TriviaQuestion
import java.lang.IllegalArgumentException

class TriviaModelViewFactory (val triviaList: List<TriviaQuestion>, val hasTimerEnabled : Boolean): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(TriviaGameViewModel::class.java)){
            return TriviaGameViewModel(triviaList, hasTimerEnabled) as T
        }
        throw IllegalArgumentException("Not a TriviaGameModel")
    }
}
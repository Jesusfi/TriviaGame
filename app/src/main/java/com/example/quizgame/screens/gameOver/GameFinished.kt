package com.example.quizgame.screens.gameOver


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.quizgame.R
import com.example.quizgame.databinding.FragmentGameFinishedBinding
import com.example.quizgame.models.QuestionResult

/**
 * A simple [Fragment] subclass.
 */
class GameFinished : Fragment() {
    lateinit var binding: FragmentGameFinishedBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_game_finished, container, false
            )

        val args = GameFinishedArgs.fromBundle(arguments!!)

        binding.infoText.text =
            "Correctly answered \n${args.numberCorrectlyAnswered} questions"

        val items: Array<QuestionResult> = args.questionsPlayerAnswered
        val adapter =
            GameResultsAdapter(
                items,
                context
            )
        val layoutManager = LinearLayoutManager(context)
        binding.rvResults.adapter = adapter
        binding.rvResults.layoutManager = layoutManager


        (activity as AppCompatActivity).supportActionBar?.title = "Results"

        binding.playAgainButton.setOnClickListener {
            it.findNavController()
                .navigate(GameFinishedDirections.actionGameFinishedToTitleFragment())
        }

        return binding.root
    }


}

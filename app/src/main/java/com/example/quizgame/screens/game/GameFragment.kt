package com.example.quizgame.screens.game


import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

import com.example.quizgame.Interfaces.TriviaApiListener
import com.example.quizgame.models.QuizApiResponse
import com.example.quizgame.R
import com.example.quizgame.databinding.FragmentGameBinding
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_game.*

class GameFragment : Fragment() {

    private lateinit var binding: FragmentGameBinding
    private lateinit var handler: Handler

    private val correctSoundRef = R.raw.correct_choice_sound
    private val incorrectSoundRef =
        R.raw.incorrect_choice_sound

    private lateinit var correctAnswerPlayer: MediaPlayer
    private lateinit var incorrectAnswerPlayer: MediaPlayer

    private lateinit var viewModel: TriviaGameViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_game, container, false
        )
        binding.progressBar.isIndeterminate = true

        correctAnswerPlayer = MediaPlayer.create(context, correctSoundRef)
        incorrectAnswerPlayer = MediaPlayer.create(context, incorrectSoundRef)


        val args = GameFragmentArgs.fromBundle(arguments!!)

        val category = when (args.triviaCategory.toLowerCase()) {
            "arts" -> "&category=25"
            "general knowledge" -> "&category=9"
            "animals" -> "&category=27"
            "books" -> "&category=10"
            else -> ""
        }
        val url = "https://opentdb.com/api.php?amount=10${category}&type=multiple"



        loadQuestions(url, object : TriviaApiListener {
            override fun onCompleteListerner(response: QuizApiResponse) {
                binding.progressBar.isIndeterminate = false
                binding.progressBar.max = 30000

                val triviaModelViewFactory = TriviaModelViewFactory(response.results,args.isTimerOn)
                viewModel = ViewModelProviders.of(this@GameFragment, triviaModelViewFactory)
                    .get(TriviaGameViewModel::class.java)

                handler = Handler()

                setObservers()
                setOnClickListeners()
            }

            override fun onErrorListener() {

            }
        }, context)

        return binding.root

    }

    private fun setObservers() {
        viewModel.currentQuestion.observe(this, Observer { triviaQuestion ->
            binding.triviaQuestion = triviaQuestion
            resetRadioButtonBackground()
        })
        viewModel.currentShuffledList.observe(this, Observer {
            binding.possibleAnswers = it
        })
        viewModel.playerDecisionResult.observe(this, Observer {
            val isUserCorrect = it.first
            val indexOfCorrectAnswer = it.second

            if (isUserCorrect) {
                correctAnswerPlayer.start()
                highlightButtonCorrect(indexOfCorrectAnswer)
            } else {
                incorrectAnswerPlayer.start()
                highlightButtonCorrect(indexOfCorrectAnswer)
            }
        })
        viewModel.onGameOverEvent.observe(this, Observer {
            if(it){
                findNavController().navigate(GameFragmentDirections.actionGameFragmentToGameFinished(viewModel.listOfAnswersPlayerReached, viewModel.numberPlayerAnsweredCorrectly))
            }
        })
        viewModel.timeLeft.observe(this, Observer {
            binding.progressBar.progress = it.toInt()
        })
    }

    private fun resetRadioButtonBackground() {
        val radioButtonList: List<View> = listOf(answer_one, answer_two, answer_three, answer_four)

        for (view in radioButtonList) {
            val tempRadio = view as RadioButton
            tempRadio.setBackgroundResource(R.drawable.question_button)
        }
    }

    private fun highlightButtonCorrect(index: Int) {
        when (index) {
            0 -> binding.answerOne.setBackgroundResource(R.drawable.correct_answer_background)
            1 -> binding.answerTwo.setBackgroundResource(R.drawable.correct_answer_background)
            2 -> binding.answerThree.setBackgroundResource(R.drawable.correct_answer_background)
            3 -> binding.answerFour.setBackgroundResource(R.drawable.correct_answer_background)
        }
    }

    private fun highlightButtonIncorrect(index: Int) {

    }

    private fun setOnClickListeners() {
        val radioButtonList: List<View> = listOf(answer_one, answer_two, answer_three, answer_four)

        for (view in radioButtonList) {
            view.setOnClickListener {
                val radioButton = it as RadioButton
                disableRadioButtons(radioButtonList)

                viewModel.onUserAnswer(radioButton.text.toString())

                handler.postDelayed(Runnable {
                    viewModel.nextQuestion()
                    enableRadioButtons(radioButtonList)
                }, 1000L)
            }
        }
    }

// TODO Change function to use coroutines
    private fun loadQuestions(
        url: String,
        listener: TriviaApiListener,
        context: Context?
    ) {
        val queue = Volley.newRequestQueue(context)

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->
                val gson = Gson()

                val quizApiResponse: QuizApiResponse =
                    gson.fromJson(response, QuizApiResponse::class.java)

                listener.onCompleteListerner(quizApiResponse)
            },
            Response.ErrorListener {
                it.printStackTrace()
                listener.onErrorListener()
            })

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }


    private fun enableRadioButtons(listOfViews: List<View>) {
        for (view2 in listOfViews) {
            val tempRadio = view2 as RadioButton
            tempRadio.isEnabled = true
            tempRadio.isChecked = false
        }
    }

    private fun disableRadioButtons(listOfViews: List<View>) {
        for (view in listOfViews) {
            val tempRadio = view as RadioButton
            tempRadio.isEnabled = false
        }
    }


    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)
    }

    fun getRadioButtonReference(index: Int): RadioButton {
        return when (index) {
            0 -> binding.answerOne
            1 -> binding.answerTwo
            2 -> binding.answerThree
            else -> binding.answerFour
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        incorrectAnswerPlayer.stop()
        incorrectAnswerPlayer.reset()
        incorrectAnswerPlayer.release()

        correctAnswerPlayer.stop()
        correctAnswerPlayer.reset()
        correctAnswerPlayer.release()
    }

}

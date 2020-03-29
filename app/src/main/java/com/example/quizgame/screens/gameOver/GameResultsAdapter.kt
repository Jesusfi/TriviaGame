package com.example.quizgame.screens.gameOver

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizgame.R
import com.example.quizgame.models.QuestionResult
import kotlinx.android.synthetic.main.rv_results.view.*

class GameResultsAdapter(
    private val items: Array<QuestionResult>,
    private val context: Context?
) : RecyclerView.Adapter<GameResultsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val questionText: TextView = view.question_text
        val textUserAnswer: TextView = view.user_answer_text
        val correctAnswerText: TextView = view.correct_answer_text
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.rv_results, parent, false)
        return ViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = items[position]
        holder.questionText.text = "${currentItem.question}"
        holder.textUserAnswer.text = "${currentItem.userAnswer}"
        holder.correctAnswerText.text = "${currentItem.correctAnswer}"
    }
}
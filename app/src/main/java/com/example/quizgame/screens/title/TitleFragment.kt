package com.example.quizgame.screens.title


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.quizgame.R
import com.example.quizgame.databinding.FragmentTitleBinding

/**
 * A simple [Fragment] subclass.
 */
class TitleFragment : Fragment() {

    lateinit var binding: FragmentTitleBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_title, container, false)

        (activity as AppCompatActivity).supportActionBar?.title = ""

        setUpAdapter()

        binding.playButton.setOnClickListener {
            val category = binding.spinner.selectedItem.toString()
            val isTimerOn = binding.toggleButton2.isChecked

            it.findNavController().navigate(
                TitleFragmentDirections.actionTitleFragmentToGameFragment(
                    category,
                    isTimerOn
                )
            )
        }

        return binding.root
    }

    private fun setUpAdapter() {
        val adapter = ArrayAdapter.createFromResource(
            context!!,
            R.array.category_name,
            R.layout.spinnerr_item
        )
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = adapter
    }


}

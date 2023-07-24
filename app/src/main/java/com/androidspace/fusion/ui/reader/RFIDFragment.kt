package com.androidspace.fusion.ui.reader

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.androidspace.fusion.R

class RFIDFragment : Fragment() {

    companion object {
        fun newInstance() = RFIDFragment()
    }

    private lateinit var viewModel: RFIDViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_r_f_i_d, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RFIDViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
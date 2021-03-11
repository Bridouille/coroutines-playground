package com.coroutines.playground.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.coroutines.playground.R
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    val mainVm: MainVM by viewModels()

    lateinit var getCharBtn: Button
    lateinit var editText: TextInputEditText
    lateinit var queryRes: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getCharBtn = findViewById(R.id.cancel_previous)
        getCharBtn.setOnClickListener {
            mainVm.performAction(MainEvent.LoadButtonClicked)
        }
        editText = findViewById(R.id.edit_text)
        editText.doAfterTextChanged { text ->
            mainVm.performAction(MainEvent.TextChanged(text.toString()))
        }

        queryRes = findViewById(R.id.query_result)

        lifecycleScope.launchWhenCreated {
            mainVm.observeState().collect(::render)
        }
    }

    private fun render(vs: MainVs) {
        getCharBtn.text = if (vs.isLoading) {
            "Loading ..."
        } else {
            "Launch unique network request!"
        }
        queryRes.text = vs.query
    }
}
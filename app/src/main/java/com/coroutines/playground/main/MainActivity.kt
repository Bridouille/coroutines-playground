package com.coroutines.playground.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.coroutines.playground.R
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    val mainVm: MainVM by viewModels()

    lateinit var fetchStatusBtn: Button
    lateinit var loader: ProgressBar
    lateinit var errorTv: TextView
    lateinit var listRv: RecyclerView

    private val rvAdapter = StatusRVAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fetchStatusBtn = findViewById(R.id.fetch_status)
        loader = findViewById(R.id.loader)
        errorTv = findViewById(R.id.error)
        listRv = findViewById(R.id.status_list)

        listRv.apply {
            adapter = rvAdapter
        }

        lifecycleScope.launchWhenCreated {
            mainVm.observeState().onEach(::render).launchIn(lifecycleScope)
        }
    }

    private fun render(vs: MainVs) {
        Timber.d("vs=$vs")
        loader.isVisible = vs.isLoading
        errorTv.isVisible = vs.error != null
        errorTv.text = vs.error
        listRv.isVisible = vs.error == null && !vs.isLoading
        vs.status?.let {
            rvAdapter.submitList(it.components)
        }

        fetchStatusBtn.text = if (vs.isLoading) {
            "Cancel request"
        } else {
            "Fetch statuses"
        }
        fetchStatusBtn.setOnClickListener {
            mainVm.performAction(MainEvent.FetchButtonClicked(vs.isLoading))
        }
    }
}
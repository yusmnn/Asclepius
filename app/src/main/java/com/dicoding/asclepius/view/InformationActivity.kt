package com.dicoding.asclepius.view

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.R
import com.dicoding.asclepius.adapter.NewsAdapter
import com.dicoding.asclepius.data.remote.response.ArticlesItem
import com.dicoding.asclepius.databinding.ActivityInformationBinding
import com.dicoding.asclepius.viewmodel.InformationViewModel
import com.google.android.material.snackbar.Snackbar

class InformationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInformationBinding
    private val informationViewModel by viewModels<InformationViewModel>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        findViewById<ImageView>(R.id.arrow_back).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val layoutManager = LinearLayoutManager(this)
        binding.rvNews.layoutManager = layoutManager

        informationViewModel.listNews.observe(this) { news ->
            setNewsData(news)
        }

        informationViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        informationViewModel.snackbarText.observe(this) {
            it.getContentIfNotHandled()?.let { snackBarText ->
                Snackbar.make(
                    window.decorView.rootView,
                    snackBarText,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setNewsData(listNews: List<ArticlesItem>) {
        val adapter = NewsAdapter()
        binding.rvNews.adapter = adapter
        val listNewsFilter = listNews.filterNullImages()
        adapter.submitList(listNewsFilter)
    }

    private fun List<ArticlesItem>.filterNullImages(): List<ArticlesItem> {
        return this.filter { it.urlToImage != null }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
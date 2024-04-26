package com.dicoding.asclepius.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dicoding.asclepius.R
import com.dicoding.asclepius.adapter.BookmarkAdapter
import com.dicoding.asclepius.databinding.ActivityBookmarkBinding
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.factory.ViewModelFactory
import com.dicoding.asclepius.util.Result
import com.dicoding.asclepius.viewmodel.BookmarkViewModel

class BookmarkActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookmarkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val factory: ViewModelFactory =  ViewModelFactory.getInstance(this)
        val bookmarkViewModel: BookmarkViewModel by viewModels {
            factory
        }

        binding = ActivityBookmarkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        findViewById<ImageView>(R.id.arrow_back).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val adapter = BookmarkAdapter()
        binding.rvBookmark.adapter = adapter
        val layoutManager = LinearLayoutManager(this)
        binding.rvBookmark.layoutManager = layoutManager

        bookmarkViewModel.getAllBookmarksData().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is Result.Success -> {
                        if (result.data.isEmpty()) binding.bookmarkText.text = getString(R.string.no_bookmark_message)
                        else binding.bookmarkText.text = ""

                        binding.progressBar.visibility = View.GONE
                        val bookmarkData = result.data
                        adapter.submitList(bookmarkData)

                        adapter.setOnItemClickCallback(object : BookmarkAdapter.OnItemClickCallback {
                            override fun removeItem(position: Int) {
                                val item = bookmarkData[position]
                                bookmarkViewModel.delete(item.image)
                            }
                        })
                    }

                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(
                            this, getString(R.string.an_error) + result.error, Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}
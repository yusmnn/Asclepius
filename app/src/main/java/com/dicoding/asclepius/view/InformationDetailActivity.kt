package com.dicoding.asclepius.view


import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.remote.response.ArticlesItem
import com.dicoding.asclepius.databinding.ActivityInformationDetailBinding
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class InformationDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInformationDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInformationDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        @Suppress("DEPRECATION")
        val data = intent.getParcelableExtra<ArticlesItem>(DATA)


        if (data != null) {
            binding.newsTitle.text = data.title
            binding.publishedAt.text  = formatPublishedAt(data.publishedAt)
            binding.newsUrl.text = data.url
            binding.newsDescription.text = data.description
            Glide.with(this)
                .load(data.urlToImage)
                .into(binding.newsImage)


            val webView = binding.webView
            webView.visibility = View.VISIBLE

            val webSettings: WebSettings = webView.settings
            webSettings.javaScriptEnabled = true


            binding.newsUrl.setOnClickListener {
                val url = data.url
                if (url!!.isNotEmpty()) {
                    webView.visibility = View.VISIBLE
                    webView.loadUrl(url)
                } else {
                    Toast.makeText(this, "URL unavailable", Toast.LENGTH_SHORT).show()
                    binding.newsUrl.text = "URL unavailable"
                }
            }
        }

        findViewById<ImageView>(R.id.arrow_back).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }

    private fun formatPublishedAt(publishedAtString: String?): String {
        if (publishedAtString.isNullOrEmpty()) {
            return "Published date unavailable"
        }

        try {
            val formatter: DateFormat
            return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                val parsedDate = formatter.parse(publishedAtString)


                val outputFormatter = SimpleDateFormat("dd MMM yyyy, HH:mm")
                val formattedDate = outputFormatter.format(parsedDate)
                formattedDate
            } else {
                val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")
                val publishedAt = Instant.parse(publishedAtString)
                val formattedDate = formatter.format(publishedAt.atZone(ZoneId.systemDefault()))
                formattedDate
            }
        } catch (e: ParseException) {
            Log.e("InformationDetailActivity", "Error parsing publishedAt: $e")
            return "Invalid published date format"
        }
    }


    companion object {
        private const val DATA = "data_detail"
    }
}
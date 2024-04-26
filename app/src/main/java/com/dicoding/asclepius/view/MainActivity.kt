package com.dicoding.asclepius.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.RoundedCorner
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.dicoding.asclepius.R
import com.dicoding.asclepius.adapter.NewsAdapter
import com.dicoding.asclepius.data.remote.response.ArticlesItem
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.viewmodel.InformationViewModel
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import com.yalantis.ucrop.UCrop
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import java.io.File
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentImageUri: Uri? = null
    private val informationViewModel by viewModels<InformationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.analyzeButton.setOnClickListener {
            currentImageUri?.let {
                analyzeImage(it)
            } ?: run {
                showToast(getString(R.string.empty_image_warning))
            }
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


        binding.tvSeeMore.setOnClickListener {
            val intent = Intent(this, InformationActivity::class.java)
            startActivity(intent)
        }

        binding.fabBookmark.setOnClickListener BookmarkActivity@{
            val intent = Intent(this, BookmarkActivity::class.java)
            startActivity(intent)
        }

    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val fileName = getFileNameFromUri(uri)

            // Gabungkan nama file untuk keunikan
            val uniqueFileName = "$fileName.jpg"
            val destinationUri = Uri.fromFile(File(cacheDir, uniqueFileName))

            UCrop.of(uri, destinationUri)
                .withMaxResultSize(400, 400)
                .withAspectRatio(16f, 9f)
                .start(this)

        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }


    private fun getFileNameFromUri(uri: Uri): String? {
        val path = uri.path ?: return null
        val segments = path.split("/")
        return if (segments.isNotEmpty()) segments.last() else null
    }


    private fun showImage() {
        currentImageUri?.let {
            Picasso.get()
                .load(it)
                .transform(RoundedCornersTransformation(24, 0))
                .into(binding.previewImageView)
        }
    }

    private fun analyzeImage(uri: Uri) {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(ResultActivity.EXTRA_IMAGE_URI, currentImageUri.toString())
        startActivity(intent)
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            if (data != null) {
                currentImageUri = UCrop.getOutput(data)
                showImage()
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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

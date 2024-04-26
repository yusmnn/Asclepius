package com.dicoding.asclepius.view

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.factory.ViewModelFactory
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.dicoding.asclepius.viewmodel.ResultViewModel
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.text.NumberFormat

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private lateinit var label: String
    private lateinit var score: String

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        val resultViewModel: ResultViewModel by viewModels {
            factory
        }

        val image = intent.getStringExtra(EXTRA_IMAGE_URI)!!

        findViewById<ImageView>(R.id.arrow_back).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val imageUri = Uri.parse(image)
            imageUri?.let {
                Picasso.get()
                    .load(it)
                    .transform(RoundedCornersTransformation(24, 0))
                    .into(binding.resultImage)
                Log.d("Image URI", "showImage: $it")
            }

        classify(imageUri)

        resultViewModel.fetchBookmarkData(image).observe(this) { data ->
            if (data != null) {
                binding.fabFav.setImageResource(R.drawable.baseline_bookmark_24)
                binding.fabFav.setOnClickListener {
                    resultViewModel.delete(image)
                    Toast.makeText(
                        this, getString(R.string.successfully_removed_bookmark), Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                binding.fabFav.setImageResource(R.drawable.baseline_bookmark_border_24)
                binding.fabFav.setOnClickListener {
                    resultViewModel.insert(image, label, score)
                    Toast.makeText(
                        this, getString(R.string.successfully_added_bookmark), Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun classify(imageUri: Uri) {
        imageClassifierHelper = ImageClassifierHelper(
            context = this,
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    runOnUiThread {
                        Toast.makeText(this@ResultActivity, error, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                    runOnUiThread {
                        results?.let { it ->
                            if (it.isNotEmpty() && it[0].categories.isNotEmpty()) {

                                // Menemukan 'category' dengan skor tertinggi menggunakan maxBy
                                val highestScoringCategory =
                                    it[0].categories.maxByOrNull { it.score }

                                if (highestScoringCategory != null) {
                                    val displayResult = "${highestScoringCategory.label} " +
                                            NumberFormat.getPercentInstance()
                                                .format(highestScoringCategory.score).trim()

                                    binding.resultText.text = displayResult

                                    label = highestScoringCategory.label
                                    score = NumberFormat.getPercentInstance()
                                        .format(highestScoringCategory.score).trim()
                                } else {
                                    binding.resultText.text =
                                        getString(R.string.no_category_with_high_enough_score_found)
                                }
                            } else {
                                binding.resultText.text = ""
                            }
                        }
                    }
                }
            }
        )

        imageClassifierHelper.classifyStaticImage(imageUri)
    }


    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
    }

}
package com.dicoding.mystoryapp.ui.add_activity

import android.Manifest
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.dicoding.mystoryapp.databinding.ActivityAddStoryBinding
import com.dicoding.mystoryapp.db.UserAuth
import com.dicoding.mystoryapp.db.UserPreference
import com.dicoding.mystoryapp.helper.ViewModelFactory
import com.dicoding.mystoryapp.models.AddStoriesViewModel
import com.dicoding.mystoryapp.ui.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.*

class AddStoryActivity : AppCompatActivity() {

    private var _activityAddStoryBinding: ActivityAddStoryBinding? = null
    private val binding get() = _activityAddStoryBinding

    private var getFile: File? = null

    private lateinit var addStoriesViewModel: AddStoriesViewModel

    private lateinit var userAuth: UserAuth

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityAddStoryBinding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        userAuth()
        TOKEN = "Bearer " + userAuth.token

        // Pembuatan View Model
        addStoriesViewModel = obtainViewModel(this@AddStoryActivity)

        addStoriesViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        addStoriesViewModel.responseMessage.observe(this) {
            it.getContentIfNotHandled()?.let { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }

        addStoriesViewModel.addStoriesResponse.observe(this) { response ->

            if (response.error == false) {
                MainActivity.IS_FROM_ADDSTORY = true
                val intent = Intent(this@AddStoryActivity, MainActivity::class.java)
                startActivity(intent)
            }

        }

        playAnimation()
        cameraPermission()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding?.apply {
            btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
            btnCamera.setOnClickListener { openCameraX() }
            btnGallery.setOnClickListener { openGallery() }
            btnUpload.setOnClickListener { uploadImage(TOKEN.toString()) }
            tcLocation.setOnClickListener { getMyLastLocation() }
        }
    }

    private fun locationToString(location: Location): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addressList = geocoder.getFromLocation(location.latitude, location.longitude, 1)
        return addressList?.get(0)?.getAddressLine(0) ?: "Not Found"
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val userLocation = locationToString(location)

                    currentLocation = location
                    binding?.tcLocation?.text = userLocation
                } else {
                    Toast.makeText(
                        this@AddStoryActivity,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun uploadImage(authToken: String) {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val description =
                binding?.edDesc?.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            if (currentLocation != null) {

                addStoriesViewModel.uploadStory(
                    authToken,
                    imageMultipart,
                    description,
                    currentLocation?.latitude.toString().toRequestBody("text/plain".toMediaType()),
                    currentLocation?.longitude.toString().toRequestBody("text/plain".toMediaType())
                )

            } else {
                addStoriesViewModel.uploadStory(authToken, imageMultipart, description, null, null)
            }

        } else {
            Toast.makeText(
                this@AddStoryActivity,
                "Silakan masukkan berkas gambar terlebih dahulu.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@AddStoryActivity)
                getFile = myFile
                binding?.postImage?.setImageURI(uri)
            }
        }
    }

    private fun openGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra("picture", File::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.data?.getSerializableExtra("picture")
            } as? File

            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            myFile?.let { file ->
                rotateFile(file, isBackCamera)
                getFile = file
                binding?.postImage?.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private fun openCameraX() {
        val intent = Intent(this@AddStoryActivity, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding?.formContainer, View.TRANSLATION_Y, 2200f, 0f).apply {
            duration = 1400
        }.start()
    }

    private fun cameraPermission() {
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun showLoading(state: Boolean) {
        binding?.progressBar?.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun obtainViewModel(activity: AppCompatActivity): AddStoriesViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(AddStoriesViewModel::class.java)
    }

    private fun userAuth() {
        val userPref = UserPreference(this)
        userAuth = userPref.getUser()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    // Precise location access granted.
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Only approximate location access granted.
                    getMyLastLocation()
                }
                else -> {
                    // No location access granted.
                }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        var TOKEN: String? = null
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
        const val LOCATION_PERMISSION_REQUEST_CODE = 333
    }
}
package com.example.safeaid.screens.sos

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import com.example.androidtraining.R
import com.example.androidtraining.databinding.FragmentSosBinding
import kotlin.toString
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale


class SosFragment : Fragment(R.layout.fragment_sos) {

    private var _binding: FragmentSosBinding? = null
    private val binding get() = _binding!!

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: Location? = null

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> {
                // Permission granted
                getCurrentLocation()
            }
            else -> {
                // Permission denied
                Toast.makeText(
                    requireContext(),
                    "Cần quyền truy cập vị trí để hiển thị địa chỉ chính xác",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSosBinding.bind(view)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // start two pulses with phase offset
        startPulse(binding.pulse1, 0L)
        startPulse(binding.pulse2, 900L)

        binding.btnShare.setOnClickListener {
            shareLocation()
        }

        requestLocationPermission()
    }


    private fun startPulse(view: View, delay: Long) {
        // Remove any clipping constraints
        view.clipToOutline = false

        val scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 0.6f, 2.5f).apply {
            duration = 2000L
            startDelay = delay
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            interpolator = FastOutLinearInInterpolator()
        }

        val scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 0.6f, 2.5f).apply {
            duration = 2000L
            startDelay = delay
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            interpolator = FastOutLinearInInterpolator()
        }

        val alpha = ObjectAnimator.ofFloat(view, View.ALPHA, 0.9f, 0f).apply {
            duration = 2000L
            startDelay = delay
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            interpolator = FastOutLinearInInterpolator()
        }

        AnimatorSet().apply {
            playTogether(scaleX, scaleY, alpha)
            start()
        }
    }

    private fun requestLocationPermission() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                currentLocation = it
                getAddressFromLocation(it)
            }
        }
    }

    private fun getAddressFromLocation(location: Location) {
        try {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())

            @Suppress("DEPRECATION")
            geocoder.getFromLocation(location.latitude, location.longitude, 1)?.let { addresses ->
                if (addresses.isNotEmpty()) {
                    val address = addresses[0]

                    // Format address according to Vietnamese style
                    val formattedAddress = StringBuilder()

                    // Add street number and name (thoroughfare)
                    if (!address.featureName.isNullOrEmpty() &&
                        !address.featureName.equals(address.thoroughfare, ignoreCase = true) &&
                        !address.featureName.equals(address.subThoroughfare, ignoreCase = true)) {
                        formattedAddress.append(address.featureName).append(", ")
                    } else if (!address.subThoroughfare.isNullOrEmpty()) {
                        formattedAddress.append(address.subThoroughfare).append(" ")
                    }

                    // Add street name (thoroughfare)
                    if (!address.thoroughfare.isNullOrEmpty()) {
                        formattedAddress.append(address.thoroughfare).append(", ")
                    }

                    // Add ward/phường (subLocality)
                    if (!address.subLocality.isNullOrEmpty()) {
                        formattedAddress.append(address.subLocality).append(", ")
                    }

                    // Add district/quận/huyện (locality or subAdminArea)
                    if (!address.locality.isNullOrEmpty()) {
                        formattedAddress.append(address.locality).append(", ")
                    } else if (!address.subAdminArea.isNullOrEmpty()) {
                        formattedAddress.append(address.subAdminArea).append(", ")
                    }

                    // Add province/city/tỉnh/thành phố (adminArea)
                    if (!address.adminArea.isNullOrEmpty()) {
                        formattedAddress.append(address.adminArea).append(", ")
                    }

                    // Add country name
                    if (!address.countryName.isNullOrEmpty()) {
                        formattedAddress.append(address.countryName)
                    }

                    // Cleanup: remove trailing comma and spaces
                    var addressText = formattedAddress.toString().trim()
                    if (addressText.endsWith(",")) {
                        addressText = addressText.substring(0, addressText.length - 1)
                    }

                    if (addressText.isNotEmpty()) {
                        binding.tvAddress.text = addressText
                    } else {
                        // Fallback if formatted address is empty
                        binding.tvAddress.text = "Vị trí: ${location.latitude}, ${location.longitude}"
                    }
                }
            }
        } catch (e: Exception) {
            binding.tvAddress.text = "Vị trí: ${location.latitude}, ${location.longitude}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun shareLocation() {
        val address = binding.tvAddress.text.toString()

        // Only add coordinates if we have a valid location
        val locationCoords = currentLocation?.let {
            val lat = it.latitude
            val lng = it.longitude
            "$lat, $lng"
        } ?: ""

        // Create Google Maps direction link
        val googleMapsLink = currentLocation?.let {
            "https://www.google.com/maps/dir/?api=1&destination=${it.latitude},${it.longitude}"
        } ?: ""

        // Build the complete message with address, coordinates and maps link
        val shareText = buildString {
            append("SOS! Tôi đang cần trợ giúp khẩn cấp tại vị trí: $address\n")
            if (locationCoords.isNotEmpty()) {
                append("Tọa độ GPS: $locationCoords\n\n")
            }
            if (googleMapsLink.isNotEmpty()) {
                append("Chỉ đường đến vị trí của tôi: $googleMapsLink")
            }
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "SOS - Yêu cầu trợ giúp khẩn cấp")
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        val chooserIntent = Intent.createChooser(shareIntent, "Chia sẻ vị trí qua")
        startActivity(chooserIntent)
    }
}

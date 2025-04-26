package com.example.safeaid.screens.sos

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import com.example.androidtraining.R
import com.example.androidtraining.databinding.FragmentSosBinding

class SosFragment : Fragment(R.layout.fragment_sos) {

    private var _binding: FragmentSosBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSosBinding.bind(view)

        // start two pulses with phase offset
        startPulse(binding.pulse1, 0L)
        startPulse(binding.pulse2, 900L)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun startPulse(view: View, delay: Long) {
        val scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 1f, 2f).apply {
            duration    = 2000L
            startDelay  = delay
            repeatCount = ValueAnimator.INFINITE
            interpolator = FastOutLinearInInterpolator()
        }
        val scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f, 2f).apply {
            duration    = 2000L
            startDelay  = delay
            repeatCount = ValueAnimator.INFINITE
            interpolator = FastOutLinearInInterpolator()
        }
        val alpha = ObjectAnimator.ofFloat(view, View.ALPHA, 0.6f, 0f).apply {
            duration    = 2000L
            startDelay  = delay
            repeatCount = ValueAnimator.INFINITE
            interpolator = FastOutLinearInInterpolator()
        }

        AnimatorSet().apply {
            playTogether(scaleX, scaleY, alpha)
            start()
        }
    }
}

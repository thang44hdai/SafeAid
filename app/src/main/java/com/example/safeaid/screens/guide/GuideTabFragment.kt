package com.example.safeaid.screens.guide

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidtraining.databinding.FragmentGuideTabBinding
import com.example.safeaid.MainNavigator
import com.example.safeaid.core.ui.BaseFragment
import com.example.safeaid.screens.guide.viewmodel.GuideViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.example.safeaid.core.response.GuideStepResponse
import kotlin.getValue

@AndroidEntryPoint
class GuideTabFragment : BaseFragment<FragmentGuideTabBinding>() {
    private val mainNavigator: MainNavigator by activityViewModels()
    private val viewModel: GuideViewModel by activityViewModels()
    private val stepAdapter = StepAdapter { step ->
        val bundle = Bundle().apply {
            putParcelable("step", step)
        }
        mainNavigator.offerNavEvent(GoToStepDetail(bundle))
    }

    companion object {
        fun newInstance(steps: ArrayList<GuideStepResponse>): GuideTabFragment {
            val fragment = GuideTabFragment()
            val args = Bundle()
            args.putParcelableArrayList("steps", steps)
            fragment.arguments = args
            return fragment
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val steps = arguments?.getParcelableArrayList<GuideStepResponse>("steps")

        stepAdapter.submitList(steps)
    }

    override fun isHostFragment(): Boolean = false

    override fun onInit() {
        setupRecyclerView()
    }

    override fun onInitObserver() {}

    override fun onInitListener() {}

    private fun setupRecyclerView() {
        viewBinding.rvSteps.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = stepAdapter
        }
    }
}
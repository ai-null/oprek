package com.ainul.oprek.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.ainul.oprek.R
import com.ainul.oprek.adapter.ListItemAdapter
import com.ainul.oprek.adapter.listener.ListItemListener
import com.ainul.oprek.databinding.FragmentDashboardBinding
import com.google.android.material.transition.MaterialContainerTransform

class DashboardFragment : Fragment(), ListItemListener {

    private lateinit var binding: FragmentDashboardBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)

        val adapter = ListItemAdapter(this)
        val data = mutableListOf<String>()

        binding.recentProjectsList.adapter = adapter
        data.add("Halo")
        data.add("Halo1")
        data.add("Halo2")
        data.add("Halo3")
        data.add("Halo4")
        data.add("Halo5")
        data.add("Halo6")
        data.add("Halo7")
        data.add("Halo8")
        adapter.data = data

        return binding.root
    }

    override fun onClick(view: View) {
        val transitionName = resources.getString(R.string.item_detail_transition_name)
        val extras = FragmentNavigatorExtras(view to transitionName)
        this.findNavController()
            .navigate(R.id.action_dashboardFragment_to_detailProjectFragment)
    }
}
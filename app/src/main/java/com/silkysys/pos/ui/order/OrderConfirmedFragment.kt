package com.silkysys.pos.ui.order

import androidx.navigation.fragment.findNavController
import com.silkysys.pos.R
import com.silkysys.pos.databinding.FragmentOrderConfirmedBinding
import com.silkysys.pos.util.BaseFragment
import com.silkysys.pos.util.goTo


class OrderConfirmedFragment :
    BaseFragment<FragmentOrderConfirmedBinding>(FragmentOrderConfirmedBinding::inflate) {

    override fun FragmentOrderConfirmedBinding.initialize() {
        btnHome.setOnClickListener {
            findNavController().popBackStack()
            goTo(R.id.action_global_homeFragment)
        }
    }
}
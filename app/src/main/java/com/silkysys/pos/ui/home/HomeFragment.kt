package com.silkysys.pos.ui.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.TextView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.silkysys.pos.R
import com.silkysys.pos.data.local.CartDao
import com.silkysys.pos.data.local.Constants
import com.silkysys.pos.data.model.category.get.CategoriesResponse
import com.silkysys.pos.data.model.product.DataItem
import com.silkysys.pos.data.network.Resource
import com.silkysys.pos.databinding.FragmentHomeBinding
import com.silkysys.pos.ui.adapter.CategoriesAdapter
import com.silkysys.pos.ui.adapter.home.HomeAdapter
import com.silkysys.pos.util.*
import com.silkysys.pos.util.dialog.alertError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate),
    OnProductClick, View.OnClickListener, OnItemClick {


    @Inject
    lateinit var cartCounter: CartCounter

    @Inject
    lateinit var cartDao: CartDao

    private lateinit var viewModel: HomeViewModel
    private lateinit var tvCounter: TextView
    private lateinit var homeAdapter: HomeAdapter
    private var categoryId: Int? = null


    override fun FragmentHomeBinding.initialize() {
        initViews()
        handleLoadState()
        viewModel.apply {
            initGetCategories()
            Coroutines.main { getProducts().collectLatest { homeAdapter.submitData(it) } }
            categoriesResponse.observe(this@HomeFragment) { updateCategories(it) }
        }

        // Register scroll listener on recyclerview
        binding.rvHome.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) btnFab.visibility = VISIBLE
                else if (dy <= 0) btnFab.visibility = INVISIBLE
            }
        })
        binding.btnFab.setOnClickListener(this@HomeFragment)
    }


    override fun onClick(item: View) {
        if (item.id == R.id.btn_fab) {
            binding.rvHome.smoothScrollToPosition(0)
        } else {
            // Retry button in connection lost
            retryLoadingCategory()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.cart_menu, menu)
                val actionView = menu.findItem(R.id.action_cart).actionView
                tvCounter = actionView.findViewById(R.id.tv_cart_badge)

                // Retrieve badge cart counter from shared prefer
                if (cartCounter.counter() != 0) tvCounter.text = cartCounter.counter().toString()
                else tvCounter.visibility = INVISIBLE
                // Click listener
                actionView.setOnClickListener {
                    onMenuItemSelected(menu.findItem(R.id.action_cart))
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_cart -> {
                        goTo(R.id.action_homeFragment_to_cartFragment)
                        true
                    }
                    R.id.scan_barcode -> {
                        goTo(R.id.action_homeFragment_to_scannerFragment)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }


    override fun onProductClicked(model: DataItem, tvQuantity: TextView, operation: String) {
        val qty = tvQuantity.text.toString().toInt()
        // Add to cart
        if (operation == Constants.ADD) {
            Coroutines.background {
                val response = viewModel.initAddToCart(model, Constants.ADD)
                Coroutines.main {
                    if (response) {
                        // Increase qty on screen by 1
                        tvQuantity.text = qty.plus(1).toString()
                        // Update cart counter badge
                        cartCounter.editCounter(Constants.ADD, tvCounter, tvCounter)
                    } else requireContext().toast(getString(R.string.stock_empty))
                }
            }
        }
        // Remove 1 qty from a specific product in cart
        else {
            when (qty) {
                0 -> {
                    requireContext().toast(getString(R.string.invalid_qty))
                }
                1 -> {
                    // Delete item from cart
                    deleteOrRemove(model, tvQuantity, Constants.DEL)
                    cartCounter.editCounter(Constants.SUB, tvCounter, tvCounter)
                }
                else -> {
                    // Decrease 1 qty from item
                    deleteOrRemove(model, tvQuantity, Constants.SUB)
                    cartCounter.editCounter(Constants.SUB, tvCounter, tvCounter)
                }
            }
        }
    }


    override fun <T> onClicked(model: T, qty: Int) {
        // Get a specific category from products
        binding.apply {
            model as com.silkysys.pos.data.model.category.get.DataItem
            // Hide empty state if it's visible after fetching a category
            if (tvNoProducts.isVisible && ivNoProducts.isVisible) {
                tvNoProducts.visibility = INVISIBLE
                ivNoProducts.visibility = INVISIBLE
            }
            // Update the products via selected category
            Coroutines.main {
                categoryId = model.id
                viewModel.getProducts(model.id).collectLatest { homeAdapter.submitData(it) }
            }
        }
    }


    private fun initViews() {
        tvCounter = TextView(requireContext())
        viewModel = ViewModelProvider(this@HomeFragment)[HomeViewModel::class.java]
        homeAdapter = HomeAdapter(this@HomeFragment, cartDao)
        if (resources.getBoolean(R.bool.isTablet)) setupAdapter(3) else setupAdapter(2)
    }

    // Handle load state from paging
    private fun handleLoadState() {
        binding.apply {
            homeAdapter.addLoadStateListener {
                when (it.refresh) {
                    is LoadState.Loading -> {
                        if (rvHome.isVisible) rvHome.visibility = INVISIBLE
                        progressBar.show()
                    }
                    is LoadState.NotLoading -> {
                        if (rvHome.isInvisible) rvHome.visibility = VISIBLE
                        progressBar.hide()
                        if (homeAdapter.itemCount == 0) {
                            // Display empty state
                            rvHome.visibility = INVISIBLE
                            tvNoProducts.visibility = VISIBLE
                            ivNoProducts.visibility = VISIBLE
                        }
                    }
                    is LoadState.Error -> {
                        progressBar.hide()
                        alertError {
                            retryButton {
                                retryLoadingCategory()
                            }
                        }.show()
                    }
                }
            }
        }
    }

    private fun setupAdapter(spanCount: Int) {
        binding.apply {
            rvHome.layoutManager = GridLayoutManager(requireContext(), spanCount)
            rvHome.adapter = homeAdapter
        }
    }

    private fun updateCategories(response: Resource<CategoriesResponse>) {
        binding.apply {
            setupHorizontalLayoutManager(rvCategories)
            if (response is Resource.Success) {
                rvCategories.adapter =
                    response.value.data?.let { CategoriesAdapter(it, this@HomeFragment) }
            } else handleApiError(response as Resource.Failure)
        }
    }


    private fun retryLoadingCategory() {
        Coroutines.main {
            if (categoryId != null) {
                viewModel.getProducts(categoryId).collectLatest { homeAdapter.submitData(it) }
            } else {
                viewModel.getProducts().collectLatest { homeAdapter.submitData(it) }
            }
        }
    }


    private fun deleteOrRemove(model: DataItem, tvQuantity: TextView, operation: String) {
        val qty = tvQuantity.text.toString().toInt()
        Coroutines.background {
            val response = viewModel.initAddToCart(model, operation)
            Coroutines.main {
                if (response) {
                    // Decrease qty on screen by 1
                    tvQuantity.text = qty.minus(1).toString()
                } else requireContext().toast(getString(R.string.failed))
            }
        }
    }
}
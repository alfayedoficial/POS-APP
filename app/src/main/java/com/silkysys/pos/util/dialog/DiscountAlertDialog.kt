package com.silkysys.pos.util.dialog

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.silkysys.pos.R
import com.silkysys.pos.data.local.Constants
import com.silkysys.pos.databinding.DialogDisountBinding
import com.silkysys.pos.util.formatDouble
import com.silkysys.pos.util.toast


// Create alert dialog
inline fun Fragment.alertDiscount(
    func: DiscountDialogHelper.() -> Unit
): AlertDialog {
    return DiscountDialogHelper(requireContext()).apply {
        func()
    }.create()
}


class DiscountDialogHelper(val context: Context) {
    // Inflate custom alert dialog layout
    private val binding: DialogDisountBinding by lazyFast {
        DialogDisountBinding.inflate(LayoutInflater.from(context))
    }

    //  Set view to alert dialog
    private val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        .setView(binding.root)

    // initialization
    private var dialog: AlertDialog? = null
    private var cancelable: Boolean = true
    var finalTotal: Double? = null
    var discount: Double = 0.0


    // Click listener on Ok Button
    fun doneButton(func: (() -> Unit)? = null) {
        with(binding.btnOk) {
            setClickListenerToDialogButton(func)
        }
    }

    // Click listener on Cancel Button
    fun cancelButton(func: (() -> Unit)? = null) {
        with(binding.btnCancel) {
            setClickListenerToDialogButton(func)
        }
    }

    fun calculateDiscount(price: Double?) {
        binding.apply {
            tvBasePrice.text = formatDouble(price, Constants.TWO_CELL)
            val percentageDiscount = price?.div(2)

            etDiscountAmount.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (s != null && s.isNotEmpty()) {
                        discount = s.toString().toDouble()
                        if (discount > percentageDiscount!!) {
                            context.toast(context.getString(R.string.alert_discount))
                            etDiscountAmount.text.clear()
                            tvDiscount.text = Constants.ZERO_VALUE
                            tvTotal.text = Constants.ZERO_VALUE
                            discount = 0.0
                            finalTotal = null
                        } else {
                            tvDiscount.text = s.toString()
                            finalTotal = price.minus(discount)
                            tvTotal.text = formatDouble(finalTotal, Constants.TWO_CELL)
                        }
                    }
                }

                override fun afterTextChanged(p0: Editable?) {
                }
            })
        }
    }


    fun create(): AlertDialog {
        dialog = builder
            .setCancelable(cancelable)
            .create()
        return dialog!!
    }


    private fun View.setClickListenerToDialogButton(func: (() -> Unit)?) {
        setOnClickListener {
            func?.invoke()
            dialog?.dismiss()
        }
    }
}

/**
 * Implementation of lazy that is not thread safe. Useful when you know what thread you will be
 * executing on and are not worried about synchronization.
 */
fun <T> lazyFast(operation: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) {
    operation()
}
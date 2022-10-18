package com.silkysys.pos.util.dialog

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.silkysys.pos.R
import com.silkysys.pos.data.local.Constants
import com.silkysys.pos.databinding.DialogPaymentBinding
import com.silkysys.pos.util.formatDouble
import com.silkysys.pos.util.setText


// Create alert dialog
inline fun Fragment.alertPayment(
    func: PaymentDialogHelper.() -> Unit
): AlertDialog {
    return PaymentDialogHelper(requireContext()).apply {
        func()
    }.create()
}

class PaymentDialogHelper(val context: Context) : TextWatcher, RadioGroup.OnCheckedChangeListener {
    // Inflate custom alert dialog layout
    val paymentBinding: DialogPaymentBinding by lazyFastPayment {
        DialogPaymentBinding.inflate(LayoutInflater.from(context))
    }

    //  Set view to alert dialog
    private val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        .setView(paymentBinding.root)

    // initialization
    var dialog: AlertDialog? = null
    private var cancelable: Boolean = true
    private var subTotal = 0.0
    var paymentType: String = ""
    var received = 0.0
    var remaining = 0.0
    var cardAmount = 0.0

    // Constructor
    init {
        paymentBinding.apply {
            this@PaymentDialogHelper.apply {
                etCash.addTextChangedListener(this)
                etReceived.addTextChangedListener(this)
                rgPaymentType.setOnCheckedChangeListener(this)
            }
        }
    }


    // Click listener on ok Button
    fun okButton(func: (() -> Unit)? = null) {
        paymentBinding.btnOk.setOnClickListener {
            func?.invoke()
        }
    }

    // Click listener on Cancel Button
    fun cancelButton(func: (() -> Unit)? = null) {
        paymentBinding.btnCancel.setOnClickListener {
            func?.invoke()
            dialog?.dismiss()
        }
    }

    fun create(): AlertDialog {
        dialog = builder
            .setCancelable(cancelable)
            .create()
        return dialog!!
    }


    fun updateUi(subTotal: Double) {
        this.subTotal = subTotal
        paymentBinding.apply {
            Constants.apply {
                cvCard.visibility = GONE
                cvCardCaption.visibility = GONE
                cvCash.visibility = GONE
                cvCashCaption.visibility = GONE

                etReceived.text = setText(formatDouble(subTotal, TWO_CELL))
                tvTotalAbove.text = formatDouble(subTotal, TWO_CELL)
                tvTotal.text = formatDouble(subTotal, TWO_CELL)
            }
        }
    }

    // For text watcher interface
    override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun afterTextChanged(et: Editable?) {
        paymentBinding.apply {
            if (et?.isNotEmpty() == true) {
                // Cash edit text
                if (et === etCash.editableText) {
                    val cashAmount = etCash.text.toString().toDouble()
                    cardAmount = subTotal - cashAmount
                    tvCard.text = formatDouble(cardAmount, Constants.TWO_CELL)

                    // Received edit text
                } else if (et === etReceived.editableText) {
                    try {
                        received = etReceived.text.toString().toDouble()
                    } catch (ex: Exception) {
                    }
                    remaining = received - subTotal
                    tvRemaining.text = formatDouble(remaining, Constants.TWO_CELL)
                }
            }
        }
    }

    override fun onCheckedChanged(p0: RadioGroup?, id: Int) {
        paymentBinding.apply {
            when (id) {
                R.id.rb_split -> {
                    paymentType = Constants.SPLIT
                    switchVisibility(true)
                }
                R.id.rb_cash -> {
                    paymentType = Constants.CASH
                    switchVisibility(false)
                }
                else -> {
                    paymentType = Constants.CARD
                    switchVisibility(false)
                }
            }
        }
    }

    private fun switchVisibility(visibility: Boolean) {
        paymentBinding.apply {
            if (visibility) {
                cvCard.visibility = VISIBLE
                cvCardCaption.visibility = VISIBLE
                cvCash.visibility = VISIBLE
                cvCashCaption.visibility = VISIBLE
            } else {
                cvCard.visibility = GONE
                cvCardCaption.visibility = GONE
                cvCash.visibility = GONE
                cvCashCaption.visibility = GONE
            }
        }
    }


    /**
     * Implementation of lazy that is not thread safe. Useful when you know what thread you will be
     * executing on and are not worried about synchronization.
     */
    fun <T> lazyFastPayment(operation: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) {
        operation()
    }
}
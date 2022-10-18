package com.silkysys.pos.util.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.silkysys.pos.databinding.DialogReturnedBinding


// Create alert dialog
inline fun Fragment.alertReturned(
    func: ReturnedDialogHelper.() -> Unit
): AlertDialog {
    return ReturnedDialogHelper(requireContext()).apply {
        func()
    }.create()
}


class ReturnedDialogHelper(val context: Context) {
    // Inflate custom alert dialog layout
    private val binding: DialogReturnedBinding by lazyFastReturnedDialog {
        DialogReturnedBinding.inflate(LayoutInflater.from(context))
    }

    //  Set view to alert dialog
    private val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        .setView(binding.root)

    // initialization
    private var dialog: AlertDialog? = null
    private var cancelable: Boolean = true


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

    // Click listener on scan barcode
    fun scanBarcode(func: (() -> Unit)? = null) {
        with(binding.ivBarcode) {
            setClickListenerToDialogButton(func)
        }
    }

    fun getInvoiceNumber() =
        binding.etInvoiceNum.text.toString()


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
fun <T> lazyFastReturnedDialog(operation: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) {
    operation()
}
package com.silkysys.pos.util.dialog


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.silkysys.pos.databinding.DialogErrorBinding


// Create alert dialog
inline fun Fragment.alertError(
    func: ErrorDialogHelper.() -> Unit
): AlertDialog {
    return ErrorDialogHelper(requireContext()).apply {
        func()
    }.create()
}


class ErrorDialogHelper(val context: Context) {
    // Inflate custom alert dialog layout
    private val binding: DialogErrorBinding by lazyFastErrorDialog {
        DialogErrorBinding.inflate(LayoutInflater.from(context))
    }

    //  Set view to alert dialog
    private val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        .setView(binding.root)

    // initialization
    private var dialog: AlertDialog? = null
    private var cancelable: Boolean = true


    // Click listener on Retry Button
    fun retryButton(func: (() -> Unit)? = null) {
        with(binding.btnRetry) {
            setClickListenerToDialogButton(func)
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
fun <T> lazyFastErrorDialog(operation: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) {
    operation()
}
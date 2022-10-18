package com.silkysys.pos.util.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.silkysys.pos.databinding.DialogLogoutBinding


// Create alert dialog
inline fun Fragment.alertLogout(
    func: LogoutDialogHelper.() -> Unit
): AlertDialog {
    return LogoutDialogHelper(requireContext()).apply {
        func()
    }.create()
}


class LogoutDialogHelper(val context: Context) {
    // Inflate custom alert dialog layout
    val bindingLogout: DialogLogoutBinding by lazyFastLogoutDialog {
        DialogLogoutBinding.inflate(LayoutInflater.from(context))
    }

    //  Set view to alert dialog
    private val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        .setView(bindingLogout.root)

    // initialization
     var dialog: AlertDialog? = null
    private var cancelable: Boolean = true


    // Click listener on logout Button
    fun logoutButton(func: (() -> Unit)? = null) {
        bindingLogout.btnLogout.setOnClickListener {
            func?.invoke()
        }
//        with(bindingLogout.btnLogout) {
//            setClickListenerToDialogButton(func)
//        }
    }

    // Click listener on Cancel Button
    fun cancelButton(func: (() -> Unit)? = null) {
        with(bindingLogout.btnCancel) {
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
fun <T> lazyFastLogoutDialog(operation: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) {
    operation()
}

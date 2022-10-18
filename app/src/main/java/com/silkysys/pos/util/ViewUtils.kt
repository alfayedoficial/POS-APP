package com.silkysys.pos.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Parcelable
import android.text.Editable
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.silkysys.pos.R
import com.silkysys.pos.data.network.Resource
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


fun <T> Context.startActivity(cls: Class<T>, key: String = "", value: Any = "") {
    if (key != "" && value != "") {
        Intent(this, cls).apply {
            when (value) {
                is String -> putExtra(key, value)
                is Int -> putExtra(key, value)
                is Boolean -> putExtra(key, value)
                is Parcelable -> putExtra(key, value)
            }
            startActivity(this)
        }
    } else {
        Intent(this, cls).apply {
            startActivity(this)
        }
    }
}

fun Context.openUrl(url: String) {
    Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(url)
        startActivity(this)
    }
}


fun formatDate(dateFormat: String): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern(dateFormat)
        current.format(formatter)
    } else {
        val formatter = SimpleDateFormat(dateFormat, Locale.ENGLISH)
        formatter.format(Date())
    }
}


fun Fragment.setupHorizontalLayoutManager(rv: RecyclerView) {
    rv.layoutManager = LinearLayoutManager(
        requireContext(),
        LinearLayoutManager.HORIZONTAL, false
    )
}


fun formatDouble(number: Double?, pattern: String? = ""): String? {
    return if (pattern == "#0.00") DecimalFormat("#0.00").format(number)
    else DecimalFormat("#0.0").format(number)
}


fun Context.toast(message: String?) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}


fun Context.displayError() =
    resources.getString(R.string.required_field)


fun Fragment.goTo(destination: Any) {
    findNavController().apply {
        if (destination is NavDirections) {
            navigate(destination)
        } else if (destination is Int) {
            navigate(destination)
        }
    }
}


fun setText(text: String?): Editable? {
    return if (text == null) {
        Editable.Factory.getInstance().newEditable("")
    } else {
        Editable.Factory.getInstance().newEditable(text)
    }
}

fun ProgressBar.hide() {
    visibility = INVISIBLE
}


fun ProgressBar.show() {
    visibility = VISIBLE
}


fun Context.handleApiError(
    failure: Resource.Failure
) {
    when {
        failure.isNetworkError -> toast(getString(R.string.connection_error))
        failure.errorCode == 401 -> toast(getString(R.string.session_expired))
        failure.errorCode == 404 -> toast(getString(R.string.not_found))
        failure.errorCode == 422 -> toast(getString(R.string.invalid_auth))
        else -> toast(getString(R.string.not_found))
    }
}


fun Fragment.handleApiError(
    failure: Resource.Failure
) {
    requireContext().apply {
        when {
            failure.isNetworkError -> toast(getString(R.string.connection_error))
            failure.errorCode == 401 -> toast(getString(R.string.session_expired))
            failure.errorCode == 404 -> toast(getString(R.string.not_found))
            failure.errorCode == 422 -> toast(getString(R.string.invalid_auth))
            else -> toast(getString(R.string.not_found))
        }
    }
}
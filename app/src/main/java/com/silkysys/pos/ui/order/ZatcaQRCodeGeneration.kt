package com.silkysys.pos.ui.order

import android.util.Base64
import com.silkysys.pos.data.local.Constants

const val NO_INPUT = Constants.CLEAR

class ZatcaQRCodeGeneration {

    private val sellerNameTag = Constants.ONE
    private val taxNumberTag = Constants.TWO
    private val invoiceDateTag = Constants.THREE
    private val totalAmountTag = Constants.FOUR
    private val taxAmountTag = Constants.FIVE

    private var sellerName = NO_INPUT
    private var taxNumber = NO_INPUT
    private var invoiceDate = NO_INPUT
    private var totalAmount = NO_INPUT
    private var taxAmount = NO_INPUT

    fun sellerName(value: String?) = apply {
        if (value != null) {
            this.sellerName = value
        }
    }

    fun taxNumber(value: String?) = apply {
        if (value != null) {
            this.taxNumber = value
        }
    }

    fun invoiceDate(value: String?) = apply {
        if (value != null) {
            this.invoiceDate = value
        }
    }

    fun totalAmount(value: String?) = apply {
        if (value != null) {
            this.totalAmount = value
        }
    }


    private fun convertTagsAndLengthToHexValues(
        tag: String,
        length: String,
        value: String
    ): ByteArray {
        return byteArrayOf(tag.toByte(), length.toByte()).plus(value.toByteArray())
    }

    fun getBase64(): String {

        val tlv1 = convertTagsAndLengthToHexValues(
            sellerNameTag,
            sellerName.toByteArray().size.toString(),
            sellerName
        )

        val tlv2 = convertTagsAndLengthToHexValues(
            taxNumberTag,
            taxNumber.toByteArray().size.toString(),
            taxNumber
        )

        val tlv3 = convertTagsAndLengthToHexValues(
            invoiceDateTag,
            invoiceDate.toByteArray().size.toString(),
            invoiceDate
        )

        val tlv4 = convertTagsAndLengthToHexValues(
            totalAmountTag,
            totalAmount.toByteArray().size.toString(),
            totalAmount
        )

        val tlv5 = convertTagsAndLengthToHexValues(
            taxAmountTag,
            taxAmount.toByteArray().size.toString(),
            taxAmount
        )

        val tlvs = tlv1 + tlv2 + tlv3 + tlv4 + tlv5
        return Base64.encodeToString(tlvs, Base64.DEFAULT)
    }
}
package com.silkysys.pos.data.repository

import com.silkysys.pos.data.local.Constants
import com.silkysys.pos.data.model.sell.refund.Products
import com.silkysys.pos.data.model.sell.refund.RefundRequest
import com.silkysys.pos.data.network.APIService
import com.silkysys.pos.data.network.SafeApiCall
import javax.inject.Inject

class RefundRepo @Inject constructor(private val apiService: APIService) : SafeApiCall {

    suspend fun getSpecificSell(invoiceNum: String) = safeApiCall {
        apiService.getSpecificSell(invoiceNum)
    }

    suspend fun createRefund(refund: HashMap<String, Any?>, products: List<Products>?) =
        safeApiCall {
            val id = refund[Constants.TRANSACTION_ID].toString().toInt()
            val date = refund[Constants.TRANSACTION_DATE].toString()
            val invoice = refund[Constants.INVOICE_NO].toString()
            apiService.createSellRefund(RefundRequest(id, date, invoice, products))
        }
}
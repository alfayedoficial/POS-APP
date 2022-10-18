package com.silkysys.pos.data.repository

import com.silkysys.pos.data.local.Constants
import com.silkysys.pos.data.model.contact.create.CreateContact
import com.silkysys.pos.data.network.APIService
import com.silkysys.pos.data.network.SafeApiCall
import javax.inject.Inject

class ContactRepo @Inject constructor(val apiService: APIService) : SafeApiCall {

    suspend fun createContact(map: HashMap<String, String>) = safeApiCall {
        val body = CreateContact(
            map[Constants.CONTACT_TYPE],
            map[Constants.SUPPLIER_NAME],
            map[Constants.TAX_NUMBER],
            map[Constants.FIRST_NAME],
            map[Constants.EMAIL],
            map[Constants.PHONE_NUMBER],
            map[Constants.ADDRESS]
        )
        apiService.createContact(body)
    }


    suspend fun updateContact(map: HashMap<String, String>, id: Int) = safeApiCall {
        val body = CreateContact(
            map[Constants.CONTACT_TYPE],
            map[Constants.SUPPLIER_NAME],
            map[Constants.TAX_NUMBER],
            map[Constants.FIRST_NAME],
            map[Constants.EMAIL],
            map[Constants.PHONE_NUMBER],
            map[Constants.ADDRESS]
        )
        apiService.updateContact(id, body)
    }


    suspend fun getContacts(type: String = "") = safeApiCall {
        apiService.getContacts(type)
    }

    suspend fun getContactBy(mobileNum: String, type: String = "") = safeApiCall {
        apiService.getContactByMobileNumber(mobileNum, type)
    }

    suspend fun deleteContact(contactId: Int) = safeApiCall {
        apiService.deleteContact(contactId)
    }
}
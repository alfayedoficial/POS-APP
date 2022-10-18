package com.silkysys.pos.ui.contact

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.silkysys.pos.data.model.contact.list.ContactResponse
import com.silkysys.pos.data.network.Resource
import com.silkysys.pos.data.repository.ContactRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(private val repo: ContactRepo) : ViewModel() {

    val contactsResponse = MutableLiveData<Resource<ContactResponse>>()

    // Initialize post request to create a contact
    suspend fun initAddContact(contact: HashMap<String, String>) = repo.createContact(contact)

    // Initialize put request to update a contact
    suspend fun initUpdateContact(contact: HashMap<String, String>, id: Int) =
        repo.updateContact(contact, id)

    // Initialize get request to delete the contact
    suspend fun initDeleteContact(contactId: Int) = repo.deleteContact(contactId)

    // Initialize get request to get all contacts
    fun initGetContacts(type: String = "") {
        viewModelScope.launch {
            contactsResponse.value = repo.getContacts(type)
        }
    }

    // Initialize get request to get a specific contact by mobile number
    fun initGetContactBy(mobileNum: String,type: String = "") {
        viewModelScope.launch {
            contactsResponse.value = repo.getContactBy(mobileNum,type)
        }
    }
}
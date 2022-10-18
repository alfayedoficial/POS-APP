package com.silkysys.pos.data.local

object Constants {

    // Base url
    const val BASE_URL = "https://pos.stage.silkysystems.com"

    // Create an account url
    const val CREATE_ACCOUNT = "https://pos.dev.silkysystems.com/signup/form/user"

    // User token
    const val USER_TOKEN = "USER_TOKEN"

    const val PAGE_SIZE = 10
    const val STARTING_PAGE_INDEX = 1

    const val CAMERA_REQUEST_CODE = 101

    // Shared Preferences keys and values
    const val MyPREFERENCES = "MyPrefs"
    const val CHECKED = "CHECKED"
    const val CART_BADGE = "CART_BADGE"

    // Intent keys
    const val REMEMBER_ME = "REMEMBER_ME"

    // Loading state for login screen
    const val LOADING = "Loading"
    const val FINISHED = "Finished"

    // Demo state
    const val DEMO = "Demo"

    const val SPACE = " "
    const val DECIMAL_POINT = ", "
    const val CLEAR = ""

    const val ONE = "1"
    const val TWO = "2"
    const val THREE = "3"
    const val FOUR = "4"
    const val FIVE = "5"

    // For cart operation
    const val ADD = "ADD"
    const val SUB = "SUB"
    const val DEL = "DEL"


    const val TWO_CELL = "#0.00"
    const val ZERO_VALUE = "00.00"

    // Contact type
    const val customer = "customer"

    // Keys for hash map
    const val CONTACT_ID = "contactId"
    const val SUB_TOTAL = "subTotal"
    const val DISCOUNT = "discount"
    const val INVOICE = "invoice"
    const val PAYMENT_METHOD = "payment"

    const val FIRST_NAME = "FIRST_NAME"
    const val SUPPLIER_NAME = "SUPPLIER_NAME"
    const val PHONE_NUMBER = "PHONE_NUMBER"
    const val ADDRESS = "ADDRESS"
    const val EMAIL = "EMAIL"
    const val TAX_NUMBER = "TAX_NUMBER"
    const val CONTACT_TYPE = "CONTACT_TYPE"

    const val PRODUCT_NAME = "product_name"
    const val PRODUCT_NAME_AR = "product_name_ar"
    const val UNIT = "unit"
    const val LOCATION_ID = "location_id"
    const val CATEGORY = "category"
    const val QUANTITY = "quantity"
    const val DESCRIPTION = "description"
    const val PRICE_EXC_TAX = "price_exc_tax"
    const val PRICE_INC_TAX = "price_inc_tax"
    const val SELLING_PRICE = "selling_price"

    const val PRODUCT = "product"

    const val CASH = "cash"
    const val CARD = "card"
    const val SPLIT = "other"
    const val NULL = "null"

    const val OVER_SELLING = "OVER_SELLING"

    const val CUSTOMER = "customer"
    const val SUPPLIER = "supplier"

    const val EDIT_CONTACT = "EDIT_CONTACT"
    const val LANGUAGE_AR = "ar"

    const val DATE_AM_PM = "dd/MM/yyyy  h:mm a"

    const val TRANSACTION_ID = "transaction_id"
    const val TRANSACTION_DATE = "transaction_date"
    const val INVOICE_NO = "invoice_no"

    const val TAX_TYPE = "inclusive"

    const val ALL_CATEGORIES = "All Categories"
}
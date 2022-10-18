package com.silkysys.pos.data.network

import com.silkysys.pos.data.model.auth.forget.ForgetPasswordRequest
import com.silkysys.pos.data.model.auth.forget.ForgotPasswordResponse
import com.silkysys.pos.data.model.auth.login.LoginRequest
import com.silkysys.pos.data.model.auth.login.LoginResponse
import com.silkysys.pos.data.model.auth.logout.LogoutResponse
import com.silkysys.pos.data.model.auth.user.get.UserResponse
import com.silkysys.pos.data.model.auth.user.update.UpdateUserRequest
import com.silkysys.pos.data.model.auth.user.update.UpdateUserResponse
import com.silkysys.pos.data.model.business.get.BusinessResponse
import com.silkysys.pos.data.model.business.update.BusinessRequest
import com.silkysys.pos.data.model.business.update.BusinessUpdateResponse
import com.silkysys.pos.data.model.category.add.AddCategoryRequest
import com.silkysys.pos.data.model.category.add.AddCategoryResponse
import com.silkysys.pos.data.model.category.get.CategoriesResponse
import com.silkysys.pos.data.model.contact.create.CreateContact
import com.silkysys.pos.data.model.contact.create.CreateContactResponse
import com.silkysys.pos.data.model.contact.list.ContactResponse
import com.silkysys.pos.data.model.location.BusinessLocationResponse
import com.silkysys.pos.data.model.product.ProductsResponse
import com.silkysys.pos.data.model.product.add.AddProductRequest
import com.silkysys.pos.data.model.product.add.AddProductResponse
import com.silkysys.pos.data.model.sell.create.request.SellRequest
import com.silkysys.pos.data.model.sell.create.response.SellResponse
import com.silkysys.pos.data.model.sell.get.GetSpecificSellResponse
import com.silkysys.pos.data.model.sell.refund.RefundRequest
import com.silkysys.pos.data.model.unit.UnitResponse
import retrofit2.http.*

interface APIService {

    // User api
    @POST("/connector/api/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): LoginResponse

    @GET("/connector/api/user/loggedin")
    suspend fun getUserLoggedIn(): UserResponse

    @POST("/connector/api/logout")
    suspend fun logout(): LogoutResponse

    @PATCH("/connector/api/user/update")
    suspend fun updateUser(
        @Body updateUser: UpdateUserRequest
    ): UpdateUserResponse

    @POST("/connector/api/reset-password")
    suspend fun forgetPassword(
        @Body request: ForgetPasswordRequest
    ): ForgotPasswordResponse


    // Product api
    @GET("/connector/api/product")
    suspend fun getProducts(
        @Query("category_id") id: Int? = 0,
        @Query("per_page") perPage: Int,
        @Query("page") page: Int,
    ): ProductsResponse

    @GET("/connector/api/product")
    suspend fun getProductBySku(
        @Query("sku") sku: String
    ): ProductsResponse

    @GET("/connector/api/taxonomy")
    suspend fun getCategories(
        @Query("type") type: String,
        @Query("with") with: String
    ): CategoriesResponse

    @POST("/connector/api/product")
    suspend fun addProduct(
        @Body product: AddProductRequest
    ): AddProductResponse


    // Taxonomy api
    @POST("/connector/api/taxonomy")
    suspend fun addCategory(
        @Body category: AddCategoryRequest
    ): AddCategoryResponse


    // Contact api
    @GET("/connector/api/contactapi")
    suspend fun getContacts(
        @Query("type") type: String = ""
    ): ContactResponse

    @GET("/connector/api/contactapi")
    suspend fun getContactByMobileNumber(
        @Query("mobile_num") mobile: String,
        @Query("type") type: String = ""
    ): ContactResponse

    @POST("/connector/api/contactapi")
    suspend fun createContact(
        @Body contact: CreateContact
    ): CreateContactResponse

    @PUT("/connector/api/contactapi/{contact}")
    suspend fun updateContact(
        @Path("contact") contactId: Int,
        @Body contact: CreateContact
    ): CreateContactResponse

    @DELETE("/connector/api/delete-contact/{contact}")
    suspend fun deleteContact(
        @Path("contact") contactId: Int
    ): ContactResponse


    // Sell api
    @POST("/connector/api/sell")
    suspend fun createSell(
        @Body sell: SellRequest
    ): List<SellResponse>

    @GET("/connector/api/sell/{sell}")
    suspend fun getSpecificSell(
        @Path("sell") invoiceNum: String
    ): GetSpecificSellResponse

    @POST("/connector/api/sell-return")
    suspend fun createSellRefund(
        @Body refund: RefundRequest
    )


    // Business api
    @GET("/connector/api/business-details")
    suspend fun getBusinessDetails(): BusinessResponse

    @GET("/connector/api/business-location")
    suspend fun getBusinessLocations(): BusinessLocationResponse

    @PATCH("/connector/api/business/update")
    suspend fun updateBusinessDetails(
        @Body business: BusinessRequest
    ): BusinessUpdateResponse

    // Unit api
    @GET("/connector/api/unit")
    suspend fun getUnits(): UnitResponse
}
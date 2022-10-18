package com.silkysys.pos.data.model.auth.user.update

data class UpdateUserRequest(
    var first_name: String? = "",
    var last_name: String? = "",
    var email: String? = "",
    var address: String?
)

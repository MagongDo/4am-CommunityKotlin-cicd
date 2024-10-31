package com.example.community_4am_Kotlin.domain.user

enum class Role(val description: String) {

    ROLE_ADMIN("admin"),
    ROLE_USER("user");

    fun getAuthority(): String = this.name
}
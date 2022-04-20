package com.luispalenciadelcampo.travelink.domain.repository

import com.google.firebase.auth.AuthResult
import com.luispalenciadelcampo.travelink.utils.Resource

interface AuthRepository {

    suspend fun registerUser(email: String, password: String, name: String, lastname: String, birthday: String): Resource<AuthResult>
    suspend fun logInUser(email: String, password: String): Resource<AuthResult>
    suspend fun logInUserByGoogle(idToken: String): Resource<AuthResult>
    suspend fun sendRecoverPasswordEmail(email: String): Resource<Boolean>
}
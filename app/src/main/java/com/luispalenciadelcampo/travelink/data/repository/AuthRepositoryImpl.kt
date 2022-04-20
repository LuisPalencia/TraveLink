package com.luispalenciadelcampo.travelink.data.repository

import android.util.Log
import com.google.firebase.auth.*
import com.google.firebase.database.FirebaseDatabase
import com.luispalenciadelcampo.travelink.constants.Constants
import com.luispalenciadelcampo.travelink.data.dto.User
import com.luispalenciadelcampo.travelink.domain.repository.AuthRepository
import com.luispalenciadelcampo.travelink.utils.Resource
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val firebaseAuth: FirebaseAuth
) : AuthRepository{

    private val TAG = "AuthRepositoryImpl"

    override suspend fun registerUser(
        email: String,
        password: String,
        name: String,
        lastname: String
    ): Resource<AuthResult> {
        return try {
            val resultCreateUser =
                firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            if (resultCreateUser.user != null) { // User has been created successfully
                // Send verification email
                firebaseAuth.currentUser!!.sendEmailVerification().await()

                val user = User(
                    uuid = resultCreateUser.user?.uid!!,
                    name = name,
                    lastname = lastname
                )
                //database.getReference(Constants.DB_REFERENCE_USERS).child(user.uuid!!).setValue(user).await()

                val childUpdates = hashMapOf<String, Any>(
                    "name" to user.name!!,
                    "lastname" to user.lastname!!
                )

                firebaseDatabase.getReference(Constants.DB_REFERENCE_USERS).child(user.uuid!!)
                    .updateChildren(childUpdates)

                // Sign out so the user needs to verify the email before logging in
                firebaseAuth.signOut()

                Resource.Success(resultCreateUser)
            } else {
                Resource.Error(Constants.RESULT_SIGNUP_ERROR_UNKNOWN)
            }
        }catch (e: FirebaseAuthUserCollisionException){
            Resource.Error(Constants.RESULT_SIGNUP_ERROR_ACCOUNT_ALREADY_EXISTS)
        } catch (e: Exception) {
            Log.d("safeCall", e.toString())
            Resource.Error(Constants.RESULT_SIGNUP_ERROR_UNKNOWN)
        }
    }

    override suspend fun logInUser(email: String, password: String): Resource<AuthResult> {
        return try {
            val resultLogIn = firebaseAuth.signInWithEmailAndPassword(email, password).await()

            if(resultLogIn.user!!.isEmailVerified) {
                Resource.Success(resultLogIn)
            }else{
                Resource.Error(Constants.RESULT_LOGIN_ERROR_EMAIL_NOT_VERIFIED)
            }
        }catch (e: FirebaseAuthInvalidCredentialsException){
            Resource.Error(Constants.RESULT_LOGIN_ERROR_WRONG_CREDENTIALS)
        }catch (e: Exception){
            Resource.Error(Constants.RESULT_LOGIN_ERROR_UNKNOWN)
        }
    }

    override suspend fun logInUserByGoogle(idToken: String): Resource<AuthResult> {
        return try{
            val credential = GoogleAuthProvider.getCredential(idToken, null)

            val resultLogIn = firebaseAuth.signInWithCredential(credential).await()
            if(resultLogIn.user != null){
                if (resultLogIn.additionalUserInfo?.isNewUser == true) {
                    val name: String
                    if(firebaseAuth.currentUser!!.displayName != null){
                        name = firebaseAuth.currentUser!!.displayName.toString()
                    }else{
                        name = ""
                    }

                    this.insertNewUserGoogleSignIn(resultLogIn.user!!.uid, name)
                }
                Resource.Success(resultLogIn)
            }else{
                Resource.Error(Constants.RESULT_LOGIN_GOOGLE_ERROR)
            }
            //}catch(e: FirebaseAuthInvalidCredentialsException){
            //Result.Error(Constants.RESULT_ERROR_WRONG_CREDENTIALS)
        }catch (e: Exception){
            Resource.Error(Constants.RESULT_LOGIN_GOOGLE_ERROR)
        }
    }

    override suspend fun sendRecoverPasswordEmail(email: String): Resource<Boolean> {
        return try{
            val taskResult = firebaseAuth.fetchSignInMethodsForEmail(email).await()
            // Check if the user exists first
            val isNewUser = taskResult.signInMethods?.isEmpty()
            if (isNewUser != null && !isNewUser) {
                Log.d(TAG, "Email exists")
                // If user exists, then proceed to send the email
                firebaseAuth.sendPasswordResetEmail(email).await()
                return Resource.Success(true)
            } else { // The email doesn't exist
                Log.d(TAG, "Email doesn't exist")
                return Resource.Error(Constants.RESULT_SEND_EMAIL_RECOVER_PASSWORD_ERROR_ACCOUNT_NOT_EXISTING)

            }
        }catch (e: Exception){
            Resource.Error(Constants.RESULT_SEND_EMAIL_RECOVER_PASSWORD_ERROR_UNKNOWN)
        }
    }

    private suspend fun insertNewUserGoogleSignIn(userId: String, name: String){
        val user = User(
            uuid = userId,
            name = name,
            lastname = ""
        )
        //database.getReference(Constants.DB_REFERENCE_USERS).child(user.uuid!!).setValue(user).await()

        val childUpdates = hashMapOf<String, Any>(
            "name" to user.name!!,
            "lastname" to user.lastname!!
        )

        firebaseDatabase.getReference(Constants.DB_REFERENCE_USERS).child(user.uuid!!)
            .updateChildren(childUpdates).await()
    }
}
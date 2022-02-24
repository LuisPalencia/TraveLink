package com.luispalenciadelcampo.travelink.utils

import android.util.Patterns
import java.util.regex.Pattern

class PatternsAccount {
    companion object{

        //Function that checks if an string is a valid email
        fun isValidEmail(email: String): Boolean{
            val pattern = Patterns.EMAIL_ADDRESS
            return pattern.matcher(email).matches()
        }
        //Function that checks if an string is a valid password
        fun isValidPassword(password: String): Boolean{
            /*
            Patron con las siguientes caracteristicas:
            - 1 numero
            - 1 letra minuscula
            - 1 letra mayuscula
            - Sin espacios
            - Un caracter especial
            - Al menos 6 caracteres
             */
            val passwordStringPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[.@#$%^&+=!])(?=\\S+$).{6,}$"
            val pattern = Pattern.compile(passwordStringPattern)
            return pattern.matcher(password).matches()
        }

        //Function that checks if password and confirmpassword are the same
        fun isValidConfirmPassword(password: String, confirmPassword: String): Boolean{
            return password == confirmPassword
        }
    }
}
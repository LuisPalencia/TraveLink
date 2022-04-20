package com.luispalenciadelcampo.travelink.constants

object Constants {
    // This val is because resource "R.string.default_web_client_id" is not being loaded successfully for Google Login, so it has been added manually
    const val DEFAULT_WEB_CLIENT_ID = ""


    const val DB_URL = ""
    const val PLACES_API_KEY = ""

    // Firebase database references
    const val DB_REFERENCE_USERS = "users"
    const val DB_REFERENCE_TRIPS = "trips"
    const val DB_REFERENCE_EVENTS = "events"

    // Firebase storage references
    const val STORAGE_REFERENCE_IMAGE_TRIPS = "trips"
    const val STORAGE_REFERENCE_IMAGE_EVENTS = "events"

    // Shared preferences
    const val SHARED_PREFERENCES_APP = "SHARED_PREFERENCES_APP"
    const val SHARED_PREFERENCE_REMEMBER_ME = "remember_me"



    const val MAXIMUM_DAYS_TRIP = 30

    // Bundles
    const val LOGGED_BEFORE_MAIN_ACTIVITY = "LOGGED_BEFORE_MAIN_ACTIVITY" //Bundle login
    const val BUNDLE_ID_TRIP_SELECTED = "idTrip" // ID Trip selected
    const val BUNDLE_TRIP = "trip" // Trip selected
    const val BUNDLE_ID_EVENT_SELECTED = "BUNDLE_ID_EVENT_SELECTED" // ID Event selected
    const val BUNDLE_EVENT = "event" // ID Event selected


    // Results
    const val RESULT_LOGIN_CORRECT = "RESULT_LOGIN_CORRECT"
    const val RESULT_LOGIN_ERROR_WRONG_CREDENTIALS = "RESULT_LOGIN_ERROR_WRONG_CREDENTIALS"
    const val RESULT_LOGIN_ERROR_EMAIL_NOT_VERIFIED = "RESULT_LOGIN_ERROR_EMAIL_NOT_VERIFIED"
    const val RESULT_LOGIN_ERROR_UNKNOWN = "RESULT_LOGIN_ERROR_UNKNOWN"
    const val RESULT_LOGIN_GOOGLE_ERROR = "RESULT_LOGIN_GOOGLE_ERROR"

    const val RESULT_SIGNUP_CORRECT = "RESULT_SIGNUP_CORRECT"
    const val RESULT_SIGNUP_ERROR_ACCOUNT_ALREADY_EXISTS = "RESULT_SIGN_UPERROR_ACCOUNT_ALREADY_EXISTS"
    const val RESULT_SIGNUP_ERROR_UNKNOWN = "RESULT_SIGNUP_ERROR_UNKNOWN"
    const val RESULT_SIGNUP_ERROR_EMPTY_VALUES = "RESULT_SIGNUP_ERROR_EMPTY_VALUES"

    const val RESULT_SEND_EMAIL_RECOVER_PASSWORD_ERROR_ACCOUNT_NOT_EXISTING = "RESULT_SEND_EMAIL_RECOVER_PASSWORD_ERROR_ACCOUNT_NOT_EXISTING"
    const val RESULT_SEND_EMAIL_RECOVER_PASSWORD_ERROR_UNKNOWN= "RESULT_SEND_EMAIL_RECOVER_PASSWORD_ERROR_UNKNOWN"

    const val RESULT_GET_USER_ERROR = "RESULT_GET_USER_ERROR"
    const val RESULT_GET_TRIPS_ERROR = "RESULT_GET_TRIPS_ERROR"
    const val RESULT_CREATE_TRIP_ERROR = "RESULT_CREATE_TRIP_ERROR"
    const val RESULT_REMOVE_TRIP_ERROR = "RESULT_REMOVE_TRIP_ERROR"
    const val RESULT_CREATE_EVENT_ERROR = "RESULT_REMOVE_EVENT_ERROR"
    const val RESULT_REMOVE_EVENT_ERROR = "RESULT_REMOVE_EVENT_ERROR"
    const val RESULT_UPDATE_EVENT_ERROR = "RESULT_REMOVE_EVENT_ERROR"

    const val RESULT_RATE_TRIP_ERROR = "RESULT_RATE_TRIP_ERROR"

    // Permission requests
    const val ACCESS_FINE_LOCATION_PERMISSIONS_CODE = 100
}
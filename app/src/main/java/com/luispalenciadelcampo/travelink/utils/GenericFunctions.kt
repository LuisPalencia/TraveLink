package com.luispalenciadelcampo.travelink.utils

import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import com.luispalenciadelcampo.travelink.R
import com.luispalenciadelcampo.travelink.data.dto.Event
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class GenericFunctions {

    companion object{

        fun createSimpleDateFormat(): SimpleDateFormat{
            return SimpleDateFormat("dd/MM/yyyy")
        }

        private fun createSimpleDateHourFormat(): SimpleDateFormat{
            return SimpleDateFormat("HH:mm")
        }

        //Method that converts a date to a string
        fun dateToString(date: Date): String{
            return createSimpleDateFormat().format(date)
        }

        fun stringToDate(date: String): Date {
            return createSimpleDateFormat().parse(date)
        }

        //Method that converts a date to a string
        fun dateHourToString(date: Date): String{
            return createSimpleDateHourFormat().format(date)
        }

        fun stringToDateHour(date: String): Date {
            return createSimpleDateHourFormat().parse(date)
        }

        fun getCalendarWithNoDate(): Calendar{
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.DAY_OF_MONTH, 0)
            calendar.set(Calendar.MONTH, 0)
            calendar.set(Calendar.YEAR, 2000)

            return calendar
        }

        fun getActualCalendarWithNoHour(): Calendar{
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            return calendar
        }

        fun getBitmapCompressedFromUri(uriImage: Uri, context: Context?): Bitmap{
            val parcelFileDescriptor = context?.contentResolver?.openFileDescriptor(uriImage, "r")
            val fileDescriptor = parcelFileDescriptor?.fileDescriptor
            val imageBitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor?.close()

            val outputStream = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)

            return BitmapFactory.decodeStream(ByteArrayInputStream(outputStream.toByteArray()))
        }


        //Method that checks if the device is connected to internet
        fun isDeviceConnectedToInternet(context: Context): Boolean{
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            // Returns a Network object corresponding to
            // the currently active default data network.
            val network = connectivityManager.activeNetwork ?: return false

            // Representation of the capabilities of an active network.
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                // Indicates this network uses a Wi-Fi transport,
                // or WiFi has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                // Indicates this network uses a Cellular transport. or
                // Cellular has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                // else return false
                else -> false
            }
        }

        //Method that returns an AlertDialog explaining that the device is not connected to internet
        fun getErrorInternetDialog(context: Context): AlertDialog {
            return AlertDialog.Builder(context)
                .setMessage(R.string.error_internet_content)
                .setTitle(R.string.error_internet_title)
                .setPositiveButton(R.string.ok, DialogInterface.OnClickListener { dialog, id ->
                    dialog.dismiss()
                })
                .setCancelable(false)
                .create()
        }
    }
}
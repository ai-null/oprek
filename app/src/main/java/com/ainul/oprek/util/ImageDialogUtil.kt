package com.ainul.oprek.util

import android.Manifest.permission
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import com.ainul.oprek.R
import java.io.File
import java.io.IOException

class ImageDialogUtil(val activity: FragmentActivity) {

    /**
     * This method below handle [R.layout.dialog_choose_image] clickListener
     * it takes [dialog] to hide after clicked and [view] to get the elements from layout
     *
     * @param dialog [AlertDialog]
     * @param view [View]
     */
    fun chooseImage(dialog: AlertDialog, view: View) {
        val itemChooseImage: LinearLayout = view.findViewById(R.id.dialog_item_choose_image)
        val itemTakePhoto: LinearLayout = view.findViewById(R.id.dialog_item_take_photo)

        itemChooseImage.setOnClickListener {
            if (Util.isPermitted(activity, permission.READ_EXTERNAL_STORAGE)) {
                selectImage(activity)
            } else {
                requestPermissions(
                    activity,
                    arrayOf(permission.READ_EXTERNAL_STORAGE),
                    Constants.CHOOSE_IMAGE_REQUEST_CODE
                )
            }
            dialog.dismiss()
        }

        itemTakePhoto.setOnClickListener {
            if (Util.isPermitted(activity, permission.CAMERA)) {
                // launch camera after permission permitted. Show dialog to allow the permission otherwise
                launchCamera(activity)
            } else {
                requestPermissions(
                    activity,
                    arrayOf(permission.CAMERA),
                    Constants.LAUNCH_CAMERA_REQUEST_CODE
                )
            }
            dialog.dismiss()
        }
    }

    fun launchCamera(activity: Activity) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { cameraIntent ->
            val photoFile: File? = try {
                Util.createImageFile(activity)
            } catch (e: IOException) {
                null
            }

            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    activity,
                    "com.ainul.oprek.fileprovider",
                    it
                )
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(
                    activity,
                    cameraIntent,
                    Constants.LAUNCH_CAMERA_REQUEST_CODE,
                    null
                )
            }
        }
    }

    /**
     * select image from Storage,
     *
     * it will launch activity to select image,
     * but the [permission.READ_EXTERNAL_STORAGE] need to be granted
     */
    fun selectImage(activity: Activity) {
        val selectImageIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        val packageManager = activity.packageManager
        if (selectImageIntent.resolveActivity(packageManager) !== null) {
            startActivityForResult(
                activity,
                selectImageIntent,
                Constants.CHOOSE_IMAGE_REQUEST_CODE,
                null
            )
        }
    }
}
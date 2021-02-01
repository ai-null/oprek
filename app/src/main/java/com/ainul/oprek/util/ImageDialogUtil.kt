package com.ainul.oprek.util

import android.Manifest.permission
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.ainul.oprek.R
import java.io.File
import java.io.IOException

class ImageDialogUtil(private val activity: Activity, private val fragment: Fragment?) {

    private val context = fragment?.requireContext() ?: activity.applicationContext
    private val packageManager = activity.packageManager

    /**
     * This method below handle [R.layout.dialog_choose_image] clickListener
     * it takes [dialog] to hide the dialog after being clicked
     * and [view] to get the elements from layout
     *
     * @param dialog
     * @param view [View]
     *
     * @see [selectImage]
     */
    fun chooseImage(dialog: AlertDialog, view: View) {
        val itemChooseImage: LinearLayout = view.findViewById(R.id.dialog_item_choose_image)
        val itemTakePhoto: LinearLayout = view.findViewById(R.id.dialog_item_take_photo)

        itemChooseImage.setOnClickListener {
            if (Util.isPermitted(context, permission.READ_EXTERNAL_STORAGE)) {
                selectImage()
            } else {
                requestPermission(
                    arrayOf(permission.READ_EXTERNAL_STORAGE),
                    Constants.REQUEST_CODE_CHOOSE_IMAGE
                )
            }
            dialog.dismiss()
        }

        itemTakePhoto.setOnClickListener {
            if (Util.isPermitted(context, permission.CAMERA)) {
                // launch camera after permission permitted. Show dialog to allow the permission otherwise
                launchCamera()
            } else {
                requestPermission(
                    arrayOf(permission.CAMERA),
                    Constants.REQUEST_CODE_TAKE_PICTURE
                )
            }
            dialog.dismiss()
        }
    }

    var currentPhotoPath: String = ""
        private set

    fun launchCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { cameraIntent ->
            cameraIntent.resolveActivity(packageManager).also {
                val photoFile: File? = try {
                    Util.createImageFile(activity).apply {
                        currentPhotoPath = absolutePath
                    }
                } catch (e: IOException) {
                    null
                }

                if (photoFile !== null) {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        context,
                        "com.ainul.oprek.fileprovider",
                        photoFile
                    )
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

                    startActivityResult(
                        cameraIntent,
                        Constants.REQUEST_CODE_TAKE_PICTURE,
                    )
                }
            }
        }

    }

    /**
     * select image from Storage,
     *
     * it will launch activity to select image,
     * but the [permission.READ_EXTERNAL_STORAGE] need to be granted
     */
    fun selectImage() {
        val selectImageIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        if (selectImageIntent.resolveActivity(packageManager) !== null) {
            startActivityResult(
                selectImageIntent,
                Constants.REQUEST_CODE_CHOOSE_IMAGE,
            )
        }
    }

    // ======= intent helper =======
    // the intent of this class will be switched to fragment if it set,
    // that's because startActivityResult & requestPermission with activity does not called inside fragment.
    // even tho they have the same method.
    // -
    // so this two methods resolve the problem by switching to one another based on the constructor.

    private fun startActivityResult(intent: Intent, requestCode: Int) {
        if (fragment !== null) {
            fragment.startActivityForResult(
                intent,
                requestCode
            )

            Log.i("context", "start from $fragment")
        } else {
            activity.startActivityForResult(
                intent,
                requestCode
            )
        }
    }

    private fun requestPermission(permissions: Array<String>, requestCode: Int) {
        if (fragment !== null) {
            fragment.requestPermissions(
                permissions,
                requestCode
            )
        } else {
            activity.requestPermissions(
                permissions,
                requestCode
            )
        }
    }
}
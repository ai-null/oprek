package com.ainul.oprek.util

import android.Manifest.permission
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.ainul.oprek.R
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ImageDialogUtil(private val activity: Activity, private val fragment: Fragment? = null) {

    private val context = fragment?.requireContext() ?: activity.applicationContext
    private val packageManager = activity.packageManager

    /**
     * show choose image dialog
     * this method purposed only to show the dialog, all actions handled by [chooseImage].
     */
//    fun showChooseImageDialog() {
//        val dialog = AlertDialog.Builder(context)
//
//        // dialog xml resource
//        val view = activity.layoutInflater.inflate(
//            R.layout.dialog_choose_image,
//            activity.findViewById(R.id.choose_image_container)
//        )
//
//        // set view & create dialog
//        dialog.setView(view)
//        val chooseImageDialog = dialog.create()
//
//        // show dialog
//        chooseImageDialog.window?.run {
//            setBackgroundDrawable(ColorDrawable(0))
//            chooseImageDialog.show()
//        }
//
//        chooseImage(chooseImageDialog, view)
//    }

    /**
     * This method below handle [R.layout.dialog_choose_image] clickListener
     * it takes [dialog] to hide the dialog after being clicked and [view] to get the elements from layout.
     * -
     * When item is clicked, it will execute [Util.isPermitted] to check whether permission is already
     * allowed or not.
     * If allowed proceed to select an image from gallery or launch camera to take a picture.
     * If not proceed to request the permission from the user.
     *
     * @param dialog dialog created with [AlertDialog.Builder]
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

    // File path for photo that just taken,
    // SET action is private only for this class
    var currentPhotoPath: String = ""
        private set

    /**
     * capture photo and saves the file.
     *
     * The Android Camera application saves a full-size photo if you give a file to save into.
     * This will create file with [createImageFile],
     * After file successfully created it will start default camera provided by the phone.
     */
    fun launchCamera() {
        // create an intent to take photo
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { cameraIntent ->
            // ensure that there's camera activity to handle the intent
            cameraIntent.resolveActivity(packageManager).also {
                // create the file
                val photoFile: File? = try {
                    createImageFile(activity).apply {
                        currentPhotoPath = absolutePath // set file path
                    }
                } catch (e: IOException) {
                    // Error while creating file
                    currentPhotoPath = ""
                    null
                }

                // continue only if File successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        context,
                        "com.ainul.oprek.fileprovider",
                        it
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
     * createImageFile,
     * this method takes [Activity] as parameter to get external directory to store created image.
     * the file created come from taken picture or image selected from storage,
     * then store it on specific folder for the app
     *
     * @param activity
     */
    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(activity: Activity): File {
        val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(
            "JPEG_${timestamp}",
            ".jpg",
            storageDir
        )
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
package com.ainul.oprek.util

class Constants {
    enum class Status(val value: Int) {
        PROGRESS(0),
        DONE(1),
        CANCEL(-1);
    }

    companion object {
        // Screen navigation request code
        const val REQUEST_CODE_UPDATE_PROJECT = 1
        const val REQUEST_CODE_CHOOSE_IMAGE = 2
        const val REQUEST_CODE_TAKE_PICTURE = 3
        const val REQUEST_CODE_PROFILE_ACTIVITY = 1

        // Screen navigation result code
        const val RESULT_CODE_UPDATED = 4

        // Screen data pass id
        // const val USER_ID = "USER_ID"
        const val PROJECT_ID = "PROJECT_ID"
        const val IMAGE_PATH = "image_path"

        // AddProjectSave status
        // this will tell whether addProject just updated existing data or create new data
        const val PROJECT_ADDED = 1
        const val PROJECT_UPDATED = 2
    }
}
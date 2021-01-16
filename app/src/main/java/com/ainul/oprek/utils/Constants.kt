package com.ainul.oprek.utils

class Constants {
    enum class Status(val value: Int) {
        PROGRESS(0),
        DONE(1),
        CANCEL(-1);
    }

    companion object {
        // Screen navigation request code
        const val UPDATE_PROJECT_REQUEST_CODE = 1
        const val CHOOSE_IMAGE_REQUEST_CODE = 2
        const val LAUNCH_CAMERA_REQUEST_CODE = 3

        // Screen data pass id
        const val USER_ID = "USER_ID"
        const val PROJECT_ID = "PROJECT_ID"

        // AddProjectSave status
        // this will tell whether addProject just updated existing data or create new data
        const val PROJECT_ADDED = 1
        const val PROJECT_UPDATED = 2
    }
}
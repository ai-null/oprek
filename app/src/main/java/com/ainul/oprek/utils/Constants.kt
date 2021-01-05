package com.ainul.oprek.utils

class Constants {
    enum class Status(val value: Int) {
        PROGRESS(0),
        DONE(1),
        CANCEL(-1);
    }

    companion object {
        // Screen navigation request code
        const val DETAIL_PROJECT_REQUEST_CODE = 1
        const val UPDATE_PROJECT_REQUEST_CODE = 2

        // Screen data pass id
        const val USER_ID = "USER_ID"
        const val PROJECT_ID = "PROJECT_ID"
    }
}
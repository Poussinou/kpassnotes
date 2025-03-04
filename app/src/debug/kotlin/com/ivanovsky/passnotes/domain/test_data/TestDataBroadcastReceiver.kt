package com.ivanovsky.passnotes.domain.test_data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.ivanovsky.passnotes.R
import com.ivanovsky.passnotes.data.repository.settings.Settings
import com.ivanovsky.passnotes.injection.GlobalInjector.get
import timber.log.Timber

class TestDataBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val settings: Settings = get()

        val extras = intent.extras ?: Bundle.EMPTY

        if (extras.containsKey(KEY_IS_RESET_STORED_TEST_DATA)) {
            settings.testData = null
            Timber.d("Test data removed")

            showToast(
                context = context,
                message = context.getString(R.string.test_data_removed)
            )
            return
        }

        val data = TestDataExtractor().extractFromBundle(extras)
        if (data == null) {
            Timber.e("Failed to parse test data")
            showToast(
                context = context,
                message = context.getString(R.string.failed_to_parse_test_data)
            )
            return
        }

        settings.testData = data
        Timber.d("Test data saved successfully")

        showToast(
            context = context,
            message = context.getString(R.string.test_data_successfully_imported)
        )
    }

    private fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG)
            .show()
    }

    companion object {
        private const val KEY_IS_RESET_STORED_TEST_DATA = "isResetStoredTestData"
    }
}
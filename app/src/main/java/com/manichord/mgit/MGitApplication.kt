package com.manichord.mgit

import android.app.Application
import android.content.Context
import android.util.Log
import com.manichord.mgit.utils.transport.MGitHttpConnectionFactory
import com.manichord.mgit.utils.AndroidJschCredentialsProvider
import io.sentry.Sentry
import io.sentry.android.AndroidSentryClientFactory
import com.manichord.mgit.utils.SecurePrefsException
import com.manichord.mgit.utils.SecurePrefsHelper
import com.manichord.mgit.utils.preference.PreferenceHelper
import me.sheimi.sgit.BuildConfig
import me.sheimi.sgit.R
import org.eclipse.jgit.transport.CredentialsProvider
import timber.log.Timber

/**
 * Custom Application Singleton
 */
open class MGitApplication : Application() {
    var securePrefsHelper: SecurePrefsHelper? = null
    var prefenceHelper: PreferenceHelper? = null


    companion object {
        private lateinit var mContext: Context
        private lateinit var mCredentialsProvider: CredentialsProvider
        val context: Context
            get() = mContext

        @JvmStatic fun getContext(): MGitApplication {
            return mContext as MGitApplication
        }

        @JvmStatic fun getJschCredentialsProvider(): CredentialsProvider {
            return mCredentialsProvider
        }

        init {
            MGitHttpConnectionFactory.install()
        }
    }

    override fun onCreate() {
        super.onCreate()
        // only init Sentry if not debug build
        if (!BuildConfig.DEBUG) {
            Sentry.init(AndroidSentryClientFactory(this))
            Log.d("SENTRY", "SENTRY Configured")
        }
        mContext = applicationContext
        setAppVersionPref()
        prefenceHelper = PreferenceHelper(this)
        try {
            securePrefsHelper = SecurePrefsHelper(this)
            mCredentialsProvider =
                AndroidJschCredentialsProvider(
                    securePrefsHelper
                )
        } catch (e: SecurePrefsException) {
            Timber.e(e)
        }
    }

    private fun setAppVersionPref() {
        val sharedPreference = getSharedPreferences(
            getString(R.string.preference_file_key),
            Context.MODE_PRIVATE)
        val version = BuildConfig.VERSION_NAME
        sharedPreference
            .edit()
            .putString(getString(R.string.preference_key_app_version), version)
            .apply()
    }
}

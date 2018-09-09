package com.r0adkll.deckbuilder.arch.data.remote


import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import com.r0adkll.deckbuilder.BuildConfig
import com.r0adkll.deckbuilder.R
import com.r0adkll.deckbuilder.arch.data.features.expansions.model.ExpansionVersion
import com.r0adkll.deckbuilder.arch.data.remote.plugin.RemotePlugin
import com.r0adkll.deckbuilder.arch.domain.features.cards.model.SearchProxies
import io.reactivex.Observable
import timber.log.Timber
import javax.inject.Inject
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty


/**
 * Wrapper around Firebase Remote Configuration SDK
 */
class Remote @Inject constructor(
        val plugins: Set<@JvmSuppressWildcards RemotePlugin>
) {

    /**
     * This is the versioning string for the latest expansion set offered by the api. It's format as
     * follows: <version_code>.<expansion_code> e.g. 1.sm7
     *
     * - version_code represents the version of the data that may change unrelated to new expansions (i.e. rotation legality changes)
     * - expansion_code represents the latest available expansion in the set (i.e. sm7 - Celestial Storm) which can indicate if a new expansion was added
     */
    val expansionVersion by RemoteObject(KEY_EXPANSION_VERSION, ExpansionVersion::class)


    /**
     * This is a list of search proxy/aliases that better improve the search experience for the user
     */
    val searchProxies by RemoteObject(KEY_SEARCH_PROXIES, SearchProxies::class)


    /**
     * Property to access the Firebase Remote Config instance
     */
    private val remote: FirebaseRemoteConfig
        get() = FirebaseRemoteConfig.getInstance()


    /**
     * Check for update remote config values and update them if needed. Also set
     * remote configuration settings if needed
     */
    fun check() {
        Timber.d("Checking remote config values...")

        // Configure
        val settings = FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build()
        remote.setConfigSettings(settings)
        remote.setDefaults(R.xml.remote_config_defaults)


        // Fetch
        val cacheExpiration = if (remote.info.configSettings.isDeveloperModeEnabled) 0L else CACHE_EXPIRATION
        remote.fetch(cacheExpiration)
                .addOnCompleteListener { _ ->
                    Timber.i("Remote Config values fetched. Activating!")
                    Timber.i("> Expansion Version: $expansionVersion")
                    Timber.i("> Search Proxies: $searchProxies")
                    remote.activateFetched()
                    plugins.forEach { it.onFetchActivated(this@Remote) }
                }
    }


    /**
     * Remote property class that fetches a string from the remote object
     */
    private class RemoteString(val key: String): ReadOnlyProperty<Remote, String> {

        override fun getValue(thisRef: Remote, property: KProperty<*>): String {
            return thisRef.remote.getString(key)
        }
    }


    /**
     * JSON based object property class for fetching and converting remote json objects
     */
    private class RemoteObject<out T : Any>(val key: String, private val clazz: KClass<out T>): ReadOnlyProperty<Remote, T?> {

        private val gson = Gson()

        override fun getValue(thisRef: Remote, property: KProperty<*>): T? {
            val json = thisRef.remote.getString(key)
            return try {
                gson.fromJson<T>(json, clazz.java)
            } catch (e: JsonParseException) {
                null
            } catch (e1: JsonSyntaxException) {
                null
            }
        }
    }


    companion object {
        private const val KEY_EXPANSION_VERSION = "expansion_version"
        private const val KEY_SEARCH_PROXIES = "search_proxies"

        private const val CACHE_EXPIRATION = 3600L
    }
}
package com.r0adkll.deckbuilder.arch.data.remote.plugin

import android.annotation.SuppressLint
import com.r0adkll.deckbuilder.arch.data.AppPreferences
import com.r0adkll.deckbuilder.arch.data.features.expansions.cache.ExpansionCache
import com.r0adkll.deckbuilder.arch.data.features.expansions.repository.source.ExpansionDataSource
import com.r0adkll.deckbuilder.arch.domain.features.remote.Remote
import com.r0adkll.deckbuilder.util.AppSchedulers
import timber.log.Timber

class CacheInvalidatePlugin(
        val expansionDataSource: ExpansionDataSource,
        val preferences: AppPreferences,
        val schedulers: AppSchedulers
) : RemotePlugin {

    @SuppressLint("CheckResult", "RxLeakedSubscription")
    override fun onFetchActivated(remote: Remote) {
        // Verify cache, and invalidate if needed
        remote.expansionVersion?.let { (versionCode, expansionCode) ->
            Timber.d("Checking Expansion Cache (version: $versionCode, expansion: $expansionCode, prefVersion: ${preferences.expansionsVersion})")

            try {
                val invalidCache = expansionDataSource.getExpansions(ExpansionCache.Source.LOCAL)
                        .blockingFirst().none { it.code == expansionCode }

                if (versionCode > preferences.expansionsVersion || invalidCache) {
                    Timber.i("Expansion Cache Invalidated, Refreshing from server (version: $versionCode, expansion: $expansionCode)")
                    expansionDataSource.refreshExpansions()
                            .subscribeOn(schedulers.network)
                            .subscribe({
                                Timber.d("Expansions refreshed, updating version(${preferences.expansionsVersion} > $versionCode)")
                                preferences.expansionsVersion = versionCode
                            }, {
                                Timber.e(it, "Unable to refresh expansions, keeping old cache")
                            })
                }
            } catch (e: NoSuchElementException) {
                Timber.e("Unable to verify local expansions")
            }
        }
    }
}

package com.r0adkll.deckbuilder.arch.ui.features.collection.set

import android.os.Parcelable
import com.ftinc.kit.arch.presentation.BaseActions
import com.ftinc.kit.arch.presentation.state.BaseState
import com.ftinc.kit.arch.presentation.state.Ui
import com.r0adkll.deckbuilder.arch.domain.features.cards.model.Expansion
import com.r0adkll.deckbuilder.arch.domain.features.cards.model.Filter
import com.r0adkll.deckbuilder.arch.domain.features.cards.model.PokemonCard
import com.r0adkll.deckbuilder.arch.domain.features.cards.model.StackedPokemonCard
import com.r0adkll.deckbuilder.arch.domain.features.collection.model.CollectionCount
import com.r0adkll.deckbuilder.arch.ui.features.collection.set.CollectionSetUi.State
import com.r0adkll.deckbuilder.arch.ui.features.collection.set.CollectionSetUi.State.Change
import com.r0adkll.deckbuilder.util.extensions.findAndReplace
import com.r0adkll.deckbuilder.util.extensions.findAndUpdate
import io.reactivex.Observable
import kotlinx.android.parcel.Parcelize


interface CollectionSetUi : Ui<State, Change> {

    interface Intentions {

        fun addCard(): Observable<List<PokemonCard>>
        fun removeCard(): Observable<PokemonCard>
    }

    interface Actions : BaseActions {

        fun showOverallProgress(progress: Float)
        fun showCollection(cards: List<StackedPokemonCard>)
    }

    @Parcelize
    data class State(
            override val isLoading: Boolean,
            override val error: String?,
            val expansion: Expansion?,
            val cards: List<PokemonCard>,
            val counts: List<CollectionCount>
    ): BaseState<Change>(isLoading, error), Parcelable {

        val searchFilter: Filter
            get() = Filter.DEFAULT.copy(expansions = listOf(expansion!!), pageSize = 1000)

        override fun reduce(change: Change): Ui.State<Change> = when(change) {
            Change.IsLoading -> copy(isLoading = true, error = null)
            is Change.Error -> copy(error = change.description, isLoading = false)
            is Change.Cards -> copy(cards = change.cards, isLoading = false)
            is Change.Counts -> copy(counts = change.counts)
            is Change.CountsUpdated -> copy(counts = updateCounts(change.counts))
            is Change.CountChanged -> copy(counts = counts.findAndUpdate(
                    { it.id == change.card.id },
                    {
                        CollectionCount(
                                change.card.id,
                                (it?.count ?: 0) + change.count,
                                change.card.expansion?.code ?: it?.set ?: "",
                                change.card.expansion?.series ?: it?.series ?: ""
                        )
                    }))
        }

        sealed class Change(logText: String): Ui.State.Change(logText) {
            object IsLoading : Change("user -> loading started")
            class Error(val description: String) : Change("error -> $description")
            class Cards(val cards: List<PokemonCard>) : Change("network -> cards loaded (${cards.size})")
            class Counts(val counts: List<CollectionCount>) : Change("network -> counts loaded (${counts.size})")
            class CountsUpdated(val counts: List<CollectionCount>) : Change("network -> counts updated (${counts.size})")
            class CountChanged(val card: PokemonCard, val count: Int): Change("user -> count changed ($count)")
        }

        override fun toString(): String {
            return "State(isLoading=$isLoading, error=$error, expansion=${expansion?.code}, cards=${cards.size}, counts=${counts.size})"
        }

        private fun updateCounts(updatedCounts: List<CollectionCount>): List<CollectionCount> {
            val existingCounts = counts.toMutableList()
            val newCounts = ArrayList<CollectionCount>()

            existingCounts.removeAll { updatedCounts.any { u -> it.id == u.id} }
            newCounts += updatedCounts
            newCounts += existingCounts

            return newCounts
        }

        companion object {

            val DEFAULT by lazy {
                State(false, null, null, emptyList(), emptyList())
            }
        }
    }
}
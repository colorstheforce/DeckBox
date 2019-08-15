package com.r0adkll.deckbuilder.arch.ui.features.deckbuilder


import com.r0adkll.deckbuilder.arch.domain.Format
import com.r0adkll.deckbuilder.arch.domain.features.cards.model.PokemonCard
import com.r0adkll.deckbuilder.arch.domain.features.cards.model.StackedPokemonCard
import com.r0adkll.deckbuilder.arch.domain.features.collection.model.CollectionCount
import com.r0adkll.deckbuilder.arch.domain.features.editing.model.Session
import com.r0adkll.deckbuilder.arch.domain.features.validation.model.Validation
import com.r0adkll.deckbuilder.arch.ui.components.renderers.StateRenderer
import com.r0adkll.deckbuilder.arch.ui.features.deckbuilder.deckimage.adapter.DeckImage
import io.pokemontcg.model.SuperType
import io.pokemontcg.model.SuperType.*
import io.reactivex.Observable
import paperparcel.PaperParcel
import paperparcel.PaperParcelable


interface DeckBuilderUi : StateRenderer<DeckBuilderUi.State>{

    val state: State


    interface Intentions {

        fun saveDeck(): Observable<Unit>
        fun addCards(): Observable<List<PokemonCard>>
        fun removeCard(): Observable<PokemonCard>
        fun editDeckClicks(): Observable<Boolean>
        fun editOverviewClicks(): Observable<Boolean>
        fun editDeckName(): Observable<String>
        fun editDeckDescription(): Observable<String>
        fun editDeckCollectionOnly(): Observable<Boolean>
    }


    interface Actions {

        fun showBrokenRules(errors: List<Int>)
        fun showFormat(format: Format)
        fun showIsSaving(isSaving: Boolean)
        fun showIsEditing(isEditing: Boolean)
        fun showIsOverview(isOverview: Boolean)
        fun showError(description: String)
        fun showSaveAction(hasChanges: Boolean)
        fun showCardCount(count: Int)
        fun showPokemonCards(cards: List<StackedPokemonCard>)
        fun showTrainerCards(cards: List<StackedPokemonCard>)
        fun showEnergyCards(cards: List<StackedPokemonCard>)
        fun showDeckName(name: String)
        fun showDeckDescription(description: String)
        fun showDeckImage(image: DeckImage?)
        fun showDeckCollectionOnly(collectionOnly: Boolean)
    }


    @PaperParcel
    data class State @JvmOverloads constructor(
            val sessionId: Long,

            val isSaving: Boolean,
            val isEditing: Boolean,
            val isChanged: Boolean,
            val isOverview: Boolean,
            val error: String?,

            val name: String?,
            val description: String?,
            val image: DeckImage?,
            val collectionOnly: Boolean,

            val validation: Validation,

            @Transient val pokemonCards: List<PokemonCard> = emptyList(),
            @Transient val trainerCards: List<PokemonCard> = emptyList(),
            @Transient val energyCards: List<PokemonCard> = emptyList(),
            @Transient val collectionCounts: List<CollectionCount> = emptyList()
    ) : PaperParcelable {

        val allCards: List<PokemonCard>
            get() = pokemonCards.plus(trainerCards).plus(energyCards)


        fun reduce(change: Change): State = when(change) {
            Change.Saving -> this.copy(isSaving = true, error = null)
            Change.Saved -> this.copy(isSaving = false)
            is Change.SessionUpdated -> this.copy(
                    pokemonCards = change.session.cards.filter { it.supertype == SuperType.POKEMON },
                    trainerCards = change.session.cards.filter { it.supertype == SuperType.TRAINER },
                    energyCards = change.session.cards.filter { it.supertype == SuperType.ENERGY },
                    name = change.session.name,
                    description = change.session.description,
                    image = change.session.image,
                    collectionOnly = change.session.collectionOnly,
                    isChanged = change.session.hasChanges,
                    isSaving = false,
                    error = null
            )
            is Change.Editing -> this.copy(isEditing = change.isEditing)
            is Change.Overview -> this.copy(isOverview = change.isOverview)
            is Change.Error -> this.copy(error = change.description)
            is Change.AddCards -> {
                val pokemons = change.cards.filter { it.supertype == POKEMON }
                val trainers = change.cards.filter { it.supertype == TRAINER }
                val energies = change.cards.filter { it.supertype == ENERGY }
                this.copy(pokemonCards = pokemonCards.plus(pokemons),
                        trainerCards = trainerCards.plus(trainers),
                        energyCards = energyCards.plus(energies))
            }
            is Change.RemoveCard -> when(change.card.supertype) {
                POKEMON -> this.copy(pokemonCards = pokemonCards.minus(change.card))
                TRAINER -> this.copy(trainerCards = trainerCards.minus(change.card))
                ENERGY -> this.copy(energyCards = energyCards.minus(change.card))
                UNKNOWN -> this
            }
            is Change.EditCards -> this.copy(
                    pokemonCards = change.cards.filter { it.supertype == POKEMON },
                    trainerCards = change.cards.filter { it.supertype == TRAINER },
                    energyCards = change.cards.filter { it.supertype == ENERGY })
            is Change.EditName -> this.copy(name = change.name)
            is Change.EditDescription -> this.copy(description = change.description)
            is Change.Validated -> this.copy(validation = change.validation)
            is Change.CollectionCounts -> this.copy(collectionCounts = change.counts)
        }


        override fun toString(): String {
            return "State(sessionId=$sessionId, " +
                    "isSaving=$isSaving, " +
                    "isEditing=$isEditing, " +
                    "isChanged=$isChanged, " +
                    "isOverview=$isOverview, " +
                    "error=$error, " +
                    "pokemonCards=${pokemonCards.size}, " +
                    "trainerCards=${trainerCards.size}, " +
                    "energyCards=${energyCards.size}, " +
                    "name=$name, " +
                    "description=$description, " +
                    "image=$image, " +
                    "validation=$validation, " +
                    "collection=${collectionCounts.size})"
        }


        sealed class Change(val logText: String) {
            object Saving : Change("user -> is saving deck")
            object Saved : Change("network -> deck saved!")
            class SessionUpdated(val session: Session) : Change("cache -> Session changed/updated $session")
            class Editing(val isEditing: Boolean) : Change("user -> is editing: $isEditing")
            class Overview(val isOverview: Boolean) : Change("user -> is overview: $isOverview")
            class Error(val description: String) : Change("error -> $description")
            class AddCards(val cards: List<PokemonCard>) : Change("user -> added ${cards.size} cards")
            class RemoveCard(val card: PokemonCard) : Change("user -> removing ${card.name}")
            class EditCards(val cards: List<PokemonCard>) : Change("user -> edited all cards: ${cards.size}")
            class EditName(val name: String) : Change("user -> name changed $name")
            class EditDescription(val description: String) : Change ("user -> desc changed $description")
            class Validated(val validation: Validation) : Change("cache -> validated: $validation")
            class CollectionCounts(val counts: List<CollectionCount>) : Change("cache -> collection count loaded/changed: ${counts.size}")
        }


        companion object {
            @JvmField val CREATOR = PaperParcelDeckBuilderUi_State.CREATOR

            val DEFAULT by lazy {
                State(-1L, false, false, false, false, null, null, null, null, false,
                        Validation(false, false, emptyList()), emptyList(), emptyList(), emptyList())
            }
        }
    }
}
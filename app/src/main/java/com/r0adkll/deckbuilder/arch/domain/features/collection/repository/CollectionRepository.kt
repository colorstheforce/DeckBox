package com.r0adkll.deckbuilder.arch.domain.features.collection.repository

import com.r0adkll.deckbuilder.arch.domain.features.cards.model.PokemonCard
import com.r0adkll.deckbuilder.arch.domain.features.collection.model.CollectionCount
import io.reactivex.Observable


interface CollectionRepository {

    /**
     * Observe all counts in your collection
     */
    fun observeAll(): Observable<List<CollectionCount>>

    /**
     * Get a collection count for a single card by it's id
     * @param cardId the id, i.e. sm9-1, of the card you want the collection count of
     */
    fun getCount(cardId: String): Observable<CollectionCount>

    /**
     * Get a list collection counts for multiple cards by their ids
     * @param cardIds the list of card ids to get counts for
     */
    fun getCounts(cardIds: List<String>): Observable<List<CollectionCount>>

    /**
     * Get a list of collection counts for an entire expansion set
     * @param set the expansion set code, i.e. sm9, that you want counts for
     */
    fun getCountForSet(set: String): Observable<List<CollectionCount>>

    /**
     * Get a list of collection counts for an entire series
     * @param series the series, i.e. Sun & Moon, that you want to get counts for
     */
    fun getCountForSeries(series: String): Observable<List<CollectionCount>>

    /**
     * Increment the collection count of the provided pokemon card
     * @param card the card to increment the collection count of
     */
    fun incrementCount(card: PokemonCard): Observable<CollectionCount>

    /**
     * Decrement the collection count of the provided pokemon card
     * @param card the card to decrement the collection count of
     */
    fun decrementCount(card: PokemonCard): Observable<CollectionCount>
}
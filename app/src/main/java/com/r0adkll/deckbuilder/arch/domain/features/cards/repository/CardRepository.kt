package com.r0adkll.deckbuilder.arch.domain.features.cards.repository


import com.r0adkll.deckbuilder.arch.domain.features.cards.model.Expansion
import com.r0adkll.deckbuilder.arch.domain.features.cards.model.PokemonCard
import io.pokemontcg.model.SuperType
import io.reactivex.Observable


interface CardRepository {

    fun getExpansions(): Observable<List<Expansion>>
    fun search(type: SuperType, text: String): Observable<List<PokemonCard>>
}
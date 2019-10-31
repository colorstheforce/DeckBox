package com.r0adkll.deckbuilder.arch.ui.features.search.adapter

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import android.view.ViewGroup
import com.r0adkll.deckbuilder.arch.domain.features.cards.model.PokemonCard
import com.r0adkll.deckbuilder.arch.ui.components.ListRecyclerAdapter
import com.r0adkll.deckbuilder.arch.ui.components.RecyclerViewBinding
import com.r0adkll.deckbuilder.arch.ui.components.EditCardIntentions

class SearchResultsRecyclerAdapter(
        context: Context,
        val instantDragSupport: Boolean = false,
        val editCardIntentions: EditCardIntentions = EditCardIntentions()
) : ListRecyclerAdapter<PokemonCard, PokemonCardViewHolder>(context) {

    private var selectedCards: List<PokemonCard> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonCardViewHolder {
        return PokemonCardViewHolder.create(inflater, parent, true, instantDragSupport,
                editCardIntentions.removeCardClicks, editCardIntentions.addCardClicks)
    }

    override fun onBindViewHolder(vh: PokemonCardViewHolder, i: Int) {
        super.onBindViewHolder(vh, i)
        val card = items[i]
        val count = selectedCards.count { it.id == card.id }
        vh.bind(card, count, isEditMode = true)
    }

    fun setCards(cards: List<PokemonCard>) {
        val diff = calculateDiff(items, cards)
        items = ArrayList(diff.new)
        diff.diff.dispatchUpdatesTo(DiffUpdateCallback())
    }

    fun setSelectedCards(cards: List<PokemonCard>) {
        selectedCards = cards
        notifyDataSetChanged()
    }

    fun indexOf(card: PokemonCard): Int {
        return items.indexOf(card)
    }

    companion object {
        private fun calculateDiff(old: List<PokemonCard>, new: List<PokemonCard>): RecyclerViewBinding<PokemonCard> {
            val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    val oldItem = old[oldItemPosition]
                    val newItem = new[newItemPosition]
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    val oldItem = old[oldItemPosition]
                    val newItem = new[newItemPosition]
                    return oldItem == newItem
                }

                override fun getOldListSize(): Int = old.size
                override fun getNewListSize(): Int = new.size
            })

            return RecyclerViewBinding(new = new, diff = diff)
        }
    }
}

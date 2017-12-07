package com.r0adkll.deckbuilder.arch.ui.features.filter


import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SimpleItemAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.r0adkll.deckbuilder.R
import com.r0adkll.deckbuilder.arch.ui.components.BaseFragment
import com.r0adkll.deckbuilder.arch.ui.features.search.DrawerInteractor
import com.r0adkll.deckbuilder.arch.ui.features.search.SearchActivity
import com.r0adkll.deckbuilder.arch.ui.features.filter.FilterUi.FilterAttribute
import com.r0adkll.deckbuilder.arch.ui.features.filter.FilterUi.State
import com.r0adkll.deckbuilder.arch.ui.features.filter.adapter.FilterRecyclerAdapter
import com.r0adkll.deckbuilder.arch.ui.features.filter.adapter.Item
import com.r0adkll.deckbuilder.arch.ui.features.filter.di.FilterModule
import com.r0adkll.deckbuilder.arch.ui.features.filter.di.FilterableComponent
import io.pokemontcg.model.Type
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_filter.*
import javax.inject.Inject


class FilterFragment : BaseFragment(), FilterUi, FilterUi.Intentions, FilterUi.Actions {

    @com.evernote.android.state.State
    override var state: State = State.DEFAULT
    private val filterIntentions: FilterIntentions = FilterIntentions()

    @Inject lateinit var renderer: FilterRenderer
    @Inject lateinit var presenter: FilterPresenter
    @Inject lateinit var drawerInteractor: DrawerInteractor

    private lateinit var adapter: FilterRecyclerAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_filter, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        toolbar.setNavigationOnClickListener { drawerInteractor.closeDrawer() }
        toolbar.inflateMenu(R.menu.fragment_filter)
        toolbar.setOnMenuItemClickListener {
            when {
                it.itemId == R.id.action_clear_filter -> {
                    filterIntentions.clearFilter.accept(Unit)
                    true
                }
                else -> false
            }
        }

        adapter = FilterRecyclerAdapter(activity!!, filterIntentions)
        recycler.layoutManager = LinearLayoutManager(activity)
        recycler.adapter = adapter
        (recycler.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        if (activity is SearchActivity) {
            val type = (activity as SearchActivity).superType
            state = state.copy(category = type)
        }

        renderer.start()
        presenter.start()
    }


    override fun setupComponent() {
        getComponent(FilterableComponent::class)
                .plus(FilterModule(this))
                .inject(this)
    }


    override fun onDestroy() {
        presenter.stop()
        renderer.stop()
        super.onDestroy()
    }


    override fun render(state: FilterUi.State) {
        this.state = state
        renderer.render(state)
    }


    override fun typeClicks(): Observable<Pair<String, Type>> = filterIntentions.typeClicks
    override fun attributeClicks(): Observable<FilterAttribute> = filterIntentions.attributeClicks
    override fun optionClicks(): Observable<Pair<String, Any>> = filterIntentions.optionClicks
    override fun viewMoreClicks(): Observable<Unit> = filterIntentions.viewMoreClicks
    override fun valueRangeChanges(): Observable<Pair<String, Item.ValueRange.Value>> = filterIntentions.valueRangeChanges
    override fun clearFilter(): Observable<Unit> = filterIntentions.clearFilter


    override fun setIsEmpty(isEmpty: Boolean) {
        toolbar.menu.findItem(R.id.action_clear_filter)?.isVisible = !isEmpty
    }


    override fun setItems(items: List<Item>) {
        adapter.setFilterItems(items)
    }
}
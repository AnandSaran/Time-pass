package com.gregantech.timepass.widget

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class GridPaginationScrollListener
/**
 * Supporting only GridLayoutManager for now.
 *
 * @param layoutManager
 */
    (var layoutManager: GridLayoutManager) : RecyclerView.OnScrollListener() {

    abstract fun isLastPage(): Boolean

    abstract fun isLoading(): Boolean

    private var firstVisibleItem = 0
    @Volatile
    private var mEnabled = true
    private var mPreLoadCount = 0

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val visibleItemCount = layoutManager.childCount
        val totalItemCount = layoutManager.itemCount
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

        if (!isLoading() && !isLastPage()) {
            if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                loadMoreItems()
            }
        }

        if (mEnabled) {
            val manager = recyclerView.layoutManager
            require(manager is GridLayoutManager) { "Expected recyclerview to have linear layout manager" }
            firstVisibleItem = manager.findFirstCompletelyVisibleItemPosition()
            onItemIsFirstVisibleItem(firstVisibleItem)
        }
    }

    /**
     * Called when end of scroll is reached.
     *
     * @param recyclerView - related recycler view.
     */
    abstract fun onItemIsFirstVisibleItem(index: Int)
    fun disableScrollListener() {
        mEnabled = false
    }

    fun enableScrollListener() {
        mEnabled = true
    }

    fun setPreLoadCount(mPreLoadCount: Int) {
        this.mPreLoadCount = mPreLoadCount
    }

    abstract fun loadMoreItems()
}
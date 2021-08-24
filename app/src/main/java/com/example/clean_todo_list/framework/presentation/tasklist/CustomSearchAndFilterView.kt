package com.example.clean_todo_list.framework.presentation.tasklist

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.example.clean_todo_list.R
import com.example.clean_todo_list.framework.presentation.utils.invisible
import com.example.clean_todo_list.framework.presentation.utils.visible

class CustomSearchAndFilterView(
    context: Context,
    attributeSet: AttributeSet? = null
) : FrameLayout(
    context,
    attributeSet
), View.OnClickListener {

    private var searchViewState: SearchViewState = SearchViewState.INVISIBLE

    var interaction: Interaction? = null

    //ui components
    //containers
    private val searchStateContainer: LinearLayout
    private val defaultStateContainer: LinearLayout

    //search
    private val searchBackBtn: ImageButton
    private val searchSearchEdt: EditText
    private val searchRemoveBtn: ImageButton

    //default
    private val defaultTitleTxt: TextView
    private val defaultSearchBtn: ImageButton
    private val defaultFilterBtn: ImageButton

    init {
        LayoutInflater.from(context)
            .inflate(R.layout.layout_custom_search_and_filter_view, this, true)
        //findView
        searchStateContainer = findViewById(R.id.search_state_container)
        defaultStateContainer = findViewById(R.id.default_state_container)

        searchBackBtn = findViewById(R.id.search_back_btn)
        searchSearchEdt = findViewById(R.id.search_search_edt)
        searchRemoveBtn = findViewById(R.id.search_remove_btn)

        defaultTitleTxt = findViewById(R.id.default_title_txt)
        defaultSearchBtn = findViewById(R.id.default_search_btn)
        defaultFilterBtn = findViewById(R.id.default_filter_btn)

        searchStateContainer.setOnClickListener(this)
        defaultStateContainer.setOnClickListener(this)

        searchBackBtn.setOnClickListener(this)
        searchSearchEdt.setOnClickListener(this)
        searchRemoveBtn.setOnClickListener(this)

        defaultTitleTxt.setOnClickListener(this)
        defaultSearchBtn.setOnClickListener(this)
        defaultFilterBtn.setOnClickListener(this)

    }

    private val onSearchViewTextChangeListener = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (p0.isNullOrEmpty()) {
                searchRemoveBtn.invisible()
            } else {
                searchRemoveBtn.visible()
            }

        }

        override fun afterTextChanged(p0: Editable?) {
            interaction?.onSearchTextChanged(p0.toString())
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.search_back_btn -> {
                setStateToDefault()
            }

            R.id.search_remove_btn -> {
                searchSearchEdt.setText("")
            }

            R.id.default_search_btn -> {
                setStateToSearching()
            }
            R.id.default_filter_btn -> {
                interaction?.onFilterButtonClicked()
            }

        }
    }

    private fun setStateToSearching() {
        interaction?.forceKeyBoardToOpenForEditText(searchSearchEdt)

        defaultStateContainer.invisible()
        searchStateContainer.visible()
        //others
        searchSearchEdt.addTextChangedListener(onSearchViewTextChangeListener)
        //change state
        searchViewState = SearchViewState.VISIBLE
        interaction?.onSearchStateChanged(SearchViewState.VISIBLE)

    }

    private fun setStateToDefault() {

        interaction?.hideSoftKeyboard()

        defaultStateContainer.visible()
        searchStateContainer.invisible()
        //others
        searchSearchEdt.removeTextChangedListener(onSearchViewTextChangeListener)
        searchSearchEdt.setText("")
        interaction?.onSearchTextChanged("")
        //change state
        searchViewState = SearchViewState.INVISIBLE
        interaction?.onSearchStateChanged(SearchViewState.INVISIBLE)

    }

    fun onBackClicked(): Boolean {
        return if (searchViewState.isVisible()) {
            setStateToDefault()
            false
        } else {
            true
        }
    }

    sealed class SearchViewState {
        object VISIBLE : SearchViewState()
        object INVISIBLE : SearchViewState()

        fun isVisible(): Boolean = this == VISIBLE
        fun isInvisible(): Boolean = this == INVISIBLE
    }

    interface Interaction {

        fun forceKeyBoardToOpenForEditText(editText: EditText)

        fun hideSoftKeyboard()

        fun onFilterButtonClicked()

        fun onSearchTextChanged(text: String)

        fun onSearchStateChanged(newState: SearchViewState)

    }
}

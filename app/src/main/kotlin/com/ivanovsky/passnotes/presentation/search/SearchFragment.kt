package com.ivanovsky.passnotes.presentation.search

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.autofill.AutofillManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.ivanovsky.passnotes.R
import com.ivanovsky.passnotes.data.entity.Note
import com.ivanovsky.passnotes.databinding.SearchFragmentBinding
import com.ivanovsky.passnotes.injection.GlobalInjector
import com.ivanovsky.passnotes.presentation.autofill.AutofillResponseFactory
import com.ivanovsky.passnotes.presentation.autofill.model.AutofillStructure
import com.ivanovsky.passnotes.presentation.core.BaseFragment
import com.ivanovsky.passnotes.presentation.core.extensions.getMandatoryArgument
import com.ivanovsky.passnotes.presentation.core.extensions.hideKeyboard
import com.ivanovsky.passnotes.presentation.core.extensions.setupActionBar
import com.ivanovsky.passnotes.presentation.core.extensions.withArguments

class SearchFragment : BaseFragment() {

    private val viewModel: SearchViewModel by lazy {
        ViewModelProvider(
            this,
            SearchViewModel.Factory(
                args = getMandatoryArgument(ARGUMENTS)
            )
        )
            .get(SearchViewModel::class.java)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        setupActionBar {
            title = getString(R.string.search)
            setHomeAsUpIndicator(null)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search, menu)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return SearchFragmentBinding.inflate(inflater, container, false)
            .also {
                it.lifecycleOwner = viewLifecycleOwner
                it.viewModel = viewModel
            }
            .root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                viewModel.navigateBack()
                true
            }
            R.id.menu_lock -> {
                viewModel.onLockButtonClicked()
                true
            }
            R.id.menu_settings -> {
                viewModel.onSettingsButtonClicked()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStart() {
        super.onStart()
        navigationViewModel.setNavigationEnabled(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToEvents()
    }

    private fun subscribeToEvents() {
        viewModel.hideKeyboardEvent.observe(viewLifecycleOwner) {
            hideKeyboard()
        }
        viewModel.setAutofillResponse.observe(viewLifecycleOwner) {
            setAutofillResult(it.first, it.second)
        }
    }

    private fun setAutofillResult(note: Note, structure: AutofillStructure) {
        if (Build.VERSION.SDK_INT < 26) {
            return
        }

        val factory = AutofillResponseFactory(requireContext(), GlobalInjector.get())
        val response = factory.createResponseWithNote(note, structure)

        val result = Intent()
            .apply {
                putExtra(AutofillManager.EXTRA_AUTHENTICATION_RESULT, response)
            }

        requireActivity()
            .apply {
                setResult(Activity.RESULT_OK, result)
                finish()
            }
    }

    companion object {

        private const val ARGUMENTS = "arguments"

        fun newInstance(args: SearchScreenArgs): SearchFragment = SearchFragment()
            .withArguments {
                putParcelable(ARGUMENTS, args)
            }
    }
}
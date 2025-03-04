package com.ivanovsky.passnotes.presentation.server_login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.observe
import com.ivanovsky.passnotes.R
import com.ivanovsky.passnotes.databinding.ServerLoginFragmentBinding
import com.ivanovsky.passnotes.presentation.core.FragmentWithDoneButton
import com.ivanovsky.passnotes.presentation.core.extensions.getMandatoryArgument
import com.ivanovsky.passnotes.presentation.core.extensions.hideKeyboard
import com.ivanovsky.passnotes.presentation.core.extensions.setupActionBar
import com.ivanovsky.passnotes.presentation.core.extensions.withArguments
import com.ivanovsky.passnotes.presentation.server_login.model.LoginType
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ServerLoginFragment : FragmentWithDoneButton() {

    private val args by lazy { getMandatoryArgument<ServerLoginArgs>(ARGUMENTS) }

    private val viewModel: ServerLoginViewModel by viewModel {
        parametersOf(args)
    }

    private lateinit var binding: ServerLoginFragmentBinding

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupActionBar {
            title = when (args.loginType) {
                LoginType.USERNAME_PASSWORD -> getString(R.string.login_to_server)
                LoginType.GIT -> getString(R.string.login_to_git)
            }
            setHomeAsUpIndicator(null)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ServerLoginFragmentBinding.inflate(inflater, container, false)
            .also {
                it.lifecycleOwner = viewLifecycleOwner
                it.viewModel = viewModel
            }

        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                viewModel.navigateBack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.isDoneButtonVisible.observe(viewLifecycleOwner) { isVisible ->
            setDoneButtonVisibility(isVisible)
        }
        viewModel.hideKeyboardEvent.observe(viewLifecycleOwner) {
            hideKeyboard()
        }
    }

    override fun onDoneMenuClicked() {
        viewModel.authenticate()
    }

    companion object {

        private const val ARGUMENTS = "arguments"

        fun newInstance(args: ServerLoginArgs) = ServerLoginFragment().withArguments {
            putParcelable(ARGUMENTS, args)
        }
    }
}
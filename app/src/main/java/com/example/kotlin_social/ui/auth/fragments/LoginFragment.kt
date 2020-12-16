package com.example.kotlin_social.ui.auth.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kotlin_social.R
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment(R.layout.fragment_login) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvRegisterNewAccount.setOnClickListener {
            if (findNavController().previousBackStackEntry != null) {
                findNavController().popBackStack();
            } else findNavController()
                .navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }
    }
}
package com.nemislimus.tratometr.authorization.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import com.nemislimus.tratometr.R
import com.nemislimus.tratometr.common.util.BindingFragment
import com.nemislimus.tratometr.databinding.FragmentPrivacyPolicyBinding

class PrivacyPolicyFragment : BindingFragment<FragmentPrivacyPolicyBinding>() {

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPrivacyPolicyBinding {
        return FragmentPrivacyPolicyBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.privacyTextView.text = HtmlCompat.fromHtml(
            getString(R.string.privacy_policy_full_text),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    }
}
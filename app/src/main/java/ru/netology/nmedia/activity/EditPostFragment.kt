package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.EditPostFragmentBinding
import ru.netology.nmedia.utils.AndroidUtils
import ru.netology.nmedia.utils.StringArg
import ru.netology.nmedia.viewModel.PostViewModel

@kotlinx.coroutines.ExperimentalCoroutinesApi
class EditPostFragment : Fragment() {
    companion object {
        var Bundle.edit: String? by StringArg

    }

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = EditPostFragmentBinding.inflate(inflater, container, false)

        arguments?.edit?.let(binding.editText::setText)
        binding.editText.setText(arguments?.getString("editedText"))


        binding.ok.setOnClickListener {
            if (binding.editText.text.isNullOrBlank()) {
                Toast.makeText(
                    activity,
                    this.getString(R.string.error_empty_content),
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                viewModel.changeContent(binding.editText.text.toString())
                viewModel.save()
                AndroidUtils.hideKeyboard(requireView())
                findNavController().navigateUp()
            }

        }

        binding.cancelButton.setOnClickListener {
            AndroidUtils.hideKeyboard(requireView())
            findNavController().navigateUp()
        }

        return binding.root
    }
}
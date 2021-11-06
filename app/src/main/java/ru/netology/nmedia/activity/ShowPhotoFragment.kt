package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.with
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.PostAdapter

import ru.netology.nmedia.databinding.FragmentShowPhotoBinding
import ru.netology.nmedia.utils.*

import ru.netology.nmedia.viewModel.PostViewModel

class ShowPhotoFragment : Fragment() {


        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            val binding = FragmentShowPhotoBinding.inflate(inflater, container, false)


            val url = "http://10.0.2.2:9999/media/${requireArguments().getString("url")}"

            GlideApp.with(binding.shownPhoto)
                .load(url)
                .placeholder(R.drawable.ic_baseline_image_placeholder_24)
                .error(R.drawable.ic_baseline_error_24)
                .into(binding.shownPhoto)



            return binding.root
        }
    }

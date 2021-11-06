package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.viewModel.PostViewModel

class FeedFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels(
    ownerProducer = ::requireParentFragment
)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val binding = FragmentFeedBinding.inflate(inflater,container, false)


        val adapter = PostAdapter(object : OnInteractionListener{
            override fun onLike(post: Post) {
                if (post.likedByMe) viewModel.unlikeById(post.id) else viewModel.likeById(post.id)
            }
            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }

                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }

            override fun onEdit(post: Post) {
                viewModel.edit(post)
                val text = post.content
                val bundle = Bundle()
                bundle.putString("editedText", text)
                findNavController().navigate(R.id.action_feedFragment_to_editPostFragment, bundle)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onShowPhoto(post: Post) {
                val likes = post.likes.toString()
                val id = post.id
                val isLikedByMe = post.likedByMe
                val url = post.attachment!!.url
                val bundle = Bundle()
                bundle.putString("likes", likes)
                bundle.putBoolean("likedByMe", isLikedByMe)
                bundle.putString("url", url)
                bundle.putLong("id", id)
                findNavController().navigate(R.id.action_feedFragment_to_showPhoto, bundle)
            }

        })

        binding.list.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner, { post : FeedModel->
            adapter.submitList(post.posts)
            binding.emptyText.isVisible = post.empty
        })

        viewModel.dataState.observe(viewLifecycleOwner, { state : FeedModelState->
            binding.progress.isVisible = state.loading
            binding.swiperefresh.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { viewModel.loadPosts() }
                    .show()
            }

        })

        binding.newerPostLoad.hide()


        viewModel.newerCount.observe(viewLifecycleOwner) { count ->
            if (count > 0) {
                binding.newerPostLoad.show()
                binding.newerPostLoad.text =
                    getString(R.string.have_new_posts, count.toString())
            }

        }


        binding.newerPostLoad.setOnClickListener{
            binding.newerPostLoad.hide()
            binding.list.smoothScrollToPosition(0)
                viewModel.readPosts()
            }




        binding.swiperefresh.setOnRefreshListener {
            viewModel.refreshPosts()
            binding.swiperefresh.isRefreshing = false

        }



        binding.fab.setOnClickListener{
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }

    return binding.root
    }

}

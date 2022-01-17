package ru.netology.nmedia.work

import androidx.work.DelegatingWorkerFactory
import ru.netology.nmedia.repository.PostRepository

class DependencyWorkerFactory(
    repository: PostRepository
) : DelegatingWorkerFactory() {
    init {
        addFactory(RefreshPostsWorkerFactory(repository))
        addFactory(SavePostWorkerFactory(repository))
        addFactory(DeletePostWorkerFactory(repository))
    }
}
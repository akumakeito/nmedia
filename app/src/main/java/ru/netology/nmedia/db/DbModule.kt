package ru.netology.nmedia.db

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.netology.nmedia.dao.PostDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DbModule {

    @Provides
    @Singleton
    fun provideDb(
        @ApplicationContext context: Context
    ) : AppDB = Room.databaseBuilder(context, AppDB::class.java,"app.db")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun providePostDao(appDB: AppDB) : PostDao = appDB.postDao()
}
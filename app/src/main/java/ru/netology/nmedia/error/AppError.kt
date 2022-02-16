package ru.netology.nmedia

import java.io.IOException
import java.lang.RuntimeException
import java.sql.SQLException

sealed class AppError(var code : String) : RuntimeException() {
    companion object {
        fun from (e : Throwable) : AppError = when (e) {
            is AppError -> e
            is SQLException -> DbError
            is IOException -> NetworkError
            else -> UnknownAppError
        }
    }
}
class ApiError(code: String) : AppError(code)
object NetworkError : AppError("error_network")
object UnknownAppError : AppError("error_unknown")
object DbError : AppError("error_db")

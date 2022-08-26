package com.androidspace.fusion.base.errors

data class ErrorData(
        var code: Int = -1,
        val body: ErrorBody = ErrorBody("Undefined error"))
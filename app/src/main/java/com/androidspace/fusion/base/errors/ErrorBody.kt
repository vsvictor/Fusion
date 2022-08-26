package com.androidspace.fusion.base.errors

data class ErrorBody(var message: String = "", val errs: List<Pair<String, String>> = ArrayList<Pair<String, String>>(), var data: DataError? = null)
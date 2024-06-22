package org.webproject.sso

import kotlin.Exception

sealed class SupportedException(val m: String,val status: Int): Exception(m)
class DefaultSupportedException(val msg: String, val code: Int = 405): SupportedException(msg,code)
class SupportedMissMatchException(val msg: String,val code: Int=401) : SupportedException(msg,code)
class SupportedReplayedException(val msg: String,val code: Int=401) : SupportedException(msg,code)

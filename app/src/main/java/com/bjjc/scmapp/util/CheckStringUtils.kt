package com.bjjc.scmapp.util

import java.util.regex.Pattern

/**
 * Created by Allen on 2018/11/30 11:29
 * regular expression checking
 */
class CheckStringUtils {
    companion object {
        /**
         * checking a userName.
         * 帐号是否合法(字母开头，允许5-16字节，允许字母数字下划线)
         */
        fun checkUserName(username: String): Boolean {
            val regExp = "^[a-zA-Z][a-zA-Z0-9_-]{1,19}$"
            val p = Pattern.compile(regExp)
            val m = p.matcher(username)
            return m.matches()
        }

        /**
         * checking a passWord
         * 密码(长度在6~20之间，只能包含字母、数字)
         */
        fun checkPassWord(password: String): Boolean {
            val regExp = "^[a-zA-Z0-9]{1,19}$"
            val p = Pattern.compile(regExp)
            val m = p.matcher(password)
            return m.matches()
        }

        /***
         * checking a phone number.
         */
        fun checkPhoneNum(num: String): Boolean {
            val regExp = "^((13[0-9])|(15[^4])|(18[0-9])|(17[0-8])|(14[5-9])|(166)|(19[8,9])|)\\d{8}$"
            val p = Pattern.compile(regExp)
            val m = p.matcher(num)
            return m.matches()
        }
    }
}
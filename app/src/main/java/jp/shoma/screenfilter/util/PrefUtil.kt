package jp.shoma.screenfilter.util

import android.content.Context
import android.preference.PreferenceManager


object PrefUtil {
    /**
     * preferenceにkeyがsetされているか確認する.

     * @param context Context
     * *
     * @param key     key name
     * *
     * @return value  boolean
     */
    fun hasSpKey(context: Context, key: String): Boolean {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        return sp.contains(key)
    }

    /**
     * 特定のキーと値を消す
     * @param context Context
     * *
     * @param key    key name
     * *
     * @return value boolean
     */
    fun removeSpKey(context: Context, key: String): Boolean {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        return sp.edit().remove(key).commit()
    }

    /**
     * プリファレンスから指定されたキーの値(boolean)を取得する

     * @param context コンテキスト
     * *
     * @param key     キー名
     * *
     * @return value 値(boolean)
     */
    fun getSpValBoolean(context: Context, key: String): Boolean {
        return getSpValBoolean(context, key, false)
    }

    /**
     * プリファレンスから指定されたキーの値(boolean)を取得する

     * @param context        コンテキスト
     * *
     * @param key            キー名
     * *
     * @param defaultBoolean デフォルト値
     * *
     * @return value 値(boolean)
     */
    fun getSpValBoolean(context: Context, key: String, defaultBoolean: Boolean): Boolean {
        val sp = PreferenceManager
                .getDefaultSharedPreferences(context)
        val value = sp.getBoolean(key, defaultBoolean)
        return value
    }

    /**
     * プリファレンスに指定されたキーの値(long)を保存する

     * @param context コンテキスト
     * *
     * @param key     キー名
     * *
     * @param value   値(boolean)
     * *
     * @return true:保存成功, false:保存失敗
     */
    fun putSpValBoolean(context: Context, key: String, value: Boolean): Boolean {
        val sp = PreferenceManager
                .getDefaultSharedPreferences(context)
        return sp.edit().putBoolean(key, value).commit()
    }


    /**
     * プリファレンスから指定されたキーの値(int)を取得する

     * @param context コンテキスト
     * *
     * @param key     キー名
     * *
     * @return value 値(int)
     */
    fun getSpValInt(context: Context, key: String): Int {
        return getSpValInt(context, key, 0)
    }

    /**
     * プリファレンスから指定されたキーの値(int)を取得する

     * @param context    コンテキスト
     * *
     * @param key        キー名
     * *
     * @param defaultInt デフォルト値
     * *
     * @return value 値(int)
     */
    fun getSpValInt(context: Context, key: String, defaultInt: Int): Int {
        val sp = PreferenceManager
                .getDefaultSharedPreferences(context)
        val value = sp.getInt(key, defaultInt)
        return value
    }

    /**
     * プリファレンスに指定されたキーの値(int)を保存する

     * @param context コンテキスト
     * *
     * @param key     キー名
     * *
     * @param value   値(int)
     * *
     * @return true:保存成功, false:保存失敗
     */
    fun putSpValInt(context: Context, key: String, value: Int): Boolean {
        val sp = PreferenceManager
                .getDefaultSharedPreferences(context)
        return sp.edit().putInt(key, value).commit()
    }

    /**
     * プリファレンスから指定されたキーの値(long)を取得する

     * @param context コンテキスト
     * *
     * @param key     キー名
     * *
     * @return value 値(long)
     */
    fun getSpValLong(context: Context, key: String): Long {
        return getSpValLong(context, key, 0)
    }

    /**
     * プリファレンスから指定されたキーの値(long)を取得する

     * @param context     コンテキスト
     * *
     * @param key         キー名
     * *
     * @param defaultLong デフォルト値
     * *
     * @return value 値(long)
     */
    fun getSpValLong(context: Context, key: String, defaultLong: Long): Long {
        val sp = PreferenceManager
                .getDefaultSharedPreferences(context)
        val value = sp.getLong(key, defaultLong)
        return value
    }

    /**
     * プリファレンスに指定されたキーの値(long)を保存する

     * @param context コンテキスト
     * *
     * @param key     キー名
     * *
     * @param value   値(long)
     * *
     * @return true:保存成功, false:保存失敗
     */
    fun putSpValLong(context: Context, key: String, value: Long): Boolean {
        val sp = PreferenceManager
                .getDefaultSharedPreferences(context)
        return sp.edit().putLong(key, value).commit()
    }


    /**
     * プリファレンスから指定されたキーの値(文字列)を取得する

     * @param context コンテキスト
     * *
     * @param key     キー名
     * *
     * @return value 値(文字列)
     */
    fun getSpValStr(context: Context, key: String): String {
        return getSpValStr(context, key, "")
    }

    /**
     * プリファレンスから指定されたキーの値(文字列)を取得する

     * @param context    コンテキスト
     * *
     * @param key        キー名
     * *
     * @param defaultStr デフォルト値
     * *
     * @return value 値(文字列)
     */
    fun getSpValStr(context: Context, key: String,
                    defaultStr: String): String {
        val sp = PreferenceManager
                .getDefaultSharedPreferences(context)
        val value = sp.getString(key, defaultStr)
        return value
    }

    /**
     * プリファレンスに指定されたキーの値(文字列)を保存する

     * @param context コンテキスト
     * *
     * @param key     キー名
     * *
     * @param value   値(文字列)
     * *
     * @return true:保存成功, false:保存失敗
     */
    fun putSpValStr(context: Context, key: String, value: String): Boolean {
        val sp = PreferenceManager
                .getDefaultSharedPreferences(context)
        return sp.edit().putString(key, value).commit()
    }

}
package com.ecommerce.letgoecommerce.extension

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.core.content.edit

class SpecialShared {


    companion object {

        var sharedPreferences: SharedPreferences? = null

        @Volatile
        private var instance: SpecialShared? = null

        private val any = Any()

        operator fun invoke(context: Context): SpecialShared =
            instance ?: synchronized(any) {
                instance ?: createSpecialSharedPreferences(context.applicationContext).also {
                    instance = it
                }
            }

        private fun createSpecialSharedPreferences(context: Context): SpecialShared {

            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)

            return SpecialShared()
        }
    }

    fun setUserId(id: Int) {
        sharedPreferences?.edit()?.putInt("userId",id)?.apply()
    }

    fun getUserId(): Int? {
        return sharedPreferences?.getInt("userId", -1)
    }

    fun setCityId(id: Int) {
        sharedPreferences?.edit()?.putInt("cityId",id)?.apply()
    }

    fun getCityId(): Int? {
        return sharedPreferences?.getInt("cityId", 1)
    }


}
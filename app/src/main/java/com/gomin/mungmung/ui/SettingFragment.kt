package com.gomin.mungmung.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.navigation.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.gomin.mungmung.R

class SettingFragment : PreferenceFragmentCompat()  {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        val key = preference.key
        selectSetting(key)
        return super.onPreferenceTreeClick(preference)
    }
    private fun selectSetting(key:String){
        val builder = AlertDialog.Builder(this.context,R.style.Custom_Dialog)

        when(key){
            "log_out"->{
                Log.d("clicked log out pref","OK")
                builder.setTitle("LOG OUT").setPositiveButton("Yes",DialogInterface.OnClickListener { _, i ->
                    Log.d("i : ", i.toString())
                })
                    .setNegativeButton("No",DialogInterface.OnClickListener { _, i ->
                        Log.d("i : ", i.toString())
                    })
                    .setMessage("로그아웃 하시겠습니까?")
                    .show()
            }

            "change_pw"->{
                view?.findNavController()?.navigate(R.id.changePwFragment)
            }

            "delete_account"->{
                builder.setTitle("Delete Account").setPositiveButton("Yes",DialogInterface.OnClickListener { _, i ->
                    Log.d("i : ", i.toString())
                })
                    .setNegativeButton("No",DialogInterface.OnClickListener { _, i ->
                        Log.d("i : ", i.toString())
                    })
                    .setMessage("탈퇴하시겠습니까?")
                    .show()
            }
        }
    }
}
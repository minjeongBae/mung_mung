package com.android.mungmung

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.android.mungmung.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        navController.navigate(R.id.loginFragment)

        navView.setOnItemSelectedListener { item->
            when(item.itemId){
                R.id.homeFragment->{
                    navController.navigate(R.id.homeFragment)
                    true
                }
                R.id.searchFragment->{
                    navController.navigate(R.id.searchFragment)
                    true
                }
                R.id.photoFragment->{
                    navController.navigate(R.id.photoFragment)
                    true
                }
                R.id.profileFragment->{
                    navController.navigate(R.id.profileFragment)
                    true
                }
                else -> {false}
            }

        }


//        // 로그인, 앱설정, 회원가입에서는 바텀네비게이션 안 보이게
//        navController.addOnDestinationChangedListener { _, destination, _ ->
//            if(destination.id == R.id.loginFragment || destination.id == R.id.settingFragment
//                || destination.id == R.id.signupFragment ){
//                navView.visibility = View.GONE
//            }
//            else{
//                navView.visibility = View.VISIBLE
//            }
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return true
    }
}
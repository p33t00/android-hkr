package com.hkrtbse2.cab_hkr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.hkrtbse2.cab_hkr.ui.theme.CABHKRTheme
import com.hkrtbse2.cab_hkr.data.UserPreferencesRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CABHKRTheme {
                CabNavigatorApp(userPreferencesRepository)
            }
        }
    }


}


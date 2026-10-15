package com.gramainexus.smartvillage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.gramainexus.smartvillage.ui.navigation.AppNavigation
import com.gramainexus.smartvillage.ui.theme.SmartVillageTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            SmartVillageTheme {

                AppNavigation()
            }
        }
    }
}
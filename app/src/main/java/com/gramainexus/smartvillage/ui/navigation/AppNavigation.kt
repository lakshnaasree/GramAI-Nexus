package com.gramainexus.smartvillage.ui.navigation


import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.gramainexus.smartvillage.ProfileScreen
import com.gramainexus.smartvillage.data.UserPreferences

import com.gramainexus.smartvillage.ui.screens.AgricultureScreen
import com.gramainexus.smartvillage.ui.screens.EmploymentScreen
import com.gramainexus.smartvillage.ui.screens.GovernmentSchemesScreen
import com.gramainexus.smartvillage.ui.screens.HealthcareScreen
import com.gramainexus.smartvillage.ui.screens.HomeScreen
import com.gramainexus.smartvillage.ui.screens.JobsScreen
import com.gramainexus.smartvillage.ui.screens.LanguageSelectionScreen
import com.gramainexus.smartvillage.ui.screens.LoginRegisterScreen
import com.gramainexus.smartvillage.ui.screens.ReportIssueScreen
import com.gramainexus.smartvillage.ui.screens.SplashScreen
import com.gramainexus.smartvillage.ui.screens.UpdatesScreen
import com.gramainexus.smartvillage.ui.screens.VillageSelectionScreen
import com.gramainexus.smartvillage.ui.screens.WaterFacilityScreen

import kotlinx.coroutines.launch


@Composable
fun AppNavigation() {

    // =====================================================
    // NAVIGATION CONTROLLER
    // =====================================================

    val navController = rememberNavController()


    // =====================================================
    // CONTEXT
    // =====================================================

    val context = LocalContext.current


    // =====================================================
    // USER PREFERENCES
    // =====================================================

    val userPreferences = remember {
        UserPreferences(context)
    }


    // =====================================================
    // COROUTINE SCOPE
    // =====================================================

    val coroutineScope = rememberCoroutineScope()


    // =====================================================
    // APP STATE
    // =====================================================

    var selectedLanguage by remember {
        mutableStateOf("English")
    }

    var selectedVillage by remember {
        mutableStateOf("")
    }


    // =====================================================
    // NAVIGATION HOST
    // =====================================================

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {

        // =================================================
        // 1. SPLASH
        // =================================================

        composable("splash") {

            SplashScreen(

                onSplashFinished = {

                    coroutineScope.launch {

                        val isLoggedIn =
                            userPreferences.isLoggedIn()

                        if (isLoggedIn) {

                            /*
                             * Existing logged-in user.
                             *
                             * Go directly to Home.
                             *
                             * The village will already be available
                             * from the previous login/session.
                             */
                            navController.navigate("home") {

                                popUpTo("splash") {
                                    inclusive = true
                                }

                                launchSingleTop = true
                            }

                        } else {

                            /*
                             * New user.
                             *
                             * Start onboarding.
                             */
                            navController.navigate("language") {

                                popUpTo("splash") {
                                    inclusive = true
                                }

                                launchSingleTop = true
                            }
                        }
                    }
                }
            )
        }


        // =================================================
        // 2. LANGUAGE SELECTION
        // =================================================

        composable("language") {

            LanguageSelectionScreen(

                onContinue = { language ->

                    selectedLanguage = language

                    navController.navigate("village") {

                        popUpTo("language") {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                }
            )
        }


        // =================================================
        // 3. VILLAGE SELECTION
        // =================================================

        composable("village") {

            VillageSelectionScreen(

                selectedLanguage = selectedLanguage,

                onContinue = { village ->

                    /*
                     * Save selected village in navigation state.
                     *
                     * This value is also passed to LoginRegisterScreen.
                     */
                    selectedVillage = village

                    navController.navigate("login") {

                        launchSingleTop = true
                    }
                }
            )
        }


        // =================================================
        // 4. LOGIN / REGISTER
        // =================================================

        composable("login") {

            LoginRegisterScreen(

                /*
                 * Village selected during onboarding.
                 *
                 * This is also used as a fallback for old
                 * Firebase accounts that don't have a village
                 * field saved.
                 */
                villageName = selectedVillage,

                selectedLanguage = selectedLanguage,

                /*
                 * LoginRegisterScreen returns:
                 *
                 * phone
                 * finalVillage
                 *
                 * finalVillage can come from:
                 *
                 * 1. Firestore
                 * 2. selectedVillage if Firestore is missing it
                 */
                onLoginSuccess = { _, finalVillage ->

                    /*
                     * IMPORTANT:
                     *
                     * Always update the active village here.
                     *
                     * This fixes the problem where HomeScreen
                     * receives an empty/old village value.
                     */
                    selectedVillage = finalVillage

                    navController.navigate("home") {

                        popUpTo("login") {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                }
            )
        }


        // =================================================
        // 5. HOME
        // =================================================

        composable("home") {

            HomeScreen(

                villageName = selectedVillage,

                selectedLanguage = selectedLanguage,


                // -----------------------------------------
                // LOGOUT
                // -----------------------------------------

                onLogout = {

                    coroutineScope.launch {

                        userPreferences.logout()

                        /*
                         * Clear current user session data.
                         */
                        selectedVillage = ""
                        selectedLanguage = "English"

                        navController.navigate("language") {

                            popUpTo("home") {
                                inclusive = true
                            }

                            launchSingleTop = true
                        }
                    }
                },


                // -----------------------------------------
                // REPORT ISSUE
                // -----------------------------------------

                onReportIssue = {

                    navController.navigate("reportIssue") {

                        launchSingleTop = true
                    }
                },


                // -----------------------------------------
                // PROFILE
                // -----------------------------------------

                onProfileClick = {

                    navController.navigate("profile") {

                        launchSingleTop = true
                    }
                },


                // -----------------------------------------
                // UPDATES
                // -----------------------------------------

                onUpdatesClick = {

                    navController.navigate("updates") {

                        launchSingleTop = true
                    }
                },


                // -----------------------------------------
                // GOVERNMENT SCHEMES
                // -----------------------------------------

                onGovernmentSchemesClick = {

                    navController.navigate("government_schemes") {

                        launchSingleTop = true
                    }
                },


                // -----------------------------------------
                // AGRICULTURE
                // -----------------------------------------

                onAgricultureClick = {

                    navController.navigate("agriculture") {

                        launchSingleTop = true
                    }
                },


                // -----------------------------------------
                // WATER
                // -----------------------------------------

                onWaterClick = {

                    navController.navigate("water") {

                        launchSingleTop = true
                    }
                },


                // -----------------------------------------
                // HEALTHCARE
                // -----------------------------------------

                onHealthcareClick = {

                    navController.navigate("healthcare") {

                        launchSingleTop = true
                    }
                },


                // -----------------------------------------
                // EMPLOYMENT
                // -----------------------------------------

                onEmploymentClick = {

                    navController.navigate("employment") {

                        launchSingleTop = true
                    }
                }
            )
        }


        // =================================================
        // 6. REPORT ISSUE
        // =================================================

        composable("reportIssue") {

            ReportIssueScreen(

                selectedLanguage = selectedLanguage,

                selectedVillage = selectedVillage,

                onBack = {

                    navController.popBackStack()
                },

                onIssueSubmitted = {

                    navController.popBackStack()
                }
            )
        }


        // =================================================
        // 7. UPDATES
        // =================================================

        composable("updates") {

            UpdatesScreen(

                selectedLanguage = selectedLanguage,

                villageName = selectedVillage,

                onBack = {

                    navController.popBackStack()
                }
            )
        }


        // =================================================
        // 8. PROFILE
        // =================================================

        composable("profile") {

            ProfileScreen()
        }


        // =================================================
        // 9. AGRICULTURE
        // =================================================

        composable("agriculture") {

            AgricultureScreen(

                selectedLanguage = selectedLanguage,

                villageName = selectedVillage,

                onBack = {

                    navController.popBackStack()
                }
            )
        }


        // =================================================
        // 10. WATER FACILITY
        // =================================================

        composable("water") {

            WaterFacilityScreen(

                selectedLanguage = selectedLanguage,

                villageName = selectedVillage,

                onBack = {

                    navController.popBackStack()
                },

                onEmergencyWaterClick = {

                    // Future:
                    // Emergency tanker request
                },

                onBillClick = {

                    // Future:
                    // Water bill / meter module
                }
            )
        }


        // =================================================
        // 11. HEALTHCARE
        // =================================================

        composable("healthcare") {

            HealthcareScreen(

                selectedLanguage = selectedLanguage,

                villageName = selectedVillage,

                onBack = {

                    navController.popBackStack()
                }
            )
        }


        // =================================================
        // 12. GOVERNMENT SCHEMES
        // =================================================

        composable("government_schemes") {

            GovernmentSchemesScreen(

                selectedLanguage = selectedLanguage,

                villageName = selectedVillage,

                onBack = {

                    navController.popBackStack()
                }
            )
        }


        // =================================================
        // 13. EMPLOYMENT
        // =================================================

        composable("employment") {

            EmploymentScreen(

                selectedLanguage = selectedLanguage,

                villageName = selectedVillage,

                onBack = {

                    navController.popBackStack()
                },

                onFindJobs = {

                    navController.navigate("jobs") {

                        launchSingleTop = true
                    }
                }
            )
        }


        // =================================================
        // 14. JOBS
        // =================================================

        composable("jobs") {

            JobsScreen(

                selectedLanguage = selectedLanguage,

                villageName = selectedVillage,

                onBack = {

                    navController.popBackStack()
                }
            )
        }
    }
}
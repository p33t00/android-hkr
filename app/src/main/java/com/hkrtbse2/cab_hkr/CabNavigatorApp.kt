package com.hkrtbse2.cab_hkr

import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hkrtbse2.cab_hkr.data.UserPreferencesRepository
import com.hkrtbse2.cab_hkr.data.remote.dto.Message
import com.hkrtbse2.cab_hkr.ui.IdentitiesScreen
import com.hkrtbse2.cab_hkr.ui.IdentitiesViewModel
import com.hkrtbse2.cab_hkr.ui.LoginRequestState
import com.hkrtbse2.cab_hkr.ui.LoginScreen
import com.hkrtbse2.cab_hkr.ui.LoginViewModel
import com.hkrtbse2.cab_hkr.ui.MessageScreen
import com.hkrtbse2.cab_hkr.ui.PublishedRoutesScreen
import com.hkrtbse2.cab_hkr.ui.PublishedRoutesViewModel
import com.hkrtbse2.cab_hkr.ui.RoutePlansScreen
import com.hkrtbse2.cab_hkr.ui.ServicesScreen
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

enum class RoutesToScreen(@StringRes val title: Int) {
    Start(title = R.string.start_screen),
    Services(title = R.string.services_screen),
    Login(title = R.string.login_screen),
    RoutePlans(title = R.string.route_plans_screen),
    Message(title = R.string.message_screen),
    PublishedRoutes(title = R.string.published_route_plans_screen),
    IdentitiesPlans(title = R.string.identities_screen),
}
var routeScreenvariable : String = ""
@Composable
fun CabNavigatorApp(
    userPreferencesRepository: UserPreferencesRepository,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val navDrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = RoutesToScreen.valueOf(
        backStackEntry?.destination?.route?.substringBefore('?')?.substringBefore('/') ?:
        RoutesToScreen.Start.name
    )
    val logOutHandler = {
        scope.launch {
            userPreferencesRepository.removeUserPreferences()
            navController.popBackStack(RoutesToScreen.Login.name, false)
        }
    }

    ModalNavigationDrawer(
        drawerState = navDrawerState,
        drawerContent = { NavDrawerContent(navController, navDrawerState) },
        gesturesEnabled = false
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                topBar = {
                    if (currentScreen !in listOf(RoutesToScreen.Login, RoutesToScreen.Services)) {
                        AppBar(
                            currentScreen,
                            navController::navigateUp,
                            {
                                scope.launch {
                                    navDrawerState.open()
                                }
                            },
                            navController.previousBackStackEntry != null,
                            { logOutHandler() }
                        )
                    }
                }
            ) {
                NavHost(
                    navController = navController,
                    startDestination = RoutesToScreen.Login.name,
                    modifier = Modifier
                        .padding(it)
                        .padding(20.dp)
                ) {
                    composable(route = RoutesToScreen.Login.name) {
                        val loginVM = hiltViewModel<LoginViewModel>()
                        if (loginVM.isLoggedIn !is LoginRequestState.Success ||
                            !(loginVM.isLoggedIn as LoginRequestState.Success).isLoggedIn
                        ) {
                            LoginScreen { loginVM.loginUser() }
                        } else if (loginVM.serviceUrl != null && loginVM.serviceUrl!!.isEmpty()) {
                            ServicesScreen(
                                loginVM.services,
                                { url -> loginVM.setUserServiceUrl(url) },
                                loginVM::initServices
                            )
                        } else {
                            navController.navigate(RoutesToScreen.Start.name)
                        }
                    }
                    composable(route = RoutesToScreen.Start.name) {
                        RoutePlansScreen(routePlan = routeScreenvariable)
                    }
//                    composable(
//                        route = "${RoutesToScreen.RoutePlans.name}?rp={rp}",
//                        arguments = listOf(
//                            navArgument("rp") {
//                                type = NavType.StringType
//                                nullable = false
//                            }
//                        )
//                    ) {backStackEntry ->
//                        val routePlan: String = backStackEntry.arguments?.getString("rp").let { rp ->
//                            Json.decodeFromString(Uri.decode(rp))
//                        }
//                        routeScreenvariable = routePlan
//                        RoutePlansScreen(routePlan)
//                    }
                    composable(route = RoutesToScreen.PublishedRoutes.name) {
                        val publishedRoutesVM = hiltViewModel<PublishedRoutesViewModel>()
                        PublishedRoutesScreen(
                            messages = publishedRoutesVM.routePlanMessagesState,
                            onMessageSelect = { msgId ->
                                val msg = publishedRoutesVM.getPublishedMessage(msgId)
                                val encMsg = Uri.encode(Json.encodeToString(msg))
                                navController.navigate("${RoutesToScreen.Message.name}?msg=$encMsg")
                            },
                            retryHandler = publishedRoutesVM::initRPMessages,
                            modifier = modifier.fillMaxSize()
                        )
                    }
//                    composable(
//                        route = "${RoutesToScreen.Message.name}?msg={msg}",
//                        arguments = listOf(
//                            navArgument("msg") {
//                                type = NavType.StringType
//                                defaultValue = null
//                                nullable = true
//                            }
//                        )
//                    ) { backStackEntry ->
//                        val msg: Message = backStackEntry.arguments?.getString("msg").let { msgStr ->
//                            Json.decodeFromString(Uri.decode(msgStr))
//                        }
//                        val routeEnc = Uri.encode(Json.encodeToString(msg.message))
//
//                        MessageScreen(
//                            msg,
//                            onRouteView = {
//                                navController.navigate("${RoutesToScreen.RoutePlans.name}?rp=${routeEnc}")
//                            },
//                            onAclEdit = {
//                                navController.navigate("${RoutesToScreen.IdentitiesPlans.name}/${msg.messageId}")
//                            }
//                        )
//                    }
                    composable(route = "${RoutesToScreen.IdentitiesPlans.name}/{dataId}") {
                        val identitiesVM = hiltViewModel<IdentitiesViewModel>()

                        IdentitiesScreen(
                            modifier.fillMaxSize(),
                            identitiesVM.reqStatus,
                            identitiesVM.identities,
                            { idx -> identitiesVM.changeCheckState(idx) },
                            identitiesVM::saveRoutePlanAcl,
                            identitiesVM::initRoutePlanAcl,
                            {
                                navController.popBackStack(RoutesToScreen.Message.name, false)
                            })
                    }
                }
            }
        }
    }
}

@Composable
fun AppBar(
    currentScreen: RoutesToScreen,
    navigateBack: () -> Unit,
    openNavDrawer: () -> Unit,
    canNavigateBack: Boolean,
    onLogout: () -> Unit
) = TopAppBar(
    title = { Text(stringResource(currentScreen.title)) },
    actions = {
        IconButton(onClick = { onLogout() }) {
            Icon(imageVector = Icons.Default.Lock, contentDescription = "Logout button")
        }
    },
    navigationIcon = {
        if (currentScreen != RoutesToScreen.Start && canNavigateBack) {
            IconButton(onClick = navigateBack) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back button"
                )
            }
        } else {
            IconButton(
                onClick = openNavDrawer
            ) {
                Icon(
                    Icons.Default.Menu,
                    contentDescription = "TopNavBar"
                )
            }
        }
    },
//    colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.scrim),
    modifier = Modifier
)

@Composable
fun NavDrawerContent(
    navController: NavController,
    navDrawerState: DrawerState,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    ModalDrawerSheet(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(20.dp)
        ) {
            Text("CAB Navigator", style = MaterialTheme.typography.titleLarge)
        }
        Spacer(modifier = Modifier.padding(vertical = 16.dp))

        Divider()

        NavigationDrawerItem(
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
            label = { Text("Home") },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            selected = false,
            onClick = {
                scope.launch { navDrawerState.close() }
                navController.navigate(RoutesToScreen.Start.name)
            })

//        NavigationDrawerItem(
//            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
//            label = { Text("Services") },
//            icon = { Icon(Icons.Default.List, contentDescription = "Services") },
//            selected = false,
//            onClick = {
//                scope.launch { navDrawerState.close() }
//                navController.navigate(RoutesToScreen.Services.name)
//            })

//        NavigationDrawerItem(
//            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
//            label = { Text("Route Plans") },
//            icon = { Icon(Icons.Default.LocationOn, contentDescription = "Route Plans") },
//            selected = false,
//            onClick = {
//                scope.launch { navDrawerState.close() }
//                navController.navigate(RoutesToScreen.RoutePlans.name)
//            })

        NavigationDrawerItem(
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
            label = { Text("Published Route Plans") },
            icon = { Icon(Icons.Default.LocationOn, contentDescription = "Published Route Plans") },
            selected = false,
            onClick = {
                scope.launch { navDrawerState.close() }
                navController.navigate(RoutesToScreen.PublishedRoutes.name)
            })
    }
}
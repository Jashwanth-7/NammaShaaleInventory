package com.jashwanth.nammashaaleinventory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jashwanth.nammashaale.data.Asset
import com.jashwanth.nammashaale.data.AssetCondition
import com.jashwanth.nammashaale.data.UserRole
import com.jashwanth.nammashaale.viewmodel.AssetViewModel
import com.jashwanth.nammashaale.viewmodel.UserViewModel
import com.jashwanth.nammashaale.LoginScreen
import com.jashwanth.nammashaale.SignupScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val userViewModel: UserViewModel = viewModel()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { role ->
                    val dest = if (role == UserRole.ADMIN) "admin_dashboard" else "teacher_dashboard"
                    navController.navigate(dest) { popUpTo("login") { inclusive = true } }
                },
                onNavigateToSignup = { navController.navigate("signup") },
                viewModel = userViewModel
            )
        }
        composable("signup") {
            SignupScreen(
                onSignupSuccess = { navController.navigate("login") },
                onNavigateToLogin = { navController.navigate("login") },
                viewModel = userViewModel
            )
        }
        composable("admin_dashboard") {
            AdminDashboard(
                onViewAssets = { navController.navigate("asset_list") },
                onLogout = {
                    userViewModel.logout()
                    navController.navigate("login") { popUpTo(0) { inclusive = true } }
                }
            )
        }
        composable("teacher_dashboard") {
            TeacherDashboard(
                onNavigateToRegistration = { navController.navigate("asset_registration") },
                onViewAssets = { navController.navigate("asset_list") },
                onLogout = {
                    userViewModel.logout()
                    navController.navigate("login") { popUpTo(0) { inclusive = true } }
                }
            )
        }
        composable("asset_list") {
            AssetListScreen(onBack = { navController.popBackStack() })
        }
        composable("asset_registration") {
            AssetRegistrationScreen(
                onAssetSaved = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard(onViewAssets: () -> Unit, onLogout: () -> Unit, assetViewModel: AssetViewModel = viewModel()) {
    val assets by assetViewModel.allAssets.collectAsState(initial = emptyList())

    val total = assets.size
    val working = assets.count { it.condition == AssetCondition.WORKING }
    val repair = assets.count { it.condition == AssetCondition.NEEDS_REPAIR }
    val broken = assets.count { it.condition == AssetCondition.BROKEN }

    Scaffold(topBar = { TopAppBar(title = { Text("Admin Dashboard") }) }) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .padding(16.dp)) {
            Text("School Summary", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DashboardCard("Total Assets", total.toString(), Color.Gray, Modifier.weight(1f))
                DashboardCard("Working", working.toString(), Color(0xFF4CAF50), Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DashboardCard("Needs Repair", repair.toString(), Color(0xFFFFC107), Modifier.weight(1f))
                DashboardCard("Broken", broken.toString(), Color(0xFFF44336), Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = onViewAssets, modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)) {
                Text("View Detailed Inventory")
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) { Text("Logout") }
        }
    }
}

@Composable
fun DashboardCard(label: String, value: String, color: Color, modifier: Modifier) {
    Card(modifier = modifier, elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text(value, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherDashboard(onNavigateToRegistration: () -> Unit, onViewAssets: () -> Unit, onLogout: () -> Unit) {
    Scaffold(topBar = { TopAppBar(title = { Text("Teacher Portal") }) }) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .padding(24.dp), verticalArrangement = Arrangement.Center) {
            Button(onClick = onNavigateToRegistration, modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)) {
                Icon(Icons.Default.Add, null); Spacer(Modifier.width(8.dp)); Text("Register New Item")
            }
            Spacer(Modifier.height(16.dp))
            Button(onClick = onViewAssets, modifier = Modifier
                .fillMaxWidth()
                .height(64.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) {
                Icon(Icons.Default.List, null); Spacer(Modifier.width(8.dp)); Text("Update Item Conditions")
            }
            Spacer(Modifier.height(32.dp))
            TextButton(onClick = onLogout, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text("Logout", color = Color.Red) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetListScreen(onBack: () -> Unit, viewModel: AssetViewModel = viewModel()) {
    val assets by viewModel.allAssets.collectAsState(initial = emptyList())
    var searchQuery by remember { mutableStateOf("") }

    val filtered = assets.filter { it.itemName.contains(searchQuery, true) || it.category.contains(searchQuery, true) }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Search & Update") }, navigationIcon = {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
        })
    }) { padding ->
        Column(Modifier.padding(padding)) {
            OutlinedTextField(
                value = searchQuery, onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search items or categories...") },
                leadingIcon = { Icon(Icons.Default.Search, null) }
            )
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filtered) { asset ->
                    AssetCard(asset) { viewModel.updateAssetCondition(asset.id, it) }
                }
            }
        }
    }
}

@Composable
fun AssetCard(asset: Asset, onUpdate: (AssetCondition) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(asset.itemName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                StatusChip(asset.condition)
            }
            Text("Category: ${asset.category} | Qty: ${asset.quantity}")
            Text("Location: ${asset.location}")
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                HealthBtn(AssetCondition.WORKING, Color.Green, Icons.Default.CheckCircle) { onUpdate(it) }
                HealthBtn(AssetCondition.NEEDS_REPAIR, Color.Yellow, Icons.Default.Warning) { onUpdate(it) }
                HealthBtn(AssetCondition.BROKEN, Color.Red, Icons.Default.Close) { onUpdate(it) }
            }
        }
    }
}

@Composable
fun StatusChip(condition: AssetCondition) {
    val color = when(condition) { AssetCondition.WORKING -> Color.Green; AssetCondition.NEEDS_REPAIR -> Color.Yellow; AssetCondition.BROKEN -> Color.Red }
    Surface(color = color.copy(0.2f), shape = RoundedCornerShape(4.dp), modifier = Modifier.border(1.dp, color, RoundedCornerShape(4.dp))) {
        Text(condition.name, modifier = Modifier.padding(4.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun HealthBtn(cond: AssetCondition, color: Color, icon: ImageVector, onClick: (AssetCondition) -> Unit) {
    IconButton(onClick = { onClick(cond) }) { Icon(icon, null, tint = color) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetRegistrationScreen(onAssetSaved: () -> Unit, onBack: () -> Unit, viewModel: AssetViewModel = viewModel()) {
    var name by remember { mutableStateOf("") }; var cat by remember { mutableStateOf("") }
    var sn by remember { mutableStateOf("") }; var qty by remember { mutableStateOf("1") }
    var date by remember { mutableStateOf("") }; var loc by remember { mutableStateOf("") }

    Scaffold(topBar = { TopAppBar(title = { Text("Register Asset") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }) }) { padding ->
        Column(Modifier
            .padding(padding)
            .padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(name, { name = it }, label = { Text("Item Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(cat, { cat = it }, label = { Text("Category (e.g. Lab)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(sn, { sn = it }, label = { Text("Serial Number") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(qty, { qty = it }, label = { Text("Quantity") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(date, { date = it }, label = { Text("Purchase Date") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(loc, { loc = it }, label = { Text("School/Classroom") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.weight(1f))
            Button(onClick = {
                if(name.isNotBlank()) {
                    viewModel.insertAsset(Asset(0, name, cat, sn, qty.toIntOrNull() ?: 1, date, loc, null, AssetCondition.WORKING))
                    onAssetSaved()
                }
            }, modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)) { Text("Save Asset") }
        }
    }
}

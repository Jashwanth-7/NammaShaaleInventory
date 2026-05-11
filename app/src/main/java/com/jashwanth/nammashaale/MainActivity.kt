package com.jashwanth.nammashaale

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
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
                    navController.navigate(dest) {
                        popUpTo("login") { inclusive = true }
                    }
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
            AssetListScreen(
                onBack = { navController.popBackStack() }
            )
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
fun AdminDashboard(onViewAssets: () -> Unit, onLogout: () -> Unit) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Admin Panel") }) }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(16.dp))
            Text("Headmaster Dashboard", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Text("Overview of School Assets", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = onViewAssets,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.List, contentDescription = null)
                    Spacer(Modifier.width(16.dp))
                    Text("View Full Inventory", style = MaterialTheme.typography.titleLarge)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.ExitToApp, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Logout")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherDashboard(onNavigateToRegistration: () -> Unit, onViewAssets: () -> Unit, onLogout: () -> Unit) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Teacher Portal") }) }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            Text("Welcome, Teacher", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            
            Button(
                onClick = onNavigateToRegistration,
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Register New Asset")
            }
            
            Button(
                onClick = onViewAssets,
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Update Condition Status")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            TextButton(onClick = onLogout) {
                Text("Logout", color = Color.Red)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetListScreen(
    onBack: () -> Unit,
    viewModel: AssetViewModel = viewModel()
) {
    val assets by viewModel.allAssets.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Asset Health Tracking") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                }
            )
        }
    ) { padding ->
        if (assets.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No assets in the system.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(assets) { asset ->
                    AssetItem(asset, onUpdateCondition = { newCondition ->
                        viewModel.updateAssetCondition(asset.id, newCondition)
                    })
                }
            }
        }
    }
}

@Composable
fun AssetItem(asset: Asset, onUpdateCondition: (AssetCondition) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = asset.itemName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Unique ID: #${asset.id}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
                ConditionIndicator(asset.condition)
            }
            
            Spacer(Modifier.height(8.dp))
            Text("Category: ${asset.category}", style = MaterialTheme.typography.bodySmall)
            Text("Location: ${asset.location}", style = MaterialTheme.typography.bodySmall)
            Text("Qty: ${asset.quantity} | SN: ${asset.serialNumber}", style = MaterialTheme.typography.bodySmall)

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)
            
            Text("Update Condition:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            
            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                HealthButton(AssetCondition.WORKING, Color(0xFF4CAF50), "Working", asset.condition == AssetCondition.WORKING) { onUpdateCondition(AssetCondition.WORKING) }
                HealthButton(AssetCondition.NEEDS_REPAIR, Color(0xFFFFC107), "Repair", asset.condition == AssetCondition.NEEDS_REPAIR) { onUpdateCondition(AssetCondition.NEEDS_REPAIR) }
                HealthButton(AssetCondition.BROKEN, Color(0xFFF44336), "Broken", asset.condition == AssetCondition.BROKEN) { onUpdateCondition(AssetCondition.BROKEN) }
            }
        }
    }
}

@Composable
fun HealthButton(condition: AssetCondition, color: Color, label: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FilledTonalIconButton(
            onClick = onClick,
            modifier = Modifier.size(48.dp),
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = if (isSelected) color else color.copy(alpha = 0.1f),
                contentColor = if (isSelected) Color.White else color
            )
        ) {
            val icon = when(condition) {
                AssetCondition.WORKING -> Icons.Default.CheckCircle
                AssetCondition.NEEDS_REPAIR -> Icons.Default.Warning
                AssetCondition.BROKEN -> Icons.Default.Close
            }
            Icon(icon, contentDescription = label)
        }
        Text(label, style = MaterialTheme.typography.labelSmall, fontSize = 10.sp)
    }
}

@Composable
fun ConditionIndicator(condition: AssetCondition) {
    val (text, color) = when (condition) {
        AssetCondition.WORKING -> "WORKING" to Color(0xFF4CAF50)
        AssetCondition.NEEDS_REPAIR -> "REPAIR" to Color(0xFFFFC107)
        AssetCondition.BROKEN -> "BROKEN" to Color(0xFFF44336)
    }
    Surface(
        color = color.copy(alpha = 0.2f),
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier.border(1.dp, color, RoundedCornerShape(4.dp))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetRegistrationScreen(
    onAssetSaved: () -> Unit,
    onBack: () -> Unit,
    viewModel: AssetViewModel = viewModel()
) {
    var itemName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var serialNumber by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    var purchaseDate by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Asset Registration") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(value = itemName, onValueChange = { itemName = it }, label = { Text("Item Name (e.g. Microscope)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category (e.g. Lab Equipment)") }, modifier = Modifier.fillMaxWidth())
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = quantity, onValueChange = { quantity = it }, label = { Text("Quantity") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = serialNumber, onValueChange = { serialNumber = it }, label = { Text("Serial Number") }, modifier = Modifier.weight(2f))
            }
            
            OutlinedTextField(value = purchaseDate, onValueChange = { purchaseDate = it }, label = { Text("Purchase Date (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location (School/Classroom)") }, modifier = Modifier.fillMaxWidth())

            // Photo Button (Icon placeholder fixed to a standard icon for stability)
            OutlinedButton(
                onClick = { /* Implement Photo logic */ },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Icon(Icons.Default.AccountBox, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Add Item Photo")
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (itemName.isNotBlank()) {
                        val asset = Asset(
                            itemName = itemName,
                            category = category,
                            serialNumber = serialNumber,
                            quantity = quantity.toIntOrNull() ?: 1,
                            purchaseDate = purchaseDate,
                            location = location,
                            itemPhotoPath = null,
                            condition = AssetCondition.WORKING
                        )
                        viewModel.insertAsset(asset)
                        onAssetSaved()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Register Asset", fontSize = 18.sp)
            }
        }
    }
}

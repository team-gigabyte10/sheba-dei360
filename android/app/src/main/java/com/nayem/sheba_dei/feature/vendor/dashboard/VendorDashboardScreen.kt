package com.nayem.sheba_dei.feature.vendor.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nayem.sheba_dei.ui.components.GlobalAppBar
import com.nayem.sheba_dei.ui.components.SetStatusBarColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorDashboardScreen(
    onNavigateToProducts: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToReports: () -> Unit
) {
    SetStatusBarColor()
    Scaffold(
        topBar = {
            GlobalAppBar(
                title = "Vendor Dashboard"
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Today's Sales", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "$120.50", style = MaterialTheme.typography.headlineMedium)
                }
            }
            
            Button(
                onClick = onNavigateToOrders,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Manage Orders")
            }

            Button(
                onClick = onNavigateToProducts,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Manage Products")
            }

            Button(
                onClick = onNavigateToReports,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View Reports")
            }
        }
    }
}

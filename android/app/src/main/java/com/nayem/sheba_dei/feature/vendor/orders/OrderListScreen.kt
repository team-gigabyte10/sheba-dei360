package com.nayem.sheba_dei.feature.vendor.orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nayem.sheba_dei.ui.components.GlobalAppBar
import com.nayem.sheba_dei.ui.components.SetStatusBarColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListScreen(
    onNavigateBack: () -> Unit,
    onOrderClick: (String) -> Unit
) {
    SetStatusBarColor()
    Scaffold(
        topBar = {
            GlobalAppBar(
                title = "Customer Orders",
                onBackClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(10) { index ->
                OrderItem(
                    orderId = "#ORD-100$index",
                    status = if (index % 2 == 0) "Pending" else "Processing",
                    onClick = { onOrderClick("orderId_$index") }
                )
            }
        }
    }
}

@Composable
fun OrderItem(orderId: String, status: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Order $orderId", style = MaterialTheme.typography.titleMedium)
                Text(text = "2 items", style = MaterialTheme.typography.bodyMedium)
            }
            Badge(
                containerColor = if (status == "Pending") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            ) {
                Text(
                    text = status,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

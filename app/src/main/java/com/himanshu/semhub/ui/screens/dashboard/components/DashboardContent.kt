package com.himanshu.semhub.ui.screens.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.himanshu.semhub.R
import com.himanshu.semhub.data.model.DashboardSummary
import com.himanshu.semhub.data.model.Goal
import com.himanshu.semhub.data.model.Task


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardContent(
    dashboardSummary: DashboardSummary,
    tasks: List<Task>,
    goals: List<Goal>,
    isLoading: Boolean,
    error: String?,
    onErrorDismiss: () -> Unit,
    onRefresh: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Dashboard") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Error message
                error?.let { errorMessage ->
                    Snackbar(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp),
                        action = {
                            TextButton(onClick = onErrorDismiss) {
                                Text("Dismiss")
                            }
                        }
                    ) {
                        Text(errorMessage)
                    }
                }

                // Loading indicator
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Summary cards
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Tasks summary card
                                SummaryCard(
                                    title = "Tasks",
                                    count = dashboardSummary.taskCount,
                                    iconResId = R.drawable.flag,
                                    modifier = Modifier.weight(1f)
                                )

                                // Goals summary card
                                SummaryCard(
                                    title = "Goals",
                                    count = dashboardSummary.goalCount,
                                    iconResId = R.drawable.flag,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // Recent tasks section
                        item {
                            Text(
                                text = "Recent Tasks",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        // Task items
                        if (tasks.isEmpty()) {
                            item {
                                EmptyStateCard(message = "No tasks available")
                            }
                        } else {
                            items(tasks.take(3)) { task ->
                                TaskCard(task = task)
                            }

                            // View all tasks button
                            if (tasks.size > 3) {
                                item {
                                    TextButton(
                                        onClick = { /* Navigate to tasks screen */ },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("View All Tasks")
                                    }
                                }
                            }
                        }

                        // Analytics section
                        item {
                            Text(
                                text = "Analytics",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        // Screen Time card
                        item {
                            AnalyticsCard(
                                title = "Screen Time",
                                description = "Track your device usage and set healthy limits",
                                iconResId = R.drawable.clock,
                                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }

                        // Insights card
                        item {
                            AnalyticsCard(
                                title = "Insights",
                                description = "Get personalized productivity recommendations",
                                iconResId = R.drawable.insights,
                                backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }

                        // Refresh button
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = onRefresh,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Filled.Refresh, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Refresh Dashboard")
                            }
                        }
                    }
                }
            }
        }
    }
}


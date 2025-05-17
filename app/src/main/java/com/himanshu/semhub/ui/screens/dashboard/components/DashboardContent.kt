package com.himanshu.semhub.ui.screens.dashboard.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Summary cards
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

                        // Recent tasks section with compact header
                        CompactSectionHeader(title = "Recent Tasks")

                        // Single task preview (most recent)
                        if (tasks.isEmpty()) {
                            EmptyStateCard(
                                message = "No tasks available",
                                compact = true
                            )
                        } else {
                            TaskCard(
                                task = tasks.first(),
                                compact = false
                            )
                            if (tasks.size > 1) {
                                Text(
                                    text = "View all ${tasks.size} tasks",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .align(Alignment.End)
                                        .clickable { /* Navigate to tasks */ }
                                )
                            }
                        }

                        // Recent goals section
                        CompactSectionHeader(title = "Recent Goals")

                        // Single goal preview (most recent)
                        if (goals.isEmpty()) {
                            EmptyStateCard(
                                message = "No goals available",
                                compact = true
                            )
                        } else {
                            GoalCard(
                                goal = goals.first(),
                                compact = false
                            )
                            if (goals.size > 1) {
                                Text(
                                    text = "View all ${goals.size} goals",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .align(Alignment.End)
                                        .clickable { /* Navigate to goals */ }
                                )
                            }
                        }

                        // Analytics section
                        CompactSectionHeader(title = "Analytics")

                        // Analytics cards
                        AnalyticsCard(
                            title = "Screen Time",
                            iconResId = R.drawable.clock,
                            backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )

                        AnalyticsCard(
                            title = "Insights",
                            iconResId = R.drawable.insights,
                            backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                        )

                        // Spacer to push refresh button to bottom if needed
                        Spacer(modifier = Modifier.weight(1f, fill = false))

                        // Refresh button
                        Button(
                            onClick = onRefresh,
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Icon(
                                Icons.Filled.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Refresh")
                        }
                    }
                }
            }
        }
    }
}

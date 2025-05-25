package com.himanshu.semhub.ui.screens.goalDetails
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.himanshu.semhub.R
import com.himanshu.semhub.data.model.Goal
import com.himanshu.semhub.data.model.Subtask
import com.himanshu.semhub.data.model.Task

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalTaskDetailScreen(
    goalId: Int,
    navController: NavController,
    viewModel: GoalDetailsViewModel = hiltViewModel()
) {
    val goal by viewModel.selectedGoal.collectAsState()
    val associatedTasks by viewModel.associatedTasks.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(goalId) {
        viewModel.loadGoalDetails(goalId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Goal Overview",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                error != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = error ?: "An error occurred",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                goal == null -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 4.dp
                    )
                }
                else -> {
                    GoalTaskDetailContent(
                        goal = goal!!,
                        tasks = associatedTasks,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun GoalTaskDetailContent(
    goal: Goal,
    tasks: List<Task>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        // Goal Header Card with Gradient Background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primaryContainer
                        )
                    ),
                    shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = goal.name,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Type: ${goal.type}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                )
                goal.targetDate?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.clock),
                            contentDescription = "Target Date",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Target: ${formatDate(it)}",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                        )
                    }
                }
                goal.targetTasks?.let { taskIds ->
                    if (taskIds.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Tasks: ${taskIds.size}",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                                modifier = Modifier.weight(1f)
                            )
//                            if (tasks.isNotEmpty()) {
//                                val progress = tasks.count { it.status == true }.toFloat() / taskIds.size.toFloat()
//                                LinearProgressIndicator(
//                                    progress = { progress },
//                                    modifier = Modifier
//                                        .width(100.dp)
//                                        .height(8.dp),
//                                    color = MaterialTheme.colorScheme.secondary,
//                                    trackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f)
//                                )
//                            }
//
                        }
                    }
                }
            }
        }

        // Tasks Section
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (tasks.isNotEmpty()) {
                item {
                    Text(
                        text = "Associated Tasks",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                items(tasks) { task ->
                    AnimatedTaskCard(task = task)
                }
            } else {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.assignment),
                            contentDescription = "No tasks",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No associated tasks found.",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedTaskCard(task: Task) {
    var isHovered by remember { mutableStateOf(false) }
    val elevation by animateDpAsState(
        targetValue = if (isHovered) 8.dp else 4.dp,
        animationSpec = tween(durationMillis = 300), label = ""
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isHovered = true
                        tryAwaitRelease()
                        isHovered = false
                    }
                )
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(elevation)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = task.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Subject: ${task.subject}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            Text(
                text = "Type: ${task.type}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            task.priority?.let {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.flag),
                        contentDescription = "Priority",
                        tint = when (it.lowercase()) {
                            "high" -> MaterialTheme.colorScheme.error
                            "medium" -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.onSurface
                        },
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Priority: ${it.replaceFirstChar { char -> char.uppercase() }}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }
            task.deadline?.let {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.clock),
                        contentDescription = "Deadline",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Due: ${formatDate(it)}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }
            task.estimatedHours?.let {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Estimated Hours: $it",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            task.subtasks?.let { subtasks ->
                if (subtasks.isNotEmpty()) {
                    Text(
                        text = "Subtasks (${subtasks.size})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    subtasks.take(3).forEach { subtask ->
                        SubtaskItem(subtask = subtask)
                    }
                    if (subtasks.size > 3) {
                        Text(
                            text = "+ ${subtasks.size - 3} more",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SubtaskItem(subtask: Any) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 4.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Subtask",
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = (subtask as? Subtask)?.title ?: "Unnamed Subtask",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        (subtask as? Subtask)?.estimatedHours?.let {
            Text(
                text = "$it hrs",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun formatDate(date: String): String {
    return try {
        val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val outputFormat = java.text.SimpleDateFormat("MMM dd, yyyy HH:mm")
        val parsedDate = inputFormat.parse(date)
        parsedDate?.let { outputFormat.format(it) } ?: date
    } catch (e: Exception) {
        date
    }
}

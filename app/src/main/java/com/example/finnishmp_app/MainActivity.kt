/**
 * MainActivity is the main entry point for the Finnish Parliament Member (FinnishMP) application.
 * This app is built using Jetpack Compose for a modern, declarative user interface, allowing
 * users to view and interact with information about members of the Finnish Parliament.
 *
 * Key Features:
 * - Displays detailed information about Parliament members, such as name, party affiliation, and ratings.
 * - Provides navigation between different members using arrow buttons.
 * - Allows users to rate and comment on each member.
 * - Uses a ViewModel (`FinnishMPViewModel`) for state management.
 * - Utilizes a `NavController` for handling screen navigation.
 * - Leverages a database (`PMDatabase`) for storing and retrieving member information.
 *
 * This class sets up the user interface with a `NavHost` and manages lifecycle events like closing
 * the database connection in `onDestroy`.
 */
package com.example.finnishmp_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.finnishmp_app.FinnishMPApp.Companion.appContext
import com.example.finnishmp_app.db.PMDatabase
import com.example.finnishmp_app.db.ParliamentMember
import com.example.finnishmp_app.ui.theme.FinnishMP_AppTheme

enum class Screens {
    Info
}

val COLORS = mapOf(
    "primary" to Color(ContextCompat.getColor(appContext, R.color.primary))
)
/*
Muche Berhanu 2219580
*/

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = FinnishMPViewModel()

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            FinnishMP_AppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(navController = navController, startDestination = Screens.Info.name + "/") {
                        composable(route = Screens.Info.name + "/{hetekaId}?") {
                            val hetekaId: Int? = it.arguments?.getString("hetekaId")?.toIntOrNull()
                            viewModel.setMember(hetekaId)
                            MemberView(navController, viewModel, modifier = Modifier.padding(innerPadding))
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        val db = PMDatabase.getInstance()
        if (db.isOpen) {
            db.openHelper.close()
        }
        super.onDestroy()
    }
}

@Composable
fun MemberView(nav: NavController, viewModel: FinnishMPViewModel, modifier: Modifier = Modifier) {
    val member: State<ParliamentMember?> = viewModel.member.collectAsState(initial = null)
    val nextMember: State<ParliamentMember?> = viewModel.nextMember.collectAsState(initial = null)
    val previousMember: State<ParliamentMember?> = viewModel.previousMember.collectAsState(initial = null)
    val image = ImageHandler.getImage(member.value?.pictureUrl)

    Column(modifier = Modifier.padding(0.dp, 62.dp, 0.dp, 0.dp)) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(4.dp, COLORS["primary"]!!),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                NavigationButtons(nav, previousMember, nextMember)

                image?.let {
                    Image(
                        bitmap = it,
                        contentDescription = "${member.value?.firstname} ${member.value?.lastname}",
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }

                DisplayMemberInfo(member)

                StarRating(member, viewModel)

                CommentSection(member, viewModel)
            }
        }
    }
}

@Composable
fun NavigationButtons(nav: NavController, previousMember: State<ParliamentMember?>, nextMember: State<ParliamentMember?>) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            onClick = {
                nav.navigate(Screens.Info.name + "/${previousMember.value?.hetekaId}")
            },
            colors = ButtonDefaults.buttonColors(containerColor = COLORS["primary"]!!)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Previous Member",
                modifier = Modifier.size(32.dp) // Adjust the size if needed
            )
        }
        Button(
            onClick = {
                nav.navigate(Screens.Info.name + "/${nextMember.value?.hetekaId}")
            },
            colors = ButtonDefaults.buttonColors(containerColor = COLORS["primary"]!!)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = "Next Member",
                modifier = Modifier.size(32.dp) // Adjust the size if needed
            )
        }
    }
}

@Composable
fun DisplayMemberInfo(member: State<ParliamentMember?>) {
    member.value?.let {
        Text(
            text = "${it.firstname ?: ""} ${it.lastname ?: ""} (${it.bornYear ?: ""})",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Party: ${it.party ?: ""}, Constituency: ${it.constituency ?: ""}",
            fontSize = 24.sp
        )
        Text(
            text = "Rating: ${it.rating ?: ""}",
            fontSize = 24.sp
        )
    }
}

@Composable
fun StarRating(member: State<ParliamentMember?>, viewModel: FinnishMPViewModel) {
    val currentRating = member.value?.rating?.toIntOrNull() ?: 0

    Text(
        text = "Rate and comment:",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 16.dp)
    )

    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier.fillMaxWidth()
    ) {
        for (i in 1..5) {
            Star(
                isRated = i <= currentRating,
                onClick = {
                    member.value?.rating = i.toString()
                    viewModel.updateMember(member.value!!)
                }
            )
        }
    }
}

@Composable
fun Star(isRated: Boolean, onClick: () -> Unit) {
    val starColor = if (isRated) Color.Yellow else Color.LightGray

    Text(
        text = "★",
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        color = starColor,
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp)
    )
}



@Composable
fun CommentSection(member: State<ParliamentMember?>, viewModel: FinnishMPViewModel) {
    TextField(
        value = member.value?.notes ?: "",
        modifier = Modifier
            .padding(16.dp, 0.dp)
            .fillMaxWidth(),
        textStyle = TextStyle.Default.copy(fontSize = 24.sp),
        onValueChange = {
            member.value?.notes = it
            viewModel.updateMember(member.value!!)
        }
    )
}

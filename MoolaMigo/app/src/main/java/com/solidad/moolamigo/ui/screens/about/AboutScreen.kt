package com.solidad.moolamigo.ui.screens.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.solidad.moolamigo.R
import com.solidad.moolamigo.ui.theme.newgreen

@Composable
fun AboutScreen(navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background((newgreen))
            .padding(horizontal = 10.dp, vertical = 100.dp) ,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // App Icon or Mascot
        Icon(
            painter = painterResource(id = R.drawable.img),
            contentDescription = "MoolaMigo Mascot",
            tint = Color.Unspecified,
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            "MoolaMigo",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF40916C)
        )

        Text(
            "Your money’s best friend",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Version 1.0.0",
            fontSize = 14.sp,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "MoolaMigo helps you budget better by making it simple, friendly, and fun. "
                    + "Track your expenses, set savings goals, and stay in control of your finances with your coin buddy by your side.",
            fontSize = 20.sp,
            color = Color.Black,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            "Developed by Team Moola",
            fontSize = 14.sp,
            color = Color.Gray
        )


    }
}

@Preview(showBackground = true)
@Composable
fun AboutScreenPreview(){
    AboutScreen(navController= rememberNavController())
}
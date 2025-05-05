package com.solidad.moolamigo.ui.screens.item

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.solidad.moolamigo.navigation.ROUT_CLOTH
import com.solidad.moolamigo.navigation.ROUT_FOOD
import com.solidad.moolamigo.navigation.ROUT_TRANSPORT
import com.solidad.moolamigo.ui.theme.newgreen1

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemScreen(navController : NavController){
    Column (modifier = Modifier.fillMaxSize()
    ){
        val mContext = LocalContext.current

//TopAppBar
        TopAppBar(
            title = { Text(text = "Products") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = newgreen1,
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White,
                actionIconContentColor = Color.White
            ),
            navigationIcon = {
                IconButton(onClick = {}) {
                    Icon(imageVector = Icons.Default.Menu, contentDescription = "menu")
                }
            },
            actions = {
                IconButton(onClick = {}) {
                    Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "")
                }

                IconButton(onClick = {}) {
                    Icon(imageVector = Icons.Default.Notifications, contentDescription = "")
                }

                IconButton(onClick = {
                    //navController.navigate(ROUT_INTENT)
                }) {
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "")
                }



            }
        )
//End


        Spacer(modifier = Modifier.height(20.dp))

        //SearchBar
        var search by remember { mutableStateOf("") }
        OutlinedTextField(
            value = search,
            onValueChange = { search = it},
            modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp),
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "")},
            placeholder = {Text(text = "Search...")}

        )

        //End of Searchbar

        Spacer(modifier = Modifier.height(20.dp))

        Column (modifier = Modifier.verticalScroll(rememberScrollState())){
            //Row

            Row (modifier = Modifier.padding(start = 20.dp)){

                Spacer(modifier = Modifier.width(20.dp))


                    Button(
                        onClick = {
                            val callIntent=Intent(Intent.ACTION_DIAL)
                            callIntent.data="tel:0748995456".toUri()
                            mContext.startActivity(callIntent)


                        },
                        colors = ButtonDefaults.buttonColors(newgreen1),
                        shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .clickable{navController.navigate(ROUT_CLOTH)}

                    ) {
                        Text(text = "Clothes")


                    }





                }


            }
            //End of raw

            //Row

            Row (modifier = Modifier.padding(start = 20.dp)){


                Spacer(modifier = Modifier.width(20.dp))


                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(newgreen1),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .clickable{navController.navigate(ROUT_TRANSPORT)}

                    ) {
                        Text(text = "Transport")


                    }





                }

        //Row

        Row (modifier = Modifier.padding(start = 20.dp)){


            Spacer(modifier = Modifier.width(20.dp))


            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(newgreen1),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .clickable{navController.navigate(ROUT_FOOD)}

            ) {
                Text(text = "Foods & Restaurants")


            }





        }



    }
            //End of raw







        }







@Preview(showBackground = true)
@Composable
fun ItemScreenPreview(){
    ItemScreen(rememberNavController())


}

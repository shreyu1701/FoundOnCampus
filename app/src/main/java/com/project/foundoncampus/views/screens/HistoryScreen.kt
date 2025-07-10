package com.project.foundoncampus.views.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.project.foundoncampus.model.HistoryItem


val dummyHistoryList = listOf(
    HistoryItem(
        id = "1",
        name = "Black Wallet",
        type = "Lost",
        date = "2025-07-05",
        status = "Pending",
        thumbnailUrl = "https://eu-images.contentstack.com/v3/assets/blt7dcd2cfbc90d45de/blt6e3ce87e83b602fe/60dbb40b5c97640f9442cbe3/2_264.jpg"
    ),
    HistoryItem(
        id = "2",
        name = "Blue Backpack",
        type = "Found",
        date = "2025-07-01",
        status = "Returned",
        thumbnailUrl = "https://www.bhphotovideo.com/images/images2500x2500/jansport_js00typ701f_right_pack_31l_backpack_1186489.jpg"
    ),
    HistoryItem(
        id = "3",
        name = "Red Water Bottle",
        type = "Lost",
        date = "2025-06-28",
        status = "Closed",
        thumbnailUrl = "https://tse2.mm.bing.net/th/id/OIP.gGbd-AUqCACvC_3U704GxgHaLH?rs=1&pid=ImgDetMain&o=7&rm=3"
    ),
    HistoryItem(
        id = "4",
        name = "Apple AirPods Pro",
        type = "Lost",
        date = "2025-06-21",
        status = "Pending",
        thumbnailUrl = "https://tse1.mm.bing.net/th/id/OIP.JuponZSD-nCR4sozn_CsagHaHa?rs=1&pid=ImgDetMain&o=7&rm=3"
    ),
    HistoryItem(
        id = "5",
        name = "Dell Laptop Charger",
        type = "Found",
        date = "2025-06-19",
        status = "Returned",
        thumbnailUrl = "https://i.ebayimg.com/images/g/SuEAAOSwUYla4jTm/s-l1600.jpg"
    ),
    HistoryItem(
        id = "6",
        name = "Library Card",
        type = "Lost",
        date = "2025-06-15",
        status = "Closed",
        thumbnailUrl = "https://display.curiocity.com/uploads/2024/05/357361164_18367049209013111_5218287473921744643_n.jpg?format=jpeg&w=1024&h=543"
    ),
    HistoryItem(
        id = "7",
        name = "Keychain with Car Keys",
        type = "Found",
        date = "2025-06-12",
        status = "Pending",
        thumbnailUrl = "https://images.unsplash.com/photo-1517694712202-14dd9538aa97"
    ),
    HistoryItem(
        id = "8",
        name = "Grey Scarf",
        type = "Lost",
        date = "2025-06-10",
        status = "Closed",
        thumbnailUrl = "https://tse3.mm.bing.net/th/id/OIP.8xMTrRARxf3vP4RgvXzbkwHaHa?rs=1&pid=ImgDetMain&o=7&rm=3"
    ),
    HistoryItem(
        id = "9",
        name = "HP Calculator",
        type = "Found",
        date = "2025-06-07",
        status = "Returned",
        thumbnailUrl = "https://ssl-product-images.www8-hp.com/digmedialib/prodimg/lowres/c06916132.png"
    ),
    HistoryItem(
        id = "10",
        name = "Silver Bracelet",
        type = "Lost",
        date = "2025-06-05",
        status = "Pending",
        thumbnailUrl = "https://m.media-amazon.com/images/I/51K37T9W52L._AC_UY1100_.jpg"
    )
)


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HistoryScreen(navController: NavController,
                  dummyHistoryList: List<HistoryItem> = com.project.foundoncampus.views.screens.dummyHistoryList,
                  onItemClick: (HistoryItem) -> Unit = {}
) {

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("History") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Blue,
                    titleContentColor = Color.White
                ),

                )
        }//topBar
    ) { innerPadding ->
        LazyColumn(contentPadding = innerPadding) {
            items(dummyHistoryList) { item ->
                HistoryItemCard(item, onItemClick)
            }
        }
    }//Scaffold
}



@Composable
fun HistoryItemCard(item: HistoryItem, onClick: (HistoryItem) -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onClick(item) },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            if (item.thumbnailUrl != null) {
                // Use Coil or Glide for image loading
                AsyncImage(
                    model = item.thumbnailUrl,
                    contentDescription = item.name,
                    modifier = Modifier.size(56.dp).clip(CircleShape)
                )
            } else {
                Icon(Icons.Default.Image, contentDescription = "No Image", modifier = Modifier.size(56.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(item.name, style = MaterialTheme.typography.titleMedium)
                Text("Type: ${item.type}")
                Text("Status: ${item.status}")
                Text(item.date, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
package com.example.quickdraw.main.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun BasicTabLayout(paddingValues: PaddingValues = PaddingValues(0.dp)) {
    SecondaryTabRow(
        selectedTabIndex = 2,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier.padding(paddingValues)
    ) {
        Tab(selected = false, onClick = {}, text = { Text("Weapons")})
        Tab(selected = false, onClick = {}, text = { Text("Bullets")})
        //Tab(selected = false, onClick = {}, text = { Text("Money")})
        Tab(selected = false, onClick = {}, text = { Text("Medikits")})
        Tab(selected = false, onClick = {}, text = { Text("Upgrades")})
    }
}
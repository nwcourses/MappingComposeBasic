package com.example.mappingcompose

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import com.example.mappingcompose.ui.theme.MappingComposeTheme
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker


class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            MappingComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(

                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    MapControlComposable(
                        //  Modifier.padding(innerPadding),
                        Modifier.padding(top = 1.dp))
                }
            }
        }
    }
}


@Composable
fun MapControlComposable(
    modifier: Modifier,
    controlsHeight: Dp = 64.dp
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {

        var tmpLat: String by remember { mutableStateOf("") }
        var tmpLon: String by remember { mutableStateOf("") }
        var geoPoint: GeoPoint by remember { mutableStateOf(GeoPoint(51.05, -0.72)) }


        Surface(

            modifier = modifier
                .fillMaxWidth()
                .height(controlsHeight)
                .align(Alignment.BottomCenter)
                .zIndex(2.0f)
        ) {


            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(2.dp, MaterialTheme.colorScheme.secondary))
            ) {

                TextField(
                    tmpLat,
                    modifier = Modifier
                        .weight(1.0f)
                        .padding(end = 8.dp),
                    onValueChange = { tmpLat = it },
                    label = { Text("Latitude") })
                TextField(
                    tmpLon,
                    modifier = Modifier
                        .weight(1.0f)
                        .padding(end = 8.dp),
                    onValueChange = { tmpLon = it },
                    label = { Text("Longitude") })
                Button(
                    {
                        geoPoint = GeoPoint(tmpLat.toDouble(), tmpLon.toDouble())
                    },
                    modifier = Modifier.weight(1.0f)
                ) { Text("Go!") }
            }
        }
        MapComposable(
            mod = Modifier
                .fillMaxSize()
                .align(Alignment.TopCenter)
                .height(this.maxHeight - controlsHeight)
                .zIndex(1.0f)
                .border(
                    BorderStroke(4.dp, MaterialTheme.colorScheme.primary)
                ), lonLat = geoPoint
        )
    }
}

@Composable
fun MapComposable(mod: Modifier, lonLat: GeoPoint) {

    AndroidView(
        modifier = mod,
        factory = { ctx ->
            // This line sets the user agent, a requirement to download OSM maps
            Configuration.getInstance()
                .load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))

            val map1 = MapView(ctx).apply {
                setClickable(true)
                setMultiTouchControls(true)
                setTileSource(TileSourceFactory.MAPNIK)
            }
            val marker = Marker(map1)
            marker.apply {
                position = GeoPoint(51.05, -0.72)
                title = "Start Position"
            }

            map1.overlays.add(marker)
            map1
        },
        update = { view ->
            view.controller.setZoom(14.0)
            view.controller.setCenter(lonLat)
        }
    )
}







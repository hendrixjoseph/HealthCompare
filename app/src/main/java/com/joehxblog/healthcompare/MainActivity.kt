package com.joehxblog.healthcompare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val healthClient = HealthConnectClient.getOrCreate(this)

        val permissions = setOf(
            HealthPermission.getReadPermission(StepsRecord::class),
            HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class)
        )

        val requestPermissionActivityContract = PermissionController.createRequestPermissionResultContract()

        fun launch() {
            setContent {
                MaterialTheme {
                    Surface {
                        HealthDashboard(RealHealthFunctions(healthClient))
//                        HealthDashboard(MockHealthFunctions())
                    }
                }
            }
        }

        val requestPermissions = registerForActivityResult(requestPermissionActivityContract) { granted ->
            if (granted.containsAll(permissions)) {
                launch()
            } else {
                // Lack of required permissions
            }
        }

        suspend fun checkPermissionsAndRun(healthConnectClient: HealthConnectClient) {
            val granted = healthConnectClient.permissionController.getGrantedPermissions()
            if (granted.containsAll(permissions)) {
                launch()
            } else {
                requestPermissions.launch(permissions)
            }
        }

        lifecycleScope.launch {
            checkPermissionsAndRun(healthClient)
        }
    }
}
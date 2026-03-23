package com.joehxblog.healthcompare

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.lifecycle.lifecycleScope
import com.joehxblog.healthcompare.permission.HealthPermissionActivity
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val healthClient = HealthConnectClient.getOrCreate(this)

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

        val permissions = setOf(
            HealthPermission.getReadPermission(StepsRecord::class),
            HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class)
        )

        val rationaleLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    launch()
                } else {
                    finish()
                }
        }

        suspend fun checkPermissionsAndRun(healthConnectClient: HealthConnectClient) {
            val granted = healthConnectClient.permissionController.getGrantedPermissions()
            if (granted.containsAll(permissions)) {
                launch()
            } else {
                val intent = Intent(this, HealthPermissionActivity::class.java)
                rationaleLauncher.launch(intent)
            }
        }

        lifecycleScope.launch {
            checkPermissionsAndRun(healthClient)
        }
    }
}
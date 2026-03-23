package com.joehxblog.healthcompare.permission

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord

class HealthPermissionActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val requestPermissionActivityContract = PermissionController.createRequestPermissionResultContract()

        val permissions = setOf(
            HealthPermission.getReadPermission(StepsRecord::class),
            HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class)
        )

        val requestPermissions = registerForActivityResult(requestPermissionActivityContract) { granted ->
            if (granted.containsAll(permissions)) {
                setResult(RESULT_OK)
                finish()
            } else {
                finish()
            }
        }

        setContent {
            MaterialTheme {
                HealthPermissionScreen(
                    onPermissionGranted = {
                        requestPermissions.launch(permissions)
                    }
                )
            }
        }
    }
}
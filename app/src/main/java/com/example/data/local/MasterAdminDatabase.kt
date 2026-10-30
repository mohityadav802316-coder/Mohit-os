package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.MasterAdminDao
import com.example.data.local.entity.AuditLogEntity
import com.example.data.local.entity.ChangeRequestEntity
import com.example.data.local.entity.DeploymentEntity
import com.example.data.local.entity.DynamicContentFieldEntity
import com.example.data.local.entity.IntegrationEntity
import com.example.data.local.entity.MediaAssetEntity
import com.example.data.local.entity.OwnerConfigEntity
import com.example.data.local.entity.ProjectEntity

@Database(
    entities = [
        ProjectEntity::class,
        ChangeRequestEntity::class,
        DeploymentEntity::class,
        IntegrationEntity::class,
        AuditLogEntity::class,
        MediaAssetEntity::class,
        OwnerConfigEntity::class,
        DynamicContentFieldEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class MasterAdminDatabase : RoomDatabase() {
    abstract fun dao(): MasterAdminDao

    companion object {
        @Volatile
        private var INSTANCE: MasterAdminDatabase? = null

        fun getDatabase(context: Context): MasterAdminDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MasterAdminDatabase::class.java,
                    "master_admin_database.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}

package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.MasterAdminDatabase
import com.example.data.repository.MasterAdminRepository
import com.example.domain.model.DatabaseProvider
import com.example.domain.model.EnvironmentType
import com.example.domain.model.HeroContent
import com.example.domain.model.HostingProvider
import com.example.domain.model.ProjectType
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read app name string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Master Admin", appName)
  }

  @Test
  fun `test project creation and retrieval in repository`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = MasterAdminDatabase.getDatabase(context)
    val repo = MasterAdminRepository(db.dao())

    val project = repo.addProject(
      name = "Test Enterprise API",
      description = "Fastify gateway service",
      url = "https://api.test.internal",
      projectType = ProjectType.BACKEND_API,
      environment = EnvironmentType.STAGING,
      repositoryUrl = "github.com/mohityadav/test-api",
      hostingProvider = HostingProvider.CLOUD_RUN,
      databaseProvider = DatabaseProvider.POSTGRESQL
    )

    assertNotNull(project.id)
    assertEquals("Test Enterprise API", project.name)
    assertEquals(ProjectType.BACKEND_API, project.projectType)

    val fetched = repo.getProjectById(project.id)
    assertNotNull(fetched)
    assertEquals("https://api.test.internal", fetched?.url)
  }

  @Test
  fun `test website CMS hero update`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = MasterAdminDatabase.getDatabase(context)
    val repo = MasterAdminRepository(db.dao())

    val updatedHero = HeroContent(
      title = "Mohit Yadav - AI Principal",
      subtitle = "Senior Android & Full-Stack Architect",
      badge = "Verified Leader",
      bio = "Deploying production-grade architectures globally.",
      avatarUrl = "https://images.unsplash.com/sample.jpg",
      ctaText = "View Case Studies",
      ctaUrl = "#case-studies"
    )

    val result = repo.updateHeroContent(updatedHero)
    assertTrue(result.isSuccess)
    assertEquals("Mohit Yadav - AI Principal", repo.contentState.value.hero.title)
  }

  @Test
  fun `test owner enrollment security restrictions`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = MasterAdminDatabase.getDatabase(context)
    val repo = MasterAdminRepository(db.dao())

    // 1. Rejects unauthorized email
    val invalidEmailResult = repo.enrollOwner(
      name = "Impostor",
      email = "hacker@example.com",
      password = "Password123!",
      bootstrapCode = "MASTER_BOOTSTRAP_2026",
      enableGoogle = true
    )
    assertTrue(invalidEmailResult.isFailure)
    assertTrue(invalidEmailResult.exceptionOrNull()?.message?.contains("restricted", ignoreCase = true) == true)

    // 2. Rejects invalid bootstrap secret
    val invalidSecretResult = repo.enrollOwner(
      name = "Mohit Yadav",
      email = "mohityadav802316@gmail.com",
      password = "Password123!",
      bootstrapCode = "WRONG_SECRET",
      enableGoogle = true
    )
    assertTrue(invalidSecretResult.isFailure)
    assertTrue(invalidSecretResult.exceptionOrNull()?.message?.contains("Invalid Bootstrap Secret", ignoreCase = true) == true)

    // 3. Rejects too short password
    val shortPwResult = repo.enrollOwner(
      name = "Mohit Yadav",
      email = "mohityadav802316@gmail.com",
      password = "123",
      bootstrapCode = "MASTER_BOOTSTRAP_2026",
      enableGoogle = true
    )
    assertTrue(shortPwResult.isFailure)

    // 4. Successfully enrolls legitimate owner
    val validEnrollResult = repo.enrollOwner(
      name = "Mohit Yadav",
      email = "mohityadav802316@gmail.com",
      password = "StrongMasterPass123!",
      bootstrapCode = "MASTER_BOOTSTRAP_2026",
      enableGoogle = true
    )
    assertTrue(validEnrollResult.isSuccess)

    // 5. Verify enrollment locked
    val reEnrollAttempt = repo.enrollOwner(
      name = "Mohit Yadav",
      email = "mohityadav802316@gmail.com",
      password = "AnotherPassword",
      bootstrapCode = "MASTER_BOOTSTRAP_2026",
      enableGoogle = true
    )
    assertTrue(reEnrollAttempt.isFailure)
    assertTrue(reEnrollAttempt.exceptionOrNull()?.message?.contains("already locked", ignoreCase = true) == true)

    // 6. Test password verification for reset
    val wrongReset = repo.resetOwnerEnrollment("WrongPassword")
    assertTrue(wrongReset.isFailure)

    val validReset = repo.resetOwnerEnrollment("StrongMasterPass123!")
    assertTrue(validReset.isSuccess)
  }

  @Test
  fun `test dynamic content field self-service CMS`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = MasterAdminDatabase.getDatabase(context)
    val repo = MasterAdminRepository(db.dao())

    val project = repo.addProject(
      name = "Mohit Portfolio Live",
      description = "Next.js Production Website",
      url = "https://mohityadav.dev",
      projectType = ProjectType.WEB_APP,
      environment = EnvironmentType.PRODUCTION,
      repositoryUrl = "github.com/mohityadav/portfolio",
      hostingProvider = HostingProvider.VERCEL,
      databaseProvider = DatabaseProvider.SUPABASE
    )

    // Add dynamic field without code edits
    val field = repo.addDynamicField(
      projectId = project.id,
      sectionKey = "announcements",
      fieldKey = "banner_text",
      fieldLabel = "Top Banner Announcement",
      fieldType = "TEXT",
      defaultValue = "Open for Senior Staff Architect roles"
    )

    assertNotNull(field.id)
    assertEquals("banner_text", field.fieldKey)

    // Update field value
    repo.updateDynamicFieldValue(field.id, "Available for consultation Q4 2026")

    // Delete field
    repo.deleteDynamicField(field.id)
  }

  @Test
  fun `test project self-configurable api and database handshake`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = MasterAdminDatabase.getDatabase(context)
    val repo = MasterAdminRepository(db.dao())

    val project = repo.addProject(
      name = "Microservice Gateway",
      description = "Core Gateway",
      url = "https://gateway.mohityadav.dev",
      projectType = ProjectType.BACKEND_API,
      environment = EnvironmentType.PRODUCTION,
      repositoryUrl = "github.com/mohityadav/gateway",
      hostingProvider = HostingProvider.CLOUD_RUN,
      databaseProvider = DatabaseProvider.POSTGRESQL,
      apiBaseUrl = "https://gateway.mohityadav.dev/api/v1",
      apiAuthHeader = "Bearer",
      apiAuthToken = "token_sample_test",
      dbConnectionString = "postgres://admin:pass@postgres.internal:5432/gateway_db"
    )

    // Test API handshake
    val apiTest = repo.testProjectApiConnection(
      projectId = project.id,
      url = "https://gateway.mohityadav.dev/api/v1",
      authHeader = "Bearer",
      token = "token_sample_test"
    )
    assertTrue(apiTest.isSuccess)
    val testDetails = apiTest.getOrNull()
    assertNotNull(testDetails)
    assertEquals(200, testDetails?.statusCode)
    assertTrue(testDetails?.isSuccess == true)

    // Test DB handshake
    val dbTest = repo.testProjectDatabaseConnection(
      projectId = project.id,
      provider = DatabaseProvider.POSTGRESQL,
      connectionUri = "postgres://admin:pass@postgres.internal:5432/gateway_db"
    )
    assertTrue(dbTest.isSuccess)
  }
}

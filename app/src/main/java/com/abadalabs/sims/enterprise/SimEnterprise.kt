package com.abadalabs.sims.enterprise

import android.content.Context
import androidx.room.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import com.example.BuildConfig

// ==========================================
// 1. ROOM DATABASE: ENTITY, DAO & DATABASE
// ==========================================

@Entity(tableName = "sims")
data class SimEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val hunger: Float = 100f, // 0 a 100
    val energy: Float = 100f, // 0 a 100
    val happiness: Float = 100f, // 0 a 100
    val memories: String = "[]" // JSON serializado de las últimas 3 memorias
)

@Dao
interface SimDao {
    @Query("SELECT * FROM sims")
    fun getAllSims(): Flow<List<SimEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSim(sim: SimEntity)

    @Update
    suspend fun updateSim(sim: SimEntity)
}

@Database(entities = [SimEntity::class], version = 1, exportSchema = false)
abstract class SimDatabase : RoomDatabase() {
    abstract fun simDao(): SimDao

    companion object {
        @Volatile
        private var INSTANCE: SimDatabase? = null

        fun getDatabase(context: Context): SimDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SimDatabase::class.java,
                    "sim_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// ==========================================
// 2. GEMINI AI SERVICE (Google AI SDK)
// ==========================================

@Serializable
data class SimActionPlan(
    val nextAction: String,
    val reason: String,
    val hungerDelta: Float,
    val energyDelta: Float,
    val happinessDelta: Float
)

class GeminAiService {
    // Clave de API provista por el entorno seguro via BuildConfig
    private val apiKey = BuildConfig.GEMINI_API_KEY
    
    private val generativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-2.5-flash",
            apiKey = apiKey.ifBlank { "PLACEHOLDER_KEY" },
            generationConfig = generationConfig {
                responseMimeType = "application/json"
            }
        )
    }

    suspend fun generateNextPlan(
        simName: String,
        hunger: Float,
        energy: Float,
        happiness: Float,
        recentMemories: List<String>
    ): SimActionPlan? {
        try {
            if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                return generateLocalSimPlan(simName, hunger, energy, happiness)
            }

            val prompt = """
                Actúa como el motor de IA de un Sim. Analiza el estado actual del Sim llamado $simName:
                - Hambre: $hunger / 100
                - Energía: $energy / 100
                - Felicidad: $happiness / 100
                - Últimas memorias: ${recentMemories.joinToString(", ")}

                Devuelve un JSON estricto con la siguiente estructura exacta:
                {
                  "nextAction": "Acción recomendada en formato breve",
                  "reason": "Razón corta de la acción",
                  "hungerDelta": <cambio numérico para hambre, ej: 10.0 o -5.0>,
                  "energyDelta": <cambio numérico para energía>,
                  "happinessDelta": <cambio numérico para felicidad>
                }
            """.trimIndent()

            val response = generativeModel.generateContent(prompt)
            val jsonText = response.text ?: return generateLocalSimPlan(simName, hunger, energy, happiness)
            val cleanJson = jsonText.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
            
            return Json { ignoreUnknownKeys = true }.decodeFromString<SimActionPlan>(cleanJson)
        } catch (e: Exception) {
            return generateLocalSimPlan(simName, hunger, energy, happiness)
        }
    }

    private fun generateLocalSimPlan(simName: String, hunger: Float, energy: Float, happiness: Float): SimActionPlan {
        return when {
            hunger < 40f -> SimActionPlan(
                nextAction = "Cocinar una comida caliente",
                reason = "$simName tiene mucha hambre.",
                hungerDelta = 35f,
                energyDelta = -5f,
                happinessDelta = 10f
            )
            energy < 30f -> SimActionPlan(
                nextAction = "Dormir una siesta reparadora",
                reason = "$simName siente fatiga acumulada.",
                hungerDelta = -5f,
                energyDelta = 40f,
                happinessDelta = 5f
            )
            happiness < 40f -> SimActionPlan(
                nextAction = "Jugar videojuegos y relajarse",
                reason = "$simName necesita entretenimiento.",
                hungerDelta = -5f,
                energyDelta = -5f,
                happinessDelta = 30f
            )
            else -> SimActionPlan(
                nextAction = "Trabajar en un proyecto creativo",
                reason = "$simName está en estado óptimo y motivado.",
                hungerDelta = -10f,
                energyDelta = -15f,
                happinessDelta = 15f
            )
        }
    }
}

// ==========================================
// 3. SIM VIEWMODEL
// ==========================================

class SimViewModel(private val simDao: SimDao) : ViewModel() {
    private val aiService = GeminAiService()

    val sims: Flow<List<SimEntity>> = simDao.getAllSims()

    fun addNewSim(name: String) {
        viewModelScope.launch {
            val newSim = SimEntity(
                name = name,
                memories = Json.encodeToString(listOf("Llegó a la ciudad", "Comenzó su nueva vida"))
            )
            simDao.insertSim(newSim)
        }
    }

    fun triggerAiNeedUpdate(sim: SimEntity) {
        viewModelScope.launch {
            val memoriesList: List<String> = try {
                Json.decodeFromString(sim.memories)
            } catch (e: Exception) {
                emptyList()
            }

            val plan = aiService.generateNextPlan(sim.name, sim.hunger, sim.energy, sim.happiness, memoriesList)
            if (plan != null) {
                val updatedHunger = (sim.hunger + plan.hungerDelta).coerceIn(0f, 100f)
                val updatedEnergy = (sim.energy + plan.energyDelta).coerceIn(0f, 100f)
                val updatedHappiness = (sim.happiness + plan.happinessDelta).coerceIn(0f, 100f)
                
                val updatedMemories = (listOf(plan.nextAction) + memoriesList).take(3)
                val serializedMemories = Json.encodeToString(updatedMemories)

                val updatedSim = sim.copy(
                    hunger = updatedHunger,
                    energy = updatedEnergy,
                    happiness = updatedHappiness,
                    memories = serializedMemories
                )
                simDao.updateSim(updatedSim)
            }
        }
    }
}

class SimViewModelFactory(private val simDao: SimDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SimViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SimViewModel(simDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

// ==========================================
// 4. JETPACK COMPOSE UI (SCREENS & COMPONENTS)
// ==========================================

@Composable
fun SimListScreen(
    sims: List<SimEntity>,
    onAddSimClick: (String) -> Unit,
    onTriggerAi: (SimEntity) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var newSimName by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Sim")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (sims.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay Sims registrados. ¡Crea uno nuevo!", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(sims, key = { it.id }) { sim ->
                        SimCard(sim = sim, onTriggerAi = { onTriggerAi(sim) })
                    }
                }
            }

            if (showAddDialog) {
                AlertDialog(
                    onDismissRequest = { showAddDialog = false },
                    title = { Text("Crear Nuevo Sim") },
                    text = {
                        OutlinedTextField(
                            value = newSimName,
                            onValueChange = { newSimName = it },
                            label = { Text("Nombre del Sim") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                if (newSimName.isNotBlank()) {
                                    onAddSimClick(newSimName)
                                    newSimName = ""
                                    showAddDialog = false
                                }
                            }
                        ) {
                            Text("Guardar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showAddDialog = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SimCard(sim: SimEntity, onTriggerAi: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = sim.name, style = MaterialTheme.typography.titleLarge)
                IconButton(onClick = onTriggerAi) {
                    Icon(Icons.Default.Refresh, contentDescription = "Actualizar por IA")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            NeedProgressBar(label = "Hambre", value = sim.hunger)
            Spacer(modifier = Modifier.height(4.dp))
            NeedProgressBar(label = "Energía", value = sim.energy)
            Spacer(modifier = Modifier.height(4.dp))
            NeedProgressBar(label = "Felicidad", value = sim.happiness)
        }
    }
}

@Composable
fun NeedProgressBar(label: String, value: Float) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = label, style = MaterialTheme.typography.bodySmall)
            Text(text = "${value.toInt()}%", style = MaterialTheme.typography.bodySmall)
        }
        LinearProgressIndicator(
            progress = { value / 100f },
            modifier = Modifier.fillMaxWidth().height(6.dp),
        )
    }
}

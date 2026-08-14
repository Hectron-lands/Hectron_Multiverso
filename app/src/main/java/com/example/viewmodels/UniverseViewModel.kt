package com.example.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.Planet
import com.example.data.Player
import com.example.data.Universe
import com.example.data.UniverseEvent
import com.example.data.Faction
import com.example.data.Achievement
import com.example.data.Lore
import com.example.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONObject

class UniverseViewModel(
    private val database: AppDatabase
) : ViewModel() {
    private val dao = database.universeDao()

    val universe: StateFlow<Universe?> = dao.getUniverse()
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val planets: StateFlow<List<Planet>> = dao.getPlanets()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        
    val events: StateFlow<List<UniverseEvent>> = dao.getEvents()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        
    val players: StateFlow<List<Player>> = dao.getPlayers()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val factions: StateFlow<List<Faction>> = dao.getFactions()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        
    val achievements: StateFlow<List<Achievement>> = dao.getAchievements()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        
    val loreList: StateFlow<List<Lore>> = dao.getLore()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-pro",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    fun initializeUniverse(name: String, ownerId: String) {
        viewModelScope.launch {
            if (universe.value == null) {
                dao.insertUniverse(Universe(name = name, ownerId = ownerId))
                dao.insertPlayer(Player(name = ownerId, level = 100)) // Streamer is max level
                
                // Initialize Factions
                val initialFactions = listOf(
                    Faction(name = "Federación Galáctica", description = "Alianza política enfocada en la diplomacia y el orden.", colorHex = "#3B82F6", bonus = "+10% Comercio"),
                    Faction(name = "Consorcio Mercantil", description = "Conglomerado económico que domina las rutas comerciales.", colorHex = "#F59E0B", bonus = "+20% Oro"),
                    Faction(name = "Colectivo Científico", description = "Sociedad dedicada a desentrañar los secretos del universo.", colorHex = "#10B981", bonus = "+15% Conocimiento")
                )
                dao.insertFactions(initialFactions)
                
                // Initialize Achievements
                val initialAchievements = listOf(
                    Achievement(name = "Primer Viajero Estelar", description = "Exploró su primer planeta desconocido.", iconName = "RocketLaunch"),
                    Achievement(name = "Constructor Maestro", description = "Construyó 10 estructuras espaciales.", iconName = "Handyman"),
                    Achievement(name = "Descubridor de Mundos Raros", description = "Encontró un planeta de clase exótica.", iconName = "TravelExplore"),
                    Achievement(name = "Magnate Galáctico", description = "Acumuló 100,000 unidades de Oro.", iconName = "MonetizationOn"),
                    Achievement(name = "Señor de la Guerra", description = "Ganó 5 batallas de facciones.", iconName = "Shield")
                )
                dao.insertAchievements(initialAchievements)
            }
        }
    }

    fun generateLore(topic: String) {
        viewModelScope.launch {
            try {
                val prompt = "Genera un lore épico (2-3 párrafos) sobre '$topic' para el universo de ciencia ficción 'HECTRON Live Universe'. Describe detalles sobre planetas, razas o eventos históricos."
                val response = generativeModel.generateContent(prompt)
                val text = response.text?.trim()
                if (text != null) {
                    dao.insertLore(Lore(universeId = universe.value?.id ?: "unknown", topic = topic, content = text))
                    
                    // Sync Lore to the BigQuery Universe Server via Retrofit
                    try {
                        com.example.network.NetworkModule.universeApi.syncLocalLore(
                            listOf(
                                com.example.network.LoreEntryResponse(
                                    id = java.util.UUID.randomUUID().toString(),
                                    title = topic,
                                    content = text,
                                    faction = null,
                                    generatedAt = System.currentTimeMillis()
                                )
                            )
                        )
                    } catch (e: Exception) {
                        // Fail silently if backend is offline to prevent crashing the app
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun generatePlanet(discoverer: String) {
        viewModelScope.launch {
            try {
                val prompt = """
                    Generate a JSON object representing a sci-fi planet.
                    Format: { "name": "...", "type": "...", "description": "...", "resources": { "iron": 100, "gold": 50, "crystal": 10, "energy": 500, "knowledge": 5 } }
                    type must be one of: rocky, gas, ice, lava, ocean. 
                    Resources should be random numbers based on the type.
                    Only return valid JSON without markdown wrapping.
                """.trimIndent()
                
                val response = generativeModel.generateContent(prompt)
                val text = response.text?.replace("```json", "")?.replace("```", "")?.trim()
                
                if (text != null) {
                    val json = JSONObject(text)
                    val resources = json.getJSONObject("resources")
                    val planet = Planet(
                        universeId = universe.value?.id ?: "unknown",
                        name = json.getString("name"),
                        type = json.getString("type"),
                        description = json.getString("description"),
                        iron = resources.optInt("iron", 0),
                        gold = resources.optInt("gold", 0),
                        crystal = resources.optInt("crystal", 0),
                        energy = resources.optInt("energy", 0),
                        knowledge = resources.optInt("knowledge", 0),
                        discoveredBy = discoverer
                    )
                    dao.insertPlanet(planet)
                    
                    dao.insertEvent(UniverseEvent(
                        universeId = universe.value?.id ?: "unknown",
                        title = "Planet Discovered",
                        description = "${planet.name} (${planet.type}) was discovered by ${discoverer}."
                    ))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun triggerEvent(prompt: String) {
        viewModelScope.launch {
            try {
                val fullPrompt = """
                    You are the narrator of Hectron Live Universe. 
                    Based on this viewer prompt: "$prompt", generate a 2-3 sentence epic narrative event. 
                    Format the response strictly as a JSON object: { "title": "Event Title", "description": "Narrative description." }. 
                    Do not include markdown tags.
                """.trimIndent()
                
                val response = generativeModel.generateContent(fullPrompt)
                val text = response.text?.replace("```json", "")?.replace("```", "")?.trim()
                
                if (text != null) {
                    val json = JSONObject(text)
                    val event = UniverseEvent(
                        universeId = universe.value?.id ?: "unknown",
                        title = json.getString("title"),
                        description = json.getString("description")
                    )
                    dao.insertEvent(event)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

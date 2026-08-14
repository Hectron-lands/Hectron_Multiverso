package com.example.viewmodels

import androidx.lifecycle.ViewModel
import com.example.ecs.EcsEngine

class EcsViewModel : ViewModel() {
    val engine = EcsEngine()
    
    init {
        // Start with a small population to demonstrate the MetaBrain and Emergence
        engine.initializePopulation(12)
        engine.startSimulation()
    }
    
    override fun onCleared() {
        super.onCleared()
        engine.stopSimulation()
    }
}

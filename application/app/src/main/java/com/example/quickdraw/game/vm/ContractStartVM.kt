package com.example.quickdraw.game.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.quickdraw.TAG
import com.example.quickdraw.network.data.EmployMercenary
import com.example.quickdraw.network.data.EmployedMercenary
import com.example.quickdraw.network.data.MercenaryEmployed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

data class MercenaryOption(
    val id:Int,
    val power:Int
)

class ContractStartVM(): ViewModel() {
    val selectedContractState = MutableStateFlow(-1)
    val selectedMercenariesState = MutableStateFlow<List<MercenaryOption>>(listOf())

    fun selectMercenary(m: EmployedMercenary){
        if(!isMercenarySelected(m)){
            selectedMercenariesState.update { x->x+MercenaryOption(m.idEmployment,m.power) }
        }
    }

    fun unselectMercenary(id:Int){
        selectedMercenariesState.update { it.filter { merc->merc.id!=id } }
    }

    fun isMercenarySelected(m: EmployedMercenary):Boolean{
        return selectedMercenariesState.value.any{x->x.id==m.idEmployment}
    }

    fun successChance(required:Int): Float{
        var successRate = 100.0f
        if(required>0){
            successRate =
                kotlin.math.round((selectedMercenariesState.value.sumOf { x -> x.power }
                    .toDouble() / (required).toDouble()) * 100).toFloat()
                    .coerceAtMost(100.0f)
        }
        return successRate
    }

    fun unselectContract(){
        selectedContractState.update { -1 }
        selectedMercenariesState.update { listOf() }
    }

    fun selectContract(contract:Int){
        selectedContractState.update { if(contract>=0) contract else -1 }
    }
}
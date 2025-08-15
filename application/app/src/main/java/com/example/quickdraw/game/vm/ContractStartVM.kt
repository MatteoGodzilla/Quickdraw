package com.example.quickdraw.game.vm

import com.example.quickdraw.network.data.EmployMercenary
import com.example.quickdraw.network.data.EmployedMercenary
import com.example.quickdraw.network.data.MercenaryEmployed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class ContractStartVM {
    val selectedContractState = MutableStateFlow(-1)
    val selectedMercenariesState = MutableStateFlow<List<Pair<Int,Int>>>(listOf())

    fun selectMercenary(m: EmployedMercenary){
        if(!isMercenarySelected(m)){
            selectedMercenariesState.update { x->x+Pair<Int,Int>(m.idEmployment,m.power) }
        }
    }

    fun unselectMercenary(id:Int){
        selectedMercenariesState.update { it.filter { merc->merc.first!=id } }
    }

    fun isMercenarySelected(m: EmployedMercenary):Boolean{
        return selectedMercenariesState.value.any{x->x.first==m.idEmployment}
    }

    fun successChance(required:Int): Float{
        var successRate = 100.0f
        if(required>0){
            successRate =
                kotlin.math.round((selectedMercenariesState.value.sumOf { x -> x.second }
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
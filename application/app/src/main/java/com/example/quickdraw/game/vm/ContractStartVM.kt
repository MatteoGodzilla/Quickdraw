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
        if(!selectedMercenariesState.value.any{x->x.first==m.idEmployment}){
            selectedMercenariesState.update { x->x+Pair<Int,Int>(m.idEmployment,m.power) }
        }
    }

    fun unselectMercenary(id:Int){
        selectedMercenariesState.update { it.filter { merc->merc.first==id } }
    }

    fun isContractSelected():Boolean{
        return selectedContractState.value >=0
    }

    fun successChance(required:Int): Float{
        return 0.0f
    }

    fun unselectContract(){
        selectedContractState.update { -1 }
        selectedMercenariesState.update { listOf() }
    }

    fun selectContract(contract:Int){
        selectedContractState.update { if(contract>=0) contract else -1 }
    }
}
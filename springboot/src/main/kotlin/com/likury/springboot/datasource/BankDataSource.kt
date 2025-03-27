package com.likury.springboot.datasource

import com.likury.springboot.model.Bank

interface BankDataSource {
    fun getBanks(): Collection<Bank>
}
package com.likury.springboot.service

import com.likury.springboot.datasource.BankDataSource
import com.likury.springboot.model.Bank
import org.springframework.stereotype.Service

@Service
class BankService(private val dataSource: BankDataSource) {
    fun getBanks(): Collection<Bank> = dataSource.getBanks()
}
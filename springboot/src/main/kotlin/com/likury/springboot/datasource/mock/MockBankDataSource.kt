package com.likury.springboot.datasource.mock

import com.likury.springboot.datasource.BankDataSource
import com.likury.springboot.model.Bank
import org.springframework.stereotype.Repository

@Repository
class MockBankDataSource : BankDataSource {

    val banks = listOf( Bank("1234", 0.0, 1))
    override fun getBanks(): Collection<Bank> = banks


}
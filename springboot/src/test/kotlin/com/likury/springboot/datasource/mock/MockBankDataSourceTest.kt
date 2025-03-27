package com.likury.springboot.datasource.mock

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MockBankDataSourceTest {

    private val mockDataSource = MockBankDataSource()

    @Test
    fun `should provide a collection of banks`() {
        //given
        //when
        val banks = mockDataSource.getBanks()
        //then
        assertFalse(banks.isEmpty())
    }

    @Test
    fun `should provide some mock data`() {
        //given
        //when
        val banks = mockDataSource.getBanks()
        //that
        banks.forEach {
            assertTrue(it.accountNumber.isNotBlank())
        }
    }


}
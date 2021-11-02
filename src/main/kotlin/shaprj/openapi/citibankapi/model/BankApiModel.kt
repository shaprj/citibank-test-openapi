package shaprj.openapi.citibankapi.model

import java.math.BigDecimal
import java.util.*

data class CitiBankAccount(
    val accountUuid: UUID?,
    val currency: CitiBankCurrency?,
    var amount: BigDecimal?,
    val card: CitiBankCard?
)

data class CitiBankCard(
    val cardUuid: UUID?,
    val exists: Boolean?,
    val cardNumber: String?,
    val accountUuid: UUID?
)

enum class CitiBankCurrency {
    RUR, EUR, USD
}

data class CitiBankCustomer(
    val customerUuid: UUID,
    val customerData: CitiBankCustomerData
)

data class CitiBankCustomerApi(
    val sessionUuid: UUID, val customerData: CitiBankCustomerData
)

data class CitiBankCustomerData(
    val name: String,
    val lastName: String,
    val age: Int,
    val email: String?,
    val phone: String?
)

data class CitiBankRequest(
    val requestUuid: UUID,
    val requestData: CitiBankRequestData
)

data class CitiBankRequestApi(
    val sessionUuid: UUID,
    val requestData: CitiBankRequestData
)

class CitiBankRequestData(
    val customerFromUuid: UUID,
    val customerToUuid: UUID,
    val currency: CitiBankCurrency,
    val amount: BigDecimal
)

enum class CitiBankRequestStatus {
    UNKNOWN, CREATED, UNAVAILIBLE, INSUFFICIENT_FUNDS, SUCCESS, ERROR
}
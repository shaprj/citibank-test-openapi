package shaprj.openapi.citibankapi.util

import shaprj.openapi.citibankapi.model.*
import java.math.BigDecimal
import java.util.*

val customers = mutableMapOf<UUID, CitiBankCustomer>()
val sessions = mutableMapOf<UUID, UUID>()
val accounts = mutableMapOf<UUID, MutableList<CitiBankAccount>>()
val cards = mutableMapOf<UUID, CitiBankCard>()
val requestsByAccount = mutableMapOf<UUID, CitiBankRequest>()
val requestStatuses = mutableMapOf<UUID, CitiBankRequestStatus>()

fun addCitiBankCustomerAndReturnUuid(sessionUuid: UUID?, customerData: CitiBankCustomerData?): UUID? {

    val existCustomer = findCustomerByData(customerData)
    if (existCustomer != null) {
        return existCustomer.customerUuid
    }

    val customerUuid = UUID.randomUUID()
    val customer = CitiBankCustomer(customerUuid, customerData!!)
    customers[customerUuid] = customer
    sessions[sessionUuid!!] = customerUuid
    val accountUuid = UUID.randomUUID();
    val card = CitiBankCard(
        UUID.randomUUID(),
        true,
        generateCardNumber(),
        accountUuid
    )
    var account = CitiBankAccount(
        accountUuid,
        CitiBankCurrency.EUR,
        BigDecimal(1000),
        card
    )
    accounts.put(customer.customerUuid, mutableListOf(account))
    cards.put(account.accountUuid!!, card)
    return customerUuid;
}

fun findCustomerByData(customer: CitiBankCustomerData?): CitiBankCustomer? {
    val customers = customers.values.filter {
        it.customerData.name.equals(customer!!.name)
                && it.customerData.lastName.equals(customer.lastName)
                && it.customerData.age.equals(customer.age)
                && it.customerData.phone.equals(customer.phone)
    }
    return if (customers.isEmpty()) null else customers.get(0)
}

fun addRequestByAccount(sessionUuid: UUID, citiBankRequestData: CitiBankRequestData): UUID? {
    val requestUuid = UUID.randomUUID()

    if (!sessions.containsKey(sessionUuid) || sessions.get(sessionUuid) != citiBankRequestData.customerToUuid) {
        requestStatuses.put(requestUuid, CitiBankRequestStatus.UNAVAILIBLE)
    } else {
        val citiBankRequest = CitiBankRequest(requestUuid, citiBankRequestData)
        requestsByAccount.put(requestUuid, citiBankRequest)
        requestStatuses.put(requestUuid, CitiBankRequestStatus.CREATED)
    }

    return requestUuid
}

fun processRequests() {
    requestStatuses.filter { it.value.equals(CitiBankRequestStatus.CREATED) }
        .forEach {
            val citiBankRequest = requestsByAccount.get(it.key)!!
            val citiBankRequestData = citiBankRequest.requestData
            val existCurrencyAccountsFrom = accounts
                .filter { it.key == citiBankRequestData.customerFromUuid }
                .flatMap { it.value }
                .filter { it.currency!!.equals(citiBankRequestData.currency) }
                .toMutableList()
            val existCurrencyAccountTo = accounts
                .filter { it.key == citiBankRequestData.customerToUuid }
                .flatMap { it.value }
                .filter { it.currency!!.equals(citiBankRequestData.currency) }
                .toMutableList()
            if (existCurrencyAccountsFrom.sumOf { it.amount!! }.compareTo(citiBankRequestData.amount) == -1) {
                requestStatuses[it.key] = CitiBankRequestStatus.INSUFFICIENT_FUNDS
            } else {
                if (isTransactionProcessed(
                        citiBankRequest, existCurrencyAccountsFrom, citiBankRequestData.customerFromUuid,
                        existCurrencyAccountTo, citiBankRequestData.customerToUuid
                    )
                ) {
                    requestStatuses[it.key] = CitiBankRequestStatus.SUCCESS
                } else {
                    requestStatuses[it.key] = CitiBankRequestStatus.ERROR
                }
            }
        }
}

fun isTransactionProcessed(
    request: CitiBankRequest,
    listFrom: MutableList<CitiBankAccount>,
    customerFromUuid: UUID,
    listTo: MutableList<CitiBankAccount>,
    customerToUuid: UUID
): Boolean {
    var minusAmount = request.requestData.amount
    for (i in listFrom.indices) {
        val accountAmount = listFrom.get(i).amount!!
        var newAmount: BigDecimal;
        if (minusAmount.toInt() > 0) {
            val subtracted = if (accountAmount.compareTo(minusAmount) == 1) minusAmount else accountAmount
            minusAmount = minusAmount.subtract(subtracted)
            newAmount = accountAmount.subtract(subtracted)
            updateAmount(customerFromUuid, listFrom.get(i).accountUuid!!, newAmount)
        }
    }
    var plusAmount = listTo.get(0).amount!!.add(request.requestData.amount)
    updateAmount(customerToUuid, listTo.get(0).accountUuid!!, plusAmount)
    return true;
}


fun updateAmount(customerUuid: UUID, accountUuid: UUID, newAmount: BigDecimal) {
    accounts[customerUuid]?.filter { it.accountUuid == accountUuid }?.first()?.amount = newAmount
}

fun findRequestStatus(sessionUuid: UUID?, requestUuid: UUID?) =
    if (requestStatuses.containsKey(requestUuid)) requestStatuses.get(requestUuid)
    else CitiBankRequestStatus.UNKNOWN

fun findAllAccounts(sessionUuid: UUID?, citiBankCustomerUuid: UUID?) = accounts.get(citiBankCustomerUuid)

fun findAllCards(sessionUuid: UUID?, citiBankCustomerUuid: UUID?) = cards
    .filter { it.value.accountUuid in accounts.get(citiBankCustomerUuid)!!.map { it.accountUuid } }
    .map { it.value }
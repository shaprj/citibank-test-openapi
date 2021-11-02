package shaprj.openapi.citibankapi.controller

import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.*
import shaprj.openapi.citibankapi.model.*
import shaprj.openapi.citibankapi.util.*
import java.util.*

@RestController
@RequestMapping("/openapi")
class CitiBankApi {

    @PostMapping(path = ["/create/session"])
    @Operation(tags = ["Bank operations"], summary = "Create session")
    fun createSession(@RequestBody token: String) =
        if (isTokenValid(token)) createSessionAndReturnUuid()
        else null;

    @PostMapping(path = ["/create/customer"])
    @Operation(tags = ["Bank operations"], summary = "Create customer")
    fun createCustomer(@RequestBody request: CitiBankCustomerApi) =
        if (isSessionUuidValid(request.sessionUuid)) addCitiBankCustomerAndReturnUuid(
            request.sessionUuid,
            request.customerData
        )
        else null

    @GetMapping(path = ["/get/accounts"])
    @Operation(tags = ["Bank operations"], summary = "Get all accounts by customer uuid")
    fun accounts(@RequestParam sessionUuid: UUID, @RequestParam citiBankCustomerUuid: UUID): List<CitiBankAccount?>? =
        if (isSessionUuidValid(sessionUuid)) findAllAccounts(sessionUuid, citiBankCustomerUuid)
        else null

    @GetMapping(path = ["/get/cards"])
    @Operation(tags = ["Bank operations"], summary = "Get all cards by customer uuid")
    fun cards(@RequestParam sessionUuid: UUID, @RequestParam citiBankCustomerUuid: UUID): List<CitiBankCard?>? =
        if (isSessionUuidValid(sessionUuid)) findAllCards(sessionUuid, citiBankCustomerUuid)
        else null

    @PostMapping(path = ["/create/request"])
    @Operation(tags = ["Bank operations"], summary = "Create request by account")
    fun createRequest(@RequestBody request: CitiBankRequestApi): UUID? =
        if (isSessionUuidValid(request.sessionUuid)) addRequestByAccount(request.sessionUuid, request.requestData)
        else null

    @GetMapping(path = ["/get/request/status"])
    @Operation(tags = ["Bank operations"], summary = "Get request status")
    fun requestStatus(@RequestParam sessionUuid: UUID, @RequestParam requestUuid: UUID): CitiBankRequestStatus? =
        if (isSessionUuidValid(sessionUuid)) findRequestStatus(sessionUuid, requestUuid)
        else null
}
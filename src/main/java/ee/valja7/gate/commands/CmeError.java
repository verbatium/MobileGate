package ee.valja7.gate.commands;

import java.util.Arrays;

/**
 * Created by valeri on 14.05.16.
 */
public enum CmeError {
    PhoneFailure(0, "Phone failure"),
    NoConnectionToPhone(1, "No connection to phone"),
    PhoneAdapterLinkReserved(2, "Phone-adapter link reserved"),
    OperationNotAllowed(3, "Operation not allowed"),
    OperationNotSupported(4, "Operation not supported"),
    PhSimPinRequired(5, "PH-SIM PIN required"),
    PhFSimPinRequired(6, "PH-FSIM PIN required"),
    PhFSimPukRequired(7, "PH-FSIM PUK required"),
    SIMNotInserted(10, "SIM not inserted"),
    SIMPINRequired(11, "SIM PIN required"),
    SIMPUKRequired(12, "SIM PUK required"),
    SIMFailure(13, "SIM failure"),
    SIMBusy(14, "SIM busy"),
    SIMWrong(15, "SIM wrong"),
    IncorrectPassword(16, "Incorrect password"),
    SIMPIN2Required(17, "SIM PIN2 required"),
    SIMPUK2Required(18, "SIM PUK2 required"),
    MemoryFull(20, "Memory full"),
    InvalidIndex(21, "Invalid index"),
    NotFound(22, "Not found"),
    MemoryFailure(23, "Memory failure"),
    TextStringTooLong(24, "Text string too long"),
    InvalidCharactersInTextString(25, "Invalid characters in text string"),
    DialStringTooLong(26, "Dial string too long"),
    InvalidCharactersInDialString(27, "Invalid characters in dial string"),
    NoNetworkService(30, "No network service"),
    NetworkTimeout(31, "Network timeout"),
    NetworkNotAllowedEmergencyCallsOnly(32, "Network not allowed emergency calls only"),
    NetworkPersonalizationPINRequired(40, "Network personalization PIN required"),
    NetworkPersonalizationPUKRequired(41, "Network personalization PUK required"),
    NetworkSubsetPersonalizationPINRequired(42, "Network subset personalization PIN required"),
    NetworkSubsetPersonalizationPUKRequired(43, "Network subset personalization PUK required"),
    ServiceProviderPersonalizationPINRequired(44, "Service provider personalization PIN required"),
    ServiceProviderPersonalizationPUKRequired(45, "Service provider personalization PUK required"),
    CorporatePersonalizationPINRequired(46, "Corporate personalization PIN required"),
    CorporatePersonalizationPUKRequire(47, "Corporate personalization PUK required"),
    NoTextInformationIsAvailableCurrently(99, "No text information is available currently"),
    Unknown(100, "Unknown"),
    PUK1Blocked(101, "PUK1 blocked"),
    PUK2Blocked(102, "PUK2 blocked"),
    ServiceOptionNotSupported(132, "Service option not supported"),
    RequestedServiceOptionNotSubscribed(133, "Requested service option not subscribed"),
    ServiceOptionTemporarilyOutOfOrder(134, "Service option temporarily out of order");

    private int code;
    private String description;

    CmeError(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static CmeError GetByCode(int code) {
        return Arrays.stream(CmeError.values())
                .filter(cme_error -> cme_error.getCode() == code)
                .findFirst()
                .orElse(null);
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
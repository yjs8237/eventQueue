package com.isi.utils;

import javax.telephony.*;
import javax.telephony.events.*;
import javax.telephony.callcontrol.*;
import javax.telephony.callcontrol.events.*;
import javax.telephony.media.events.*;

import com.cisco.jtapi.extensions.*;

public class CodeToString {

    public final static String StateToString(int StateCode) {
        switch (StateCode) {
            case Provider.IN_SERVICE:
                return "IN_SERVICE      ";
            case Provider.OUT_OF_SERVICE:
                return "OUT_OF_SERVICE  ";
            case Provider.SHUTDOWN:
                return "SHUTDOWN        ";

            case Call.ACTIVE:
                return "ACTIVE          ";
            case Call.IDLE:
                return "IDLE            ";
            case Call.INVALID:
                return "INVALID         ";

            case CallControlConnection.ALERTING:
                return "ALERTING        ";
            case CallControlConnection.DIALING:
                return "DIALING         ";
            case CallControlConnection.DISCONNECTED:
                return "DISCONNECTED    ";
            case CallControlConnection.ESTABLISHED:
                return "ESTABLISHED     ";
            case CallControlConnection.FAILED:
                return "FAILED          ";
            case CallControlConnection.IDLE:
                return "IDLE            ";
            case CallControlConnection.INITIATED:
                return "INITIATED       ";
            case CallControlConnection.NETWORK_ALERTING:
                return "NETWORK_ALERTING";
            case CallControlConnection.NETWORK_REACHED:
                return "NETWORK_REACHED ";
            case CallControlConnection.OFFERED:
                return "OFFERED         ";
            case CallControlConnection.UNKNOWN:
                return "UNKNOWN         ";
            case CallControlConnection.QUEUED:
                return "QUEUED          ";

            case CallControlTerminalConnection.BRIDGED:
                return "BRIDGED         ";
            case CallControlTerminalConnection.DROPPED:
                return "DROPPED         ";
            case CallControlTerminalConnection.HELD:
                return "HELD            ";
            case CallControlTerminalConnection.IDLE:
                return "IDLE            ";
            case CallControlTerminalConnection.INUSE:
                return "INUSE           ";
            case CallControlTerminalConnection.RINGING:
                return "PASSIVE         ";
            case CallControlTerminalConnection.TALKING:
                return "TALKING         ";
            case CallControlTerminalConnection.UNKNOWN:
                return "UNKNOWN         ";

            case Connection.ALERTING:
                return "ALERTING        ";
            case Connection.CONNECTED:
                return "CONNECTED      ";
            case Connection.DISCONNECTED:
                return "DISCONNECTED   ";
            case Connection.FAILED:
                return "FAILED         ";
            case Connection.IDLE:
                return "IDLE           ";
            case Connection.INPROGRESS:
                return "INPROGRESS     ";
            case Connection.UNKNOWN:
                return "UNKNOWN        ";

            case TerminalConnection.ACTIVE:
                return "ACTIVE         ";
            case TerminalConnection.DROPPED:
                return "DROPPED        ";
            case TerminalConnection.IDLE:
                return "IDLE           ";
            case TerminalConnection.PASSIVE:
                return "PASSIVE        ";
            case TerminalConnection.RINGING:
                return "RINGING        ";
            case TerminalConnection.UNKNOWN:
                return "UNKNOWN        ";

            default:
                return "NULL        ";
        }
    }

    public final static String TermStateToString(int state) {
        String Statestring;

        switch (state) {
            case CiscoTerminal.DEVICESTATE_ACTIVE:
                Statestring = "DS_ACTIVE";
                break;
            case CiscoTerminal.DEVICESTATE_ALERTING:
                Statestring = "DS_ALERTING";
                break;
            case CiscoTerminal.DEVICESTATE_HELD:
                Statestring = "DS_HELD";
                break;
            case CiscoTerminal.DEVICESTATE_IDLE:
                Statestring = "DS_IDLE";
                break;
            case CiscoTerminal.DEVICESTATE_UNKNOWN:
                Statestring = "DS_UNKNOWN";
                break;
            default:
                Statestring = String.valueOf(state);
                break;
        }
        return Statestring;
    }

    public final static String CauseToString(int cause) {
        String Causestring;
        switch (cause) {
            case CallEv.CAUSE_CALL_CANCELLED:
                Causestring = "CAUSE_CALL_CANCELLED";
                break;
            case CallEv.CAUSE_DEST_NOT_OBTAINABLE:
                Causestring = "CAUSE_DEST_NOT_OBTAINABLE";
                break;
            case CallEv.CAUSE_INCOMPATIBLE_DESTINATION:
                Causestring = "CAUSE_INCOMPATIBLE_DESTINATION";
                break;
            case CallEv.CAUSE_LOCKOUT:
                Causestring = "CAUSE_LOCKOUT";
                break;
            case CallEv.CAUSE_NETWORK_CONGESTION:
                Causestring = "CAUSE_NETWORK_CONGESTION";
                break;
            case CallEv.CAUSE_NETWORK_NOT_OBTAINABLE:
                Causestring = "CAUSE_NETWORK_NOT_OBTAINABLE";
                break;
            case CallEv.CAUSE_NEW_CALL:
                Causestring = "CAUSE_NEW_CALL";
                break;
            case CallEv.CAUSE_NORMAL:
                Causestring = "CAUSE_NORMAL";
                break;
            case CallEv.CAUSE_RESOURCES_NOT_AVAILABLE:
                Causestring = "CAUSE_RESOURCES_NOT_AVAILABLE";
                break;
            case CallEv.CAUSE_SNAPSHOT:
                Causestring = "CAUSE_SNAPSHOT";
                break;
            case CallEv.CAUSE_UNKNOWN:
                Causestring = "CAUSE_UNKNOWN";
                break;

            case CallCtlEv.CAUSE_ALTERNATE:
                Causestring = "CAUSE_ALTERNATE";
                break;
            case CallCtlEv.CAUSE_BUSY:
                Causestring = "CAUSE_BUSY";
                break;
            case CallCtlEv.CAUSE_CALL_BACK:
                Causestring = "CAUSE_CALL_BACK";
                break;
            case CallCtlEv.CAUSE_CALL_NOT_ANSWERED:
                Causestring = "CAUSE_CALL_NOT_ANSWERED";
                break;
            case CallCtlEv.CAUSE_CALL_PICKUP:
                Causestring = "CAUSE_CALL_PICKUP";
                break;
            case CallCtlEv.CAUSE_CONFERENCE:
                Causestring = "CAUSE_CONFERENCE";
                break;
            case CallCtlEv.CAUSE_DO_NOT_DISTURB:
                Causestring = "CAUSE_DO_NOT_DISTURB";
                break;
            case CallCtlEv.CAUSE_PARK:
                Causestring = "CAUSE_PARK";
                break;
            case CallCtlEv.CAUSE_REDIRECTED:
                Causestring = "CAUSE_REDIRECTED";
                break;
            case CallCtlEv.CAUSE_REORDER_TONE:
                Causestring = "CAUSE_REORDER_TONE";
                break;
            case CallCtlEv.CAUSE_TRANSFER:
                Causestring = "CAUSE_TRANSFER";
                break;
            case CallCtlEv.CAUSE_TRUNKS_BUSY:
                Causestring = "CAUSE_TRUNKS_BUSY";
                break;
            case CallCtlEv.CAUSE_UNHOLD:
                Causestring = "CAUSE_UNHOLD";
                break;

            case CiscoOutOfServiceEv.CAUSE_CALLMANAGER_FAILURE:
                Causestring = "CAUSE_CALLMANAGER_FAILURE";
                break;
            case CiscoOutOfServiceEv.CAUSE_CTIMANAGER_FAILURE:
                Causestring = "CAUSE_CTIMANAGER_FAILURE";
                break;
            case CiscoOutOfServiceEv.CAUSE_DEVICE_FAILURE:
                Causestring = "CAUSE_DEVICE_FAILURE";
                break;
            case CiscoOutOfServiceEv.CAUSE_DEVICE_UNREGISTERED:
                Causestring = "CAUSE_DEVICE_UNREGISTERED";
                break;
            case CiscoOutOfServiceEv.CAUSE_LINE_RESTRICTED:
                Causestring = "CAUSE_LINE_RESTRICTED";
                break;
            case CiscoOutOfServiceEv.CAUSE_NOCALLMANAGER_AVAILABLE:
                Causestring = "CAUSE_NOCALLMANAGER_AVAILABLE";
                break;
            case CiscoOutOfServiceEv.CAUSE_REHOME_TO_HIGHER_PRIORITY_CM:
                Causestring = "CAUSE_REHOME_TO_HIGHER_PRIORITY_CM";
                break;
            case CiscoOutOfServiceEv.CAUSE_REHOMING_FAILURE:
                Causestring = "CAUSE_REHOMING_FAILURE";
                break;
            default:
                Causestring = String.valueOf(cause);
        }
        return Causestring;
    }

    public final static String MetaToSTring(int MetaCode) {
        String Metastring;
        switch (MetaCode) {
            case CallEv.META_CALL_STARTING:
                Metastring = "META_CALL_STARTING";
                break;
            case CallEv.META_CALL_PROGRESS:
                Metastring = "META_CALL_PROGRESS";
                break;
            case CallEv.META_CALL_ADDITIONAL_PARTY:
                Metastring = "META_CALL_ADDITIONAL_PARTY";
                break;
            case CallEv.META_CALL_REMOVING_PARTY:
                Metastring = "META_CALL_REMOVING_PARTY";
                break;
            case CallEv.META_CALL_ENDING:
                Metastring = "META_CALL_ENDING";
                break;
            case CallEv.META_CALL_MERGING:
                Metastring = "META_CALL_MERGING";
                break;
            case CallEv.META_CALL_TRANSFERRING:
                Metastring = "META_CALL_TRANSFERRING";
                break;
            case CallEv.META_SNAPSHOT:
                Metastring = "META_SNAPSHOT";
                break;
            case CallEv.META_UNKNOWN:
                Metastring = "META_UNKNOWN";
                break;
            default:
                Metastring = String.valueOf(MetaCode);
        }
        return Metastring;
    }

    public final static String EvtToString(int evt) {
        String Evtstring;
        switch (evt) {

            case CiscoTermDeviceStateActiveEv.ID:
                Evtstring = "CiscoTermDeviceStateActiveEv";
                break;
            case CiscoTermDeviceStateAlertingEv.ID:
                Evtstring = "CiscoTermDeviceStateAlertingEv";
                break;
            case CiscoTermDeviceStateHeldEv.ID:
                Evtstring = "CiscoTermDeviceStateHeldEv";
                break;
            case CiscoTermDeviceStateIdleEv.ID:
                Evtstring = "CiscoTermDeviceStateIdleEv";
                break;
            case CiscoTermOutOfServiceEv.ID:
                Evtstring = "CiscoTermOutOfServiceEv";
                break;
            case CiscoTermInServiceEv.ID:
                Evtstring = "CiscoTermInServiceEv";
                break;
            case CiscoTermRemovedEv.ID:
                Evtstring = "CiscoTermRemovedEv";
                break;

            case CiscoAddrInServiceEv.ID:
                Evtstring = "CiscoAddrInServiceEv";
                break;
            case CiscoAddrOutOfServiceEv.ID:
                Evtstring = "CiscoAddrOutOfServiceEv";
                break;
            case CiscoAddrRemovedFromTerminalEv.ID:
                Evtstring = "CiscoAddrRemovedFromTerminalEv";
                break;

            case CiscoAddrRemovedEv.ID:
                Evtstring = "CiscoAddrRemovedEv";
                break;
            case AddrObservationEndedEv.ID:
                Evtstring = "AddrObservationEndedEv";
                break;
            case CallInvalidEv.ID:
                Evtstring = "CallInvalidEv";
                break;
            case CallObservationEndedEv.ID:
                Evtstring = "CallObservationEndedEv";
                break;
            case CiscoConsultCallActiveEv.ID:
                Evtstring = "CiscoConsultCallActiveEv";
                break;
            case CiscoCallChangedEv.ID:
                Evtstring = "CiscoCallChangedEv";
                break;

            case CallCtlConnAlertingEv.ID:
                Evtstring = "CallCtlConnAlertingEv";
                break;
            case CallCtlConnDialingEv.ID:
                Evtstring = "CallCtlConnDialingEv";
                break;
            case CallCtlConnDisconnectedEv.ID:
                Evtstring = "CallCtlConnDisconnectedEv";
                break;
            case CallCtlConnEstablishedEv.ID:
                Evtstring = "CallCtlConnEstablishedEv";
                break;
            case CallCtlConnFailedEv.ID:
                Evtstring = "CallCtlConnFailedEv";
                break;
            case CallCtlConnInitiatedEv.ID:
                Evtstring = "CallCtlConnInitiatedEv";
                break;
            case CallCtlConnNetworkAlertingEv.ID:
                Evtstring = "CallCtlConnNetworkAlertingEv";
                break;
            case CallCtlConnNetworkReachedEv.ID:
                Evtstring = "CallCtlConnNetworkReachedEv";
                break;
            case CallCtlConnOfferedEv.ID:
                Evtstring = "CallCtlConnOfferedEv";
                break;
            case CallCtlConnQueuedEv.ID:
                Evtstring = "CallCtlConnQueuedEv";
                break;
            case CallCtlConnUnknownEv.ID:
                Evtstring = "CallCtlConnUnknownEv";
                break;

            case CallCtlTermConnBridgedEv.ID:
                Evtstring = "CallCtlTermConnBridgedEv";
                break;
            case CallCtlTermConnDroppedEv.ID:
                Evtstring = "CallCtlTermConnDroppedEv";
                break;
            case CallCtlTermConnHeldEv.ID:
                Evtstring = "CallCtlTermConnHeldEv";
                break;
            case CallCtlTermConnInUseEv.ID:
                Evtstring = "CallCtlTermConnInUseEv";
                break;
            case CallCtlTermConnRingingEv.ID:
                Evtstring = "CallCtlTermConnRingingEv";
                break;
            case CallCtlTermConnTalkingEv.ID:
                Evtstring = "CallCtlTermConnTalkingEv";
                break;
            case CallCtlTermConnUnknownEv.ID:
                Evtstring = "CallCtlTermConnUnknownEv";
                break;
            case CallCtlTermDoNotDisturbEv.ID:
                Evtstring = "CallCtlTermDoNotDisturbEv";
                break;

            case CiscoTransferStartEv.ID:
                Evtstring = "CiscoTransferStartEv";
                break;
            case CiscoTransferEndEv.ID:
                Evtstring = "CiscoTransferEndEv";
                break;
            case CiscoConferenceStartEv.ID:
                Evtstring = "CiscoConferenceStartEv";
                break;
            case CiscoConferenceEndEv.ID:
                Evtstring = "CiscoConferenceEndEv";
                break;
            case MediaTermConnDtmfEv.ID:
                Evtstring = "MediaTermConnDtmfEv";
                break;

            case ProvInServiceEv.ID:
                Evtstring = "ProvInServiceEv";
                break;
            case ProvObservationEndedEv.ID:
                Evtstring = "ProvObservationEndedEv";
                break;
            case ProvOutOfServiceEv.ID:
                Evtstring = "ProvOutOfServiceEv";
                break;

            case ProvShutdownEv.ID:
                Evtstring = "ProvShutdownEv";
                break;
            case CallActiveEv.ID:
                Evtstring = "CallActiveEv";
                break;

            case ConnAlertingEv.ID:
                Evtstring = "ConnAlertingEv";
                break;
            case ConnConnectedEv.ID:
                Evtstring = "ConnConnectedEv";
                break;
            case ConnCreatedEv.ID:
                Evtstring = "ConnCreatedEv";
                break;
            case ConnDisconnectedEv.ID:
                Evtstring = "ConnDisconnectedEv";
                break;
            case ConnFailedEv.ID:
                Evtstring = "ConnFailedEv";
                break;
            case ConnInProgressEv.ID:
                Evtstring = "ConnInProgressEv";
                break;
            case ConnUnknownEv.ID:
                Evtstring = "ConnUnknownEv";
                break;

            case TermConnActiveEv.ID:
                Evtstring = "TermConnActiveEv";
                break;
            case TermConnCreatedEv.ID:
                Evtstring = "TermConnCreatedEv";
                break;
            case TermConnDroppedEv.ID:
                Evtstring = "TermConnDroppedEv";
                break;
            case TermConnPassiveEv.ID:
                Evtstring = "TermConnPassiveEv";
                break;
            case TermConnRingingEv.ID:
                Evtstring = "TermConnRingingEv";
                break;
            case TermConnUnknownEv.ID:
                Evtstring = "TermConnUnknownEv";
                break;
            case TermObservationEndedEv.ID:
                Evtstring = "TermObservationEndedEv";
                break;

            case CallCtlAddrDoNotDisturbEv.ID:
                Evtstring = "CallCtlAddrDoNotDisturbEv";
                break;
            case CallCtlAddrForwardEv.ID:
                Evtstring = "CallCtlAddrForwardEv";
                break;
            case CallCtlAddrMessageWaitingEv.ID:
                Evtstring = "CallCtlAddrMessageWaitingEv";
                break;

            default:
                Evtstring = "";
        }
        return Evtstring;
    }
}

package com.Bank.BPDZ.DTO;
import org.springframework.stereotype.Component;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import org.xml.sax.InputSource;
import java.io.StringReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class PacsXmlParser {

    public Pacs008DTO parsePacs008Xml(String xml) throws Exception {
        // Set up DOM parser
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xml)));

        Pacs008DTO dto = new Pacs008DTO();

        // Populate fields
        dto.setInstructionId(getText(doc, "InstrId", 0));
        dto.setEndToEndId(getText(doc, "EndToEndId", 0));
        dto.setDebtorName(getText(doc, "Nm", 0));                // 1st <Nm>
        dto.setCreditorName(getText(doc, "Nm", 1));              // 2nd <Nm>
        dto.setDebtorIban(getText(doc, "IBAN", 0));              // 1st <IBAN>
        dto.setCreditorIban(getText(doc, "IBAN", 1));            // 2nd <IBAN>
        dto.setSenderBic(getText(doc, "BICFI", 0));              // 1st <BICFI>
        dto.setReceiverBic(getText(doc, "BICFI", 1));            // 2nd <BICFI>
        dto.setChargeBearer(getText(doc, "ChrgBr", 0));
        dto.setPaymentDetails(getText(doc, "PaymentDetails", 0)); // optional

        // Amount + Currency
        Node amtNode = doc.getElementsByTagName("InstdAmt").item(0);
        if (amtNode != null) {
            dto.setAmount(new BigDecimal(amtNode.getTextContent()));
            dto.setCurrency(amtNode.getAttributes().getNamedItem("Ccy").getTextContent());
        }

        // Transmission Mode
        dto.setTransmissionMode(getText(doc, "TransmissionMode", 0));

        return dto;
    }

    private String getText(Document doc, String tag, int index) {
        NodeList list = doc.getElementsByTagName(tag);
        return (list.getLength() > index) ? list.item(index).getTextContent() : null;
    }
    //-------------------------------------------------------------pars Pacs.009------------------------------------------------------------------------
    public Pacs009DTO parsePacs009Xml(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xml)));
        doc.getDocumentElement().normalize();

        Pacs009DTO dto = new Pacs009DTO();

        // Group Header
        Element grpHdr = (Element) doc.getElementsByTagName("GrpHdr").item(0);
        dto.setMessageId(getTextContent(grpHdr, "MsgId"));

        // Transaction Info
        Element cdtTrfTxInf = (Element) doc.getElementsByTagName("CdtTrfTxInf").item(0);

        // Payment Identification
        Element pmtId = (Element) cdtTrfTxInf.getElementsByTagName("PmtId").item(0);
        dto.setInstructionId(getTextContent(pmtId, "InstrId"));
        dto.setEndToEndId(getTextContent(pmtId, "EndToEndId"));

        // Amount
        Element amt = (Element) cdtTrfTxInf.getElementsByTagName("Amt").item(0);
        Element instdAmt = (Element) amt.getElementsByTagName("InstdAmt").item(0);
        dto.setCurrency(instdAmt.getAttribute("Ccy"));
        dto.setAmount(new BigDecimal(instdAmt.getTextContent()));

        // Settlement Method (e.g. CLRG, INGA)
        dto.setSettlementMethod(getTextContent(cdtTrfTxInf, "SttlmMtd"));

        // Transmission Mode (custom, e.g. FILE, MQ)
        dto.setTransmissionMode(getTextContent(cdtTrfTxInf, "TxMode"));

        // Sender BIC (Debtor Agent)
        Element dbtrAgt = (Element) cdtTrfTxInf.getElementsByTagName("DbtrAgt").item(0);
        Element dbtrAgtFinInstnId = (Element) dbtrAgt.getElementsByTagName("FinInstnId").item(0);
        dto.setSenderBic(getTextContent(dbtrAgtFinInstnId, "BICFI"));

        // Receiver BIC (Creditor Agent)
        Element cdtrAgt = (Element) cdtTrfTxInf.getElementsByTagName("CdtrAgt").item(0);
        Element cdtrAgtFinInstnId = (Element) cdtrAgt.getElementsByTagName("FinInstnId").item(0);
        dto.setReceiverBic(getTextContent(cdtrAgtFinInstnId, "BICFI"));

        // Debtor Name
        Element dbtr = (Element) cdtTrfTxInf.getElementsByTagName("Dbtr").item(0);
        dto.setDebtorName(getTextContent(dbtr, "Nm"));

        // Creditor Name
        Element cdtr = (Element) cdtTrfTxInf.getElementsByTagName("Cdtr").item(0);
        dto.setCreditorName(getTextContent(cdtr, "Nm"));

        // Charge Bearer
        dto.setChargeBearer(getTextContent(cdtTrfTxInf, "ChrgBr"));

        return dto;
    }


    // Helper method
    private String getTextContent(Element parent, String tagName) {
        NodeList list = parent.getElementsByTagName(tagName);
        if (list.getLength() > 0) {
            Node node = list.item(0);
            return node.getTextContent();
        }
        return null;
    }

    public String detectPacsType(String xmlContent) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xmlContent)));

        // Get the root element and its namespace
        Element root = doc.getDocumentElement();
        String namespace = root.getAttribute("xmlns");

        if (namespace.contains("pacs.009")) {
            return "pacs009";
        } else if (namespace.contains("pacs.008")) {
            return "pacs008";
        } else {
            return "unknown";
        }
    }
}

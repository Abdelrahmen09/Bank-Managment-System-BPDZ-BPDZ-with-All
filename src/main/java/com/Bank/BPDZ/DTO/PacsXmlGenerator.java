package com.Bank.BPDZ.DTO;

import java.io.StringWriter;
import java.io.StringReader;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.xml.sax.InputSource;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


@Component
public class PacsXmlGenerator {

    public String generatePacs008Xml(Pacs008DTO dto) throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        // Root element with namespace
        Element rootElement = doc.createElement("Document");
        rootElement.setAttribute("xmlns", "urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09");
        doc.appendChild(rootElement);

        // FIToFICustomerCreditTransfer element
        Element fiToFi = doc.createElement("FIToFICstmrCdtTrf");
        rootElement.appendChild(fiToFi);

        // Group Header
        Element grpHdr = doc.createElement("GrpHdr");
        fiToFi.appendChild(grpHdr);

        addElement(doc, grpHdr, "MsgId", dto.getInstructionId());
        addElement(doc, grpHdr, "CreDtTm", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        addElement(doc, grpHdr, "NbOfTxs", "1");
        
        // Payment Information
        Element cdtTrfTxInf = doc.createElement("CdtTrfTxInf");
        fiToFi.appendChild(cdtTrfTxInf);

        // Payment Identification
        Element pmtId = doc.createElement("PmtId");
        cdtTrfTxInf.appendChild(pmtId);
        addElement(doc, pmtId, "InstrId", dto.getInstructionId());
        addElement(doc, pmtId, "EndToEndId", dto.getEndToEndId());

        // Amount
        Element amt = doc.createElement("Amt");
        cdtTrfTxInf.appendChild(amt);
        Element instdAmt = doc.createElement("InstdAmt");
        instdAmt.setAttribute("Ccy", dto.getCurrency());
        instdAmt.setTextContent(dto.getAmount().toString());
        amt.appendChild(instdAmt);

        // Charge Bearer
        addElement(doc, cdtTrfTxInf, "ChrgBr", dto.getChargeBearer());

        // Debtor
        Element dbtr = doc.createElement("Dbtr");
        cdtTrfTxInf.appendChild(dbtr);
        addElement(doc, dbtr, "Nm", dto.getDebtorName());

        // Debtor Account
        Element dbtrAcct = doc.createElement("DbtrAcct");
        cdtTrfTxInf.appendChild(dbtrAcct);
        Element id = doc.createElement("Id");
        dbtrAcct.appendChild(id);
        addElement(doc, id, "IBAN", dto.getDebtorIban());

        // Debtor Agent (Bank)
        Element dbtrAgt = doc.createElement("DbtrAgt");
        cdtTrfTxInf.appendChild(dbtrAgt);
        Element finInstnId = doc.createElement("FinInstnId");
        dbtrAgt.appendChild(finInstnId);
        addElement(doc, finInstnId, "BICFI", dto.getSenderBic());

        // Creditor
        Element cdtr = doc.createElement("Cdtr");
        cdtTrfTxInf.appendChild(cdtr);
        addElement(doc, cdtr, "Nm", dto.getCreditorName());

        // Creditor Account
        Element cdtrAcct = doc.createElement("CdtrAcct");
        cdtTrfTxInf.appendChild(cdtrAcct);
        Element cdtrId = doc.createElement("Id");
        cdtrAcct.appendChild(cdtrId);
        addElement(doc, cdtrId, "IBAN", dto.getCreditorIban());

        // Creditor Agent (Bank)
        Element cdtrAgt = doc.createElement("CdtrAgt");
        cdtTrfTxInf.appendChild(cdtrAgt);
        Element cdtrFinInstnId = doc.createElement("FinInstnId");
        cdtrAgt.appendChild(cdtrFinInstnId);
        addElement(doc, cdtrFinInstnId, "BICFI", dto.getReceiverBic());

        // Convert to XML string
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        
        return writer.toString();
    }

    private void addElement(Document doc, Element parent, String tagName, String textContent) {
        Element element = doc.createElement(tagName);
        element.setTextContent(textContent);
        parent.appendChild(element);
       
    }
    
 
    
    //------------------------------------------------------------------ generate Pacs 002---------------------------------------------------------------------------
    public String generatePacs002(Pacs002DTO dto) throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        // Root element
        Element rootElement = doc.createElement("Document");
        rootElement.setAttribute("xmlns", "urn:iso:std:iso:20022:tech:xsd:pacs.002.001.10");
        doc.appendChild(rootElement);

        // Status Report
        Element statusReport = doc.createElement("FIToFIPmtStsRpt");
        rootElement.appendChild(statusReport);

        // Group Header
        Element grpHdr = doc.createElement("GrpHdr");
        statusReport.appendChild(grpHdr);
        addElement(doc, grpHdr, "MsgId", "STATUS-" + UUID.randomUUID().toString());
        addElement(doc, grpHdr, "CreDtTm", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));

        // Original Group Information
        Element orgnlGrpInf = doc.createElement("OrgnlGrpInfAndSts");
        statusReport.appendChild(orgnlGrpInf);
        addElement(doc, orgnlGrpInf, "OrgnlMsgId", dto.getOriginalMessageId());
        addElement(doc, orgnlGrpInf, "OrgnlMsgNmId", "pacs.008.001.08");
        addElement(doc, orgnlGrpInf, "GrpSts", dto.getStatus());

        // Transaction Information (for detailed status)
        Element txInf = doc.createElement("TxInfAndSts");
        statusReport.appendChild(txInf);
        addElement(doc, txInf, "OrgnlEndToEndId", dto.getOriginalEndToEndId());
        addElement(doc, txInf, "TxSts", dto.getStatus());

        // Status Reason Information (for rejections)
        if ("RJCT".equals(dto.getStatus())) {
            Element stsRsnInf = doc.createElement("StsRsnInf");
            txInf.appendChild(stsRsnInf);
            
            Element rsn = doc.createElement("Rsn");
            stsRsnInf.appendChild(rsn);
            addElement(doc, rsn, "Cd", dto.getReasonCode());
            
            if (dto.getReasonText() != null) {
                addElement(doc, stsRsnInf, "AddtlInf", dto.getReasonText());
            }
        }

        // Original Transaction Reference
        Element orgnlTxRef = doc.createElement("OrgnlTxRef");
        txInf.appendChild(orgnlTxRef);
        Element amount = addElementt(doc, orgnlTxRef, "IntrBkSttlmAmt", dto.getOriginalAmount().toString());
        amount.setAttribute("Ccy", dto.getOriginalCurrency());

        // Additional Information
        if (dto.getAdditionalInformation() != null) {
            addElement(doc, txInf, "AddtlInf", dto.getAdditionalInformation());
        }

        // Convert to XML
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        
        return writer.toString();
    }

    private Element addElementt(Document doc, Element parent, String tagName, String textContent) {
        Element element = doc.createElement(tagName);
        if (textContent != null) {
            element.setTextContent(textContent);
        }
        parent.appendChild(element);
        return element;
    }
    //-----------------------------------------------------------generate Pacs.008-----------------------------------------------------------------------
    public String generatePacs009Xml(Pacs009DTO dto) throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        // Root element with namespace
        Element rootElement = doc.createElement("Document");
        rootElement.setAttribute("xmlns", "urn:iso:std:iso:20022:tech:xsd:pacs.009.001.10");
        doc.appendChild(rootElement);

        // FICdtTrf element
        Element fiCdtTrf = doc.createElement("FICdtTrf");
        rootElement.appendChild(fiCdtTrf);

        // Group Header
        Element grpHdr = doc.createElement("GrpHdr");
        fiCdtTrf.appendChild(grpHdr);
        addElement(doc, grpHdr, "MsgId", dto.getMessageId());
        addElement(doc, grpHdr, "CreDtTm", java.time.LocalDateTime.now().toString());
        addElement(doc, grpHdr, "NbOfTxs", "1");

        // Transaction Information
        Element cdtTrfTxInf = doc.createElement("CdtTrfTxInf");
        fiCdtTrf.appendChild(cdtTrfTxInf);

        // Payment Identification
        Element pmtId = doc.createElement("PmtId");
        cdtTrfTxInf.appendChild(pmtId);
        addElement(doc, pmtId, "InstrId", dto.getInstructionId());
        addElement(doc, pmtId, "EndToEndId", dto.getEndToEndId());

        // Amount
        Element amt = doc.createElement("Amt");
        cdtTrfTxInf.appendChild(amt);
        Element instdAmt = doc.createElement("InstdAmt");
        instdAmt.setAttribute("Ccy", dto.getCurrency());
        instdAmt.setTextContent(dto.getAmount().toString());
        amt.appendChild(instdAmt);

        // Settlement Method
        addElement(doc, cdtTrfTxInf, "SttlmMtd", dto.getSettlementMethod());

        // Transmission Mode (custom field, assume your own extension)
        addElement(doc, cdtTrfTxInf, "TxMode", dto.getTransmissionMode());

        // Debtor
        Element dbtr = doc.createElement("Dbtr");
        cdtTrfTxInf.appendChild(dbtr);
        addElement(doc, dbtr, "Nm", dto.getDebtorName());

        // Creditor
        Element cdtr = doc.createElement("Cdtr");
        cdtTrfTxInf.appendChild(cdtr);
        addElement(doc, cdtr, "Nm", dto.getCreditorName());

        // Debtor Agent (Sender BIC)
        Element dbtrAgt = doc.createElement("DbtrAgt");
        cdtTrfTxInf.appendChild(dbtrAgt);
        Element dbtrAgtFinInstnId = doc.createElement("FinInstnId");
        dbtrAgt.appendChild(dbtrAgtFinInstnId);
        addElement(doc, dbtrAgtFinInstnId, "BICFI", dto.getSenderBic());

        // Creditor Agent (Receiver BIC)
        Element cdtrAgt = doc.createElement("CdtrAgt");
        cdtTrfTxInf.appendChild(cdtrAgt);
        Element cdtrAgtFinInstnId = doc.createElement("FinInstnId");
        cdtrAgt.appendChild(cdtrAgtFinInstnId);
        addElement(doc, cdtrAgtFinInstnId, "BICFI", dto.getReceiverBic());

        // Charge Bearer
        addElement(doc, cdtTrfTxInf, "ChrgBr", dto.getChargeBearer());

        // Convert to XML string
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));

        return writer.toString();
    }

    // Helper method
   
    

    
}

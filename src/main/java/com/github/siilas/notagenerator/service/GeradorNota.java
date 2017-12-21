package com.github.siilas.notagenerator.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import com.fincatto.nfe310.classes.nota.NFNotaProcessada;
import com.fincatto.nfe310.danfe.NFDanfeReport;
import com.fincatto.nfe310.parsers.NotaParser;
import com.github.siilas.notagenerator.exception.ServiceException;
import com.github.siilas.notagenerator.log.Logger;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRXmlDataSource;

public class GeradorNota {

    private static final String NFE = "1";
    private static final String MANUAL = "2";
    private static final List<String> OPCOES = Arrays.asList(NFE, MANUAL);

    public void gerar(String caminhoXml, String opcao) throws Exception {
        File nota = new File(caminhoXml);
        validar(nota, opcao);
        if (NFE.equals(opcao)) {
            geraDanfeNfe(nota, getPdfName(caminhoXml));
        } else if (MANUAL.equals(opcao)) {
            geraDanfeManual(nota, getPdfName(caminhoXml));
        }
    }

    private void validar(File xmlNota, String opcao) {
        if (!xmlNota.exists() && !xmlNota.isFile() && !xmlNota.canRead()) {
            throw new ServiceException("XML inválido!");
        }
        if (!OPCOES.contains(opcao)) {
            throw new ServiceException("Opção inválida!");
        }
    }

    private String getPdfName(String caminhoXml) {
        return caminhoXml.replace(".xml", ".pdf");
    }

    private void geraDanfeNfe(File xmlNota, String arquivoDestino) {
        try {
            byte[] logo = null;
            NFNotaProcessada notaProcessada = new NotaParser().notaProcessadaParaObjeto(xmlNota);
            NFDanfeReport report = new NFDanfeReport(notaProcessada);
            byte[] pdf = report.gerarDanfeNFe(logo);
            FileOutputStream output = new FileOutputStream(arquivoDestino);
            output.write(pdf);
            output.close();
        } catch (Exception e) {
            Logger.error("Erro gerar DANFE (NFE)", e);
        }
    }

    private void geraDanfeManual(File xmlNota, String arquivoDestino) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlNota);
            InputStream jasper = getClass().getResourceAsStream("/reports/danfe.jasper");
            JRXmlDataSource dataSource = new JRXmlDataSource(document, "/nfeProc/NFe/infNFe/det");
            JasperPrint report = JasperFillManager.fillReport(jasper, new HashMap<>(), dataSource);
            JasperExportManager.exportReportToPdfFile(report, arquivoDestino);
        } catch (Exception e) {
            Logger.error("Erro gerar DANFE (Manual)", e);
        }
    }

}

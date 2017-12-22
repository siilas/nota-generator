package com.github.siilas.notagenerator.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    public void gerar(String caminhoXml, String opcao, String caminhoLogo) throws Exception {
        File nota = new File(caminhoXml);
        validar(nota, opcao);
        if (NFE.equals(opcao)) {
            geraDanfeNfe(nota, getPdfName(caminhoXml, opcao), caminhoLogo);
        } else if (MANUAL.equals(opcao)) {
            geraDanfeManual(nota, getPdfName(caminhoXml, opcao), caminhoLogo);
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

    private String getPdfName(String caminhoXml, String opcao) {
        return caminhoXml.replace(".xml", "_" + opcao + ".pdf");
    }

    private void geraDanfeNfe(File xmlNota, String arquivoDestino, String caminhoLogo) {
        try {
            NFNotaProcessada notaProcessada = new NotaParser().notaProcessadaParaObjeto(xmlNota);
            NFDanfeReport report = new NFDanfeReport(notaProcessada);
            byte[] pdf = report.gerarDanfeNFe(getLogo(caminhoLogo));
            FileOutputStream output = new FileOutputStream(arquivoDestino);
            output.write(pdf);
            output.close();
        } catch (Exception e) {
            Logger.error("Erro gerar DANFE (NFE)", e);
        }
    }

    private void geraDanfeManual(File xmlNota, String arquivoDestino, String caminhoLogo) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlNota);
            InputStream jasper = getClass().getResourceAsStream("/reports/danfe.jasper");
            JRXmlDataSource dataSource = new JRXmlDataSource(document, "/nfeProc/NFe/infNFe/det");
            HashMap<String, Object> params = new HashMap<>();
            params.put("Logo", caminhoLogo);
            JasperPrint report = JasperFillManager.fillReport(jasper, params, dataSource);
            JasperExportManager.exportReportToPdfFile(report, arquivoDestino);
        } catch (Exception e) {
            Logger.error("Erro gerar DANFE (Manual)", e);
        }
    }

    private byte[] getLogo(String caminhoLogo) {
        try {
            return Files.readAllBytes(Paths.get(caminhoLogo));
        } catch (Exception e) {
            return null;
        }
    }

}

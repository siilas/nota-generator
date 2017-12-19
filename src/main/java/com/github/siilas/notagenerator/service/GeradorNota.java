package com.github.siilas.notagenerator.service;

import java.io.File;
import java.io.FileOutputStream;

import com.fincatto.nfe310.classes.nota.NFNotaProcessada;
import com.fincatto.nfe310.danfe.NFDanfeReport;
import com.fincatto.nfe310.parsers.NotaParser;
import com.github.siilas.notagenerator.exception.ServiceException;
import com.github.siilas.notagenerator.log.Logger;

public class GeradorNota {

    public void gerar(String caminhoXml) throws Exception {
        File nota = new File(caminhoXml);
        validar(nota);
        geraDanfe(nota, getPdfName(caminhoXml));
    }

    private void validar(File xmlNota) {
        if (!xmlNota.exists() && !xmlNota.isFile() && !xmlNota.canRead()) {
            throw new ServiceException("XML inválido!");
        }
    }

    private String getPdfName(String caminhoXml) {
        return caminhoXml.replace(".xml", ".pdf");
    }

    private void geraDanfe(File xmlNota, String arquivoDestino) {
        try {
            byte[] logo = null;
            NFNotaProcessada notaProcessada = new NotaParser().notaProcessadaParaObjeto(xmlNota);
            NFDanfeReport report = new NFDanfeReport(notaProcessada);
            byte[] pdf = report.gerarDanfeNFe(logo);
            FileOutputStream output = new FileOutputStream(arquivoDestino);
            output.write(pdf);
            output.close();
        } catch (Exception e) {
            Logger.error("Erro gerar DANFE", e);
        }
    }

}

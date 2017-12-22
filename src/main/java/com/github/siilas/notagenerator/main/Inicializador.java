package com.github.siilas.notagenerator.main;

import com.github.siilas.notagenerator.exception.ServiceException;
import com.github.siilas.notagenerator.log.Logger;
import com.github.siilas.notagenerator.service.GeradorNota;
import com.github.siilas.notagenerator.utils.ParamUtils;

public class Inicializador {

    public static void main(String[] args) {
        try {
            Logger.info("Iniciando a criação da DANFE!");
            if (args == null || args.length == 0) {
                throw new ServiceException("Informe o caminho do XML!");
            }
            String caminhoArquivo = ParamUtils.get(args, 0);
            String opcao = ParamUtils.get(args, 1);
            String caminhoLogo = ParamUtils.get(args, 2);
            new GeradorNota().gerar(caminhoArquivo, opcao, caminhoLogo);
            Logger.info("DANFE criada com sucesso!");
        } catch (ServiceException e) {
            Logger.error(e.getMessage());
        } catch (Exception e) {
            Logger.error("Erro ao gerar PDF!", e);
        }
    }

}

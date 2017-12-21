package com.github.siilas.notagenerator.main;

import com.github.siilas.notagenerator.exception.ServiceException;
import com.github.siilas.notagenerator.log.Logger;
import com.github.siilas.notagenerator.service.GeradorNota;

public class Inicializador {

    public static void main(String[] args) {
        try {
            Logger.info("Iniciando a criação da DANFE!");
            if (args == null || args.length == 0) {
                throw new ServiceException("Informe o caminho do XML!");
            }
            new GeradorNota().gerar(args[0], args[1]);
            Logger.info("DANFE criada com sucesso!");
        } catch (ServiceException e) {
            Logger.error(e.getMessage());
        } catch (Exception e) {
            Logger.error("Erro ao gerar PDF!", e);
        }
    }

}

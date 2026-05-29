package edu.upb.eventop;

import edu.upb.eventop.integracion.Sistema1AuthRequest;
import edu.upb.eventop.integracion.Sistema1AuthResponse;
import edu.upb.eventop.integracion.SistemaA;
import edu.upb.eventop.integracion.StereumCreateChargeRequest;
import edu.upb.eventop.integracion.StereumCreateChargeResponse;
import edu.upb.eventop.integracion.StereumCustomer;
import edu.upb.eventop.integracion.StereumService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Order(2)
@Component
public class StartupHttpClient implements CommandLineRunner {

    private final SistemaA sistemaA;
    private final StereumService stereumService;

    @Value("${sistema1.username:root}")
    private String username;

    @Value("${sistema1.password:Abc123**}")
    private String password;

    public StartupHttpClient(SistemaA sistemaA, StereumService stereumService) {
        this.sistemaA = sistemaA;
        this.stereumService = stereumService;
    }

    @Override
    public void run(String... args) {

        String token = null;
        try {
            Sistema1AuthResponse auth = sistemaA.auth(new Sistema1AuthRequest(username, password));
            token = auth.getAccessToken();
            log.info("Autenticado contra Sistema 1. Token obtenido: {}", token);
        } catch (Exception e) {
            log.error("No se pudo autenticar contra el Sistema 1. Continuo sin token.", e);
        }

        try {
            sistemaA.eventos(token);
        } catch (Exception e) {
            log.error("Fallo al consumir eventos del Sistema 1.", e);
        }

        try {
            sistemaA.empresas(token);
        } catch (Exception e) {
            log.error("Fallo al consumir empresas del Sistema 1.", e);
        }

        // Genera un QR de cobro consumiendo la API de Stereum.
        try {
            StereumCreateChargeRequest cobro = new StereumCreateChargeRequest(
                    "BO",
                    "100",
                    "USDT",
                    "POLYGON",
                    "Compra de prueba",
                    "10",
                    new StereumCustomer("Ricardo", "Laredo", "76887344"));

            StereumCreateChargeResponse respuesta = stereumService.crearCobro(cobro);
            log.info("QR de Stereum generado correctamente: {}", respuesta);
        } catch (Exception e) {
            log.error("Fallo al generar el QR consumiendo la API de Stereum.", e);
        }

    }
}

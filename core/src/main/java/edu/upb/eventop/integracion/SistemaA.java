package edu.upb.eventop.integracion;

import edu.upb.eventop.repository.dto.response.EmpresaDto;
import edu.upb.eventop.repository.dto.response.EventoResponseDto;
import edu.upb.eventop.service.exception.NotDataFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.List;



@Slf4j
@Service
public class SistemaA {

    @Value("${sistema1.url-base}")
    private String urlBase;
    @Value("${sistema1.connect-timeout}")
    private int connectTimeout = 10000;
    @Value("${sistema1.read-timeout}")
    private int readTimeout = 40000;


    public Sistema1AuthResponse auth(Sistema1AuthRequest request) throws Exception {
        RestClient restClient = create();

        ResponseEntity<Sistema1AuthResponse> response;
        try {
            response = restClient.post()
                    .uri(urlBase + "/api/v1/auth")
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .retrieve()
                    .toEntity(Sistema1AuthResponse.class);
        } catch (NotDataFoundException e) {
            log.error("NotDataFoundException. {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Exception. ", e);
            throw e;
        }

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Se genero error: {}", response.getStatusCode().value());
            throw new Exception("Se genero error");
        }

        return response.getBody();
    }


    public List<EventoResponseDto> eventos() throws Exception {
        return eventos(null);
    }

    public List<EventoResponseDto> eventos(String token) throws Exception {
        RestClient restClient = create();

        log.info("==> Consumiendo GET {}/api/v1/eventos", urlBase);

        ResponseEntity<List<EventoResponseDto>> response;
        try {
            response = restClient.get()
                    .uri(urlBase + "/api/v1/eventos")
                    .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                    .headers(headers -> {
                        if (token != null && !token.isBlank()) {
                            headers.setBearerAuth(token);
                        }
                    })
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<List<EventoResponseDto>>() {});
        } catch (Exception e) {
            log.error("Error al consumir /api/v1/eventos del Sistema 1. ", e);
            throw e;
        }

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Se genero error al obtener eventos: {}", response.getStatusCode().value());
            throw new Exception("Se genero error al obtener eventos");
        }

        List<EventoResponseDto> eventos = response.getBody();
        log.info("<== Respuesta /api/v1/eventos (status {}): {} evento(s)", response.getStatusCode().value(),
                eventos == null ? 0 : eventos.size());
        if (eventos != null) {
            eventos.forEach(e -> log.info("    Evento -> id={}, nombre={}, descripcion={}",
                    e.getId(), e.getNombre(), e.getDescripcion()));
        }
        return eventos;
    }


    public List<EmpresaDto> empresas(String token) throws Exception {
        RestClient restClient = create();

        log.info("==> Consumiendo GET {}/api/v1/empresas", urlBase);

        ResponseEntity<List<EmpresaDto>> response;
        try {
            response = restClient.get()
                    .uri(urlBase + "/api/v1/empresas")
                    .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                    .headers(headers -> {
                        if (token != null && !token.isBlank()) {
                            headers.setBearerAuth(token);
                        }
                    })
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<List<EmpresaDto>>() {});
        } catch (Exception e) {
            log.error("Error al consumir /api/v1/empresas del Sistema 1. ", e);
            throw e;
        }

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Se genero error al obtener empresas: {}", response.getStatusCode().value());
            throw new Exception("Se genero error al obtener empresas");
        }

        List<EmpresaDto> empresas = response.getBody();
        log.info("<== Respuesta /api/v1/empresas (status {}): {} empresa(s)", response.getStatusCode().value(),
                empresas == null ? 0 : empresas.size());
        if (empresas != null) {
            empresas.forEach(e -> log.info("    Empresa -> id={}, nombre={}, descripcion={}",
                    e.getId(), e.getNombre(), e.getDescripcion()));
        }
        return empresas;
    }


    private RestClient create() {
        SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(Duration.ofMillis(connectTimeout));
        clientHttpRequestFactory.setReadTimeout(Duration.ofMillis(readTimeout));

        return RestClient.builder().requestFactory(clientHttpRequestFactory).build();
    }

}

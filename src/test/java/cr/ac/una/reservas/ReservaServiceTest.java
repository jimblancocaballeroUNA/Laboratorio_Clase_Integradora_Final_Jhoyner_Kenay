package cr.ac.una.reservas;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cr.ac.una.reservas.exception.ReservaInvalidaException;
import cr.ac.una.reservas.model.Reserva;
import cr.ac.una.reservas.service.ReservaService;
import cr.ac.una.reservas.strategy.ValidacionBasica;
import cr.ac.una.reservas.strategy.ValidacionCapacidad;

class ReservaServiceTest {

    private ReservaService service;

    @BeforeEach
    void setUp() {

        service = new ReservaService();

        service.agregarValidador(
                new ValidacionBasica()
        );

        service.agregarValidador(
                new ValidacionCapacidad(30)
        );
    }

    @Test
    void debeAceptarReservaValida()
            throws Exception {

        Reserva reserva =
                new Reserva(
                        "R-001",
                        "Ana",
                        "Lab 1",
                        LocalDate.now(),
                        8,
                        10,
                        20
                );

        service.registrarReserva(reserva);

        assertEquals(
                1,
                service.listarReservas().size()
        );
    }

    @Test
    void debeRechazarCantidadPersonasCero() {

        Reserva reserva =
                new Reserva(
                        "R-002",
                        "Luis",
                        "Lab 1",
                        LocalDate.now(),
                        10,
                        12,
                        0
                );

        assertThrows(
                ReservaInvalidaException.class,
                () -> service.registrarReserva(
                        reserva
                )
        );
    }
}

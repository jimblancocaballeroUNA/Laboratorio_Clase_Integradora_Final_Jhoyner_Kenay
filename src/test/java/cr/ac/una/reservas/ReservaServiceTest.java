package cr.ac.una.reservas;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cr.ac.una.reservas.exception.ReservaInvalidaException;
import cr.ac.una.reservas.model.Reserva;
import cr.ac.una.reservas.observer.ResumenObserver;
import cr.ac.una.reservas.service.ReservaService;
import cr.ac.una.reservas.strategy.ValidacionBasica;
import cr.ac.una.reservas.strategy.ValidacionCapacidad;
import cr.ac.una.reservas.strategy.ValidacionHorarioPermitido;

class ReservaServiceTest {

    private static final LocalDate FECHA = LocalDate.of(2026, 11, 2);

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

        service.agregarValidador(
                new ValidacionHorarioPermitido()
        );
    }

    private Reserva reserva(String codigo, String laboratorio,
            int inicio, int fin, int personas) {
        return new Reserva(codigo, "Ana", laboratorio, FECHA, inicio, fin, personas);
    }

    // ---- Pruebas existentes ------------------------------------------

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

    // ---- Tercera prueba obligatoria ----------------------------------
    // Los horarios se superponen, pero no hay conflicto real:
    // laboratorios distintos, o la reserva anterior fue cancelada.

    @Test
    void debeAceptarReservasSuperpuestas() throws ReservaInvalidaException {
        Reserva r1 = reserva("R-001", "Lab 1", 8, 10, 20);
        Reserva r2 = reserva("R-002", "Lab 2", 9, 11, 20);
        service.registrarReserva(r1);
        service.registrarReserva(r2);
        assertEquals(2, service.listarReservas().size());

        // Misma sala y horario que r1, pero r1 se cancela antes.
        r1.cancelar();
        service.registrarReserva(reserva("R-003", "Lab 1", 8, 10, 15));
        assertEquals(3, service.listarReservas().size());
    }

    // ---- Pruebas adicionales ------------------------------------------

    @Test
    void debeRechazarSuperposicionEnMismoLaboratorio() throws ReservaInvalidaException {
        service.registrarReserva(reserva("R-001", "Lab 1", 8, 10, 20));

        assertThrows(ReservaInvalidaException.class, () ->
                service.registrarReserva(reserva("R-002", "Lab 1", 9, 11, 10)));
        assertEquals(1, service.listarReservas().size());
    }

    @Test
    void debeAceptarReservasConsecutivas() throws ReservaInvalidaException {
        service.registrarReserva(reserva("R-001", "Lab 1", 8, 10, 20));
        service.registrarReserva(reserva("R-002", "Lab 1", 10, 12, 20));

        assertEquals(2, service.listarReservas().size());
    }

    @Test
    void debeRechazarHoraInicioIgualAHoraFinal() {
        assertThrows(ReservaInvalidaException.class, () ->
                service.registrarReserva(reserva("R-001", "Lab 1", 9, 9, 10)));
    }

    @Test
    void debeRechazarReservaFueraDelHorarioPermitido() {
        assertThrows(ReservaInvalidaException.class, () ->
                service.registrarReserva(reserva("R-001", "Lab 1", 6, 8, 10)));
        assertThrows(ReservaInvalidaException.class, () ->
                service.registrarReserva(reserva("R-002", "Lab 1", 20, 22, 10)));
    }

    @Test
    void debeRechazarCapacidadExcedida() {
        assertThrows(ReservaInvalidaException.class, () ->
                service.registrarReserva(reserva("R-001", "Lab 1", 8, 10, 31)));
    }

    @Test
    void debeNotificarAlResumenObserver() throws ReservaInvalidaException {
        ResumenObserver resumen = new ResumenObserver();
        service.agregarObserver(resumen);

        service.registrarReserva(reserva("R-001", "Lab 1", 8, 10, 20));
        service.registrarReserva(reserva("R-002", "Lab 2", 8, 10, 5));

        assertEquals(2, resumen.getTotalReservas());
        assertEquals(25, resumen.getTotalPersonas());
    }
}
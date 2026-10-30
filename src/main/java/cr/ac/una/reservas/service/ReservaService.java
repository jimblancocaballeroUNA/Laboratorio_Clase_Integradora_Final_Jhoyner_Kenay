package cr.ac.una.reservas.service;

import java.util.ArrayList;
import java.util.List;

import cr.ac.una.reservas.exception.ReservaInvalidaException;
import cr.ac.una.reservas.model.Reserva;
import cr.ac.una.reservas.observer.ReservaObserver;
import cr.ac.una.reservas.strategy.ValidadorReserva;

public class ReservaService {

    private final List<Reserva> reservas;
    private final List<ValidadorReserva> validadores;
    private final List<ReservaObserver> observers;

    public ReservaService() {
        reservas = new ArrayList<>();
        validadores = new ArrayList<>();
        observers = new ArrayList<>();
    }

    public void agregarValidador(
            ValidadorReserva validador
    ) {
        validadores.add(validador);
    }

    public void agregarObserver(
            ReservaObserver observer
    ) {
        observers.add(observer);
    }

    public void registrarReserva(
            Reserva reserva
    ) throws ReservaInvalidaException {

        for (ValidadorReserva validador
                : validadores) {

            validador.validar(reserva);
        }

        validarCodigoDuplicado(reserva);
        validarSuperposicion(reserva);

        reservas.add(reserva);
        notificarRegistro(reserva);
    }

    private void validarCodigoDuplicado(
            Reserva nueva
    ) throws ReservaInvalidaException {

        for (Reserva existente : reservas) {

            if (existente.getCodigo()
                    .equalsIgnoreCase(
                            nueva.getCodigo()
                    )) {

                throw new ReservaInvalidaException(
                        "Ya existe una reserva "
                        + "con ese código"
                );
            }
        }
    }

    private void validarSuperposicion(
            Reserva nueva
    ) throws ReservaInvalidaException {

        /*
         * TODO:
         * Implementar la regla que impida
         * reservas superpuestas para el mismo
         * laboratorio y fecha.
         */
    }

    private void notificarRegistro(
            Reserva reserva
    ) {

        for (ReservaObserver observer
                : observers) {

            observer.reservaRegistrada(reserva);
        }
    }

    public List<Reserva> listarReservas() {
        return List.copyOf(reservas);
    }
}

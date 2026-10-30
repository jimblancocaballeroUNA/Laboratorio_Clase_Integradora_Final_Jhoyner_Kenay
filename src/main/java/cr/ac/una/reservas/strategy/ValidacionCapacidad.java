package cr.ac.una.reservas.strategy;

import cr.ac.una.reservas.exception.ReservaInvalidaException;
import cr.ac.una.reservas.model.Reserva;

public class ValidacionCapacidad
        implements ValidadorReserva {

    private final int capacidadMaxima;

    public ValidacionCapacidad(
            int capacidadMaxima
    ) {
        this.capacidadMaxima = capacidadMaxima;
    }

    @Override
    public void validar(Reserva reserva)
            throws ReservaInvalidaException {

        if (reserva.getCantidadPersonas() <= 0) {

            throw new ReservaInvalidaException(
                    "La cantidad de personas "
                    + "debe ser mayor que cero"
            );
        }

        if (reserva.getCantidadPersonas()
                > capacidadMaxima) {

            throw new ReservaInvalidaException(
                    "La cantidad de personas supera "
                    + "la capacidad del laboratorio"
            );
        }
    }
}

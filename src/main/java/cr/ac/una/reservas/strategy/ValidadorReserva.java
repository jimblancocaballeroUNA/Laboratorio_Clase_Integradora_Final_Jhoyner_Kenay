package cr.ac.una.reservas.strategy;

import cr.ac.una.reservas.exception.ReservaInvalidaException;
import cr.ac.una.reservas.model.Reserva;

public interface ValidadorReserva {

    void validar(Reserva reserva)
            throws ReservaInvalidaException;
}

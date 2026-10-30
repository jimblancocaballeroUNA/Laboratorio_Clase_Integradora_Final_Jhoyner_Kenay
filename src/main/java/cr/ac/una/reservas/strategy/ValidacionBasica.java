package cr.ac.una.reservas.strategy;

import cr.ac.una.reservas.exception.ReservaInvalidaException;
import cr.ac.una.reservas.model.Reserva;

public class ValidacionBasica
        implements ValidadorReserva {

    @Override
    public void validar(Reserva reserva)
            throws ReservaInvalidaException {

        if (reserva == null) {
            throw new ReservaInvalidaException(
                    "La reserva no puede ser nula"
            );
        }

        if (reserva.getCodigo() == null
                || reserva.getCodigo().isBlank()) {
            throw new ReservaInvalidaException(
                    "El código es obligatorio"
            );
        }

        if (reserva.getSolicitante() == null
                || reserva.getSolicitante().isBlank()) {
            throw new ReservaInvalidaException(
                    "El solicitante es obligatorio"
            );
        }

        if (reserva.getFecha() == null) {
            throw new ReservaInvalidaException(
                    "La fecha es obligatoria"
            );
        }

        /*
         * Existe un error intencional en esta condición.
         * Utilice el depurador para identificarlo.
         */
        if (reserva.getHoraInicio()
                < reserva.getHoraFinal()) {

            throw new ReservaInvalidaException(
                    "La hora final debe ser posterior "
                    + "a la hora inicial"
            );
        }
    }
}

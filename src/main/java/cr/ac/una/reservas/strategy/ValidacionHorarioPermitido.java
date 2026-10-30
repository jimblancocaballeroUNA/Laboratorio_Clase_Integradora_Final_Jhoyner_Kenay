package cr.ac.una.reservas.strategy;

import cr.ac.una.reservas.exception.ReservaInvalidaException;
import cr.ac.una.reservas.model.Reserva;

public class ValidacionHorarioPermitido implements ValidadorReserva {

    private static final int HORA_APERTURA = 7;
    private static final int HORA_CIERRE = 21;

    @Override
    public void validar(Reserva reserva) throws ReservaInvalidaException {
        if (reserva.getHoraInicio() < HORA_APERTURA || reserva.getHoraFinal() > HORA_CIERRE) {
            throw new ReservaInvalidaException(
                    "La reserva excede el horario permitido del edificio (" 
                    + HORA_APERTURA + ":00 - " + HORA_CIERRE + ":00)"
            );
        }
    }
}
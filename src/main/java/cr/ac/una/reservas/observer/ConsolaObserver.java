package cr.ac.una.reservas.observer;

import cr.ac.una.reservas.model.Reserva;

public class ConsolaObserver
        implements ReservaObserver {

    @Override
    public void reservaRegistrada(
            Reserva reserva
    ) {

        System.out.println(
                "[NOTIFICACIÓN] Reserva creada: "
                + reserva.getCodigo()
        );
    }
}

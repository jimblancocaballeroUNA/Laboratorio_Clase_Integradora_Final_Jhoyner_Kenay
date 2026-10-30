package cr.ac.una.reservas;

import java.time.LocalDate;

import cr.ac.una.reservas.exception.ReservaInvalidaException;
import cr.ac.una.reservas.model.Reserva;
import cr.ac.una.reservas.observer.ConsolaObserver;
import cr.ac.una.reservas.service.ReservaService;
import cr.ac.una.reservas.strategy.ValidacionBasica;
import cr.ac.una.reservas.strategy.ValidacionCapacidad;

public class App {

    public static void main(String[] args) {

        ReservaService service =
                new ReservaService();

        service.agregarValidador(
                new ValidacionBasica()
        );

        service.agregarValidador(
                new ValidacionCapacidad(30)
        );

        service.agregarObserver(
                new ConsolaObserver()
        );

        Reserva reserva =
                new Reserva(
                        "R-001",
                        "Ana",
                        "Laboratorio 1",
                        LocalDate.now(),
                        8,
                        10,
                        20
                );

        try {

            service.registrarReserva(reserva);

            System.out.println();
            System.out.println(
                    "Reservas registradas:"
            );

            for (Reserva r
                    : service.listarReservas()) {

                System.out.println(r);
            }

        } catch (ReservaInvalidaException ex) {

            System.out.println(
                    "No se pudo registrar: "
                    + ex.getMessage()
            );
        }
    }
}

package cr.ac.una.reservas.observer;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import cr.ac.una.reservas.model.Reserva;

/**
 * Observer que lleva el conteo de reservas registradas, el total de
 * personas y la cantidad de reservas por laboratorio.
 */
public class ResumenObserver implements ReservaObserver {

    private int totalReservas;
    private int totalPersonas;
    private final Map<String, Integer> reservasPorLaboratorio = new LinkedHashMap<>();

    @Override
    public void reservaRegistrada(Reserva reserva) {
        totalReservas++;
        totalPersonas += reserva.getCantidadPersonas();
        reservasPorLaboratorio.merge(reserva.getLaboratorio(), 1, Integer::sum);

        System.out.println("[RESUMEN] Reserva #" + totalReservas + ": " + reserva);
        System.out.println("[RESUMEN] Personas acumuladas: " + totalPersonas
                + " | Por laboratorio: " + reservasPorLaboratorio);
    }

    public int getTotalReservas() {
        return totalReservas;
    }

    public int getTotalPersonas() {
        return totalPersonas;
    }

    public Map<String, Integer> getReservasPorLaboratorio() {
        return Collections.unmodifiableMap(reservasPorLaboratorio);
    }
}
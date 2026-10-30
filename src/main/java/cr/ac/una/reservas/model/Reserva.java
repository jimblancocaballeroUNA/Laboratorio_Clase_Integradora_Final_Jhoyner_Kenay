package cr.ac.una.reservas.model;

import java.time.LocalDate;
import java.util.Objects;

public class Reserva {

    private String codigo;
    private String solicitante;
    private String laboratorio;
    private LocalDate fecha;
    private int horaInicio;
    private int horaFinal;
    private int cantidadPersonas;
    private EstadoReserva estado;

    public Reserva(
            String codigo,
            String solicitante,
            String laboratorio,
            LocalDate fecha,
            int horaInicio,
            int horaFinal,
            int cantidadPersonas
    ) {
        this.codigo = codigo;
        this.solicitante = solicitante;
        this.laboratorio = laboratorio;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFinal = horaFinal;
        this.cantidadPersonas = cantidadPersonas;
        this.estado = EstadoReserva.PENDIENTE;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public String getLaboratorio() {
        return laboratorio;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public int getHoraInicio() {
        return horaInicio;
    }

    public int getHoraFinal() {
        return horaFinal;
    }

    public int getCantidadPersonas() {
        return cantidadPersonas;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void confirmar() {
        estado = EstadoReserva.CONFIRMADA;
    }

    public void cancelar() {
        estado = EstadoReserva.CANCELADA;
    }

    @Override
    public String toString() {
        return codigo
                + " | "
                + solicitante
                + " | "
                + laboratorio
                + " | "
                + fecha
                + " | "
                + horaInicio
                + ":00-"
                + horaFinal
                + ":00 | "
                + cantidadPersonas
                + " personas | "
                + estado;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Reserva otra)) {
            return false;
        }

        return Objects.equals(codigo, otra.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }
}

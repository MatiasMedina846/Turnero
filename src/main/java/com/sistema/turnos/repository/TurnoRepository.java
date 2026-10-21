package com.sistema.turnos.repository;

import com.sistema.turnos.model.EstadoTurno;
import com.sistema.turnos.model.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TurnoRepository extends JpaRepository<Turno, Long> {

    @Query("SELECT COUNT(t) > 0 FROM Turno t WHERE t.empleado.id = :empleadoId AND t.estado = :estado AND t.fechaHoraInicio < :fin AND t.fechaHoraFin > :inicio")
    boolean existsOverlap(@Param("empleadoId") Long empleadoId, @Param("estado") EstadoTurno estado, @Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT COUNT(t) > 0 FROM Turno t WHERE t.empleado.id = :empleadoId AND t.estado = :estado AND t.id <> :turnoId AND t.fechaHoraInicio < :fin AND t.fechaHoraFin > :inicio")
    boolean existsOverlapExcluding(@Param("empleadoId") Long empleadoId, @Param("estado") EstadoTurno estado, @Param("turnoId") Long turnoId, @Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    List<Turno> findAllByOrderByFechaHoraInicioAsc();
}

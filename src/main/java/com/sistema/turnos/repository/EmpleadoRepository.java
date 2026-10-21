package com.sistema.turnos.repository;

import com.sistema.turnos.model.Empleado;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    /**
     * Loads the employee taking a row lock (SELECT ... FOR UPDATE). Used when booking so that
     * two concurrent requests for the same employee are serialized and cannot both pass the
     * overlap check.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Empleado e WHERE e.id = :id")
    Optional<Empleado> findByIdForUpdate(@Param("id") Long id);
}

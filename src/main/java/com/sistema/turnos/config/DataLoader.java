package com.sistema.turnos.config;

import com.sistema.turnos.model.Empleado;
import com.sistema.turnos.model.Servicio;
import com.sistema.turnos.repository.EmpleadoRepository;
import com.sistema.turnos.repository.ServicioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    private final EmpleadoRepository empleadoRepository;
    private final ServicioRepository servicioRepository;

    public DataLoader(EmpleadoRepository empleadoRepository, ServicioRepository servicioRepository) {
        this.empleadoRepository = empleadoRepository;
        this.servicioRepository = servicioRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (empleadoRepository.count() == 0) {
            empleadoRepository.save(new Empleado("Carlos López", "Corte de cabello"));
            empleadoRepository.save(new Empleado("María García", "Manicuría"));
            empleadoRepository.save(new Empleado("Juan Pérez", "Barbería"));
            log.info("Datos iniciales de empleados cargados.");
        } else {
            log.info("Empleados ya existen en la base de datos, se omite carga.");
        }

        if (servicioRepository.count() == 0) {
            servicioRepository.save(new Servicio("Corte Clásico", 30));
            servicioRepository.save(new Servicio("Corte + Barba", 45));
            servicioRepository.save(new Servicio("Manicuría Completa", 60));
            servicioRepository.save(new Servicio("Corte Ejecutivo", 90));
            log.info("Datos iniciales de servicios cargados.");
        } else {
            log.info("Servicios ya existen en la base de datos, se omite carga.");
        }
    }
}

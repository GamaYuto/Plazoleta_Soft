package com.plazoleta.msrestaurante.repository;

import com.plazoleta.msrestaurante.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    List<Empleado> findByRestauranteId(Long restauranteId);
}

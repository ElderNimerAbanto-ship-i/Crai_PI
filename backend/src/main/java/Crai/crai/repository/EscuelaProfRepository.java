package Crai.crai.repository;

import Crai.crai.entity.EscuelaProf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EscuelaProfRepository extends JpaRepository<EscuelaProf, Long> {
}

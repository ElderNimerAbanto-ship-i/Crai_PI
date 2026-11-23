package Crai.crai.repository;

import Crai.crai.entity.Rol;
import Crai.crai.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    boolean existsByUsername(String username);
    boolean existsByCodigoUni(String codigoUni);
    boolean existsByRol(Rol rol);
    Optional<Usuario> findByCodigoUni(String codigoUni);
    Optional<Usuario> findByUsername(String username);
}

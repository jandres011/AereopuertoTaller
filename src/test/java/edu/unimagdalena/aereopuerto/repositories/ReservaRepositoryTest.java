package edu.unimagdalena.aereopuerto.repositories;

import edu.unimagdalena.aereopuerto.entities.Pasajero;
import edu.unimagdalena.aereopuerto.entities.Pasaporte;
import edu.unimagdalena.aereopuerto.entities.Reserva;
import edu.unimagdalena.aereopuerto.entities.Vuelo;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Import(TestcontainersConfiguration.class)
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ReservaRepositoryTest {

    Pasajero pasajero1;
    Pasajero pasajero2;
    Pasajero pasajero3;
    Set<Reserva> reservas1 = new HashSet<>();
    Set<Reserva> reservas2 = new HashSet<>();
    Set<Reserva> reservas3 = new HashSet<>();
    Reserva reserva1;
    Reserva reserva2;
    Reserva reserva3;
    Vuelo vuelo1;
    Vuelo vuelo2;
    Vuelo vuelo3;
    Vuelo vuelo4;
    Vuelo vuelo5;
    Pasaporte pasaporte1;
    Pasaporte pasaporte2;
    Pasaporte pasaporte3;

    @Autowired
    PasajeroRepository pasajeroRepository;
    @Autowired
    ReservaRepository reservaRepository;
    @Autowired
    VueloRepository vueloRepository;
    @Autowired
    PasaporteRepository pasaporteRepository;

    @BeforeEach
    void setUp() {
        pasaporte1 = pasaporteRepository.save(Pasaporte.builder().numero("B894A").build());
        pasaporte2 = pasaporteRepository.save(Pasaporte.builder().numero("A578B").build());
        pasaporte3 = pasaporteRepository.save(Pasaporte.builder().numero("A588C").build());

        pasajero1 = pasajeroRepository.save(Pasajero.builder().NID("25A").nombre("Juan Antonio").pasaporte(pasaporte1).reservas(reservas1).build());
        pasajero2 = pasajeroRepository.save(Pasajero.builder().NID("21B").nombre("Juan Andrés").pasaporte(pasaporte2).reservas(reservas2).build());
        pasajero3 = pasajeroRepository.save(Pasajero.builder().NID("25C").nombre("Andrés Juan").pasaporte(pasaporte3).reservas(reservas3).build());

        vueloRepository.save(vuelo1 = Vuelo.builder().origen("Santa Marta").destino("Bogotá").build());
        vueloRepository.save(vuelo2 = Vuelo.builder().origen("Santa Marta").destino("Medellín").reservas(new HashSet<>(reservas2)).build());
        vueloRepository.save(vuelo3 = Vuelo.builder().origen("Barranquilla").destino("Valledupar").build());
        vueloRepository.save(vuelo4 = Vuelo.builder().origen("Manizales").destino("Cucuta").build());
        vueloRepository.save(vuelo5 = Vuelo.builder().origen("Venezia").destino("Madrid").build());

        reserva1 = Reserva.builder().pasajero(pasajero1).vuelo(vuelo1).build();
        reserva2 = Reserva.builder().pasajero(pasajero1).vuelo(vuelo2).build();
        reserva3 = Reserva.builder().pasajero(pasajero1).vuelo(vuelo3).codigoReserva(UUID.fromString("3be0e7b7-7c36-4f3d-b822-c3aba5bdf68a")).build();

        reservas1.add(reserva1);
        reservas1.add(reserva2);
        reservas1.add(reserva3);
        reservas2.add(reserva3);
        reservas2.add(reserva1);
        reservaRepository.saveAll(reservas1);
        reservaRepository.saveAll(reservas2);
    }

    @AfterEach
    void tearDown() {
        reservaRepository.deleteAll();
        pasajeroRepository.deleteAll();
        pasaporteRepository.deleteAll();
        vueloRepository.deleteAll();
    }

    @Test
    @Order(1)
    void countReservasByPasajeroNombre() {
        Long  countTest = reservaRepository.countReservasByPasajeroNombre("Juan Antonio");
        assertEquals(pasajero1.getReservas().size(), countTest);
    }

    @Test
    @Order(2)
    void findReservaById() {
        Reserva reservaTest = reservaRepository.findReservaById(reserva2.getId());
        Assertions.assertNotNull(reservaTest);
        Assertions.assertEquals(reserva2.getId(), reservaTest.getId());
    }

    @Test
    @Order(3)
    void findReservasByPasajeroId() {
        List<Reserva> reservasTest = reservaRepository.findReservasByPasajeroId(pasajero3.getId());
        Assertions.assertTrue(reservasTest.isEmpty());
    }

    @Test
    @Order(4)
    void findReservaByCodigoReserva() {
        Reserva reservaTest = reservaRepository.findReservaByCodigoReserva(UUID.fromString("3be0e7b7-7c36-4f3d-b822-c3aba5bdf68a"));
        Assertions.assertNotNull(reservaTest);
        Assertions.assertEquals(reserva3.getCodigoReserva(), reservaTest.getCodigoReserva());
    }

    @Test
    @Order(5)
    void findReservasByVueloId() {
        List<Reserva> reservasTest = reservaRepository.findReservasByVueloId(vuelo2.getId());
        Assertions.assertFalse(reservasTest.isEmpty());
        Assertions.assertEquals(1, reservasTest.size());
    }

    @Test
    @Order(6)
    void findReservasByOrigenAndDestino() {
        List<Reserva> reservasTest = reservaRepository.findReservasByOrigenAndDestino(vuelo2.getOrigen(), vuelo2.getDestino());
        Assertions.assertFalse(reservasTest.isEmpty());
        Assertions.assertEquals(1, reservasTest.size());
    }

    @Test
    @Order(7)
    void findReservasByCiudadOrigen() {
        List<Reserva> reservasTest = reservaRepository.findReservasByCiudadOrigen(vuelo2.getOrigen());
        Assertions.assertFalse(reservasTest.isEmpty());
        Assertions.assertEquals(2, reservasTest.size());
    }

    @Test
    @Order(8)
    void findReservationsByFlightDestination() {
        List<Reserva> vuelosTest = reservaRepository.findReservationsByFlightDestination(vuelo2.getDestino());
        Assertions.assertFalse(vuelosTest.isEmpty());
        Assertions.assertEquals(1, vuelosTest.size());
    }

    @Test
    @Order(9)
    void findReservasByCodigoVuelo() {
        List<Reserva> reservasTest = reservaRepository.findReservasByCodigoVuelo(vuelo2.getId());
        Assertions.assertFalse(reservasTest.isEmpty());
        Assertions.assertEquals(1, reservasTest.size());
    }

    @Test
    @Order(10)
    void findRecentReservations() {
        List<Reserva> reservasTest = reservaRepository.findRecentReservations();
        Assertions.assertFalse(reservasTest.isEmpty());
    }
}
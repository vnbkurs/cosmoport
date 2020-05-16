package com.space.service;

import com.space.controller.ShipOrder;
import com.space.exception.BadRequestException;
import com.space.exception.NotFoundException;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShipService {

    private final ShipRepository shipRepository;

    @Autowired
    public ShipService(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    @Transactional
    public Ship createShip(Ship ship) {
        if (ship.getName() == null ||
                ship.getName().isEmpty() ||
                ship.getName().length() > 50 ||
                ship.getPlanet() == null ||
                ship.getPlanet().isEmpty() ||
                ship.getPlanet().length() > 50 ||
                ship.getShipType() == null ||
                ship.getProdDate() == null ||
                ship.getProdDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear() < 2800 ||
                ship.getProdDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear() > 3019 ||
                ship.getSpeed() == null ||
                ship.getSpeed() < 0.01d ||
                ship.getSpeed() > 0.99d ||
                ship.getCrewSize() == null ||
                ship.getCrewSize() < 1 ||
                ship.getCrewSize() > 9999) {
            return null;
        } else if (ship.getUsed() == null) {
            ship.setUsed(false);
        }

        ship.setSpeed((double) Math.round(ship.getSpeed() * 100) / 100);
        ship.setRating(countedShipRating(ship));
        return shipRepository.save(ship);
    }
    @Transactional
    public void delete(Long id) {
        if (id==0) {
            throw new BadRequestException();
        }
        if (!shipRepository.existsById(id)) {
            throw new NotFoundException();
        }
        shipRepository.deleteById(id);
    }

    @Transactional
    public Ship getShipById(Long id) {
        if (id==0) {
            throw new BadRequestException();
        }
        if (!shipRepository.existsById(id)) {
            throw new NotFoundException();
        }
        return shipRepository.findById(id).orElse(null);
    }

    @Transactional
    public Ship updateShip(Ship newShip, Long id) {

        if (id==0) {
            throw new BadRequestException();
        }
        Ship shipUpdate = getShipById(id);
        if (newShip == null || shipUpdate == null) {
            throw new BadRequestException();
        }
        if (newShip.getName() != null) {
            if (newShip.getName().length() > 50 ||  newShip.getName().isEmpty()) {
                throw new BadRequestException();
            }
            shipUpdate.setName(newShip.getName());
        }
        if (newShip.getPlanet() != null) {
            if (newShip.getPlanet().length() > 50 ||  newShip.getPlanet().isEmpty()) {
                throw new BadRequestException();
            }
            shipUpdate.setPlanet(newShip.getPlanet());
        }
        if (newShip.getShipType() != null) {
            shipUpdate.setShipType(newShip.getShipType());
        }
        if (newShip.getProdDate() != null) {
            if (newShip.getProdDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear() < 2800 ||
                    newShip.getProdDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear() > 3019) {
                throw new BadRequestException();
            }
            shipUpdate.setProdDate(newShip.getProdDate());
        }
        if (newShip.getUsed() != null) {
            shipUpdate.setUsed(newShip.getUsed());
        }
        if (newShip.getSpeed() != null) {
            if ( newShip.getSpeed() < 0.01d ||  newShip.getSpeed() > 0.99d) {
                throw new BadRequestException();
            }
            shipUpdate.setSpeed(newShip.getSpeed());
        }
        if (newShip.getCrewSize() != null) {
            if (newShip.getCrewSize() < 1 ||  newShip.getCrewSize() > 9999) {
                throw new BadRequestException();
            }
            shipUpdate.setCrewSize(newShip.getCrewSize());
        }

        shipUpdate.setRating(countedShipRating(shipUpdate));
        return shipRepository.save(shipUpdate);
    }

    public List<Ship> getShipList(String name, String planet, ShipType shipType, Long after, Long before,
                                  Boolean getUsed, Double minSpeed, Double maxSpeed, Integer minCrewSize,
                                  Integer maxCrewSize, Double minRating, Double maxRating) {
        List<Ship> ListShips = (List<Ship>) shipRepository.findAll();
        if (name != null) {
            ListShips = ListShips.stream().filter(ship -> ship.getName().contains(name)).collect(Collectors.toList());
        }
        if (planet != null) {
            ListShips = ListShips.stream().filter(ship -> ship.getPlanet().contains(planet)).collect(Collectors.toList());
        }
        if (shipType != null) {
            ListShips = ListShips.stream().filter(ship -> ship.getShipType().equals(shipType)).collect(Collectors.toList());
        }
        if (after != null) {
            ListShips = ListShips.stream().filter(ship -> ship.getProdDate().after(new Date(after))).collect(Collectors.toList());
        }
        if (before != null) {
            ListShips = ListShips.stream().filter(ship -> ship.getProdDate().before(new Date(before))).collect(Collectors.toList());
        }
        if (getUsed != null) {
            ListShips = ListShips.stream().filter(ship -> ship.getUsed().equals(getUsed)).collect(Collectors.toList());
        }
        if (minSpeed != null) {
            ListShips = ListShips.stream().filter(ship -> ship.getSpeed() >= minSpeed).collect(Collectors.toList());
        }
        if (maxSpeed != null) {
            ListShips = ListShips.stream().filter(ship -> ship.getSpeed() <= maxSpeed).collect(Collectors.toList());
        }
        if (minCrewSize != null) {
            ListShips = ListShips.stream()
                    .filter(ship -> ship.getCrewSize() >= minCrewSize).collect(Collectors.toList());
        }
        if (maxCrewSize != null) {
            ListShips = ListShips.stream().filter(ship -> ship.getCrewSize() <= maxCrewSize).collect(Collectors.toList());
        }
        if (minRating != null) {
            ListShips = ListShips.stream().filter(ship -> ship.getRating() >= minRating).collect(Collectors.toList());
        }
        if (maxRating != null) {
            ListShips = ListShips.stream().filter(ship -> ship.getRating() <= maxRating).collect(Collectors.toList());
        }
        return ListShips;

    }

    public List<Ship> filteredShips(final List<Ship> shipList, ShipOrder order, Integer pageNumber, Integer pageSize) {
        pageNumber = pageNumber == null ? 0 : pageNumber;
        pageSize = pageSize == null ? 3 : pageSize;
        return shipList.stream().sorted(getComparator(order)).skip(pageNumber * pageSize).limit(pageSize).collect(Collectors.toList());
    }

    private Comparator<Ship> getComparator(ShipOrder shipOrder) {
        if (shipOrder == null) {
            return Comparator.comparing(Ship::getId);
        }
        Comparator<Ship> comparator = null;
        switch (shipOrder.getFieldName()) {
            case "id":
                comparator = Comparator.comparing(Ship::getId);
                break;
            case "speed":
                comparator = Comparator.comparing(Ship::getSpeed);
                break;
            case "prodDate":
                comparator = Comparator.comparing(Ship::getProdDate);
                break;
            case "rating":
                comparator = Comparator.comparing(Ship::getRating);
        }

        return comparator;
    }

        private Double countedShipRating(Ship ship) {
        double speed = ship.getSpeed();
        double coefficientUsed = ship.getUsed() ? 0.5d : 1.0d;
        int currentYear = 3019;
        int productionDate = ship.getProdDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear();
        double rating = (80 * speed * coefficientUsed) / (double) (currentYear - productionDate + 1);
        return (double) Math.round(rating * 100) / 100;
    }
}